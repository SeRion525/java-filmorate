package ru.yandex.practicum.filmorate.repository.mpa;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class MpaListResultSetExtractor implements ResultSetExtractor<List<Mpa>> {
    @Override
    public List<Mpa> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
        List<Mpa> mpaList = new ArrayList<>();
        while (resultSet.next()) {
            Mpa mpa = new Mpa();
            mpa.setId(resultSet.getLong("mpa_id"));
            mpa.setName(resultSet.getString("name"));
            mpaList.add(mpa);
        }
        return mpaList;
    }
}
