package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    @PositiveOrZero(message = "id пользователя не может быть отрицательным числом")
    private int id;
    @NotBlank(message = "Электронная почта не может быть пустой")
    @Email(message = "Электронная почта не может быть пустой и должна содержать символ @")
    private String email;
    @NotBlank(message = "Логин не может быть пустым и содержать пробелы")
    private String login;
    private String name;
    @Past(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;
    private Set<Integer> friends = new HashSet<>();
}
