package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.validator.group.Create;
import ru.yandex.practicum.filmorate.validator.group.Default;
import ru.yandex.practicum.filmorate.validator.group.Update;

import java.util.List;

@Service
@Validated
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film findById(@Positive long filmId) {
        return filmStorage.findById(filmId);
    }

    @Validated({Create.class, Default.class})
    public Film create(@Valid Film film) {
        return filmStorage.create(film);
    }

    @Validated({Update.class, Default.class})
    public Film update(@Valid Film newFilm) {
        return filmStorage.update(newFilm);
    }

    public void addLike(@Positive long filmId, @Positive long userId) {
        Film film = filmStorage.findById(filmId);
        userStorage.findById(userId);
        film.getUserLikes().add(userId);
        log.debug("Пользователь с ID = {} лайкнул фильм с ID = {}", userId, filmId);
    }

    public void removeLike(@Positive long filmId, @Positive long userId) {
        Film film = filmStorage.findById(filmId);
        userStorage.findById(userId);
        film.getUserLikes().remove(userId);
        log.debug("Пользователь с ID = {} удалил лайк у фильма с ID = {}", userId, filmId);
    }

    public List<Film> findPopular(@Positive int count) {
        List<Film> films = filmStorage.findAll();
        List<Film> popularFilms = films.stream()
                .sorted((f1, f2) -> Integer.compare(f2.getUserLikes().size(), f1.getUserLikes().size()))
                .limit(count)
                .toList();

        log.trace("Список популярных фильмов:\n{}", popularFilms);
        return popularFilms;
    }
}
