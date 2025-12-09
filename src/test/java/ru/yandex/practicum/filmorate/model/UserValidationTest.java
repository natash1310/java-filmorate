package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Валидация модели User")
class UserValidationTest extends BaseValidationTest {

    private User createValidUser() {
        User user = new User();
        user.setId(0);
        user.setEmail("user@example.com");
        user.setLogin("login");
        user.setName("User Name");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        return user;
    }

    @Test
    @DisplayName("Корректный пользователь проходит валидацию")
    void validUser_shouldPassValidation() {
        User user = createValidUser();

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Отрицательный id не проходит валидацию")
    void negativeId_shouldFailValidation() {
        User user = createValidUser();
        user.setId(-1);

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Некорректный email не проходит валидацию")
    void invalidEmail_shouldFailValidation() {
        User user = createValidUser();
        user.setEmail("wrong-email");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Пустой логин не проходит валидацию")
    void blankLogin_shouldFailValidation() {
        User user = createValidUser();
        user.setLogin("   ");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Дата рождения в будущем не проходит валидацию")
    void futureBirthday_shouldFailValidation() {
        User user = createValidUser();
        user.setBirthday(LocalDate.now().plusDays(1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Вчерашняя дата рождения проходит валидацию")
    void yesterdayBirthday_shouldPassValidation() {
        User user = createValidUser();
        user.setBirthday(LocalDate.now().minusDays(1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertTrue(violations.isEmpty());
    }
}
