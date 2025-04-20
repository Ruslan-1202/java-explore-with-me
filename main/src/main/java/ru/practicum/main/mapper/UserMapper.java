package ru.practicum.main.mapper;

import org.springframework.stereotype.Service;
import ru.practicum.main.db.entity.User;
import ru.practicum.main.dto.UserCreateDTO;
import ru.practicum.main.dto.UserDTO;

@Service
public class UserMapper {
    public User userCreateDTOToUser(UserCreateDTO userCreateDTO, Long id) {
        return new User(id, userCreateDTO.getName(), userCreateDTO.getEmail());
    }
    public UserDTO userToUserDTO(User user) {
        return new UserDTO(user.getId(), user.getName(), user.getEmail());
    }
}
