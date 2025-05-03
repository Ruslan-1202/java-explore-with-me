package ru.practicum.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestStatusUpdateResultDTO {
    private List<RequestDTO> confirmedRequests;
    private List<RequestDTO> rejectedRequests;
}
