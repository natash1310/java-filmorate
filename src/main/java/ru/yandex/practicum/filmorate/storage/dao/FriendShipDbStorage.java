package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.interfaces.dal.FriendshipStorage;
import ru.yandex.practicum.filmorate.model.Friendship;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class FriendShipDbStorage implements FriendshipStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public boolean addAsFriend(int userId, int friendId) {
        Friendship friends = Friendship.builder()
                .userId(userId)
                .friendId(friendId)
                .build();
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("friendship");
        return simpleJdbcInsert.execute(toMap(friends)) > 0;
    }

    @Override
    public boolean removeFromFriends(int userId, int friendId) {
        String sqlQuery = "delete from FRIENDSHIP where USER_ID = ? and FRIEND_ID = ?";
        return jdbcTemplate.update(sqlQuery, userId, friendId) > 0;
    }

    @Override
    public HashSet<Integer> getListOfFriends(int userId) {
        String sqlQuery = "select FRIEND_ID from FRIENDSHIP where USER_ID = ?";
        return new HashSet<>(jdbcTemplate.queryForList(sqlQuery, Integer.class, userId));
    }

    @Override
    public List<Integer> getAListOfMutualFriends(int userId, int otherId) {
        String sqlQuery = "select FRIEND_ID " +
                "from (select *  from FRIENDSHIP where USER_ID = ? or USER_ID = ?) " +
                "group by FRIEND_ID HAVING (COUNT(*) > 1)";
        return jdbcTemplate.queryForList(sqlQuery, Integer.class, userId, otherId);
    }


    private Map<String, Object> toMap(Friendship friends) {
        Map<String, Object> values = new HashMap<>();
        values.put("user_Id", friends.getUserId());
        values.put("friend_Id", friends.getFriendId());
        return values;
    }
}
