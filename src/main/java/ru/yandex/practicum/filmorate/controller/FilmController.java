package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();
    private final Logger log = LoggerFactory.getLogger(FilmController.class);

    @GetMapping
    public List<Film> getAll() {
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Фильм с названием {} и ID = {} успешно добавлен", film.getName(), film.getId());
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) {
        @NotNull
        Long id = newFilm.getId();

        Film oldFilm = films.get(id);
        try {
            if (oldFilm == null) {
                throw new NotFoundException("Фильм с ID = " + id + " не найден");
            }
        } catch (NotFoundException exception) {
            log.warn(exception.getMessage(), exception);
            throw new NotFoundException(exception);
        }

        if (newFilm.getName() != null) {
            @Valid
            String newName = newFilm.getName();

            oldFilm.setName(newName);
            log.trace("Название фильма с ID = {} было изменено", oldFilm.getId());
        }

        if (newFilm.getDescription() != null) {
            @Valid
            String newDescription = newFilm.getDescription();

            oldFilm.setDescription(newDescription);
            log.trace("Описание фильма с ID = {} было изменено", oldFilm.getId());
        }

        if (newFilm.getReleaseDate() != null) {
            @Valid
            LocalDate newReleaseDate = newFilm.getReleaseDate();

            oldFilm.setReleaseDate(newReleaseDate);
            log.trace("Дата релиза фильма с ID = {} была изменена", oldFilm.getId());
        }

        if (newFilm.getDuration() != null) {
            @Valid
            Integer newDuration = newFilm.getDuration();

            oldFilm.setDuration(newDuration);
            log.trace("Продолжительность фильма с ID = {} была изменена", oldFilm.getId());
        }

        log.debug("Фильм с названием {} и ID = {} успешно обновлен", oldFilm.getName(), oldFilm.getId());
        return oldFilm;

    }

    private long getNextId() {
        long nextId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);

        return ++nextId;
    }
}
