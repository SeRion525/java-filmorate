package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.validator.group.Create;
import ru.yandex.practicum.filmorate.validator.group.Default;
import ru.yandex.practicum.filmorate.validator.group.Update;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/directors")
@Validated
public class DirectorController {

    private final DirectorService directorService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Validated(Default.class)
    public Director save(@Valid @RequestBody Director director) {
        return directorService.save(director);
    }

    @GetMapping
    public List<Director> getDirectors() {
        return directorService.getDirectors();
    }

    @GetMapping("/{id}")
    public Director getDirectorById(@PathVariable long id) {
        return directorService.getDirectorById(id);
    }

    @PutMapping
    @Validated({Default.class, Update.class})
    public Director updateDirector(@RequestBody @Valid Director director) {
        return directorService.update(director);
    }

    @DeleteMapping("/{id}")
    public void deleteDirectorById(@PathVariable int id) {
        directorService.deleteDirectorById(id);
    }
}