package ru.yandex.practicum.filmorate.repository.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user);

    void update(User newUser);

    List<User> getAll();

    Optional<User> getById(long userId);

    List<User> getFriends(long userId);

    void addFriend(long userId, long friendId);

    void removeFriend(long userId, long friendId);

    List<User> getCommonFriends(long id, long otherId);
}
