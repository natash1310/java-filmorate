package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.interfaces.UserStorage;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.services.UserService;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserStorage userStorage;
    private final UserService userService;

    public UserController(UserStorage userStorage, UserService userService) {
        this.userStorage = userStorage;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<Collection<User>> getUsers() {
        log.info("Получен запрос на получение всех пользователей. Количество пользователей: {}",
                userStorage.getAllUsers().size());
        return ResponseEntity.ok(userStorage.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable("id") @Positive Integer id) {
        return ResponseEntity.ok(userStorage.getUser(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        log.info("Получен запрос на создание пользователя с email: {}", user.getEmail());
        return ResponseEntity.ok(userService.addUser(user));
    }

    @PutMapping
    public ResponseEntity<User> updateUser(@Valid @RequestBody User user) {
        log.info("Получен запрос на обновление пользователя с id = {}", user.getId());
        return ResponseEntity.ok(userService.updateUser(user));
    }

    @PutMapping("/{id}/friends/{friendId}")
    public ResponseEntity<User> addFriend(@PathVariable("id") @Positive Integer id,
                                          @PathVariable("friendId") @Positive Integer friendId) {
        return ResponseEntity.ok(userService.addFriend(id, friendId));
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public ResponseEntity<User> removeFriend(@PathVariable("id") @Positive Integer id,
                                             @PathVariable("friendId") @Positive Integer friendId) {
        return ResponseEntity.ok(userService.removeFriend(id, friendId));
    }

    @GetMapping("/{id}/friends")
    public ResponseEntity<List<User>> getAllFriends(@PathVariable("id") @Positive Integer id) {
        return ResponseEntity.ok(userService.getAllFriends(id));
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public ResponseEntity<List<User>> getCommonFriends(@PathVariable("id") @Positive Integer id,
                                                       @PathVariable("otherId") @Positive Integer otherId) {
        return ResponseEntity.ok(userService.getCommonFriends(id, otherId));
    }

}
