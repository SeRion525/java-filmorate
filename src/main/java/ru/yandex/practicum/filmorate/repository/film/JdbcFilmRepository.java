package ru.yandex.practicum.filmorate.repository.film;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.JdbcBaseRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public class JdbcFilmRepository extends JdbcBaseRepository<Film> implements FilmRepository {


    private static final String DELETE_FILM_QUERY = """
            DELETE FROM films WHERE film_id = :filmId;
            """;

    private static final String DELETE_FILM_GENRES_BY_FILM_ID_QUERY = """
            DELETE FROM films_genres WHERE film_id = :filmId;
            """;
    
    private static final String GET_ALL_QUERY = """
            SELECT films.*, mpa.name AS mpa_name, genres.genre_id, genres.name AS genre_name FROM films
            LEFT OUTER JOIN mpa ON mpa.mpa_id = films.mpa_id
            LEFT OUTER JOIN films_genres ON films_genres.film_id = films.film_id
            LEFT OUTER JOIN genres ON genres.genre_id = films_genres.genre_id;
            """;

    private static final String GET_FILM_BY_ID = """
            SELECT films.*, mpa.name AS mpa_name, genres.genre_id, genres.name AS genre_name FROM films
            LEFT OUTER JOIN mpa ON mpa.mpa_id = films.mpa_id
            LEFT OUTER JOIN films_genres ON films_genres.film_id = films.film_id
            LEFT OUTER JOIN genres ON genres.genre_id = films_genres.genre_id
            WHERE films.film_id = :filmId;
            """;

    private static final String INSERT_FILM_QUERY = """
            INSERT INTO films(name, description, release_date, duration, mpa_id)
            VALUES (:name, :desc, :date, :dur, :mpa);
            """;

    private static final String INSERT_FILMS_GENRES_BY_FILM_ID_QUERY = """
            INSERT INTO films_genres(film_id, genre_id)
            VALUES (:filmId, :genreId);
            """;

    private static final String UPDATE_FILM_QUERY = """
            UPDATE films
            SET name = :name, description = :desc, release_date = :date, duration = :dur, mpa_id = :mpa
            WHERE film_id = :filmId
            """;

    private static final String ADD_LIKE_QUERY = """
            MERGE INTO likes AS t
            USING (VALUES(:filmId, :userId)) AS s(film_id, user_id) ON s.user_id = t.user_id AND s.film_id = t.film_id
            WHEN NOT MATCHED THEN INSERT VALUES(s.film_id, s.user_id);
            """;

    private static final String DELETE_LIKE_QUERY = "DELETE FROM likes WHERE film_id = :filmId AND user_id = :userId;";

    private static final String GET_LIKES_BY_FILM_ID_QUERY = "SELECT COUNT(*) AS likes_count FROM likes WHERE film_id = :filmId";

    private static final String GET_MOST_POPULAR_QUERY = """
            SELECT FILMS.*, MPA.NAME AS MPA_NAME, GENRES.GENRE_ID, GENRES.NAME AS GENRE_NAME
            FROM FILMS
            LEFT OUTER JOIN MPA ON MPA.MPA_ID = FILMS.MPA_ID
            LEFT OUTER JOIN FILMS_GENRES ON FILMS_GENRES.FILM_ID = FILMS.FILM_ID
            LEFT OUTER JOIN GENRES ON GENRES.GENRE_ID = FILMS_GENRES.GENRE_ID
            JOIN (SELECT films.film_id, COUNT(likes.film_id) AS likes_count FROM films
                LEFT OUTER JOIN likes ON likes.film_id = films.film_id
                GROUP BY films.film_id
                ORDER BY likes_count DESC
                LIMIT :count
            ) AS popular(film_id, likes_count) ON films.film_id = popular.film_id
            ORDER BY popular.likes_count DESC, films.name ASC;
            """;

    public JdbcFilmRepository(NamedParameterJdbcOperations jdbc,
                              ResultSetExtractor<Film> extractor, ResultSetExtractor<List<Film>> extractorToList) {
        super(jdbc, extractor, extractorToList);
    }

    @Override
    public Film save(Film film) {
        long id = insert(INSERT_FILM_QUERY, toMapSqlParameterSource(film));
        film.setId(id);

        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            return film;
        }

        jdbc.batchUpdate(INSERT_FILMS_GENRES_BY_FILM_ID_QUERY, getFilmIdAndGenreIdsSqlParameters(film));
        return film;
    }

    @Override
    public void update(Film newFilm) {
        MapSqlParameterSource params = toMapSqlParameterSource(newFilm);
        update(UPDATE_FILM_QUERY, params);

        if (newFilm.getGenres() == null || newFilm.getGenres().isEmpty()) {
            return;
        }

        update(DELETE_FILM_GENRES_BY_FILM_ID_QUERY, new MapSqlParameterSource("filmId", newFilm.getId()));
        jdbc.batchUpdate(INSERT_FILMS_GENRES_BY_FILM_ID_QUERY, getFilmIdAndGenreIdsSqlParameters(newFilm));
    }

    @Override
    public List<Film> getAll() {
        return findAll(GET_ALL_QUERY);
    }

    @Override
    public Optional<Film> getById(long filmId) {
        return findOne(GET_FILM_BY_ID, new MapSqlParameterSource("filmId", filmId));
    }

    @Override
    public void addLike(long filmId, long userId) {
        merge(ADD_LIKE_QUERY, new MapSqlParameterSource("filmId", filmId)
                .addValue("userId", userId));
    }

    @Override
    public void deleteLike(long filmId, long userId) {
        merge(DELETE_LIKE_QUERY, new MapSqlParameterSource("filmId", filmId)
                .addValue("userId", userId));
    }

    @Override
    public int getLikesByFilmId(long filmId) {
        return jdbc.query(GET_LIKES_BY_FILM_ID_QUERY, new MapSqlParameterSource("filmId", filmId),
                        (rs, rowNum) -> rs.getInt("likes_count"))
                .getFirst();
    }

    @Override
    public List<Film> getMostPopular(int count) {
        return findMany(GET_MOST_POPULAR_QUERY, new MapSqlParameterSource("count", count));
    }

    private MapSqlParameterSource toMapSqlParameterSource(Film film) {
        MapSqlParameterSource params = new MapSqlParameterSource();

        if (film.getId() != null) {
            params.addValue("filmId", film.getId());
        }

        params.addValue("name", film.getName());
        params.addValue("desc", film.getDescription());
        params.addValue("date", film.getReleaseDate());
        params.addValue("dur", film.getDuration());

        if (film.getMpa() != null) {
            params.addValue("mpa", film.getMpa().getId());
        } else {
            params.addValue("mpa", null);
        }

        return params;
    }

    private SqlParameterSource[] getFilmIdAndGenreIdsSqlParameters(Film film) {
        Set<Genre> genres = film.getGenres();

        return genres.stream()
                .map(genre -> new MapSqlParameterSource("filmId", film.getId())
                        .addValue("genreId", genre.getId()))
                .toArray(SqlParameterSource[]::new);
    }

    @Override
    public void delete(long filmId) {
        jdbc.update(DELETE_FILM_GENRES_BY_FILM_ID_QUERY, new MapSqlParameterSource("filmId", filmId));
        jdbc.update(DELETE_FILM_QUERY, new MapSqlParameterSource("filmId", filmId));
    }

}
