package ru.yandex.practicum.filmorate.repository.event;

import ru.yandex.practicum.filmorate.model.feed.Event;

import java.util.List;

public interface EventRepository {
    Event save(Event event);

    List<Event> getAll();

    List<Event> getByUserId(long userId);
}
