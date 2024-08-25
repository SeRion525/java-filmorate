package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.genre.GenreRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BaseGenreService implements GenreService {
    private final GenreRepository genreRepository;

    @Override
    public Genre getGenreById(long genreId) {
        return genreRepository.getById(genreId)
                .orElseThrow(() -> new NotFoundException("Не найден жанр с ID = " + genreId));
    }

    @Override
    public List<Genre> getAllGenres() {
        return genreRepository.getAll();
    }
}
