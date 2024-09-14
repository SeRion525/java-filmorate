package ru.yandex.practicum.filmorate.repository.film;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
public class FilmListResultSetExtractor extends AbstractFilmResultExtractor implements ResultSetExtractor<List<Film>> {
    @Override
    public List<Film> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
        List<Film> films = new ArrayList<>();
        Film currentFilm = null;
        Set<Genre> genres = new LinkedHashSet<>();
        Set<Director> directors = new LinkedHashSet<>();

        while (resultSet.next()) {
            long filmId = resultSet.getLong("film_id");
            if (currentFilm == null) {
                currentFilm = mapFilm(resultSet);
            } else if (currentFilm.getId() != filmId) {
                currentFilm.setGenres(genres);
                currentFilm.setDirectors(directors);
                films.add(currentFilm);

                genres = new LinkedHashSet<>();
                directors = new LinkedHashSet<>();
                currentFilm = mapFilm(resultSet);
            }

            Genre genre = mapGenre(resultSet);
            if (genre != null) {
                genres.add(genre);
            }

            Director director = mapDirector(resultSet);
            if (director != null) {
                directors.add(director);
            }
        }

        if (currentFilm != null) {
            currentFilm.setGenres(genres);
            currentFilm.setDirectors(directors);
            films.add(currentFilm);
        }

        return films;
    }
}
