package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Проверить валидацию фильмов")
public class FilmValidationTest {
    private FilmController filmController;
    private Film film;

    @BeforeEach
    void setUp() {
        filmController = new FilmController();

        film = new Film();
        film.setId(1L);
        film.setName("Test");
        film.setDescription("Some description");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(90);
    }

    @Nested
    @DisplayName("Валидация при создании")
    class FilmValidationWhenCreateTest {
        @Test
        @DisplayName("Создать корректный фильм")
        void shouldCreateFilm() {
            Film createdFilm = filmController.create(film);

            List<Film> films = filmController.getAll();

            assertNotNull(createdFilm, "Созданный фильм не вернулся");
            assertNotNull(films, "Список фильмов не вернулся");
            assertEquals(1, films.size(), "Фильм не создался в контроллере");
            assertEquals(film, createdFilm, "Вернулся неккоректный фильм");
            assertEquals(film, films.getFirst(), "Создался неккоректный фильм");
        }

        @Test
        @DisplayName("Выбросить исключение при создании фильма с пустым названием")
        void shouldExceptionWhenCreateFilmWithEmptyName() {
            film.setName(null);

            assertThrows(ValidationException.class, () -> filmController.create(film));

            film.setName(" ");

            assertThrows(ValidationException.class, () -> filmController.create(film));
        }

        @Test
        @DisplayName("Создать фильм с описанием длиной в 200 символов")
        void shouldCreateFilmWithDescriptionLength200() {
            film.setDescription("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                    "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                    "aaaaaaaaaaaaaa");

            assertDoesNotThrow(() -> filmController.create(film));
        }

        @Test
        @DisplayName("Выбросить исключение при создании фильма с описанием длинее 200 символов")
        void shouldExceptionWhenCreateFilmWithDescriptionLengthOver200() {
            film.setDescription("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                    "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                    "aaaaaaaaaaaaaaa");

            assertThrows(ValidationException.class, () -> filmController.create(film));
        }

        @Test
        @DisplayName("Выбросить исключение при создании фильма с датой релиза раньше 28.12.1895")
        void shouldExceptionWhenCreateFilmWithReleaseDateBeforeFilmCreation() {
            film.setReleaseDate(LocalDate.of(1895, 12, 27));

            assertThrows(ValidationException.class, () -> filmController.create(film));
        }

        @Test
        @DisplayName("Создать фильм с с датой релиза 28.12.1895")
        void shouldCreateFilmWithReleaseDateOfFilmCreation() {
            film.setReleaseDate(LocalDate.of(1895, 12, 28));

            assertDoesNotThrow(() -> filmController.create(film));
        }

        @Test
        @DisplayName("Выбросить исключение при создании фильма с нулевой продолжительностью")
        void shouldExceptionWhenCreateFilmWithZeroDuration() {
            film.setDuration(0);

            assertThrows(ValidationException.class, () -> filmController.create(film));
        }

        @Test
        @DisplayName("Выбросить исключение при создании фильма с отрицательной продолжительностью")
        void shouldExceptionWhenCreateFilmWithNegativeDuration() {
            film.setDuration(-1);

            assertThrows(ValidationException.class, () -> filmController.create(film));
        }
    }

    @Nested
    @DisplayName("Валидация при обновлении")
    class FilmValidationWhenUpdateTest {
        private Film newFilm;

        @BeforeEach
        void setUp() {
            filmController.create(film);

            newFilm = new Film();
            newFilm.setId(1L);
            newFilm.setName("new name");
            newFilm.setDescription("new description");
            newFilm.setReleaseDate(LocalDate.now().minusDays(2));
            newFilm.setDuration(95);
        }

        @Test
        @DisplayName("Обновить фильм")
        void shouldCreateFilm() {
            Film updatedFilm = filmController.update(newFilm);

            List<Film> films = filmController.getAll();

            assertNotNull(updatedFilm, "Обновлённый фильм не вернулся");
            assertEquals(newFilm, updatedFilm, "Вернулся неккоректный фильм");
            assertEquals(newFilm, films.getFirst(), "Фильм не обновился");
        }

        @Test
        @DisplayName("Выбросить исключение при пустом ID")
        void shouldExceptionWhenUpdateFilmWithoutId() {
            newFilm.setId(null);

            assertThrows(ValidationException.class, () -> filmController.update(newFilm));
        }

        @Test
        @DisplayName("Выбросить исключение при неккоректном ID")
        void shouldExceptionWhenUpdateFilmWithIncorrectId() {
            newFilm.setId(2L);

            assertThrows(NotFoundException.class, () -> filmController.update(newFilm));
        }

        @Test
        @DisplayName("Выбросить исключение при обновлении фильма с пустым названием")
        void shouldExceptionWhenUpdateFilmWithEmptyName() {
            newFilm.setName(" ");

            assertThrows(ValidationException.class, () -> filmController.update(newFilm));
        }

        @Test
        @DisplayName("Обновить фильм с описанием длиной в 200 символов")
        void shouldUpdateFilmWithDescriptionLength200() {
            newFilm.setDescription("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                    "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                    "aaaaaaaaaaaaaa");

            assertDoesNotThrow(() -> filmController.update(newFilm));
        }

        @Test
        @DisplayName("Выбросить исключение при обновлении фильма с описанием длинее 200 символов")
        void shouldExceptionWhenUpdateFilmWithDescriptionLengthOver200() {
            newFilm.setDescription("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                    "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                    "aaaaaaaaaaaaaaa");

            assertThrows(ValidationException.class, () -> filmController.update(newFilm));
        }

        @Test
        @DisplayName("Выбросить исключение при обновлении фильма с датой релиза раньше 28.12.1895")
        void shouldExceptionWhenUpdateFilmWithReleaseDateBeforeFilmCreation() {
            newFilm.setReleaseDate(LocalDate.of(1895, 12, 27));

            assertThrows(ValidationException.class, () -> filmController.update(newFilm));
        }

        @Test
        @DisplayName("Обновить фильм с с датой релиза 28.12.1895")
        void shouldUpdateFilmWithReleaseDateOfFilmCreation() {
            newFilm.setReleaseDate(LocalDate.of(1895, 12, 28));

            assertDoesNotThrow(() -> filmController.update(newFilm));
        }

        @Test
        @DisplayName("Выбросить исключение при обновлении фильма с нулевой продолжительностью")
        void shouldExceptionWhenUpdateFilmWithZeroDuration() {
            newFilm.setDuration(0);

            assertThrows(ValidationException.class, () -> filmController.update(newFilm));
        }

        @Test
        @DisplayName("Выбросить исключение при обновлении фильма с отрицательной продолжительностью")
        void shouldExceptionWhenUpdateFilmWithNegativeDuration() {
            newFilm.setDuration(-1);

            assertThrows(ValidationException.class, () -> filmController.update(newFilm));
        }
    }
}
