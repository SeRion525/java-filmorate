package ru.yandex.practicum.filmorate.repository.director;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.repository.JdbcBaseRepository;

import java.util.List;
import java.util.Optional;

@Repository
public class JdbcDirectorRepository extends JdbcBaseRepository<Director> implements DirectorRepository {
    private static final String INSERT_DIRECTOR_QUERY = """
            INSERT
            INTO directors (name)
            VALUES (:name);
            """;

    private static final String GET_ALL_QUERY = """
            SELECT *
            FROM directors;
            """;

    private static final String GET_BY_ID_QUERY = """
            SELECT *
            FROM directors
            WHERE director_id = :directorId;
            """;

    private static final String GET_ALL_BY_IDS_QUERY = """
            SELECT *
            FROM directors
            WHERE director_id IN (:directorIds);
            """;


    private static final String UPDATE_DIRECTOR_QUERY = """
            UPDATE directors
            SET name = :name
            WHERE director_id = :directorId;
            """;

    private static final String DELETE_DIRECTOR_QUERY = """
            DELETE
            FROM directors
            WHERE director_id = :directorId;
            """;

    public JdbcDirectorRepository(NamedParameterJdbcOperations jdbc, ResultSetExtractor<Director> extractor,
                                  ResultSetExtractor<List<Director>> extractorToList) {
        super(jdbc, extractor, extractorToList);
    }

    @Override
    public Director save(Director director) {
        long id = insert(INSERT_DIRECTOR_QUERY, toMapSqlParameterSource(director));
        director.setId(id);
        return director;
    }

    @Override
    public List<Director> getAll() {
        return findAll(GET_ALL_QUERY);
    }

    @Override
    public Optional<Director> getById(long directorId) {
        return findOne(GET_BY_ID_QUERY, new MapSqlParameterSource("directorId", directorId));
    }

    @Override
    public List<Director> getByIds(List<Long> genreIds) {
        return findMany(GET_ALL_BY_IDS_QUERY, new MapSqlParameterSource("directorIds", genreIds));
    }

    @Override
    public void update(Director newDirector) {
        MapSqlParameterSource params = toMapSqlParameterSource(newDirector);
        update(UPDATE_DIRECTOR_QUERY, params);
    }

    @Override
    public void delete(long id) {
        update(DELETE_DIRECTOR_QUERY, new MapSqlParameterSource("directorId", id));
    }

    private MapSqlParameterSource toMapSqlParameterSource(Director director) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("directorId", director.getId());
        params.addValue("name", director.getName());

        return params;
    }
}
