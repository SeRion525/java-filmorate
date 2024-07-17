package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.validator.group.Create;
import ru.yandex.practicum.filmorate.validator.group.Default;
import ru.yandex.practicum.filmorate.validator.group.Update;

import java.util.List;

@Service
@Validated
@Slf4j
@RequiredArgsConstructor
public class UserService {
    public final UserStorage userStorage;

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public User findById(@Positive long userId) {
        return userStorage.findById(userId);
    }

    @Validated({Default.class, Create.class})
    public User create(@Valid User user) {
        return userStorage.create(user);
    }

    @Validated({Default.class, Update.class})
    public User update(@Valid User newUser) {
        return userStorage.update(newUser);
    }

    public List<User> findAllFriends(@Positive long userId) {
        User user = userStorage.findById(userId);
        List<User> friends = user.getFriends().stream()
                .map(userStorage::findById)
                .toList();

        log.trace("Список друзей пользователя с ID = {}\n{}", userId, friends);
        return friends;
    }

    public void addFriend(@Positive long userId, @Positive long friendId) {
        User user = userStorage.findById(userId);
        User friend = userStorage.findById(friendId);

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        log.debug("Пользователи с ID = {} и {} подружились", userId, friendId);
    }

    public void removeFriend(@Positive long userId, @Positive long friendId) {
        User user = userStorage.findById(userId);
        User friend = userStorage.findById(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        log.debug("Пользователь с ID = {} удалил друга с ID = {}", userId, friendId);
    }

    public List<User> findCommonFriends(@Positive long id, @Positive long otherId) {
        User user = userStorage.findById(id);
        User otherUser = userStorage.findById(otherId);
        List<User> commonFriends = user.getFriends().stream()
                .filter(friendId -> otherUser.getFriends().contains(friendId))
                .map(userStorage::findById)
                .toList();

        log.trace("Список общих друзей пользователей с ID = {} и {}:\n{}", id, otherId, commonFriends);
        return commonFriends;
    }
}
