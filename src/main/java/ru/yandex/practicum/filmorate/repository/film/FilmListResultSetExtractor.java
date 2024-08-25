package ru.yandex.practicum.filmorate.repository.film;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

@Component
public class FilmListResultSetExtractor implements ResultSetExtractor<List<Film>> {
    @Override
    public List<Film> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
        List<Film> films = new ArrayList<>();
        Film currentFilm = null;
        LinkedHashSet<Genre> genres = new LinkedHashSet<>();

        while (resultSet.next()) {
            long filmId = resultSet.getLong("film_id");
            if (currentFilm == null) {
                currentFilm = mapFilm(resultSet);
            } else if (currentFilm.getId() != filmId) {
                currentFilm.setGenres(genres);
                films.add(currentFilm);

                genres = new LinkedHashSet<>();
                currentFilm = mapFilm(resultSet);
            }

            Genre genre = mapGenre(resultSet);
            if (genre != null) {
                genres.add(genre);
            }

        }

        if (currentFilm != null) {
            currentFilm.setGenres(genres);
            films.add(currentFilm);
        }

        return films;
    }

    private Film mapFilm(ResultSet resultSet) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getLong("film_id"));
        film.setName(resultSet.getString("name"));
        film.setDescription(resultSet.getString("description"));

        Date releaseDate = resultSet.getDate("release_date");
        if (releaseDate != null) {
            film.setReleaseDate(releaseDate.toLocalDate());
        }

        film.setDuration(resultSet.getInt("duration"));

        Long mpaId = resultSet.getLong("mpa_id");

        if (!resultSet.wasNull()) {
            Mpa mpa = new Mpa();
            mpa.setId(mpaId);
            mpa.setName(resultSet.getString("mpa_name"));
            film.setMpa(mpa);
        }

        return film;
    }

    private Genre mapGenre(ResultSet resultSet) throws SQLException {
        long genreId = resultSet.getLong("genre_id");
        Genre genre = null;
        if (!resultSet.wasNull()) {
            genre = new Genre();
            genre.setId(genreId);
            genre.setName(resultSet.getString("genre_name"));
        }

        return genre;
    }
}
