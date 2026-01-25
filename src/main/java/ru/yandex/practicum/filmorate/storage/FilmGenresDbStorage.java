package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.interfaces.FilmGenresStorage;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class FilmGenresDbStorage implements FilmGenresStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addGenres(Set<Genre> genres, int filmId) {
        String sqlQuery = "INSERT INTO FILM_GENRE_LINE (FILM_ID, GENRE_ID) VALUES (?, ?)";
        Set<Genre> uniqueGenres = new HashSet<>(genres);
        getJdbcTemplate().batchUpdate(sqlQuery, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i)
                    throws SQLException {
                Genre genre = uniqueGenres.stream()
                        .toList()
                        .get(i);
                ps.setInt(1, filmId);
                ps.setInt(2, genre.getId());
            }

            @Override
            public int getBatchSize() {
                return uniqueGenres.size();
            }
        });
    }

    @Override
    public void deleteGenres(int filmId) {
        String sqlQuery = "delete from FILM_GENRE_LINE where FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, filmId);
    }

    @Override
    public Set<Genre> getGenresByFilmId(int filmId) {
        String sqlQuery = "SELECT g.GENRE_ID, g.NAME " +
                "FROM FILM_GENRE_LINE fgl " +
                "JOIN GENRE g ON fgl.GENRE_ID = g.GENRE_ID " +
                "WHERE fgl.FILM_ID = ?";

        List<Genre> genres = jdbcTemplate.query(sqlQuery, this::mapRowToGenre, filmId);
        return new HashSet<>(genres);
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("genre_id"))
                .name(resultSet.getString("name"))
                .build();
    }

    private JdbcOperations getJdbcTemplate() {
        return jdbcTemplate;
    }
}
