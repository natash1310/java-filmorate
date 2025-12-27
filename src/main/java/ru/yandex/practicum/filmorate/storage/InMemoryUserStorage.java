package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.interfaces.UserStorage;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
public class InMemoryUserStorage implements UserStorage {
    private static final Logger log = LoggerFactory.getLogger(InMemoryUserStorage.class);
    private final Map<Integer, User> users = new HashMap<>();
    private int idGen = 1;


    @Autowired
    public InMemoryUserStorage() {
    }

    @Override
    public User getUser(Integer id) {
        if (!users.containsKey(id)) {
            log.error("Такого пользователя не существует!, {}", id);
            throw new NotFoundException("Такого пользователя не существует!");
        }
        return users.get(id);
    }

    @Override
    public List<User> getAllUsers() {
        log.debug("Количество пользователей всего: {}", users.size());
        return new ArrayList<>(users.values());
    }

    @Override
    public User addNewUser(User user) {
        user.setId(idGen);
        users.put(idGen, user);
        idGen++;
        log.info("Добавлен новый пользователь, {}", user);
        return user;
    }

    @Override
    public User updateNewUser(User user) {
        getUser(user.getId());
        users.put(user.getId(), user);
        log.info("Пользователь обновлен - , {}", user);
        return user;
    }
}
