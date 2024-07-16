package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film findById(long filmId) {
        return filmStorage.findById(filmId);
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film newFilm) {
        return filmStorage.update(newFilm);
    }

    public void addLike(long filmId, long userId) {
        Film film = filmStorage.findById(filmId);
        userStorage.findById(userId);
        film.getUserLikes().add(userId);
        log.debug("Пользователь с ID = {} лайкнул фильм с ID = {}", userId, filmId);
    }

    public void removeLike(long filmId, long userId) {
        Film film = filmStorage.findById(filmId);
        userStorage.findById(userId);
        film.getUserLikes().remove(userId);
        log.debug("Пользователь с ID = {} удалил лайк у фильма с ID = {}", userId, filmId);
    }

    public List<Film> findPopular(int count) {
        List<Film> films = filmStorage.findAll();
        List<Film> popularFilms = films.stream()
                .sorted((f1, f2) -> Integer.compare(f2.getUserLikes().size(), f1.getUserLikes().size()))
                .limit(count)
                .toList();

        log.trace("Список популярных фильмов:\n{}", popularFilms);
        return popularFilms;
    }
}
