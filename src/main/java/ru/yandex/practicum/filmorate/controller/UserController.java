package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserStorage userStorage;
    private final UserService userService;

    @GetMapping
    public List<User> findAll() {
        return userStorage.findAll();
    }

    @GetMapping("/{id}")
    public User findById(@PathVariable long id) {
        return userStorage.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@Valid @RequestBody User user) {
        return userStorage.create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        return userStorage.update(newUser);
    }

    @PutMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addFriend(@PathVariable @Positive long id, @PathVariable @Positive long friendId) {
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable @Positive long id, @PathVariable @Positive long friendId) {
        userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> findAllFriends(@PathVariable @Positive long id) {
        return userService.findAllFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> findCommonFriends(@PathVariable @Positive long id, @PathVariable @Positive long otherId) {
        return userService.findCommonFriends(id, otherId);
    }
}
