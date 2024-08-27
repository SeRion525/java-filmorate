package ru.yandex.practicum.filmorate.repository.genre;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class GenreListResultSetExtractor implements ResultSetExtractor<List<Genre>> {
    @Override
    public List<Genre> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
        List<Genre> genres = new ArrayList<>();
        while (resultSet.next()) {
            Genre genre = new Genre();
            genre.setId(resultSet.getLong("genre_id"));
            genre.setName(resultSet.getString("name"));
            genres.add(genre);
        }
        return genres;
    }
}
