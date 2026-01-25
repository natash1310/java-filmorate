package ru.yandex.practicum.filmorate.interfaces;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendshipStorage {
    boolean addAsFriend(int userId, int friendId);

    boolean removeFromFriends(int userId, int friendId);

    List<User> getListOfFriends(int userId);

    List<User> getAListOfMutualFriends(int userId, int otherId);

}
