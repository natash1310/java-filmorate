package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
@Validated
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int globalId = 0;

    @GetMapping
    public ResponseEntity<Collection<User>> getUsers() {
        log.info("Получен запрос на получение всех пользователей. Количество пользователей: {}", users.size());
        return ResponseEntity.ok(users.values());
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        log.info("Получен запрос на создание пользователя с email: {}", user.getEmail());
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            log.error("Ошибка валидации: email не указан");
            throw new ConditionsNotMetException("Email должен быть указан");
        }

        if (user.getLogin().contains(" ")) {
            log.error("Ошибка валидации: логин '{}' содержит пробелы", user.getLogin());
            throw new ValidationException("Логин не может содержать пробелы");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            log.debug("Имя пользователя не указано, используется логин: {}", user.getLogin());
            user.setName(user.getLogin());
        }

        user.setId(getNextId());

        users.put(user.getId(), user);
        log.info("Пользователь успешно создан с id = {}", user.getId());
        return ResponseEntity.ok(user);
    }


    @PutMapping
    public ResponseEntity<User> updateUser(@Valid @RequestBody User user) {
        log.info("Получен запрос на обновление пользователя с id = {}", user.getId());
        User existingUser = users.get(user.getId());
        if (existingUser == null) {
            log.error("Ошибка обновления: пользователь с id = {} не найден", user.getId());
            throw new ConditionsNotMetException("Пользователь с id = " + user.getId() + " не найден");
        }

        if (user.getLogin() != null) {
            existingUser.setLogin(user.getLogin());
        }

        if (user.getName() != null) {
            existingUser.setName(user.getName());
        }

        if (user.getBirthday() != null) {
            existingUser.setBirthday(user.getBirthday());
        }

        if (user.getEmail() != null) {
            existingUser.setEmail(user.getEmail());
        }

        users.put(existingUser.getId(), existingUser);
        log.info("Пользователь с id = {} успешно обновлён", user.getId());
        return ResponseEntity.ok(existingUser);
    }


    private int getNextId() {
        return ++globalId;
    }
}
