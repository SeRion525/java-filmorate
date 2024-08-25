package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.user.UserRepository;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BaseUserService implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<User> getUsers() {
        return userRepository.getAll();
    }

    @Override
    public User getUserById(long userId) {
        return userRepository.getById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с ID = " + userId));
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public User update(User newUser) {
        User savedUser = userRepository.getById(newUser.getId())
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с ID = " + newUser.getId()));

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
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с ID = " + userId));
        return userRepository.getFriends(userId);
    }

    @Override
    public void addFriend(long userId, long friendId) {
        User user = userRepository.getById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с ID = " + userId));
        User friend = userRepository.getById(friendId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с ID = " + friendId));
        userRepository.addFriend(user.getId(), friend.getId());
    }

    @Override
    public void removeFriend(long userId, long friendId) {
        User user = userRepository.getById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с ID = " + userId));
        User friend = userRepository.getById(friendId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с ID = " + friendId));
        userRepository.removeFriend(user.getId(), friend.getId());
    }

    @Override
    public List<User> getCommonFriends(long id, long otherId) {
        User user = userRepository.getById(id)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с ID = " + id));
        User otherUser = userRepository.getById(otherId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с ID = " + otherId));
        return userRepository.getCommonFriends(user.getId(), otherUser.getId());
    }
}
