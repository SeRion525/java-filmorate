package ru.yandex.practicum.filmorate.repository.genre;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class GenreResultSetExtractor implements ResultSetExtractor<Genre> {
    @Override
    public Genre extractData(ResultSet resultSet) throws SQLException, DataAccessException {
        resultSet.next();
        Genre genre = new Genre();
        genre.setId(resultSet.getLong("genre_id"));
        genre.setName(resultSet.getString("name"));
        return genre;
    }
}
