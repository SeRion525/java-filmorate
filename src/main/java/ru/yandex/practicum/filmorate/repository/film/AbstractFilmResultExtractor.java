package ru.yandex.practicum.filmorate.repository.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class AbstractFilmResultExtractor {
    protected Film mapFilm(ResultSet resultSet) throws SQLException {
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

    protected Genre mapGenre(ResultSet resultSet) throws SQLException {
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
