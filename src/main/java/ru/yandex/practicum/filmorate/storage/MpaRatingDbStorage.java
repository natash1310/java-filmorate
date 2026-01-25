package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.interfaces.MpaRatingStorage;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Component
@RequiredArgsConstructor
@Slf4j
public class MpaRatingDbStorage implements MpaRatingStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<MpaRating> getMpaRating() {
        String sqlQuery = "select * from RATING_MPA";
        return jdbcTemplate.query(sqlQuery, this::mapRowToMpa);
    }

    @Override
    public MpaRating getMpaRatingById(int mpaId) {
        MpaRating mpa;
        String sqlQuery = "select * from RATING_MPA where MPA_ID = ?";
        try {
            mpa = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToMpa, mpaId);
        } catch (DataAccessException e) {
            throw new NotFoundException(String.format("MPA c таким id %s не найден", mpaId));
        }
        return mpa;
    }

    private MpaRating mapRowToMpa(ResultSet resultSet, int rowNum) throws SQLException {
        return MpaRating.builder()
                .id(resultSet.getInt("MPA_id"))
                .name(resultSet.getString("name"))
                .build();
    }
}
