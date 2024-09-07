package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class JdbcBaseRepository<T> {
    protected final NamedParameterJdbcOperations jdbc;
    protected final ResultSetExtractor<T> extractor;
    protected final ResultSetExtractor<List<T>> extractorToMany;

    protected Optional<T> findOne(String query, SqlParameterSource params) {
        try {
            T result = jdbc.query(query, params, extractor);
            return Optional.ofNullable(result);
        } catch (NonTransientDataAccessException ignored) {
            return Optional.empty();
        }
    }

    protected List<T> findMany(String query, SqlParameterSource params) {
        return jdbc.query(query, params, extractorToMany);
    }

    protected List<T> findAll(String query) {
        return jdbc.query(query, extractorToMany);
    }

    protected void update(String query, SqlParameterSource params) {
        int rowsUpdated = jdbc.update(query, params);
        if (rowsUpdated == 0) {
            throw new RuntimeException("Не удалось обновить данные");
        }
    }

    protected int merge(String query, SqlParameterSource params) {
        return jdbc.update(query, params);
    }

    protected long insert(String query, SqlParameterSource params) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(query, params, keyHolder);
        Long id = keyHolder.getKeyAs(Long.class);

        if (id != null) {
            return id;
        } else {
            throw new RuntimeException("Не удалось сохранить данные");
        }
    }
}
