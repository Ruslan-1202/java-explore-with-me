package ru.practicum.main.mapper;

import org.springframework.stereotype.Service;
import ru.practicum.main.db.entity.Request;
import ru.practicum.main.dto.RequestDTO;
import ru.practicum.main.util.Utils;

@Service
public class RequestMapper {
    public RequestDTO toRequestDTO(Request request) {
        return new RequestDTO(
                request.getId(),
                request.getEvent().getId(),
                request.getUser().getId(),
                Utils.encodeDateTime(request.getCreated()),
                request.getStatus()
        );
    }
}
