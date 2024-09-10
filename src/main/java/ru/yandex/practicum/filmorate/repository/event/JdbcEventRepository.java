package ru.yandex.practicum.filmorate.repository.event;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.feed.Event;
import ru.yandex.practicum.filmorate.repository.JdbcBaseRepository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Repository
public class JdbcEventRepository extends JdbcBaseRepository<Event> implements EventRepository {
    private static final String GET_ALL_QUERY = "SELECT * FROM user_events;";

    private static final String GET_ALL_BY_USER_ID_QUERY = "SELECT * FROM user_events WHERE user_id = :userId;";

    private static final String INSERT_EVENT_QUERY = """
            INSERT INTO user_events(timestamp, type, operation, user_id, entity_id)
            VALUES (:timestamp, :type, :operation, :userId, :entityId);
            """;

    public JdbcEventRepository(NamedParameterJdbcOperations jdbc,
                               ResultSetExtractor<Event> extractor, ResultSetExtractor<List<Event>> extractorToMany) {
        super(jdbc, extractor, extractorToMany);
    }

    @Override
    public Event save(Event event) {
        long id = insert(INSERT_EVENT_QUERY, toMapSqlParameterSource(event));
        event.setEventId(id);
        return event;
    }

    @Override
    public List<Event> getAll() {
        return findAll(GET_ALL_QUERY);
    }

    @Override
    public List<Event> getByUserId(long userId) {
        return findMany(GET_ALL_BY_USER_ID_QUERY, new MapSqlParameterSource("userId", userId));
    }

    private MapSqlParameterSource toMapSqlParameterSource(Event event) {
        MapSqlParameterSource params = new MapSqlParameterSource();

        params.addValue("timestamp", Timestamp.from(Instant.ofEpochMilli(event.getTimestamp())));
        params.addValue("type", event.getEventType().name());
        params.addValue("operation", event.getOperation().name());
        params.addValue("userId", event.getUserId());
        params.addValue("entityId", event.getEntityId());

        return params;
    }
}
