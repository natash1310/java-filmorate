package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.interfaces.FriendshipStorage;
import ru.yandex.practicum.filmorate.interfaces.UserStorage;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserStorage userStorage;
    private final FriendshipStorage friendShipStorage;

    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User getUser(int userId) {
        return userStorage.getUser(userId);
    }

    public List<User> getAllFriends(int userId) {
        userStorage.getUser(userId);
        List<User> friendsList = friendShipStorage.getListOfFriends(userId);
        log.debug("Количество друзей пользователя c id:{} = {}", userId, (long) friendShipStorage
                .getListOfFriends(userId)
                .size());
        return friendsList;
    }

    public List<User> getMutualFriends(int id, int otherId) {
        userStorage.getUser(id);
        userStorage.getUser(otherId);
        List<User> mutualFriends = friendShipStorage.getAListOfMutualFriends(id, otherId);
        log.debug("Число общих друзей у пользователей с id:{} и id:{} = {}", id, otherId, mutualFriends.size());
        return mutualFriends;
    }

    public void addFriend(int userId, int friendId) {
        userStorage.getUser(userId);
        userStorage.getUser(friendId);
        friendShipStorage.addAsFriend(userId, friendId);
        log.info("Пользователи с id:{} и id:{} - подружились!", userId, friendId);
    }

    public void removeFriend(int userId, int friendId) {
        userStorage.getUser(userId);
        userStorage.getUser(friendId);
        checkRemoveFriendValidate(userId, friendId);
        friendShipStorage.removeFromFriends(userId, friendId);
        log.info("Пользователи с id:{} и id:{} - прекратили дружбу!", userId, friendId);

    }

    public User addUser(User user) {
        checkUserNameForBlankOrNull(user);
        checkPostUserValidate(user);
        User newUser = userStorage.addUser(user);
        log.info("Добавлен пользователь: {}", user);
        return newUser;
    }

    public User updateUser(User user) {
        User beforeUser = userStorage.getUser(user.getId());
        checkUserNameForBlankOrNull(user);
        User afterUser = userStorage.updateUser(user);
        log.info("Данные пользователя: {} Обновлены на: {}", beforeUser, afterUser);
        return afterUser;
    }

    private void checkPostUserValidate(User user) {
        if (userStorage.getAllUsers().contains(user)) {
            log.error("Такой пользователь уже существует!, {}", user);
            throw new ValidationException("Такой пользователь уже существует!");
        } else if (user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
            log.error("Логин не может быть пустым или содержать пробелы!, {}", user);
            throw new ValidationException("Логин не может быть пустым или содержать пробелы!");
        }
    }

    private void checkUserNameForBlankOrNull(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Имя пользователя изменено на его логин, т.к. оно было пустым.");
        }
    }

    private void checkRemoveFriendValidate(int userId, int friendId) {
        if (!userStorage.getUser(userId).getFriends().contains(friendId)) {
            log.error("Пользователи с id:{} и id:{} - не друзья", userId, friendId);
        }
    }
}