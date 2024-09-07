package ru.yandex.practicum.filmorate.repository.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmRepository {
    Film save(Film film);

    void update(Film newFilm);

    List<Film> getAll();

    Optional<Film> getById(long filmId);

    void addLike(long filmId, long userId);

    void deleteLike(long filmId, long userId);

    int getLikesByFilmId(long filmId);

    List<Film> getMostPopular(int count);

    void delete(long filmId);

    List<Film> getDirectorFilmsSortedByYear(long directorId);

    List<Film> getDirectorFilmsSortedByLikes(long directorId);

}
