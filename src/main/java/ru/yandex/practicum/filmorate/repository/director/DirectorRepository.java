package ru.yandex.practicum.filmorate.repository.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

public interface DirectorRepository {
    Director save(Director director);

    List<Director> getAll();

    Optional<Director> getById(long id);

    List<Director> getByIds(List<Long> genreIds);

    void update(Director director);

    void delete(long id);
}
