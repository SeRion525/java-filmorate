package ru.yandex.practicum.filmorate.storage.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User create(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Создан пользователь {} c ID = {}", user.getLogin(), user.getId());
        return user;
    }

    @Override
    public User update(User newUser) {
        @NotNull
        Long id = newUser.getId();

        User oldUser = users.get(id);

        if (oldUser == null) {
            throw new NotFoundException("Пользователь с ID = " + id + " не найден");
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

    @Override
    public void delete() {

    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User findById(long userId) {
        User user = users.get(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь с ID = " + userId + " не найден");
        } else {
            return user;
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
}
