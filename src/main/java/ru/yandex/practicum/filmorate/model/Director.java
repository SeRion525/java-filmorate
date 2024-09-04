package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import ru.yandex.practicum.filmorate.validator.annotation.NullOrNotBlank;
import ru.yandex.practicum.filmorate.validator.group.Create;
import ru.yandex.practicum.filmorate.validator.group.Update;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode(of = "id")
public class Director {
    private Long id;
    private String name;
}
