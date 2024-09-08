package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.repository.director.DirectorRepository;
import ru.yandex.practicum.filmorate.repository.film.FilmRepository;
import ru.yandex.practicum.filmorate.repository.genre.GenreRepository;
import ru.yandex.practicum.filmorate.repository.mpa.MpaRepository;
import ru.yandex.practicum.filmorate.repository.user.UserRepository;

import java.util.*;

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
    private final DirectorRepository directorRepository;

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

        if (film.getDirectors() != null) {
            film.setDirectors(new LinkedHashSet<>(getDirectorFromRepository(film.getDirectors())));
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

        if (newFilm.getDirectors() != null) {
            savedFilm.setDirectors(new LinkedHashSet<>(getDirectorFromRepository(newFilm.getDirectors())));
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

    @Override
    public Collection<Film> getRecommendedFilms(Long userId) {
        userRepository.getById(userId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_USER + userId));
        Map<Long, Set<Film>> usersLikedFilmsMap = filmRepository.findAllUsersWithLikedFilms();
        return getFilms(userId, usersLikedFilmsMap);
    }

    @Override
    public List<Film> filmsByDirector(long directorId, String sortBy) {
        if (sortBy.equalsIgnoreCase("year")) {
            return filmRepository.getDirectorFilmsSortedByYear(directorId);
        }

        if (sortBy.equalsIgnoreCase("likes")) {
            return filmRepository.getDirectorFilmsSortedByLikes(directorId);
        }

        throw new ValidationException("Указан неверный параметр сортировки");
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

    private List<Director> getDirectorFromRepository(Set<Director> directors) {
        final List<Long> directorIds = directors.stream().map(Director::getId).toList();
        final List<Director> savedDirectors = directorRepository.getByIds(directorIds);

        if (directorIds.size() != savedDirectors.size()) {
            throw new ValidationException("Режиссёр не найдены");
        }

        return savedDirectors;
    }

    public void deleteFilm(long filmId) {
        if (filmRepository.getById(filmId).isEmpty()) {
            throw new NotFoundException("Фильм с данным ID не найден.");
        }
        filmRepository.delete(filmId);
    }

    private Collection<Film> getFilms(Long userId, Map<Long, Set<Film>> usersLikedFilmsMap) {
        Set<Film> currentUserFilms = usersLikedFilmsMap.remove(userId);

        if (currentUserFilms == null) {
            return Collections.emptySet();
        }

        Set<Long> mostSimilarUserIds = new HashSet<>();
        int maxCommonLikes = 0;

        for (Map.Entry<Long, Set<Film>> entry : usersLikedFilmsMap.entrySet()) {
            Long id = entry.getKey();
            Set<Film> films = new HashSet<>(entry.getValue());
            films.retainAll(currentUserFilms);

            if (films.size() > maxCommonLikes) {
                maxCommonLikes = films.size();
                mostSimilarUserIds.clear();
                mostSimilarUserIds.add(id);
            } else if (films.size() == maxCommonLikes && !films.isEmpty()) {
                mostSimilarUserIds.add(id);
            }
        }

        if (mostSimilarUserIds.isEmpty()) {
            return Collections.emptySet();
        }

        Set<Film> recommendationFilms = new HashSet<>();

        for (Long id : mostSimilarUserIds) {
            Set<Film> userFilms = new HashSet<>(usersLikedFilmsMap.get(id));
            userFilms.removeAll(currentUserFilms);
            recommendationFilms.addAll(userFilms);
        }

        return recommendationFilms;
    }
}
