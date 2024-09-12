package ru.yandex.practicum.filmorate.repository.director;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class DirectorListResultSetExtractor implements ResultSetExtractor<List<Director>> {
    @Override
    public List<Director> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
        List<Director> directorList = new ArrayList<>();
        while (resultSet.next()) {
            Director director = new Director();
            director.setId(resultSet.getLong("director_id"));
            director.setName(resultSet.getString("name"));
            directorList.add(director);
        }
        return directorList;
    }
}
