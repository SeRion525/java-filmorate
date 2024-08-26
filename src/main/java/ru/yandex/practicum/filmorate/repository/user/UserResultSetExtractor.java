package ru.yandex.practicum.filmorate.repository.user;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class UserResultSetExtractor extends AbstractUserResultSetExtractor implements ResultSetExtractor<User> {
    @Override
    public User extractData(ResultSet resultSet) throws SQLException, DataAccessException {
        resultSet.next();
        return mapUser(resultSet);
    }
}
