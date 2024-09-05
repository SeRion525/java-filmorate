package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.film.FilmRepository;
import ru.yandex.practicum.filmorate.repository.genre.GenreRepository;
import ru.yandex.practicum.filmorate.repository.mpa.MpaRepository;
import ru.yandex.practicum.filmorate.repository.user.UserRepository;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static ru.yandex.practicum.filmorate.service.BaseUserService.NOT_FOUND_USER;

@Service
@Slf4j
@RequiredArgsConstructor
public class BaseFilmService implements FilmService {


    public static final String NOT_FOUND_FILM = "Не найден фильм с ID = ";

    private final FilmRepository filmRepository;
    private final UserRepository userRepository;
    private final MpaRepository mpaRepository;
    private final GenreRepository genreRepository;



    @Override
    public List<Film> getFilms() {
        return filmRepository.getAll();
    }

    @Override
    public Film getFilmById(long filmId) {
        return filmRepository.getById(filmId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_FILM + filmId));
    }

    @Override
    public Film saveFilm(Film film) {
        if (film.getMpa() != null) {
            film.setMpa(getMpaFromRepository(film.getMpa().getId()));
        }

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            film.setGenres(new LinkedHashSet<>(getGenresFromRepository(film.getGenres())));
        }

        return filmRepository.save(film);
    }

    @Override
    public Film updateFilm(Film newFilm) {
        final Film savedFilm = filmRepository.getById(newFilm.getId())
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_FILM + newFilm.getId()));

        if (newFilm.getMpa() != null) {
            savedFilm.setMpa(getMpaFromRepository(newFilm.getMpa().getId()));
        }

        if (newFilm.getGenres() != null) {
            savedFilm.setGenres(new LinkedHashSet<>(getGenresFromRepository(newFilm.getGenres())));
        }

        savedFilm.setName(newFilm.getName());
        savedFilm.setDescription(newFilm.getDescription());
        savedFilm.setDuration(newFilm.getDuration());
        savedFilm.setReleaseDate(newFilm.getReleaseDate());

        filmRepository.update(savedFilm);
        return savedFilm;
    }

    @Override
    public void addLike(long filmId, long userId) {
        User user = userRepository.getById(userId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_USER + userId));
        Film film = filmRepository.getById(filmId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_FILM + filmId));

        filmRepository.addLike(film.getId(), user.getId());
    }

    @Override
    public void removeLike(long filmId, long userId) {
        User user = userRepository.getById(userId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_USER + userId));
        Film film = filmRepository.getById(filmId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_FILM + filmId));

        filmRepository.deleteLike(film.getId(), user.getId());
    }

    @Override
    public List<Film> getMostPopular(int count) {
        return filmRepository.getMostPopular(count);
    }

    private List<Genre> getGenresFromRepository(Set<Genre> genres) {
        final List<Long> genreIds = genres.stream().map(Genre::getId).toList();
        final List<Genre> savedGenres = genreRepository.getByIds(genreIds);
        if (genreIds.size() != savedGenres.size()) {
            throw new ValidationException("Жанры не найдены");
        }
        return savedGenres;
    }

    private Mpa getMpaFromRepository(long mpaId) {
        return mpaRepository.getById(mpaId)
                .orElseThrow(() -> new ValidationException("Не найден рейтинг с ID = " + mpaId));

    }

    public void deleteFilm(long filmId) {
        if (filmRepository.getById(filmId).isEmpty()) {
            throw new NotFoundException("Фильм с данным ID не найден.");
        }
        filmRepository.delete(filmId);
    }
}
