package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.film.FilmRepository;
import ru.yandex.practicum.filmorate.repository.user.UserRepository;

import java.util.Collection;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BaseUserService implements UserService {
    public static final String NOT_FOUND_USER = "Не найден пользователь с ID = ";

    private final UserRepository userRepository;
    private final FilmService filmService;

    @Override
    public List<User> getUsers() {
        return userRepository.getAll();
    }

    @Override
    public User getUserById(long userId) {
        return userRepository.getById(userId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_USER + userId));
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public User update(User newUser) {
        User savedUser = userRepository.getById(newUser.getId())
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_USER + newUser.getId()));

        savedUser.setLogin(newUser.getLogin());
        savedUser.setName(newUser.getName());
        savedUser.setEmail(newUser.getEmail());
        savedUser.setBirthday(newUser.getBirthday());

        userRepository.update(savedUser);
        return savedUser;
    }

    @Override
    public List<User> getFriends(long userId) {
        User user = userRepository.getById(userId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_USER + userId));
        return userRepository.getFriends(user.getId());
    }

    @Override
    public void addFriend(long userId, long friendId) {
        User user = userRepository.getById(userId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_USER + userId));
        User friend = userRepository.getById(friendId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_USER + friendId));
        userRepository.addFriend(user.getId(), friend.getId());
    }

    @Override
    public void removeFriend(long userId, long friendId) {
        User user = userRepository.getById(userId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_USER + userId));
        User friend = userRepository.getById(friendId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_USER + friendId));
        userRepository.removeFriend(user.getId(), friend.getId());
    }

    @Override
    public List<User> getCommonFriends(long id, long otherId) {
        User user = userRepository.getById(id)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_USER + id));
        User otherUser = userRepository.getById(otherId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_USER + otherId));
        return userRepository.getCommonFriends(user.getId(), otherUser.getId());
    }

    public void deleteUser(long userId) {
        if (userRepository.getById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь с данным ID не найден.");
        }
        userRepository.delete(userId);
    }

    @Override
    public Collection<Film> getUserRecommendations(long id) {
        /*if (userRepository.getById(id).isEmpty()) {
            throw new NotFoundException("Пользователь с данным ID не найден.");
        }*/
        return filmService.getRecommendedFilms(id);
    }

}
