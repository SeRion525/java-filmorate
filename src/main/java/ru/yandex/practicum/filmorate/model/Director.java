package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.yandex.practicum.filmorate.validator.group.Default;
import ru.yandex.practicum.filmorate.validator.group.Update;

@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "id")
public class Director {
    @NotNull(groups = Update.class)
    private Long id;
    @NotBlank(groups = Default.class)
    private String name;
}
