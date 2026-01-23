package ru.yandex.practicum.filmorate.services;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.interfaces.UserStorageIm;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserStorageIm userStorageIm;

    public List<User> getAllFriends(Integer id) {
        User currentUser = userStorageIm.getUser(id);
        log.debug("Количество друзей пользователя c id:{} = {}", id, (int) currentUser.getFriends().stream()
                .map(userStorageIm::getUser).count());
        return currentUser.getFriends().stream()
                .map(userStorageIm::getUser)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Integer id, Integer otherId) {
        User currentUser = userStorageIm.getUser(id);
        User otherUser = userStorageIm.getUser(otherId);
        List<User> commonFriends = new ArrayList<>();
        List<Integer> friendsByUser = new ArrayList<>(currentUser.getFriends());
        for (int i = 0; i < currentUser.getFriends().size(); i++) {
            if (otherUser.getFriends().contains(friendsByUser.get(i))) {
                commonFriends.add(userStorageIm.getUser(friendsByUser.get(i)));
            }
        }
        log.debug("Число общих друзей у пользователей с id:{} и id:{} = {}", id, otherId, commonFriends.size());
        return commonFriends;
    }

    public User addFriend(Integer id, Integer friendId) {
        User currentUser = userStorageIm.getUser(id);
        User friendUser = userStorageIm.getUser(friendId);
        currentUser.getFriends().add(friendId);
        friendUser.getFriends().add(id);
        log.info("Пользователи с id:{} и id:{} - подружились!", id, friendId);
        return currentUser;
    }

    public User removeFriend(Integer id, Integer friendId) {
        User currentUser = userStorageIm.getUser(id);
        User friendUser = userStorageIm.getUser(friendId);
        checkRemoveFriendValidate(id, friendId);
        currentUser.getFriends().remove(friendId);
        friendUser.getFriends().remove(id);
        log.info("Пользователи с id:{} и id:{} - прекратили дружбу!", id, friendId);
        return currentUser;
    }

    public User addUser(User user) {
        checkUserNameForBlankOrNull(user);
        checkPostUserValidate(user);
        userStorageIm.addNewUser(user);
        log.info("Пользователь успешно создан с id = {}", user.getId());
        return user;
    }

    public User updateUser(User user) {
        userStorageIm.getUser(user.getId());
        checkUserNameForBlankOrNull(user);
        userStorageIm.updateNewUser(user);
        log.info("Пользователь с id = {} успешно обновлён", user.getId());
        return user;
    }

    private void checkPostUserValidate(User user) {
        if (userStorageIm.getAllUsers().contains(user)) {
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

    private void checkRemoveFriendValidate(Integer id, Integer friendId) {
        if (!userStorageIm.getUser(id).getFriends().contains(friendId)) {
            log.error("Пользователи с id:{} и id:{} - не друзья", id, friendId);
        }
    }


}
