package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.group.Create;
import ru.yandex.practicum.filmorate.validator.group.Default;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Проверить валидацию модели пользователя")
public class UserValidationTest {
    private Validator validator;
    private User user;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        user = new User();
        user.setId(1L);
        user.setEmail("email@email.com");
        user.setLogin("login");
        user.setBirthday(LocalDate.now().minusDays(1));
    }

    @Test
    @DisplayName("Пройти валидацию при корректных данных пользователя")
    void shouldSuccessValidationWhenValidFilmData() {
        Set<ConstraintViolation<User>> violations = validator.validate(user, Create.class, Default.class);
        assertTrue(violations.isEmpty(), "Ошибка валидации при корректных данных пользователя");
    }

    @Test
    @DisplayName("Провалить валидацию при пустой электронной почте")
    void shouldFailValidationWhenEmptyEmail() {
        user.setEmail(" ");

        Set<ConstraintViolation<User>> violations = validator.validate(user, Default.class);
        assertFalse(violations.isEmpty(), "Некорректная валидация при пустой электронной почте");
    }

    @Test
    @DisplayName("Провалить валидацию при некорректной электронной почте")
    void shouldFailValidationWhenIncorrectEmail() {
        user.setEmail("@test");

        Set<ConstraintViolation<User>> violations = validator.validate(user, Default.class);
        assertFalse(violations.isEmpty(), "Некорректная валидация при некорректной электронной почте");
    }

    @Test
    @DisplayName("Провалить валидацию при пустом логине")
    void shouldFailValidationWhenEmptyLogin() {
        user.setLogin(" ");

        Set<ConstraintViolation<User>> violations = validator.validate(user, Default.class);
        assertFalse(violations.isEmpty(), "Некорректная валидация при пустом логине");
    }

    @Test
    @DisplayName("Провалить валидацию при логине c пробелами")
    void shouldFailValidationWhenLoginHasWhiteSpace() {
        user.setLogin("a v");

        Set<ConstraintViolation<User>> violations = validator.validate(user, Default.class);
        assertFalse(violations.isEmpty(), "Некорректная валидация при логине с пробелами");
    }

    @Test
    @DisplayName("Пройти валидацию при дате рождения в настоящем")
    void shouldSuccessValidationWhenPresentBirthDay() {
        user.setBirthday(LocalDate.now());

        Set<ConstraintViolation<User>> violations = validator.validate(user, Default.class);
        assertTrue(violations.isEmpty(), "Ошибка валидации при корректной дате рождения");
    }

    @Test
    @DisplayName("Провалить валидацию при дате рождения в будущем")
    void shouldFailValidationWhenFutureBirthDay() {
        user.setBirthday(LocalDate.now().plusDays(1));

        Set<ConstraintViolation<User>> violations = validator.validate(user, Default.class);
        assertFalse(violations.isEmpty(), "Некорректная валидация при дате рождения в будущем");
    }
}
