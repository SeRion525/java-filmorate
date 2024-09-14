package ru.yandex.practicum.filmorate.repository.event;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.feed.Event;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class EventListResultSetExtractor extends AbstractEventResultSetExtractor implements ResultSetExtractor<List<Event>> {
    @Override
    public List<Event> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
        List<Event> events = new ArrayList<>();
        while (resultSet.next()) {
            events.add(mapEvent(resultSet));
        }
        return events;
    }
}
