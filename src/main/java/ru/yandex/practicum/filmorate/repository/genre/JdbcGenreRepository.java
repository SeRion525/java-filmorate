package ru.yandex.practicum.filmorate.repository.genre;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.JdbcBaseRepository;

import java.util.List;
import java.util.Optional;

@Repository
public class JdbcGenreRepository extends JdbcBaseRepository<Genre> implements GenreRepository {
    private static final String GET_ALL_QUERY = "SELECT * FROM genres";
    private static final String GET_BY_ID_QUERY = "SELECT * FROM genres WHERE genre_id = :genreId";
    private static final String GET_ALL_BY_IDS_QUERY = "SELECT * FROM genres WHERE genre_id IN (:genreIds);";

    public JdbcGenreRepository(NamedParameterJdbcOperations jdbc,
                               ResultSetExtractor<Genre> extractor, ResultSetExtractor<List<Genre>> extractorToList) {
        super(jdbc, extractor, extractorToList);
    }

    @Override
    public List<Genre> getAll() {
        return findAll(GET_ALL_QUERY);
    }

    @Override
    public Optional<Genre> getById(long genreId) {
        SqlParameterSource params = new MapSqlParameterSource("genreId", genreId);
        return findOne(GET_BY_ID_QUERY, params);
    }

    @Override
    public List<Genre> getByIds(List<Long> genreIds) {
        return findMany(GET_ALL_BY_IDS_QUERY, new MapSqlParameterSource("genreIds", genreIds));
    }
}
