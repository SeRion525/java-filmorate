package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Проверить валидацию пользователей")
public class UserValidationTest {
    private UserController userController;
    private User user;

    @BeforeEach
    void setUp() {
        userController = new UserController();
        user = new User();
        user.setId(1L);
        user.setEmail("email@email.com");
        user.setLogin("login");
        user.setBirthday(LocalDate.now().minusDays(1));
    }

    @Nested
    @DisplayName("Валидация при создании")
    class UserValidationWhenCreateTest {
        @Test
        @DisplayName("Создать корректного пользователя")
        void shouldCreateUser() {
            User createdUser = userController.create(user);

            List<User> users = userController.getAll();

            assertNotNull(createdUser, "Созданный пользователь не вернулся");
            assertNotNull(users, "Список пользователей не вернулся");
            assertEquals(1, users.size(), "Пользователь не создался в контроллере");
            assertEquals(user, createdUser, "Вернулся неккоректный пользователь");
            assertEquals(user, users.getFirst(), "Создался неккоректный пользователь");
        }

        @Test
        @DisplayName("Выбросить исключение при создании пользователя с неккоректной электронной почтой")
        void shouldThrowExceptionWhenCreateUserWithIncorrectEmail() {
            user.setEmail(null);

            assertThrows(ValidationException.class, () -> userController.create(user));

            user.setEmail(" ");

            assertThrows(ValidationException.class, () -> userController.create(user));

            user.setEmail("email");

            assertThrows(ValidationException.class, () -> userController.create(user));
        }

        @Test
        @DisplayName("Выбросить исключение при создании пользователя с пустым логином")
        void shouldThrowExceptionWhenCreateUserWithEmptyLogin() {
            user.setLogin(null);

            assertThrows(ValidationException.class, () -> userController.create(user));

            user.setLogin(" ");

            assertThrows(ValidationException.class, () -> userController.create(user));
        }

        @Test
        @DisplayName("Использовать логин в качестве имени при создании пользователя с пустым именем")
        void shouldUseLoginInNameWhenNameIsEmpty() {
            User createdUser = userController.create(user);

            assertEquals(createdUser.getLogin(), createdUser.getName(), "Имя пользователя не заменилось на логин");
        }

        @Test
        @DisplayName("Создать пользователя с датой рождения в настоящем")
        void shouldCreateUserWithPresentDateOfBirth() {
            user.setBirthday(LocalDate.now());

            assertDoesNotThrow(() -> userController.create(user));
        }

        @Test
        @DisplayName("Выбросить исключение при создании пользователя с датой рождения в будущем")
        void shouldThrowExceptionWhenCreateUserWithFutureDateOfBirth() {
            user.setBirthday(LocalDate.now().plusDays(1));

            assertThrows(ValidationException.class, () -> userController.create(user));
        }
    }

    @Nested
    @DisplayName("Валидация при обновлении")
    class UserValidationWhenUpdateTest {
        private User newUser;

        @BeforeEach
        void setUp() {
            userController.create(user);
            newUser = new User();
            newUser.setId(1L);
            newUser.setEmail("newemail@email.com");
            newUser.setLogin("newlogin");
            newUser.setBirthday(LocalDate.now().minusDays(2));
        }

        @Test
        @DisplayName("Обновить корректного пользователя")
        void shouldUpdateUser() {
            userController.update(newUser);

            List<User> users = userController.getAll();

            newUser.setName(newUser.getLogin());
            assertEquals(newUser, users.getFirst(), "Пользователь не обновился");
        }

        @Test
        @DisplayName("Выбросить исключение при пустом ID")
        void shouldExceptionWhenUpdateUserWithoutId() {
            newUser.setId(null);

            assertThrows(ValidationException.class, () -> userController.update(newUser));
        }

        @Test
        @DisplayName("Выбросить исключение при неккоректном ID")
        void shouldExceptionWhenUpdateUserWithIncorrectId() {
            newUser.setId(2L);

            assertThrows(NotFoundException.class, () -> userController.update(newUser));
        }

        @Test
        @DisplayName("Выбросить исключение при обновлении пользователя с неккоректной электронной почтой")
        void shouldThrowExceptionWhenUpdateUserWithIncorrectEmail() {
            newUser.setEmail(" ");

            assertThrows(ValidationException.class, () -> userController.update(newUser));

            newUser.setEmail("email");

            assertThrows(ValidationException.class, () -> userController.update(newUser));
        }

        @Test
        @DisplayName("Выбросить исключение при обновлении пользователя с пустым логином")
        void shouldThrowExceptionWhenUpdateUserWithEmptyLogin() {
            newUser.setLogin(" ");

            assertThrows(ValidationException.class, () -> userController.update(newUser));
        }

        @Test
        @DisplayName("Использовать логин в качестве имени при обновлении пользователя с логином в качестве имени")
        void shouldUseLoginInNameWhenNameIsEmpty() {
            user.setName(user.getLogin());
            userController.update(newUser);
            User updatedUser = userController.getAll().getFirst();

            assertEquals(updatedUser.getLogin(), updatedUser.getName(), "Имя пользователя не заменилось на логин");
        }

        @Test
        @DisplayName("Обновить пользователя с датой рождения в настоящем")
        void shouldUpdateUserWithPresentDateOfBirth() {
            newUser.setBirthday(LocalDate.now());

            assertDoesNotThrow(() -> userController.update(newUser));
        }

        @Test
        @DisplayName("Выбросить исключение при обновлении пользователя с датой рождения в будущем")
        void shouldThrowExceptionWhenUpdateUserWithFutureDateOfBirth() {
            newUser.setBirthday(LocalDate.now().plusDays(1));

            assertThrows(ValidationException.class, () -> userController.update(newUser));
        }
    }
}
