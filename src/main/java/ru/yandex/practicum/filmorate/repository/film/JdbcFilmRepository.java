package ru.yandex.practicum.filmorate.repository.film;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.JdbcBaseRepository;

import java.util.*;

@Repository
public class JdbcFilmRepository extends JdbcBaseRepository<Film> implements FilmRepository {

    private final ResultSetExtractor<Map<Long, Set<Film>>> extractorToMany = new FilmMapResultSetExtractor();

    private static final String DELETE_FILM_QUERY = """
            DELETE FROM films WHERE film_id = :filmId;
            """;

    private static final String DELETE_FILM_GENRES_BY_FILM_ID_QUERY = """
            DELETE FROM films_genres WHERE film_id = :filmId;
            """;

    private static final String GET_ALL_QUERY = """
            SELECT films.*, mpa.name AS mpa_name, genres.genre_id, genres.name AS genre_name,
            directors.director_id, directors.name as director_name FROM films
            LEFT OUTER JOIN mpa ON mpa.mpa_id = films.mpa_id
            LEFT OUTER JOIN films_genres ON films_genres.film_id = films.film_id
            LEFT OUTER JOIN genres ON genres.genre_id = films_genres.genre_id
            LEFT OUTER JOIN director_films ON director_films.film_id = films.film_id
            LEFT OUTER JOIN directors ON directors.director_id = director_films.director_id;
            """;

