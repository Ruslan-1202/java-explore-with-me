package ru.practicum.main.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.main.db.UserRepository;
import ru.practicum.main.db.entity.User;
import ru.practicum.main.dto.UserCreateDTO;
import ru.practicum.main.dto.UserDTO;
import ru.practicum.main.mapper.UserMapper;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    public UserDTO create(UserCreateDTO userCreateDTO) {
        User user = userMapper.userCreateDTOToUser(userCreateDTO, null);
        return userMapper.userToUserDTO(userRepository.save(user));
    }

    public List<UserDTO> getUsers(Long[] ids, long from, Long size) {

        List<Long> idsList = Arrays.stream(ids).toList();

        return userRepository.getUsers(idsList, from, size).stream()
                .map(userMapper::userToUserDTO)
                .toList();
    }

    public List<UserDTO> getUsersAll() {
        return userRepository.findAll().stream()
                .map(userMapper::userToUserDTO)
                .toList();
    }
}
