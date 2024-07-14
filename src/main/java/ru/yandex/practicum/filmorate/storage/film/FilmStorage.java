package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    Film create(Film film);

    Film update(Film newFilm);

    void delete();

    List<Film> findAll();

    Film findById(long filmId);
}
