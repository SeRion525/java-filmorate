package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.feed.Event;
import ru.yandex.practicum.filmorate.model.feed.EventType;
import ru.yandex.practicum.filmorate.model.feed.Operation;
import ru.yandex.practicum.filmorate.repository.event.EventRepository;
import ru.yandex.practicum.filmorate.repository.user.UserRepository;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BaseUserService implements UserService {
    public static final String NOT_FOUND_USER = "Не найден пользователь с ID = ";

    private final UserRepository userRepository;
    private final FilmService filmService;
    private final EventRepository eventRepository;

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
        User user = getUserById(userId);
        return userRepository.getFriends(user.getId());
    }

    @Override
    public void addFriend(long userId, long friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        userRepository.addFriend(user.getId(), friend.getId());

        eventRepository.save(createEvent(Operation.ADD, userId, friendId));
    }

    @Override
    public void removeFriend(long userId, long friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        userRepository.removeFriend(user.getId(), friend.getId());

        eventRepository.save(createEvent(Operation.REMOVE, userId, friendId));
    }

    @Override
    public List<User> getCommonFriends(long id, long otherId) {
        User user = getUserById(id);
        User otherUser = getUserById(otherId);
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
        userRepository.getById(id)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_USER + id));
        return filmService.getRecommendedFilms(id);
    }

    @Override
    public List<Event> getUserFeed(long userId) {
        User user = getUserById(userId);
        return eventRepository.getByUserId(user.getId());
    }

    private Event createEvent(Operation operation, Long userId, Long entityId) {
        Event event = new Event();
        event.setEventType(EventType.FRIEND);
        event.setOperation(operation);
        event.setUserId(userId);
        event.setEntityId(entityId);
        event.setTimestamp(Instant.now().toEpochMilli());
        return event;
    }
}
