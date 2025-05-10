package ru.practicum.main.service;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.db.CompilationRepository;
import ru.practicum.main.db.entity.Compilation;
import ru.practicum.main.dto.*;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.mapper.CompilationMapper;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CompilationService {
    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
    private final EventService eventService;
    private final NamedParameterJdbcOperations jdbc;

    private Compilation getById(Long id) {
        return compilationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Compilation with id " + id + " not found"));
    }

    private List<EventDTO> saveEvents(List<Long> events, Long compilationId) {
        List<EventDTO> eventDTOList = new ArrayList<>();
        if (events != null && !events.isEmpty()) {
            eventDTOList = eventService.getEventsByIds(events);
        } else {
            events = List.of();
        }

        if (eventDTOList.size() != events.size()) {
            throw new ConflictException("There are events with wrong IDs");
        }

        deleteCompilationEvents(compilationId);
        if (events.isEmpty()) {
            return List.of();
        }
        insertCompilationEvents(events, compilationId);

        return eventService.getEventsByIds(events);
    }

    private void insertCompilationEvents(List<Long> eventIds, Long compilationId) {
        SqlParameterSource[] batch = eventIds.stream()
                .map(a -> new MapSqlParameterSource()
                        .addValue("compilation_id", compilationId)
                        .addValue("event_id", a)
                )
                .toArray(SqlParameterSource[]::new);

        jdbc.batchUpdate("INSERT INTO compilation_events (compilation_id, event_id) VALUES(:compilation_id, :event_id)", batch);
    }

    private void deleteCompilationEvents(Long compilationId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("compilation_id", compilationId);

        jdbc.update("""
                    delete from compilation_events
                        where compilation_id = :compilation_id
                """, params);
    }

    @Transactional
    public CompilationDTO createCompilation(CompilationCreateDTO compilationCreateDTO) {
        if (compilationCreateDTO.getPinned() == null) {
            compilationCreateDTO.setPinned(false);
        }

        Compilation compilation = compilationRepository.save(compilationMapper.toCompilation(compilationCreateDTO));
        List<EventDTO> events = saveEvents(compilationCreateDTO.getEvents(), compilation.getId());

        return compilationMapper.toCompilationDTO(compilation, events);
    }

    @Transactional
    public void deleteCompilation(Long id) {
        Compilation compilation = getById(id);
        deleteCompilationEvents(id);
        compilationRepository.delete(compilation);
    }

    @Transactional
    public CompilationDTO editCompilation(CompilationPatchDTO compilationPatchDTO, Long id) {
        Compilation compilation = getById(id);

        if (compilationPatchDTO.getPinned() != null) {
            compilation.setPinned(compilationPatchDTO.getPinned());
        }
        if (compilationPatchDTO.getTitle() != null) {
            compilation.setTitle(compilationPatchDTO.getTitle());
        }
        compilation = compilationRepository.save(compilation);

        List<Long> eventIds = compilationPatchDTO.getEvents();
        if (eventIds != null && !eventIds.isEmpty()) {
            deleteCompilationEvents(id);
            insertCompilationEvents(eventIds, id);
        }

        return compilationMapper.toCompilationDTO(compilation, eventService.getEventsByIds(eventIds));
    }

    @Transactional(readOnly = true)
    public CompilationDTO getCompilationById(Long id) {
        List<Long> eventIds = compilationRepository.getCompilationEventIds(id);
        return compilationMapper.toCompilationDTO(getById(id), eventService.getEventsByIds(eventIds));
    }

    @Transactional(readOnly = true)
    public List<CompilationDTO> getCompilations(Boolean pinned, Long from, Long size) {
//        компиляции
        List<Compilation> compilations = compilationRepository.getCompilations(pinned, from, size);

        if (compilations == null || compilations.isEmpty()) {
            return List.of();
        }
//        к этим компиляциям ИД событий
        List<CompilationAndEventDTO> compilationsEvents = compilationRepository.findCompilationsAndEvents(
                compilations.stream()
                        .map(Compilation::getId)
                        .toList()
        );
//        собираем в кучу ИД событий
        List<EventDTO> eventDTOList = eventService.getEventsByIds(
                compilationsEvents.stream()
                        .map(a -> a.getEventId())
                        .toList()
        );

        List<CompilationDTO> compilationDTOS = new ArrayList<>();
        for (Compilation compilation : compilations) {
//            берем все евентИд для компиляции
            List<CompilationAndEventDTO> compilationsEventsLoop = compilationsEvents.stream()
                    .filter(a -> a.getCompilationId().equals(compilation.getId()))
                    .toList();

//            заполняем евентами
            List<EventDTO> eventDTOsLoop = new ArrayList<>();
            for (CompilationAndEventDTO compilationAndEventDTO : compilationsEventsLoop) {
                eventDTOsLoop.add(eventDTOList.stream()
                        .filter(event -> event.getId() == compilationAndEventDTO.getEventId())
                        .findFirst()
                        .get()
                );
            }

            compilationDTOS.add(compilationMapper.toCompilationDTO(compilation, eventDTOsLoop));
        }

        return compilationDTOS;
    }
}
