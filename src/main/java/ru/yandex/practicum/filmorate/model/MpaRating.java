package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MpaRating {
    private int id;
    @NotBlank(message = "Название рейтинга не может быть пустым")
    private String name;
}