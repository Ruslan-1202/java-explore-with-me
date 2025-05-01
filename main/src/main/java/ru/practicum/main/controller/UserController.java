package ru.practicum.main.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.dto.UserCreateDTO;
import ru.practicum.main.dto.UserDTO;
import ru.practicum.main.service.UserService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/admin/users")
@Validated
public class UserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO createUser(@Valid @RequestBody UserCreateDTO userCreateDTO) {
        log.debug("createUser: {}", userCreateDTO);
        return userService.create(userCreateDTO);
    }

    @GetMapping
    public List<UserDTO> getUsers(@RequestParam(required = false) List<Long> ids,
                                  @RequestParam(required = false, defaultValue = "0") Long from,
                                  @RequestParam(required = false, defaultValue = "10") Long size) {
        log.debug("getUser: ids={}, from={}, size={}", ids, from, size);
        return userService.getUsers(ids, from, size);
    }

    @DeleteMapping("{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        log.debug("deleteUser: {}", userId);
        userService.deleteUser(userId);
    }

    @GetMapping(value = "/all")
    public List<UserDTO> getUsersAll() {
        log.debug("getUserAll");
        return userService.getUsersAll();
    }
}
