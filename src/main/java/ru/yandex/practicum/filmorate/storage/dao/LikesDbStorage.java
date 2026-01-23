package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.interfaces.dal.LikeStorage;
import ru.yandex.practicum.filmorate.model.Like;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class LikesDbStorage implements LikeStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addLike(int filmId, int userId) {
        Like like = Like.builder()
                .filmId(filmId)
                .userId(userId)
                .build();
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("likes");
        simpleJdbcInsert.execute(toMap(like));
    }

    @Override
    public void removeLike(int filmId, int userId) {
        String sqlQuery = "delete from LIKES where FILM_ID = ? and USER_ID = ?";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    @Override
    public HashSet<Integer> getListOfLikes(int filmId) {
        String sqlQuery = "select USER_ID from LIKES where FILM_ID = ?";
        return new HashSet<>(jdbcTemplate.queryForList(sqlQuery, Integer.class, filmId));
    }

    @Override
    public HashSet<Integer> getTheBestFilms(int count) {
        String sqlQuery = "select FILMS.FILM_ID " +
                "from FILMS " +
                "left outer join LIKES ON FILMS.FILM_ID = LIKES.FILM_ID " +
                "group by FILMS.FILM_id " +
                "order by count(distinct LIKES.USER_ID) desc " +
                "limit + ?";
        return new HashSet<>(jdbcTemplate.queryForList(sqlQuery, Integer.class, count));
    }

    private Map<String, Object> toMap(Like likes) {
        Map<String, Object> values = new HashMap<>();
        values.put("user_Id", likes.getUserId());
        values.put("film_Id", likes.getFilmId());
        return values;
    }
}