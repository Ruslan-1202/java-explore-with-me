package ru.practicum.main.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.db.UserRepository;
import ru.practicum.main.db.entity.User;
import ru.practicum.main.dto.UserCreateDTO;
import ru.practicum.main.dto.UserDTO;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.mapper.UserMapper;
import ru.practicum.main.service.UserService;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDTO create(UserCreateDTO userCreateDTO) {
        User user = userMapper.userCreateDTOToUser(userCreateDTO, null);
        return userMapper.userToUserDTO(userRepository.save(user));
    }

    @Override
    public List<UserDTO> getUsers(List<Long> ids, Long from, Long size) {
        if (ids == null || ids.isEmpty()) {
            ids = new ArrayList<>();
        }
        return userRepository.getUsers(ids, from, size).stream()
                .map(userMapper::userToUserDTO)
                .toList();
    }

    @Override
    public List<UserDTO> getUsersAll() {
        return userRepository.findAll().stream()
                .map(userMapper::userToUserDTO)
                .toList();
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
    }
}
