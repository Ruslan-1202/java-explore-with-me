package ru.practicum.main.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.db.entity.User;
import ru.practicum.main.dto.UserCreateDTO;
import ru.practicum.main.dto.UserDTO;

import java.util.List;

@Transactional(readOnly = true)
public interface UserService {
    @Transactional
    UserDTO create(UserCreateDTO userCreateDTO);

    List<UserDTO> getUsers(List<Long> ids, Long from, Long size);

    List<UserDTO> getUsersAll();

    @Transactional
    void deleteUser(Long userId);

    User getUserById(Long userId);
}
