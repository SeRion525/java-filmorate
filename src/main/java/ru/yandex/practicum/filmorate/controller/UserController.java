package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
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
    private final Logger log = LoggerFactory.getLogger(UserController.class);

    @GetMapping
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Создан пользователь {} c ID = {}", user.getLogin(), user.getId());
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        @NotNull
        Long id = newUser.getId();

        User oldUser = users.get(id);
        try {
            if (oldUser == null) {
                throw new NotFoundException("Пользователь с ID = " + id + " не найден");
            }
        } catch (NotFoundException exception) {
            log.warn(exception.getMessage(), exception);
            throw new NotFoundException(exception);
        }

        if (newUser.getEmail() != null) {
            @Valid
            String newEmail = newUser.getEmail();

            oldUser.setEmail(newEmail);
            log.trace("Email пользователя с ID = {} был изменён", oldUser.getId());
        }

        if (newUser.getLogin() != null) {
            @Valid
            String login = newUser.getLogin();

            oldUser.setLogin(login);
            log.trace("Логин пользователя с ID = {} был изменён", oldUser.getId());
        }

        if (newUser.getBirthday() != null) {
            @Valid
            LocalDate newBirthday = newUser.getBirthday();

            oldUser.setBirthday(newBirthday);
            log.trace("Дата рождения пользователя с ID = {} была изменена", oldUser.getId());
        }

        if (newUser.getName() != null) {
            oldUser.setName(newUser.getName());
            log.trace("Имя пользователя с ID = {} было изменено", oldUser.getId());
        }

        log.debug("Данные пользователя с ID = {} были обнавлены", oldUser.getId());
        return oldUser;
    }

    private long getNextId() {
        long nextId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);

        return ++nextId;
    }

    /*private void validateUser(User user) {
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
    }*/
}
