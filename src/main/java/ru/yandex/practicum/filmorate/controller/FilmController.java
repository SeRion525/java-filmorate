package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.film.JdbcFilmRepository;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.validator.group.Create;
import ru.yandex.practicum.filmorate.validator.group.Default;
import ru.yandex.practicum.filmorate.validator.group.Update;

import java.util.List;

@RestController
@Validated
@RequestMapping("/films")
public class FilmController {

    @Autowired
    private JdbcFilmRepository filmRepository;

    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> getFilms() {
        return filmService.getFilms();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable @Positive long id) {
        return filmService.getFilmById(id);
    }

    @Validated({Create.class, Default.class})
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film saveFilm(@RequestBody @Valid Film film) {
        return filmService.saveFilm(film);
    }

    @Validated({Update.class, Default.class})
    @PutMapping
    public Film updateFilm(@RequestBody @Valid Film newFilm) {
        return filmService.updateFilm(newFilm);
    }

    @PutMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addLike(@PathVariable @Positive long id, @PathVariable @Positive long userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeLike(@PathVariable @Positive long id, @PathVariable @Positive long userId) {
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getMostPopular(
            @RequestParam(required = false) @Positive Integer count,
            @RequestParam(required = false) @Positive Integer year,
            @RequestParam(required = false) @Positive Long genreId
    ) {
        return filmService.getMostPopular(count, year, genreId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFilm(@PathVariable @Positive long id) {
        filmService.deleteFilm(id);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> filmsByDirector(@PathVariable @Positive long directorId, @RequestParam @NotEmpty String sortBy) {
        return filmService.filmsByDirector(directorId, sortBy);
    }


    @GetMapping("/common")
    public List<Film> findCommonFilms(@RequestParam("userId") long userId,
                                      @RequestParam("friendId") long friendId) {
        return filmRepository.findCommonFilms(userId, friendId);
    }


    @GetMapping("/search")
    public List<Film> searchFilms(@RequestParam String query, @RequestParam String by) {
        return filmService.searchFilmsByTitleAndDirectors(query, by);
    }
}
