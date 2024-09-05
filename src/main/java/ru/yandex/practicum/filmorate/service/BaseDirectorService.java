package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.repository.director.DirectorRepository;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BaseDirectorService implements DirectorService {
    public static final String NOT_FOUND_DIRECTOR = "Не найден режиссёр с ID = ";

    private final DirectorRepository directorRepository;

    @Override
    public Director save(Director director) {
        return directorRepository.save(director);
    }

    @Override
    public List<Director> getDirectors() {
        return directorRepository.getAll();
    }

    @Override
    public Director getDirectorById(long directorId) {
        return directorRepository.getById(directorId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_DIRECTOR + directorId));
    }

    @Override
    public Director update(Director updatedDirector) {
        Director savedDirector = directorRepository.getById(updatedDirector.getId())
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_DIRECTOR + updatedDirector.getId()));

        savedDirector.setName(updatedDirector.getName());

        directorRepository.update(savedDirector);
        return savedDirector;
    }

    @Override
    public void deleteDirectorById(long id) {
        Director existedDirector = directorRepository.getById(id)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_DIRECTOR + id));

        directorRepository.delete(id);
    }
}
