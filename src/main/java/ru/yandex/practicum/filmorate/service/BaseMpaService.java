package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.repository.mpa.MpaRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BaseMpaService implements MpaService {
    private final MpaRepository mpaRepository;

    @Override
    public Mpa getMpaById(long mpaId) {
        return mpaRepository.getById(mpaId)
                .orElseThrow(() -> new NotFoundException("Не найден рейтинг с ID = " + mpaId));
    }

    @Override
    public List<Mpa> getAllMpa() {
        return mpaRepository.getAll();
    }
}
