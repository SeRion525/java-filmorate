package ru.yandex.practicum.filmorate.repository.event;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.feed.Event;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class EventResultSetExtractor extends AbstractEventResultSetExtractor implements ResultSetExtractor<Event> {
    @Override
    public Event extractData(ResultSet resultSet) throws SQLException, DataAccessException {
        resultSet.next();
        return mapEvent(resultSet);
    }
}
