package ru.yandex.practicum.filmorate.interfaces.dal;

import java.util.HashSet;
import java.util.List;

public interface FriendshipStorage {
    boolean addAsFriend(int userId, int friendId);

    boolean removeFromFriends(int userId, int friendId);

    HashSet<Integer> getListOfFriends(int userId);

    List<Integer> getAListOfMutualFriends(int userId, int otherId);

}
