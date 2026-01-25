package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.interfaces.FriendshipStorage;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class FriendshipDbStorage implements FriendshipStorage {
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
    public List<User> getListOfFriends(int userId) {
        String sqlQuery = "select u.* from USERS u " +
                "join FRIENDSHIP f on u.USER_ID = f.FRIEND_ID " +
                "where f.USER_ID = ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, userId);
    }

    @Override
    public List<User> getAListOfMutualFriends(int userId, int otherId) {
        String sqlQuery = "select u.* from USERS u " +
                "where u.USER_ID in (" +
                "    select FRIEND_ID " +
                "    from (select * from FRIENDSHIP where USER_ID = ? or USER_ID = ?) " +
                "    group by FRIEND_ID HAVING (COUNT(*) > 1)" +
                ")";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, userId, otherId);
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getInt("user_id"))
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .name(resultSet.getString("name"))
                .birthday(resultSet.getDate("birthday")
                        .toLocalDate())
                .build();
    }

    private Map<String, Object> toMap(Friendship friends) {
        Map<String, Object> values = new HashMap<>();
        values.put("user_Id", friends.getUserId());
        values.put("friend_Id", friends.getFriendId());
        return values;
    }
}
