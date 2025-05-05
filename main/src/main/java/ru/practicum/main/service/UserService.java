package ru.practicum.main.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.db.UserRepository;
import ru.practicum.main.db.entity.User;
import ru.practicum.main.dto.UserCreateDTO;
import ru.practicum.main.dto.UserDTO;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.mapper.UserMapper;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    @Transactional
    public UserDTO create(UserCreateDTO userCreateDTO) {
        User user = userMapper.userCreateDTOToUser(userCreateDTO, null);
        return userMapper.userToUserDTO(userRepository.save(user));
    }

    public List<UserDTO> getUsers(List<Long> ids, Long from, Long size) {
        if (ids == null || ids.isEmpty()) {
            ids = new ArrayList<>();
        }
        return userRepository.getUsers(ids, from, size).stream()
                .map(userMapper::userToUserDTO)
                .toList();
    }

    public List<UserDTO> getUsersAll() {
        return userRepository.findAll().stream()
                .map(userMapper::userToUserDTO)
                .toList();
    }

    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
    }
}
