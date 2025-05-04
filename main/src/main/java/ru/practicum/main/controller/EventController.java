package ru.practicum.main.controller;

import jakarta.servlet.http.HttpServletRequest;
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
    public EventDTO editEvent(@Valid @RequestBody EventPatchDTO eventPatchDTO,
                              @PathVariable Long id) {

        log.debug("editEvent eventPatchDTO=[{}], id={}", eventPatchDTO, id);
        return eventService.editEvent(eventPatchDTO, id);
    }

    @GetMapping("/events/{id}")
    public EventDTO getPublishedEventById(@PathVariable Long id,
                                          HttpServletRequest request) {
        log.debug("getEventById: id={}, ip={}, path={}", id, request.getRemoteAddr(), request.getRequestURI());
        return eventService.getPublishedEventById(id, request);
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

    @GetMapping("/admin/events")
    public List<EventDTO> getAdminEvents(@RequestParam(required = false) List<Long> userIds,
                                         @RequestParam(required = false) List<String> states,
                                         @RequestParam(required = false) List<Long> categories,
                                         @RequestParam(required = false) String rangeStart,
                                         @RequestParam(required = false) String rangeEnd,
                                         @RequestParam(required = false, defaultValue = "0") int from,
                                         @RequestParam(required = false, defaultValue = "10") int size) {
        log.debug("getAdminEvents");
        return eventService.getAdminEvents(userIds, states, categories, rangeStart, rangeEnd, from, size);
    }

    @GetMapping("/events")
    public List<EventDTO> getPublicEvents(@RequestParam(required = false) String text,
                                          @RequestParam(required = false) List<Long> categories,
                                          @RequestParam(required = false) Boolean paid,
                                          @RequestParam(required = false) String rangeStart,
                                          @RequestParam(required = false) String rangeEnd,
                                          @RequestParam(required = false, defaultValue = "false") Boolean onlyAvailable,
                                          @RequestParam(required = false, defaultValue = "0") int from,
                                          @RequestParam(required = false, defaultValue = "10") int size,
                                          @RequestParam(required = false, defaultValue = "EVENT_DATE") String sort) {
        log.debug("getPublicEvents");
        return eventService.getPublicEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, from, size, sort);
    }
}
