package ru.yandex.practicum.filmorate.repository.mpa;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class MpaResultSetExtractor implements ResultSetExtractor<Mpa> {
    @Override
    public Mpa extractData(ResultSet resultSet) throws SQLException, DataAccessException {
        resultSet.next();
        Mpa mpa = new Mpa();
        mpa.setId(resultSet.getLong("mpa_id"));
        mpa.setName(resultSet.getString("name"));
        return mpa;
    }
}
