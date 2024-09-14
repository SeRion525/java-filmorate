package ru.yandex.practicum.filmorate.repository.event;

import org.springframework.dao.DataAccessException;
import ru.yandex.practicum.filmorate.model.feed.Event;
import ru.yandex.practicum.filmorate.model.feed.EventType;
import ru.yandex.practicum.filmorate.model.feed.Operation;

import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class AbstractEventResultSetExtractor {
    protected Event mapEvent(ResultSet resultSet) throws SQLException, DataAccessException {
        Event event = new Event();
        event.setEventId(resultSet.getLong("event_id"));
        event.setTimestamp(resultSet.getTimestamp("timestamp").toInstant().toEpochMilli());
        event.setEventType(EventType.valueOf(resultSet.getString("type")));
        event.setOperation(Operation.valueOf(resultSet.getString("operation")));
        event.setUserId(resultSet.getLong("user_id"));
        event.setEntityId(resultSet.getLong("entity_id"));
        return event;
    }
}
