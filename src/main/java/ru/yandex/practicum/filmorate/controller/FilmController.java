package ru.yandex.practicum.filmorate.controller;

import ch.qos.logback.classic.Logger;
import jakarta.validation.Valid;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {
    private static final LocalDate FILM_BIRTHDAY = LocalDate.of(1895, 12, 28);
    private final Map<Long, Film> films = new HashMap<>();
    private final Logger log = (Logger) LoggerFactory.getLogger(FilmController.class);

    @GetMapping
    public List<Film> getAll() {
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        try {
            validateFilm(film);
        } catch (ValidationException exception) {
            log.warn(exception.getMessage(), exception);
            throw new ValidationException(exception);
        } catch (RuntimeException otherException) {
            log.warn(otherException.getMessage(), otherException);
            throw new RuntimeException(otherException);
        }

        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Фильм с названием {} и ID = {} успешно добавлен", film.getName(), film.getId());
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) {
        try {
            if (newFilm.getId() == null) {
                throw new ValidationException("У фильма должен быть id");
            }

            Film oldFilm = films.get(newFilm.getId());

            if (oldFilm == null) {
                log.warn("Фильм с id = {} не найден", newFilm.getId());
                throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
            }

            if (newFilm.getName() != null) {
                validateName(newFilm.getName());
                oldFilm.setName(newFilm.getName());
                log.trace("Название фильма с ID = {} было изменено", oldFilm.getId());
            }

            if (newFilm.getDescription() != null) {
                validateDescription(newFilm.getDescription());
                oldFilm.setDescription(newFilm.getDescription());
                log.trace("Описание фильма с ID = {} было изменено", oldFilm.getId());
            }

            if (newFilm.getReleaseDate() != null) {
                validateReleaseDate(newFilm.getReleaseDate());
                oldFilm.setReleaseDate(newFilm.getReleaseDate());
                log.trace("Дата релиза фильма с ID = {} была изменена", oldFilm.getId());
            }

            if (newFilm.getDuration() != null) {
                validateDuration(newFilm.getDuration());
                oldFilm.setDuration(newFilm.getDuration());
                log.trace("Продолжительность фильма с ID = {} была изменена", oldFilm.getId());
            }

            log.debug("Фильм с названием {} и ID = {} успешно обновлен", oldFilm.getName(), oldFilm.getId());
            return oldFilm;

        } catch (ValidationException exception) {
            log.warn(exception.getMessage(), exception);
            throw new ValidationException(exception);
        } catch (NotFoundException exception) {
            log.warn(exception.getMessage(), exception);
            throw new NotFoundException(exception);
        } catch (RuntimeException otherException) {
            log.warn(otherException.getMessage(), otherException);
            throw new RuntimeException(otherException);
        }
    }

    private long getNextId() {
        long nextId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);

        return ++nextId;
    }

    private void validateFilm(@Valid Film film) {
        validateName(film.getName());
        validateDescription(film.getDescription());
        validateReleaseDate(film.getReleaseDate());
        validateDuration(film.getDuration());
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new ValidationException("Название фильма не может быть пустым");
        }
    }

    private void validateDescription(String description) {
        if (description == null) {
            return;
        }

        if (description.length() > 200) {
            throw new ValidationException("Максимальная длина описания не может быть больше 200 символов");
        }
    }

    private void validateReleaseDate(LocalDate releaseDate) {
        if (releaseDate == null) {
            return;
        }

        if (releaseDate.isBefore(FILM_BIRTHDAY)) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
    }

    private void validateDuration(Integer duration) {
        if (duration == null) {
            return;
        }

        if (duration <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
    }
}
