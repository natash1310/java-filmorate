package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.interfaces.dal.FilmGenresStorage;
import ru.yandex.practicum.filmorate.interfaces.dal.FilmStorage;
import ru.yandex.practicum.filmorate.interfaces.dal.LikeStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.services.GenreService;
import ru.yandex.practicum.filmorate.services.MpaRatingService;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Primary
@RequiredArgsConstructor
@Slf4j
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final LikeStorage likesStorage;
    private final MpaRatingService mpaService;
    private final GenreService genreService;
    private final FilmGenresStorage filmGenresStorage;

    @Override
    public Film getFilm(long filmId) {
        Film film;
        String sqlQuery = "select * from FILMS where FILM_ID = ?";
        try {
            film = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, filmId);
        } catch (DataAccessException e) {
            throw new NotFoundException(String.format("Фильм с id %s не найден", filmId));
        }
        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        String sqlQuery = "select * from FILMS";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    @Override
    public Film addFilm(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");
        int filmId = simpleJdbcInsert.executeAndReturnKey(toMap(film)).intValue();

        //Добавить жанры
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            filmGenresStorage.addGenres(film.getGenres(), filmId);
        }
        return getFilm(filmId);
    }

    @Override
    public Film updateFilm(Film film) {
        getFilm(film.getId());
        String sqlQuery = "update FILMS set NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, RATE = ?, " +
                "MPA_ID = ? where FILM_ID = ?";
        jdbcTemplate.update(sqlQuery
                , film.getName()
                , film.getDescription()
                , film.getReleaseDate()
                , film.getDuration()
                , film.getRate()
                , film.getMpaRating().getId()
                , film.getId());

        Film oldFilm = getFilm(film.getId());

        //Очистить жанры
        Collection<Genre> existingGenres = oldFilm.getGenres();
        if (existingGenres != null && !existingGenres.isEmpty()) {
            filmGenresStorage.deleteGenres(film.getId());
        }
        //Добавить жанры
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            filmGenresStorage.addGenres(film.getGenres(), film.getId());
        }
        return getFilm(film.getId());
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return Film.builder()
                .id(resultSet.getInt("film_id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .duration(resultSet.getInt("duration"))
                .rate(resultSet.getInt("rate"))
                .mpaRating(mpaService.getMpaById(resultSet.getInt("mpa_id")))
                .likes(likesStorage.getListOfLikes(resultSet.getInt("film_id")))
                .genres(genreService.getListOfGenres(resultSet.getInt("film_id")))
                .build();
    }

    private Map<String, Object> toMap(Film film) {
        Map<String, Object> values = new HashMap<>();
        values.put("name", film.getName());
        values.put("description", film.getDescription());
        values.put("release_date", film.getReleaseDate());
        values.put("duration", film.getDuration());
        values.put("rate", film.getRate());
        values.put("MPA_id", film.getMpaRating().getId());
        return values;
    }
}
