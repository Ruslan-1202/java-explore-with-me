package ru.practicum.main.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.db.RequestRepository;
import ru.practicum.main.db.entity.Event;
import ru.practicum.main.db.entity.Request;
import ru.practicum.main.db.entity.User;
import ru.practicum.main.dto.RequestDTO;
import ru.practicum.main.dto.RequestStatusUpdateDTO;
import ru.practicum.main.dto.RequestStatusUpdateResultDTO;
import ru.practicum.main.enumeration.EventState;
import ru.practicum.main.enumeration.RequestStatus;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.mapper.RequestMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestService {
    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;
    private final EventService eventService;
    private final UserService userService;

    private Event getEventUser(long userId, long eventId) {
        Event event = eventService.getEventById(eventId);
        if (event.getUser().getId() != userId) {
            throw new ConflictException(String.format("User is not owner of request with id=%s", eventId));
        }

        return event;
    }

    private void checkRequestForCreate(Event event, long userId) {
        if (!EventState.PUBLISHED.equals(event.getState())) {
            throw new ConflictException(String.format("Event %s is not published", event.getId()));
        }

        if (event.getParticipantLimit() != 0 && event.getParticipantLimit() <= requestRepository.countByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED)) {
            throw new ConflictException(String.format("Event %s has reached participant limit of %s", event.getId(), event.getParticipantLimit()));
        }

        User user = event.getUser();
        if (user.getId() == userId) {
            throw new ConflictException("You are inintiator of this event");
        }

        if (requestRepository.findByUserIdAndEventId(userId, event.getId()).isPresent()) {
            throw new ConflictException("Duplicated request");
        }
    }

    private Request getRequestById(long requestId) {
        return requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format("Request with id %s not found", requestId)));
    }

    @Transactional
    public RequestDTO createRequest(long userId, long eventId) {
        Event event = eventService.getEventById(eventId);

        checkRequestForCreate(event, userId);

        Request request = new Request();

        request.setEvent(event);
        request.setUser(userService.getUserById(userId));
        request.setCreated(LocalDateTime.now());
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
        } else {
            request.setStatus(RequestStatus.PENDING);
        }

        return requestMapper.toRequestDTO(requestRepository.save(request));
    }

    @Transactional
    public RequestDTO cancelRequest(long userId, long requestId) {
        Request request = getRequestById(requestId);

        if (request.getUser().getId() != userId) {
            throw new ConflictException(String.format("User is not owner of request with id=%s", requestId));
        }

        request.setStatus(RequestStatus.CANCELED);
        return requestMapper.toRequestDTO(requestRepository.save(request));
    }

    @Transactional(readOnly = true)
    public List<RequestDTO> getRequestsByUser(long userId) {
        return requestRepository.findByUserId(userId).stream()
                .map(requestMapper::toRequestDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RequestDTO> getRequestsByUserEvent(long userId, long eventId) {
        getEventUser(userId, eventId);

        return requestRepository.findByEventId(eventId).stream()
                .map(requestMapper::toRequestDTO)
                .toList();
    }

    @Transactional
    public RequestStatusUpdateResultDTO editRequestsStatus(RequestStatusUpdateDTO requestStatusUpdateDTO, long userId, long eventId) {
        Event event = getEventUser(userId, eventId);
        List<Request> requests = requestRepository.findAllById(requestStatusUpdateDTO.getRequestIds());

        Request badRequest = requests.stream()
                .filter(a -> !RequestStatus.PENDING.equals(a.getStatus()))
                .findFirst()
                .orElse(null);

        if (badRequest != null) {
            throw new ConflictException("Request id=" + badRequest.getId() + " has wrong status");
        }

        List<Request> idsToConfirm = new ArrayList<>();
        List<Request> idsToReject;

        if (RequestStatus.REJECTED.equals(requestStatusUpdateDTO.getStatus())) {
            idsToReject = requests;
        } else {
            long remainLimit;
            if (event.getParticipantLimit() != 0) {
                int sizeRequests = requestStatusUpdateDTO.getRequestIds().size();
                remainLimit = event.getParticipantLimit() - event.getConfirmedRequests() - sizeRequests + 1;
            } else {
                remainLimit = Long.MAX_VALUE;
            }

            idsToConfirm = requests.stream()
                    .limit(remainLimit)
                    .toList();

            idsToReject = requests.stream()
                    .skip(remainLimit)
                    .toList();

            if (!idsToReject.isEmpty()) {
                throw new ConflictException("Too many ids to confirm");
            }
        }

        requestRepository.setStatus(
                idsToConfirm.stream()
                        .map(Request::getId)
                        .toList(),
                RequestStatus.CONFIRMED
        );

        requestRepository.setStatus(
                idsToReject.stream()
                        .map(Request::getId)
                        .toList(),
                RequestStatus.REJECTED
        );

        event.setConfirmedRequests(event.getConfirmedRequests() + idsToConfirm.size());

        return new RequestStatusUpdateResultDTO(
                idsToConfirm.stream()
                        .peek(a -> a.setStatus(RequestStatus.CONFIRMED))
                        .map(requestMapper::toRequestDTO)
                        .toList(),
                idsToReject.stream()
                        .peek(a -> a.setStatus(RequestStatus.REJECTED))
                        .map(requestMapper::toRequestDTO)
                        .toList()
        );
    }
}
