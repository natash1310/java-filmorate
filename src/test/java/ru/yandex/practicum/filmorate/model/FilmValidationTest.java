package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Валидация модели Film")
class FilmValidationTest extends BaseValidationTest {

    private Film createValidFilm() {
        Film film = new Film();
        film.setId(0);
        film.setName("Test film");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(100);
        film.setMpa(new MpaRating(1, "test"));
        return film;
    }

    @Test
    @DisplayName("Корректный фильм проходит валидацию")
    void validFilm_shouldPassValidation() {
        Film film = createValidFilm();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Отрицательный id не проходит валидацию")
    void negativeId_shouldFailValidation() {
        Film film = createValidFilm();
        film.setId(-1);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Пустое название не проходит валидацию")
    void blankName_shouldFailValidation() {
        Film film = createValidFilm();
        film.setName("  ");

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Описание длиннее 200 символов не проходит валидацию")
    void tooLongDescription_shouldFailValidation() {
        Film film = createValidFilm();
        film.setDescription("a".repeat(201));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Null в дате релиза не проходит валидацию")
    void nullReleaseDate_shouldFailValidation() {
        Film film = createValidFilm();
        film.setReleaseDate(null);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Null в продолжительности не проходит валидацию")
    void zeroDuration_shouldFailValidation() {
        Film film = createValidFilm();
        film.setDuration(0);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertFalse(violations.isEmpty());
    }
}
