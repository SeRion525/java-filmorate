package ru.yandex.practicum.filmorate.repository.film;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Set;

@Component
public class FilmResultSetExtractor extends AbstractFilmResultExtractor implements ResultSetExtractor<Film> {
    @Override
    public Film extractData(ResultSet resultSet) throws SQLException, DataAccessException {
        Film currentFilm = null;
        Set<Genre> genres = new LinkedHashSet<>();

        while (resultSet.next()) {
            if (currentFilm == null) {
                currentFilm = mapFilm(resultSet);
            }

            Genre genre = mapGenre(resultSet);
            if (genre != null) {
                genres.add(genre);
            }
        }

        if (currentFilm != null) {
            currentFilm.setGenres(genres);
        }

        return currentFilm;
    }
}
