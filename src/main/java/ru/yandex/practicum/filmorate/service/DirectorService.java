package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorService {
    Director save(Director director);

    List<Director> getDirectors();

    Director getDirectorById(long id);

    Director update(Director director);

    Director deleteDirectorById(long id);
}
