package ru.yandex.practicum.filmorate.storage.film;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Film create(Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Фильм с названием {} и ID = {} успешно добавлен", film.getName(), film.getId());
        return film;
    }

    @Override
    public Film update(Film newFilm) {
        @NotNull
        Long id = newFilm.getId();

        Film oldFilm = films.get(id);
        if (oldFilm == null) {
            throw new NotFoundException("Фильм с ID = " + id + " не найден");
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

    @Override
    public void delete() {

    }

    @Override
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film findById(long filmId) {
        Film film = films.get(filmId);
        if (film == null) {
            throw new NotFoundException("Фильм с ID = " + filmId + " не найден");
        } else {
            return film;
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
}
