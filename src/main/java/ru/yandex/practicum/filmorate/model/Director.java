package ru.yandex.practicum.filmorate.model;

import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "id")
public class Director {
    private Long id;
    private String name;
}
