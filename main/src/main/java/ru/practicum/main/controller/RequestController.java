package ru.practicum.main.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.dto.RequestDTO;
import ru.practicum.main.dto.RequestStatusUpdateDTO;
import ru.practicum.main.dto.RequestStatusUpdateResultDTO;
import ru.practicum.main.service.RequestService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class RequestController {
    private final RequestService requestService;

    @PostMapping("/users/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDTO createRequest(@PathVariable(name = "userId") long userId,
                                    @RequestParam long eventId) {
        log.debug("createRequest: userId={}, eventId={}", userId, eventId);
        return requestService.createRequest(userId, eventId);
    }

    @PatchMapping("/users/{userId}/requests/{requestId}/cancel")
    public RequestDTO cancelRequest(@PathVariable long userId,
                                    @PathVariable long requestId) {
        log.debug("cancelRequest: userId={}, requestId={}", userId, requestId);
        return requestService.cancelRequest(userId, requestId);
    }

    @GetMapping("/users/{userId}/requests")
    public List<RequestDTO> getRequestsByUser(@PathVariable long userId) {
        log.debug("getRequestsByUser: userId={}", userId);
        return requestService.getRequestsByUser(userId);
    }

    @GetMapping("/users/{userId}/events/{eventId}/requests")
    public List<RequestDTO> getRequestsByUserEvent(@PathVariable long userId,
                                                   @PathVariable long eventId) {
        log.debug("getRequestsByUserEvent: userId={}, eventId={}", userId, eventId);
        return requestService.getRequestsByUserEvent(userId, eventId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}/requests")
    public RequestStatusUpdateResultDTO editRequestsStatus(@RequestBody RequestStatusUpdateDTO requestStatusUpdateDTO,
                                                           @PathVariable long userId,
                                                           @PathVariable long eventId) {
        log.debug("editRequestsStatus: requestStatusUpdateDTO=[{}], userId={}, eventId={}", requestStatusUpdateDTO, userId, eventId);
        return requestService.editRequestsStatus(requestStatusUpdateDTO, userId, eventId);
    }
}
