package ru.yandex.practicum.filmorate.controller;

import ch.qos.logback.classic.Logger;
import jakarta.validation.Valid;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Long, User> users = new HashMap<>();
    private final Logger log = (Logger) LoggerFactory.getLogger(UserController.class);

    @GetMapping
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        try {
            validateUser(user);
        } catch (ValidationException exception) {
            log.warn(exception.getMessage(), exception);
            throw new ValidationException(exception);
        } catch (RuntimeException otherException) {
            log.warn(otherException.getMessage(), otherException);
            throw new RuntimeException(otherException);
        }

        user.setId(getNextId());

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        users.put(user.getId(), user);
        log.info("Создан пользователь {} c ID = {}", user.getLogin(), user.getId());
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        try {
            if (newUser.getId() == null) {
                log.warn("У пользователя должен быть id");
                throw new ValidationException("У пользователя должен быть id");
            }

            User oldUser = users.get(newUser.getId());
            if (oldUser == null) {
                log.warn("Пользователь с id = {} не найден", newUser.getId());
                throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
            }


            if (newUser.getEmail() != null) {
                validateEmail(newUser.getEmail());
                oldUser.setEmail(newUser.getEmail());
                log.trace("Email пользователя с ID = {} был изменён", oldUser.getId());
            }

            if (newUser.getLogin() != null) {
                validateLogin(newUser.getLogin());
                if (oldUser.getName().equals(oldUser.getLogin())) {
                    oldUser.setName(newUser.getLogin());
                    log.trace("Имя пользователя с ID = {} было изменено", oldUser.getId());
                }
                oldUser.setLogin(newUser.getLogin());
                log.trace("Логин пользователя с ID = {} был изменён", oldUser.getId());
            }

            if (newUser.getBirthday() != null) {
                validateBirthday(newUser.getBirthday());
                oldUser.setBirthday(newUser.getBirthday());
                log.trace("Дата рождения пользователя с ID = {} была изменена", oldUser.getId());
            }

            if (newUser.getName() != null) {
                oldUser.setName(newUser.getName());
                log.trace("Имя пользователя с ID = {} было изменено", oldUser.getId());
            }

            log.debug("Данные пользователя с ID = {} были обнавлены", oldUser.getId());
            return oldUser;
        } catch (ValidationException exception) {
            log.warn(exception.getMessage(), exception);
            throw new ValidationException(exception);
        } catch (NotFoundException exception) {
            log.warn(exception.getMessage(), exception);
            throw new NotFoundException(exception);
        } catch (RuntimeException otherException) {
            log.warn(otherException.getMessage(), otherException);
            throw new RuntimeException(otherException);
        }
    }

    private long getNextId() {
        long nextId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);

        return ++nextId;
    }

    private void validateUser(User user) {
        validateEmail(user.getEmail());
        validateLogin(user.getLogin());
        validateBirthday(user.getBirthday());
    }

    private void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new ValidationException("Электронная почта не может быть пустой");
        }

        if (!email.contains("@")) {
            throw new ValidationException("Электронная почта должна содержать символ \"@\"");
        }
    }

    private void validateLogin(String login) {
        if (login == null || login.isBlank()) {
            throw new ValidationException("Логин не может быть пустым");
        }

        if (login.contains(" ")) {
            throw new ValidationException("Логин не может содержать пробелы");
        }
    }

    private void validateBirthday(LocalDate birthday) {
        if (birthday == null) {
            return;
        }

        if (birthday.isAfter(LocalDate.now())) {
            log.warn("Неккоректная дата рождения");
            throw new ValidationException("Неккоректная дата рождения");
        }
    }
}
