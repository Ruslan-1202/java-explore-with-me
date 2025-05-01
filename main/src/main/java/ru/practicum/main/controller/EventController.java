package ru.practicum.main.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.dto.EventCreateDTO;
import ru.practicum.main.dto.EventDTO;
import ru.practicum.main.dto.EventPatchDTO;
import ru.practicum.main.service.EventService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@Validated
public class EventController {
    private final EventService eventService;

    @PostMapping("/users/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventDTO createEvent(@Valid @RequestBody EventCreateDTO eventCreateDTO,
                                @PathVariable Long userId) {
        log.debug("createEvent: {}, userId={}", eventCreateDTO, userId);
        return eventService.create(eventCreateDTO, userId);
    }

    @PatchMapping("/admin/events/{id}")
    public EventDTO editEvent(@RequestBody EventPatchDTO eventPatchDTO,
                              @PathVariable Long id) {

        log.debug("editEvent eventPatchDTO=[{}], id={}", eventPatchDTO, id);
        return eventService.editEvent(eventPatchDTO, id);
    }

    @GetMapping("/events/{id}")
    public EventDTO getPublishedEventById(@PathVariable Long id) {
        log.debug("getEventById: id={}", id);
        return eventService.getPublishedEventById(id);
    }

    @GetMapping("/users/{userId}/events")
    public List<EventDTO> getEventsByUserId(@PathVariable Long userId,
                                            @RequestParam(required = false, defaultValue = "0") Long from,
                                            @RequestParam(required = false, defaultValue = "10") Long size) {
        log.debug("getEventsByUserId: userId={}, from={}, size={}", userId, from, size);
        return eventService.getEventsByUserId(userId, from, size);
    }

    @GetMapping("/users/{userId}/events/{eventId}")
    public EventDTO getEventByUserIdAndEventId(@PathVariable Long userId,
                                               @PathVariable Long eventId) {
        log.debug("getEventByUserIdAndEventId: userId={}, eventId={}", userId, eventId);
        return eventService.getEventByUserIdAndEventId(userId, eventId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}")
    public EventDTO editEventByUserIdAndEventId(@Valid @RequestBody EventPatchDTO eventPatchDTO,
                                                @PathVariable Long userId,
                                                @PathVariable Long eventId) {
        log.debug("editEventByUserIdAndEventId: eventPatchDTO=[{}] userId={}, eventId={}", eventPatchDTO, userId, eventId);
        return eventService.editEventByUserIdAndEventId(eventPatchDTO, userId, eventId);
    }

    @GetMapping("/events-all")
    public List<EventDTO> getAllEvents() {
        log.debug("getAllEvents");
        return eventService.getAllEvents();
    }
}
