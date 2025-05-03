package ru.practicum.main.service;

import lombok.RequiredArgsConstructor;
import ru.practicum.main.exception.ConflictException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.db.EventRepository;
import ru.practicum.main.db.entity.Category;
import ru.practicum.main.db.entity.Event;
import ru.practicum.main.db.entity.User;
import ru.practicum.main.dto.EventCreateDTO;
import ru.practicum.main.dto.EventDTO;
import ru.practicum.main.dto.EventPatchDTO;
import ru.practicum.main.enumeration.EventState;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.mapper.EventMapper;
import ru.practicum.stats.server.util.Utils;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class EventService {
    private final EventMapper eventMapper;
    private final EventRepository eventRepository;
    private final UserService userService;
    private final CategoryService categoryService;

    public Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event id=" + id + " not found"));
    }

    private Event getUserEvent(Long userId, Long eventId) {
        return eventRepository.getEventByIdAndUserId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event id=" + eventId + " by userId=" + userId +" not found"));
    }

    @Transactional
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
                throw new ConflictException("Wrong event date");
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
                    if (!EventState.PENDING.equals(event.getState())) {
                        throw new ConflictException("Not pending event can't be published");
                    }
                    state = EventState.PUBLISHED;
                    event.setPublished(LocalDateTime.now());
                } else if (stateAction.equals("CANCEL_REVIEW")) {
                    state = EventState.CANCELED;
                } else if (stateAction.equals("SEND_TO_REVIEW")) {
                    state = EventState.REVIEW;
                } else if (stateAction.equals("REJECT_EVENT")) {
                    if (EventState.PUBLISHED.equals(event.getState())) {
                        throw new ConflictException("Published event can't be rejected");
                    }
                    state = EventState.REJECTED;
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
        return eventMapper.eventToEventDTO(eventRepository.save(event));
    }

    public EventDTO getPublishedEventById(Long id) {
        Event event = getEventById(id);
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new NotFoundException("Published event id=" + id + " not found");
        }

        return eventMapper.eventToEventDTO(event);
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
        return eventRepository.findAllById(eventIds).stream()
                .map(eventMapper::eventToEventDTO)
                .toList();
    }

    @Transactional
    public EventDTO editEventByUserIdAndEventId(EventPatchDTO eventPatchDTO, Long userId, Long eventId) {
        var event = getUserEvent(userId, eventId);
        if (!EventState.PENDING.equals(event.getState()) && EventState.REJECTED.equals(event.getState())) {
            throw new ConflictException("Event id=" + eventId + " by userId=" + userId +" has wrong status " + event.getState());
        }
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ConflictException("Event id=" + eventId + " has wrong date");
        }

        return eventMapper.eventToEventDTO(patchEvent(event, eventPatchDTO));
    }
}
