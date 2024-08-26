package ru.yandex.practicum.filmorate.repository.mpa;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import({JdbcMpaRepository.class, MpaResultSetExtractor.class, MpaListResultSetExtractor.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DisplayName("Тестировать репозиторий рейтингов")
class JdbcMpaRepositoryTest {
    private final MpaRepository mpaRepository;

    @Test
    @DisplayName("Получить рейтинг по ID")
    void shouldGetMpaById() {
        Mpa mpa = getAllMpa().getFirst();
        Mpa savedMpa = mpaRepository.getById(1L).orElseThrow();
        assertThat(savedMpa)
                .usingRecursiveComparison()
                .isEqualTo(mpa);
    }

    @Test
    @DisplayName("Получить все рейтинги")
    void shouldGetAllMpa() {
        List<Mpa> mpaList = mpaRepository.getAll();
        assertThat(mpaList)
                .usingRecursiveComparison()
                .isEqualTo(getAllMpa());
    }

    private List<Mpa> getAllMpa() {
        Mpa mpa1 = new Mpa();
        mpa1.setId(1L);
        mpa1.setName("G");

        Mpa mpa2 = new Mpa();
        mpa2.setId(2L);
        mpa2.setName("PG");

        Mpa mpa3 = new Mpa();
        mpa3.setId(3L);
        mpa3.setName("PG-13");

        Mpa mpa4 = new Mpa();
        mpa4.setId(4L);
        mpa4.setName("R");

        Mpa mpa5 = new Mpa();
        mpa5.setId(5L);
        mpa5.setName("NC-17");

        return List.of(mpa1, mpa2, mpa3, mpa4, mpa5);
    }
}