    private static final String GET_FILM_BY_ID = """
            SELECT films.*, mpa.name AS mpa_name, genres.genre_id, genres.name AS genre_name,
            directors.director_id, directors.name as director_name FROM films
            LEFT OUTER JOIN mpa ON mpa.mpa_id = films.mpa_id
            LEFT OUTER JOIN films_genres ON films_genres.film_id = films.film_id
            LEFT OUTER JOIN genres ON genres.genre_id = films_genres.genre_id
            LEFT OUTER JOIN director_films ON director_films.film_id = films.film_id
            LEFT OUTER JOIN directors ON directors.director_id = director_films.director_id
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

    private static final String INSERT_DIRECTOR_FILMS_BY_IDS = """
            INSERT INTO director_films(film_id, director_id)
            VALUES (:filmId, :directorId);
            """;

    private static final String GET_MOST_POPULAR_QUERY = """
            SELECT FILMS.*, MPA.NAME AS MPA_NAME, GENRES.GENRE_ID, GENRES.NAME AS GENRE_NAME,
            DIRECTORS.DIRECTOR_ID, DIRECTORS.NAME as DIRECTOR_NAME
            FROM FILMS
            LEFT OUTER JOIN MPA ON MPA.MPA_ID = FILMS.MPA_ID
            LEFT OUTER JOIN FILMS_GENRES ON FILMS_GENRES.FILM_ID = FILMS.FILM_ID
            LEFT OUTER JOIN GENRES ON GENRES.GENRE_ID = FILMS_GENRES.GENRE_ID
            LEFT OUTER JOIN DIRECTOR_FILMS ON DIRECTOR_FILMS.FILM_ID = FILMS.FILM_ID
            LEFT OUTER JOIN DIRECTORS ON DIRECTORS.DIRECTOR_ID = DIRECTOR_FILMS.FILM_ID
            JOIN (SELECT films.film_id, COUNT(likes.film_id) AS likes_count FROM films
                LEFT OUTER JOIN likes ON likes.film_id = films.film_id
                GROUP BY films.film_id
                ORDER BY likes_count DESC
                LIMIT :count
            ) AS popular(film_id, likes_count) ON films.film_id = popular.film_id
            ORDER BY popular.likes_count DESC, films.name ASC;
            """;

    private static final String GET_ALL_USERS_LIKED_FILMS_QUERY = """
            SELECT l.user_id, f.*, m.id AS mpa_id, m.name AS mpa_name,
                       g.id AS genre_id, g.name AS genre_name,
                       d.id AS director_id, d.name AS director_name
                FROM likes l
                JOIN films f ON l.film_id = f.id
                LEFT JOIN film_mpa fm ON f.id = fm.film_id
                LEFT JOIN mpa m ON fm.mpa_id = m.id
                LEFT JOIN film_genres fg ON f.id = fg.film_id
                LEFT JOIN genres g ON fg.genre_id = g.id
                LEFT JOIN film_directors fd ON f.id = fd.film_id
                LEFT JOIN directors d ON fd.director_id = d.id
            """;

    private static final String GET_FILMS_BY_DIRECTOR_BY_YEAR = """
            SELECT films.*, mpa.name AS mpa_name, genres.genre_id, genres.name AS genre_name,
            directors.director_id, directors.name as director_name FROM films
            LEFT OUTER JOIN mpa ON mpa.mpa_id = films.mpa_id
            LEFT OUTER JOIN films_genres ON films_genres.film_id = films.film_id
            LEFT OUTER JOIN genres ON genres.genre_id = films_genres.genre_id
            LEFT OUTER JOIN director_films ON director_films.film_id = films.film_id
            LEFT OUTER JOIN directors ON directors.director_id = director_films.director_id
            WHERE director_films.director_id = :directorId
            GROUP BY films.film_id, films.release_date
            ORDER BY films.release_date;
            """;

    private static final String GET_FILMS_BY_DIRECTOR_BY_LIKES = """
            SELECT films.*, mpa.name AS mpa_name, genres.genre_id, genres.name AS genre_name,
                   directors.director_id, directors.name as director_name FROM films
            LEFT OUTER JOIN mpa ON mpa.mpa_id = films.mpa_id
            LEFT OUTER JOIN films_genres ON films_genres.film_id = films.film_id
            LEFT OUTER JOIN genres ON genres.genre_id = films_genres.genre_id
            LEFT OUTER JOIN director_films ON director_films.film_id = films.film_id
            LEFT OUTER JOIN directors ON directors.director_id = director_films.director_id
            LEFT OUTER JOIN likes on films.film_id = likes.film_id
            WHERE director_films.director_id = :directorId
            GROUP BY films.film_id, likes.film_id IN (
                SELECT film_id from likes
                )
            ORDER BY COUNT(likes.film_id) DESC;
            """;

    public JdbcFilmRepository(NamedParameterJdbcOperations jdbc,
                              ResultSetExtractor<Film> extractor, ResultSetExtractor<List<Film>> extractorToList) {
        super(jdbc, extractor, extractorToList);
    }

    @Override
    public Film save(Film film) {
        long id = insert(INSERT_FILM_QUERY, toMapSqlParameterSource(film));
        film.setId(id);

        if (film.getDirectors() != null || !film.getDirectors().isEmpty()) {
            updateDirectorsFilm(film);
        }

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

        if (newFilm.getDirectors() != null) {
            updateDirectorsFilm(newFilm);
        }

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

    @Override
    public List<Film> getDirectorFilmsSortedByYear(long directorId) {
        return findMany(GET_FILMS_BY_DIRECTOR_BY_YEAR, new MapSqlParameterSource("directorId", directorId));
    }

    @Override
    public List<Film> getDirectorFilmsSortedByLikes(long directorId) {
        return findMany(GET_FILMS_BY_DIRECTOR_BY_LIKES, new MapSqlParameterSource("directorId", directorId));
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

    private SqlParameterSource[] getFilmIdAndDirectorIdsSqlParameters(Film film) {
        Set<Director> directors = film.getDirectors();

        return directors.stream()
                .map(director -> new MapSqlParameterSource("filmId", film.getId())
                        .addValue("directorId", director.getId()))
                .toArray(SqlParameterSource[]::new);
    }

    private void updateDirectorsFilm(Film film) {
        addDirectorForCurrentFilm(film);
    }

    private void addDirectorForCurrentFilm(Film film) {
        if (Objects.isNull(film.getDirectors())) {
            return;
        }

        if (film.getDirectors().size() == 1) {
            update(INSERT_DIRECTOR_FILMS_BY_IDS, new MapSqlParameterSource("filmId", film.getId())
                    .addValue("directorId", film.getDirectors().stream().toList().getFirst().getId()));
            return;
        }
        jdbc.batchUpdate(INSERT_DIRECTOR_FILMS_BY_IDS, getFilmIdAndDirectorIdsSqlParameters(film));
    }
@Override
    public Map<Long, Set<Film>> findAllUsersWithLikedFilms() {

        return jdbc.query(GET_ALL_USERS_LIKED_FILMS_QUERY, extractorToMany);
    }
}
