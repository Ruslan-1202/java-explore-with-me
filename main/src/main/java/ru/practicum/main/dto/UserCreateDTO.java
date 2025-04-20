package ru.practicum.main.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCreateDTO {
    @NotBlank
    private String name; // — имя или логин пользователя;
    @NotBlank
    @Email
    private String email; // — адрес электронной почты
}
