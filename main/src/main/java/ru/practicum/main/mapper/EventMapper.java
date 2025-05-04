package ru.practicum.main.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.main.db.entity.Category;
import ru.practicum.main.db.entity.Event;
import ru.practicum.main.db.entity.User;
import ru.practicum.main.dto.*;
import ru.practicum.main.enumeration.EventState;
import ru.practicum.main.util.Utils;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EventMapper {
    private final UserMapper userMapper;
    private final CategoryMapper categoryMapper;

    public Event toEventFromEventCreateDTO(EventCreateDTO eventCreateDTO, User user, Category category) {
        Event event = new Event();

        event.setAnnotation(eventCreateDTO.getAnnotation());
        event.setCategory(category);
        event.setDescription(eventCreateDTO.getDescription());
        event.setEventDate(Utils.parseDateTime(eventCreateDTO.getEventDate()));
        event.setLat(eventCreateDTO.getLocation().getLat());

        event.setLon(eventCreateDTO.getLocation().getLon());
        event.setPaid(eventCreateDTO.getPaid() != null && eventCreateDTO.getPaid());
        event.setParticipantLimit(eventCreateDTO.getParticipantLimit() == null ? 0 : eventCreateDTO.getParticipantLimit());
        event.setRequestModeration(eventCreateDTO.getRequestModeration() == null || eventCreateDTO.getRequestModeration());
        event.setTitle(eventCreateDTO.getTitle());

        event.setUser(user);
        event.setCreated(LocalDateTime.now());

        EventState eventState = EventState.PENDING;

        event.setState(eventState);
        event.setViews(0);
        event.setConfirmedRequests(0);
        return event;
    }

    public EventDTO eventToEventDTO(Event event) {
        EventDTO eventDTO = new EventDTO();

        eventDTO.setId(event.getId());
        eventDTO.setAnnotation(event.getAnnotation());
        eventDTO.setTitle(event.getTitle());
        eventDTO.setDescription(event.getDescription());
        eventDTO.setInitiator(userMapper.userToUserShortDTO(event.getUser()));
        eventDTO.setCategory(categoryMapper.toCategoryDTO(event.getCategory()));

        eventDTO.setConfirmedRequests(event.getConfirmedRequests());
        eventDTO.setPaid(event.getPaid());
        eventDTO.setParticipantLimit(event.getParticipantLimit());
        eventDTO.setRequestModeration(event.getRequestModeration());
        eventDTO.setLocation(new LocationDTO(event.getLat(), event.getLon()));
        eventDTO.setViews(event.getViews());
        eventDTO.setState(event.getState().toString());

        eventDTO.setEventDate(Utils.encodeDateTime(event.getEventDate()));
        eventDTO.setPublishedOn(Utils.encodeDateTime(event.getPublished()));
        eventDTO.setCreatedOn(Utils.encodeDateTime(event.getCreated()));
        return eventDTO;
    }
}
