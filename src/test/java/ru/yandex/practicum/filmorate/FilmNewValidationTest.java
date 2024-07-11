package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Проверить валидацию модели фильма")
public class FilmNewValidationTest {
    private Validator validator;
    private Film film;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        film = new Film();
        film.setId(1L);
        film.setName("Test");
        film.setDescription("Some description");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(90);
    }

    @Test
    @DisplayName("Пройти валидацию при корректных данных фильма")
    void shouldSuccessValidationWhenValidFilmData() {
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty(), "Ошибка валидации при коректных данных фильма");
    }

    @Test
    @DisplayName("Провалить валидацию при некорректном имени")
    void shouldFailValidationWhenInvalidName() {
        film.setName(" ");

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Неккоректная валидация названия фильма");
    }

    @Test
    @DisplayName("Пройти валидацию при описании длинной 200 символов")
    void shouldSuccessValidationWhenDescriptionLengthEquals200() {
        film.setDescription("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaa");

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty(), "Ошибка валидации при корректном описании фильма");
    }

    @Test
    @DisplayName("Провалить валидацию при описании длинной больше 200 символов")
    void shouldFailValidationWhenDescriptionLengthOver200() {
        film.setDescription("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaa");

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Неккоректная валидация описания фильма");
    }

    @Test
    @DisplayName("Пройти валидацию при дате релиза в день рождения кино")
    void shouldSuccessValidationWhenReleaseDateIsFilmBirthday() {
        film.setReleaseDate(LocalDate.of(1895, 12, 28));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty(), "Ошибка валидации при корректной дате релиза фильма");
    }

    @Test
    @DisplayName("Провалить валидацию при дате релиза раньше дня рождения кино")
    void shouldFailValidationWhenReleaseDateIsBeforeFilmBirthday() {
        film.setReleaseDate(LocalDate.of(1895, 12, 27));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Неккоректная валидация даты релиза фильма");
    }

    @Test
    @DisplayName("Провалить валидацию при нулевой продолжительности")
    void shouldFailValidationWhenZeroDuration() {
        film.setDuration(0);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Неккоректная валидация продолжительности фильма");
    }

    @Test
    @DisplayName("Провалить валидацию при отрицательной продолжительности")
    void shouldFailValidationWhenNegativeDuration() {
        film.setDuration(-1);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Неккоректная валидация продолжительности фильма");
    }
}
