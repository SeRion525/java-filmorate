package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.yandex.practicum.filmorate.validator.annotation.NullOrNotBlank;
import ru.yandex.practicum.filmorate.validator.group.Create;
import ru.yandex.practicum.filmorate.validator.group.Update;

@Data
public class Review {
    @NotNull(groups = Update.class)
    private Long reviewId;
    @NotBlank(groups = Create.class)
    @NullOrNotBlank(groups = Update.class)
    private String content;
    @NotNull(groups = Create.class)
    private Boolean isPositive;
    @NotNull(groups = Create.class)
    private Long userId;
    @NotNull(groups = Create.class)
    private Long filmId;
    private int useful;
}
