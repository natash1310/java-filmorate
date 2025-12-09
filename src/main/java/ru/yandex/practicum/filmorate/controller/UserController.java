package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
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
    public Collection<User> getUsers() {
        log.info("Получен запрос на получение всех пользователей. Количество пользователей: {}", users.size());
        return users.values();
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@Valid @RequestBody User user) {
        log.info("Получен запрос на создание пользователя с email: {}", user.getEmail());
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            log.error("Ошибка валидации: email не указан");
            throw new ConditionsNotMetException("Имейл должен быть указан");
        }

        for (User existingUser : users.values()) {
            if (existingUser.getEmail().equals(user.getEmail())) {
                log.error("Ошибка создания пользователя: email {} уже используется", user.getEmail());
                throw new DuplicatedDataException("Этот имейл уже используется");
            }
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
        return user;
    }


    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Получен запрос на обновление пользователя с id = {}", user.getId());
        User existingUser = users.get(user.getId());
        if (existingUser == null) {
            log.error("Ошибка обновления: пользователь с id = {} не найден", user.getId());
            throw new ConditionsNotMetException("Пользователь с id = " + user.getId() + " не найден");
        }

        if (user.getEmail() != null) {
            for (User u : users.values()) {
                if (u.getEmail().equals(user.getEmail()) && !(u.getId() == (user.getId()))) {
                    log.error("Ошибка обновления: email {} уже используется другим пользователем", user.getEmail());
                    throw new DuplicatedDataException("Этот имейл уже используется");
                }
            }
            log.debug("Обновление email с '{}' на '{}'", existingUser.getEmail(), user.getEmail());
            existingUser.setEmail(user.getEmail());
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

        users.put(existingUser.getId(), existingUser);
        log.info("Пользователь с id = {} успешно обновлён", user.getId());
        return existingUser;
    }


    private int getNextId() {
        return ++globalId;
    }
}
