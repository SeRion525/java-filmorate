package ru.yandex.practicum.filmorate.repository.event;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.feed.Event;
import ru.yandex.practicum.filmorate.model.feed.EventType;
import ru.yandex.practicum.filmorate.model.feed.Operation;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class EventResultSetExtractor implements ResultSetExtractor<Event> {
    @Override
    public Event extractData(ResultSet resultSet) throws SQLException, DataAccessException {
        resultSet.next();
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
