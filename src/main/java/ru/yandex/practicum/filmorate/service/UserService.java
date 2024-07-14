package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    public final UserStorage userStorage;

    public List<User> findAllFriends(long userId) {
        User user = userStorage.findById(userId);
        List<User> friends = user.getFriends().stream()
                .map(userStorage::findById)
                .toList();

        log.trace("Список друзей пользователя с ID = {}\n{}", userId, friends);
        return friends;
    }

    public void addFriend(long userId, long friendId) {
        User user = userStorage.findById(userId);
        User friend = userStorage.findById(friendId);

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        log.debug("Пользователи с ID = {} и {} подружились", userId, friendId);
    }

    public void removeFriend(long userId, long friendId) {
        User user = userStorage.findById(userId);
        User friend = userStorage.findById(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        log.debug("Пользователь с ID = {} удалил друга с ID = {}", userId, friendId);
    }

    public List<User> findCommonFriends(long id, long otherId) {
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
