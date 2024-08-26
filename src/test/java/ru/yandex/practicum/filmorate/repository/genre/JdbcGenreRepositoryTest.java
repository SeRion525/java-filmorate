package ru.yandex.practicum.filmorate.repository.genre;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import({JdbcGenreRepository.class, GenreResultSetExtractor.class, GenreListResultSetExtractor.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DisplayName("Тестировать репозиторий жанров")
class JdbcGenreRepositoryTest {
    private final GenreRepository genreRepository;

    @Test
    @DisplayName("Получить все жанры")
    void shouldGetAllGenres() {
        List<Genre> genres = genreRepository.getAll();
        assertThat(genres)
                .usingRecursiveComparison()
                .isEqualTo(getAllGenres());
    }

    @Test
    @DisplayName("Получить жанр по ID")
    void shouldGetGenreById() {
        Genre genre = getAllGenres().getFirst();
        Genre savedGenre = genreRepository.getById(1L).orElseThrow();
        assertThat(savedGenre)
                .usingRecursiveComparison()
                .isEqualTo(genre);
    }

    @Test
    @DisplayName("Получить жанры по их ID")
    void shouldGetGenresByIds() {
        List<Genre> genres = List.of(getAllGenres().get(0), getAllGenres().get(1));
        List<Genre> savedGenres = genreRepository.getByIds(List.of(1L, 2L));
        assertThat(savedGenres)
                .usingRecursiveComparison()
                .isEqualTo(genres);
    }

    private List<Genre> getAllGenres() {
        Genre genre1 = new Genre();
        genre1.setId(1L);
        genre1.setName("Комедия");

        Genre genre2 = new Genre();
        genre2.setId(2L);
        genre2.setName("Драма");

        Genre genre3 = new Genre();
        genre3.setId(3L);
        genre3.setName("Мультфильм");

        Genre genre4 = new Genre();
        genre4.setId(4L);
        genre4.setName("Триллер");

        Genre genre5 = new Genre();
        genre5.setId(5L);
        genre5.setName("Документальный");

        Genre genre6 = new Genre();
        genre6.setId(6L);
        genre6.setName("Боевик");

        return List.of(genre1, genre2, genre3, genre4, genre5, genre6);
    }
}