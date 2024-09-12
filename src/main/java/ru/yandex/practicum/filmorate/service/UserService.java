package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.feed.Event;

import java.util.List;
import java.util.Set;

public interface UserService {
    List<User> getUsers();

    User getUserById(long userId);

    User save(User user);

    User update(User newUser);

    List<User> getFriends(long userId);

    void addFriend(long userId, long friendId);

    void removeFriend(long userId, long friendId);

    List<User> getCommonFriends(long id, long otherId);

    void deleteUser(long userId);

    Set<Film> getUserRecommendations(long id);

    List<Event> getUserFeed(long userId);
}
