package ru.yandex.practicum.filmorate.repository.film;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@Component
public class FilmMapResultSetExtractor extends AbstractFilmResultExtractor implements ResultSetExtractor<Map<Long, Set<Film>>> {

    @Override
    public Map<Long, Set<Film>> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Long, Set<Film>> usersLikedFilmsMap = new HashMap<>();
        Film currentFilm = null;
        Set<Genre> genres = new LinkedHashSet<>();
        Set<Director> directors = new LinkedHashSet<>();

        while (rs.next()) {
            long userId = rs.getLong("user_id");
            long filmId = rs.getLong("film_id");


            if (currentFilm == null || currentFilm.getId() != filmId) {

                if (currentFilm != null) {
                    currentFilm.setGenres(genres);
                    currentFilm.setDirectors(directors);

                    usersLikedFilmsMap.computeIfAbsent(userId, k -> new LinkedHashSet<>()).add(currentFilm);
                }

                currentFilm = mapFilm(rs);
                genres = new LinkedHashSet<>();
                directors = new LinkedHashSet<>();
            }

            // Добавляем жанры и режиссёров
            Genre genre = mapGenre(rs);
            if (genre != null) {
                genres.add(genre);
            }

            Director director = mapDirector(rs);
            if (director != null) {
                directors.add(director);
            }
        }


        if (currentFilm != null) {
            currentFilm.setGenres(genres);
            currentFilm.setDirectors(directors);

            long userId = rs.getLong("user_id");
            usersLikedFilmsMap.computeIfAbsent(userId, k -> new LinkedHashSet<>()).add(currentFilm);
        }

        return usersLikedFilmsMap;
    }
}

