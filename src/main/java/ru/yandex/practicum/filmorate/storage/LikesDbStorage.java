package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.interfaces.LikeStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.services.GenreService;
import ru.yandex.practicum.filmorate.services.MpaRatingService;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class LikesDbStorage implements LikeStorage {
    private final JdbcTemplate jdbcTemplate;
    private final MpaRatingService mpaService;
    private final GenreService genreService;

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
    public List<Film> getTheBestFilms(int count) {
        String sqlQuery = "select f.*, " +
                "count(distinct l.USER_ID) as likes_count " +
                "from FILMS f " +
                "left outer join LIKES l ON f.FILM_ID = l.FILM_ID " +
                "group by f.FILM_ID " +
                "order by likes_count desc " +
                "limit ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count);
    }

    private Map<String, Object> toMap(Like likes) {
        Map<String, Object> values = new HashMap<>();
        values.put("user_Id", likes.getUserId());
        values.put("film_Id", likes.getFilmId());
        return values;
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return Film.builder()
                .id(resultSet.getInt("film_id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date")
                        .toLocalDate())
                .duration(resultSet.getInt("duration"))
                .rate(resultSet.getInt("rate"))
                .mpa(mpaService.getMpaById(resultSet.getInt("mpa_id")))
                .likes(getListOfLikes(resultSet.getInt("film_id")))
                .genres(genreService.getGenresByFilmId(resultSet.getInt("film_id")))
                .build();
    }
}