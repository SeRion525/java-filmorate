package ru.yandex.practicum.filmorate.repository.mpa;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.repository.JdbcBaseRepository;

import java.util.List;
import java.util.Optional;

@Repository
public class JdbcMpaRepository extends JdbcBaseRepository<Mpa> implements MpaRepository {
    private static final String GET_ALL_QUERY = "SELECT * FROM mpa;";
    private static final String GET_BY_ID_QUERY = "SELECT * FROM mpa WHERE mpa_id = :mpaId;";

    public JdbcMpaRepository(NamedParameterJdbcOperations jdbc,
                             ResultSetExtractor<Mpa> extractor, ResultSetExtractor<List<Mpa>> extractorToList) {
        super(jdbc, extractor, extractorToList);
    }

    @Override
    public Optional<Mpa> getById(long mpaId) {
        return findOne(GET_BY_ID_QUERY, new MapSqlParameterSource("mpaId", mpaId));
    }

    @Override
    public List<Mpa> getAll() {
        return findAll(GET_ALL_QUERY);
    }
}
