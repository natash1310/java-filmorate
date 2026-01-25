package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.interfaces.FriendshipStorage;
import ru.yandex.practicum.filmorate.interfaces.UserStorage;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Primary
@RequiredArgsConstructor
@Slf4j
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FriendshipStorage friendShipStorage;


    @Override
    public Collection<User> getAllUsers() {
        String sqlQuery = "select * from USERS";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    @Override
    public User addUser(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");
        int userId = simpleJdbcInsert.executeAndReturnKey(toMap(user))
                .intValue();
        user.setId(userId);
        user.setFriends(new HashSet<>());
        return user;
    }

    @Override
    public User updateUser(User user) {
        String sqlQuery = "update USERS set EMAIL = ?, LOGIN = ?, NAME = ?, BIRTHDAY = ? where USER_ID = ?";
        jdbcTemplate.update(sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());
        log.info("Обновлены данные пользователя с id:{} Подробнее: {}", user.getId(), user);
        return getUser(user.getId());
    }

    @Override
    public User getUser(int userId) {
        User user;
        String sqlQuery = "select * from USERS where USER_ID = ?";
        try {
            user = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, userId);
        } catch (DataAccessException e) {
            throw new NotFoundException(String.format("Пользователь с id %s не найден", userId));
        }
        return user;
    }

    private Map<String, Object> toMap(User user) {
        Map<String, Object> values = new HashMap<>();
        values.put("email", user.getEmail());
        values.put("name", user.getName());
        values.put("login", user.getLogin());
        values.put("birthday", user.getBirthday());
        return values;
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        int userId = resultSet.getInt("user_id");
        List<User> friendsList = friendShipStorage.getListOfFriends(userId);
        Set<Integer> friendsIds = friendsList.stream()
                .map(User::getId)
                .collect(Collectors.toSet());

        return User.builder()
                .id(userId)
                .email(resultSet.getString("email"))
                .name(resultSet.getString("name"))
                .login(resultSet.getString("login"))
                .birthday(resultSet.getDate("birthday")
                        .toLocalDate())
                .friends(friendsIds)
                .build();
    }
}