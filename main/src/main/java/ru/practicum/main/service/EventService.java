package ru.practicum.main.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.stats.dto.StatsCreateDTO;
import ru.practicum.main.db.EventRepository;
import ru.practicum.main.db.entity.Category;
import ru.practicum.main.db.entity.Event;
import ru.practicum.main.db.entity.QEvent;
import ru.practicum.main.db.entity.User;
import ru.practicum.main.dto.EventCreateDTO;
import ru.practicum.main.dto.EventDTO;
import ru.practicum.main.dto.EventPatchDTO;
import ru.practicum.main.enumeration.EventState;
import ru.practicum.main.exception.BadRequestException;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.mapper.EventMapper;
import ru.practicum.main.util.Utils;
import ru.practicum.stats.client.StatClient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class EventService {
    private final EventMapper eventMapper;
    private final EventRepository eventRepository;
    private final UserService userService;
    private final CategoryService categoryService;
    private final NamedParameterJdbcOperations jdbc;
    private final StatClient statClient;

    private static final String APP = "ewm-main-service";

    private void sendStats(StatsCreateDTO statsCreateDTO) {
        try {
            statClient.addHit(statsCreateDTO);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event id=" + id + " not found"));
    }

    private Event getUserEvent(Long userId, Long eventId) {
        return eventRepository.getEventByIdAndUserId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event id=" + eventId + " by userId=" + userId + " not found"));
    }


    public Event patchEvent(Event event, EventPatchDTO eventPatchDTO) {
        if (eventPatchDTO.getAnnotation() != null) {
            event.setAnnotation(eventPatchDTO.getAnnotation());
        }
        if (eventPatchDTO.getCategory() != null) {
            event.setCategory(categoryService.getCategoryById(eventPatchDTO.getCategory()));
        }
        if (eventPatchDTO.getDescription() != null) {
            event.setDescription(eventPatchDTO.getDescription());
        }
        if (eventPatchDTO.getEventDate() != null) {
            if (LocalDateTime.now().isAfter(Utils.decodeDateTime(eventPatchDTO.getEventDate()).minusHours(1))) {
                throw new BadRequestException("Wrong event date");
            }
            event.setEventDate(Utils.decodeDateTime(eventPatchDTO.getEventDate()));
        }
        if (eventPatchDTO.getLocation() != null) {
            event.setLat(eventPatchDTO.getLocation().getLat());
            event.setLon(eventPatchDTO.getLocation().getLon());
        }
        if (eventPatchDTO.getPaid() != null) {
            event.setPaid(eventPatchDTO.getPaid());
        }
        if (eventPatchDTO.getParticipantLimit() != null) {
            event.setParticipantLimit(eventPatchDTO.getParticipantLimit());
        }
        if (eventPatchDTO.getRequestModeration() != null) {
            event.setRequestModeration(eventPatchDTO.getRequestModeration());
        }
        if (eventPatchDTO.getTitle() != null) {
            event.setTitle(eventPatchDTO.getTitle());
        }
        String stateAction = eventPatchDTO.getStateAction();
        if (stateAction != null && !stateAction.isBlank()) {
            EventState state;
            try {
                if (stateAction.equals("PUBLISH_EVENT")) {
                    if (EventState.PENDING.equals(event.getState())) {
                        state = EventState.PUBLISHED;
                        event.setPublished(LocalDateTime.now());
                    } else {
                        throw new ConflictException("Wrong event state");
                    }
                } else if (stateAction.equals("CANCEL_REVIEW")) {
                    state = EventState.CANCELED;
                } else if (stateAction.equals("SEND_TO_REVIEW")) {
                    state = EventState.PENDING;
                } else if (stateAction.equals("REJECT_EVENT")) {
                    if (EventState.PUBLISHED.equals(event.getState())) {
                        throw new ConflictException("Published event can't be rejected");
                    }
                    state = EventState.CANCELED;
                } else {
                    state = EventState.valueOf(stateAction);
                }

            } catch (IllegalArgumentException e) {
                throw new ConflictException("Invalid state action: " + stateAction);
            }
            event.setState(state);
        }

        return eventRepository.save(event);
    }

    @Transactional
    public EventDTO create(EventCreateDTO eventCreateDTO, Long userId) {
        User user = userService.getUserById(userId);
        Category category = categoryService.getCategoryById(eventCreateDTO.getCategory());
        Event event = eventMapper.toEventFromEventCreateDTO(eventCreateDTO, user, category);
        checkCreate(event);
        return eventMapper.eventToEventDTO(eventRepository.save(event));
    }

    private void checkCreate(Event event) {
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadRequestException("Wrong event date");
        }
    }

    public EventDTO getPublishedEventById(Long id, HttpServletRequest request) {

        StatsCreateDTO statsCreateDTO = new StatsCreateDTO(
                APP,
                request.getRequestURI(),
                request.getRemoteAddr(),
                LocalDateTime.now()
        );
        sendStats(statsCreateDTO);

        Event event = getEventById(id);
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new NotFoundException("Published event id=" + id + " not found");
        }

        setEventView(event, request);
        return eventMapper.eventToEventDTO(event);
    }

    private void setEventView(Event event, HttpServletRequest request) {
        if (eventRepository.getViewedIp(event.getId(), request.getRemoteAddr()).isEmpty()) {
            event.setViews(event.getViews() + 1);
            saveViewedIp(event.getId(), request.getRemoteAddr());
            eventRepository.save(event);
        }
    }

    private void saveViewedIp(Long eventId, String ip) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("eventId", eventId);
        params.addValue("ip", ip);
        jdbc.update("INSERT INTO event_views (event_id, ip) VALUES(:eventId, :ip)", params);
    }

    @Transactional
    public EventDTO editEvent(EventPatchDTO eventPatchDTO, Long id) {
        Event event = getEventById(id);
        return eventMapper.eventToEventDTO(patchEvent(event, eventPatchDTO));
    }

    public List<EventDTO> getEventsByUserId(Long userId, Long from, Long size) {
        return eventRepository.getEventsByUserId(userId, from, size).stream()
                .map(eventMapper::eventToEventDTO)
                .toList();
    }

    public EventDTO getEventByUserIdAndEventId(Long userId, Long eventId) {
        return eventMapper.eventToEventDTO(getUserEvent(userId, eventId));
    }

    public List<EventDTO> getAllEvents() {
        return eventRepository.findAll().stream()
                .map(eventMapper::eventToEventDTO)
                .toList();
    }

    public List<EventDTO> getEventsByIds(List<Long> eventIds) {
        if (eventIds == null || eventIds.isEmpty()) {
            return new ArrayList<>();
        }
        return eventRepository.findAllById(eventIds).stream()
                .map(eventMapper::eventToEventDTO)
                .toList();
    }

    @Transactional
    public EventDTO editEventByUserIdAndEventId(EventPatchDTO eventPatchDTO, Long userId, Long eventId) {
        var event = getUserEvent(userId, eventId);
        if (!EventState.PENDING.equals(event.getState()) && !EventState.CANCELED.equals(event.getState())) {
            throw new ConflictException("Event id=" + eventId + " by userId=" + userId + " has wrong status " + event.getState());
        }
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadRequestException("Event id=" + eventId + " has wrong date");
        }

        return eventMapper.eventToEventDTO(patchEvent(event, eventPatchDTO));
    }

    public List<EventDTO> getAdminEvents(List<Long> userIds,
                                         List<String> states,
                                         List<Long> categories,
                                         String rangeStart,
                                         String rangeEnd,
                                         int from,
                                         int size) {
        QEvent event = QEvent.event;
        List<BooleanExpression> conditions = new ArrayList<>();

        if (userIds != null && !userIds.isEmpty()) {
            conditions.add(event.user.id.in(userIds));
        }

        if (states != null && !states.isEmpty()) {
            List<EventState> eventStates;
            try {
                eventStates = states.stream()
                        .map(EventState::valueOf)
                        .toList();
            } catch (Exception e) {
                throw new ConflictException("Invalid state value");
            }

            conditions.add(event.state.in(eventStates));
        }

        if (categories != null && !categories.isEmpty()) {
            conditions.add(event.category.id.in(categories));
        }

        LocalDateTime startDate;
        if (rangeStart == null || rangeStart.isBlank()) {
            startDate = LocalDateTime.of(1970, 1, 1, 0, 0, 0);
        } else {
            startDate = Utils.decodeDateTime(rangeStart);
        }

        LocalDateTime endDate;
        if (rangeEnd == null || rangeEnd.isBlank()) {
            endDate = LocalDateTime.of(2100, 1, 1, 0, 0, 0);
        } else {
            endDate = Utils.decodeDateTime(rangeEnd);
        }

        conditions.add(event.eventDate.between(startDate, endDate));

        BooleanExpression finalCondition = conditions.stream()
                .reduce(BooleanExpression::and)
                .get();

        Sort sort = Sort.by("id").ascending();
        PageRequest pageRequest = PageRequest.of(from, size, sort);

        Page<Event> events = eventRepository.findAll(finalCondition, pageRequest);

        return events.stream()
                .map(eventMapper::eventToEventDTO)
                .toList();
    }

    public List<EventDTO> getPublicEvents(String text,
                                          List<Long> categories,
                                          Boolean paid,
                                          String rangeStart,
                                          String rangeEnd,
                                          Boolean onlyAvailable,
                                          int from,
                                          int size,
                                          String sortStr,
                                          HttpServletRequest request) {
        StatsCreateDTO statsCreateDTO = new StatsCreateDTO(
                APP,
                request.getRequestURI(),
                request.getRemoteAddr(),
                LocalDateTime.now()
        );
        sendStats(statsCreateDTO);

        QEvent event = QEvent.event;
        List<BooleanExpression> conditions = new ArrayList<>();

        conditions.add(event.state.eq(EventState.PUBLISHED));

        if (text != null) {
            text = "%" + text.toUpperCase() + "%";
            conditions.add(event.annotation.toUpperCase().like(text).or(event.description.toUpperCase().like(text)));
        }

        if (categories != null && !categories.isEmpty()) {
            conditions.add(event.category.id.in(categories));
        }

        if (paid != null) {
            conditions.add(event.paid.eq(paid));
        }

        LocalDateTime startDate;
        if (rangeStart == null || rangeStart.isBlank()) {
            startDate = LocalDateTime.now();
        } else {
            startDate = Utils.decodeDateTime(rangeStart);
        }

        LocalDateTime endDate;
        if (rangeEnd == null || rangeEnd.isBlank()) {
            endDate = LocalDateTime.of(2100, 1, 1, 0, 0, 0);
        } else {
            endDate = Utils.decodeDateTime(rangeEnd);
        }
        if (startDate.isAfter(endDate)) {
            throw new BadRequestException("Start date is after end date");
        }

        conditions.add(event.eventDate.between(startDate, endDate));

        if (onlyAvailable != null) {
            conditions.add(event.participantLimit.loe(1000));
        }

        BooleanExpression finalCondition = conditions.stream()
                .reduce(BooleanExpression::and)
                .orElse(Expressions.asBoolean(true).isTrue());

        if (sortStr.equalsIgnoreCase("EVENT_DATE")) {
            sortStr = "eventDate";
        } else {
            sortStr = sortStr.toLowerCase();
        }

        Sort sortBy = Sort.by(sortStr).ascending();
        PageRequest pageRequest = PageRequest.of(from, size, sortBy);

        Page<Event> events = eventRepository.findAll(finalCondition, pageRequest);

        return events.stream()
                .map(eventMapper::eventToEventDTO)
                .toList();
    }
}
