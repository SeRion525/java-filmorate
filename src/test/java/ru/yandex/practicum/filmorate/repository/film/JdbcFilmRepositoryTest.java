package ru.yandex.practicum.filmorate.repository.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@Import({JdbcFilmRepository.class, FilmResultSetExtractor.class, FilmListResultSetExtractor.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DisplayName("Тестировать репозиторий фильмов")
class JdbcFilmRepositoryTest {
    public static final long TEST_FILM1_ID = 1L;
    public static final long TEST_FILM2_ID = 2L;
    private final JdbcFilmRepository filmRepository;

    static Film getTestFilm1() {
        LinkedHashSet<Genre> genres = new LinkedHashSet<>();
        Genre genre1 = new Genre();
        genre1.setId(1L);
        genre1.setName("Комедия");
        Genre genre2 = new Genre();
        genre2.setId(3L);
        genre2.setName("Мультфильм");
        genres.add(genre1);
        genres.add(genre2);

        Mpa mpa = new Mpa();
        mpa.setId(3L);
        mpa.setName("PG-13");

        Film film = new Film();
        film.setId(TEST_FILM1_ID);
        film.setName("Test1");
        film.setDescription("Test1 desc");
        film.setReleaseDate(LocalDate.of(2001, 1, 1));
        film.setDuration(111);
        film.setMpa(mpa);
        film.setGenres(genres);

        return film;
    }

    static Film getTestFilm2() {
        LinkedHashSet<Genre> genres = new LinkedHashSet<>();
        Genre genre1 = new Genre();
        genre1.setId(2L);
        genre1.setName("Драма");
        Genre genre2 = new Genre();
        genre2.setId(6L);
        genre2.setName("Боевик");
        genres.add(genre1);
        genres.add(genre2);

        Mpa mpa = new Mpa();
        mpa.setId(4L);
        mpa.setName("R");

        Film film = new Film();
        film.setId(TEST_FILM2_ID);
        film.setName("Test2");
        film.setDescription("Test2 desc");
        film.setReleaseDate(LocalDate.of(2002, 2, 2));
        film.setDuration(222);
        film.setMpa(mpa);
        film.setGenres(genres);

        return film;
    }

    @Test
    @DisplayName("Получить все фильмы")
    void shouldGetAllFilms() {
        List<Film> films = List.of(getTestFilm1(), getTestFilm2());
        List<Film> savedFilms = filmRepository.getAll();

        assertThat(savedFilms)
                .usingRecursiveComparison()
                .isEqualTo(films);
    }

    @Test
    @DisplayName("Получить фильм по ID")
    void getById() {
        Optional<Film> savedFilm = filmRepository.getById(TEST_FILM1_ID);
        assertThat(savedFilm)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(getTestFilm1());
    }

    @Test
    @DisplayName("Сохранить фильм в базу данных")
    void shouldSaveFilmInDatabase() {
        LinkedHashSet<Genre> genres = new LinkedHashSet<>();
        Genre genre = new Genre();
        genre.setId(2L);
        genre.setName("Драма");
        genres.add(genre);

        Mpa mpa = new Mpa();
        mpa.setId(4L);
        mpa.setName("R");

        Film film = new Film();
        film.setName("otherFilm");
        film.setMpa(mpa);
        film.setDuration(30);
        film.setGenres(genres);
        film = filmRepository.save(film);
        Optional<Film> savedFilm = filmRepository.getById(film.getId());

        assertThat(savedFilm)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(film);
    }

    @Test
    @DisplayName("Обновить данные фильм")
    void shouldUpdateFilmData() {
        Film film = getTestFilm1();
        film.setName("newName");
        LinkedHashSet<Genre> genres = film.getGenres();
        genres.clear();
        Genre genre = new Genre();
        genre.setId(2L);
        genre.setName("Драма");
        genres.add(genre);

        assertDoesNotThrow(() -> filmRepository.update(film));

        Optional<Film> updatedFilm = filmRepository.getById(TEST_FILM1_ID);
        assertThat(updatedFilm)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(film);
    }

    @Nested
    @DisplayName("Тестировать лайки")
    class LikeJdbcFilmRepositoryTest {
        @Test
        @Order(1)
        @DisplayName("Добвить лайк фильму")
        void shouldAddLike() {
            filmRepository.addLike(TEST_FILM1_ID, 2);
            assertEquals(2, filmRepository.getLikesByFilmId(TEST_FILM1_ID));
        }

        @Test
        @Order(2)
        @DisplayName("Удалить лайк у фильма")
        void shouldDeleteLike() {
            filmRepository.deleteLike(TEST_FILM1_ID, 1);
            assertEquals(0, filmRepository.getLikesByFilmId(TEST_FILM1_ID));
        }

        @Test
        @Order(3)
        @DisplayName("Получить список популярных фильмов")
        void shouldGetMostPopularFilms() {
            List<Film> films = List.of(getTestFilm2(), getTestFilm1());
            filmRepository.addLike(2, 2);
            List<Film> popular = filmRepository.getMostPopular(2);

            assertThat(popular)
                    .usingRecursiveComparison()
                    .isEqualTo(films);
        }
    }
}