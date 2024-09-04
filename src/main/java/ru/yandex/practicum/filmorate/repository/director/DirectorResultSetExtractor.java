package ru.yandex.practicum.filmorate.repository.director;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class DirectorResultSetExtractor implements ResultSetExtractor<Director> {
    @Override
    public Director extractData(ResultSet resultSet) throws SQLException, DataAccessException {
        resultSet.next();
        Director director = new Director();
        director.setId(resultSet.getLong("director_id"));
        director.setName(resultSet.getString("name"));
        return director;
    }
}
