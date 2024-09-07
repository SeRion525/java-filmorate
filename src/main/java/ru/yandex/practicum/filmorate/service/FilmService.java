package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmService {
    List<Film> getFilms();

    Film getFilmById(long filmId);

    Film saveFilm(Film film);

    Film updateFilm(Film newFilm);

    void addLike(long filmId, long userId);

    void removeLike(long filmId, long userId);

    List<Film> getMostPopular(int count, Integer year, Long genreId);

    void deleteFilm(long filmId);

    List<Film> filmsByDirector(long directorId, String sortBy);

    Collection<Film> getRecommendedFilms(Long userId);
}
