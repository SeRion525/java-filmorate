package ru.yandex.practicum.filmorate.repository.user;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.JdbcBaseRepository;

import java.util.List;
import java.util.Optional;

@Repository
public class JdbcUserRepository extends JdbcBaseRepository<User> implements UserRepository {
    private static final String GET_ALL_QUERY = "SELECT users.* FROM users;";

    private static final String GET_BY_ID_QUERY = "SELECT users.* FROM users WHERE user_id = :userId";

    private static final String INSERT_USER_QUERY = "INSERT INTO users(login, email, name, birthday) " +
            "VALUES (:login, :email, :name, :birth);";

    private static final String UPDATE_USER_QUERY = "UPDATE users " +
            "SET login = :login, email = :email, name = :name, birthday = :birth " +
            "WHERE user_id = :userId;";

    private static final String GET_FRIENDS_BY_USER_ID_QUERY = "SELECT users.* FROM users " +
            "JOIN friends ON friends.friend_id = users.user_id " +
            "WHERE friends.user_id = :userId;";

    private static final String ADD_FRIEND_QUERY = "MERGE INTO friends AS f " +
            "USING (VALUES(:userId, :friendId)) AS s(user_id, friend_id) " +
            "ON s.user_id = f.user_id AND s.friend_id = f.friend_id " +
            "WHEN NOT MATCHED THEN " +
            "INSERT VALUES (s.user_id, s.friend_id);";

    private static final String REMOVE_FRIEND_QUERY = "DELETE FROM friends WHERE user_id = :userId AND friend_id = :friendId";

    private static final String GET_COMMON_FRIENDS_QUERY = "SELECT users.* FROM users " +
            "WHERE user_id IN (" +
            "SELECT friend_id FROM friends " +
            "WHERE user_id = :id " +
            "INTERSECT " +
            "SELECT friend_id FROM friends " +
            "WHERE user_id = :otherId" +
            ");";

    private static final String INSERT_FRIENDS_BY_USER_ID_QUERY = "INSERT friends(user_id, friend_id, status) " +
            "VALUES (:userId, :friendId, :status);";

    private static final String DELETE_FRIEND_BY_USER_ID_QUERY = "DELETE FROM friends WHERE user_id = :userId";

    public JdbcUserRepository(NamedParameterJdbcOperations jdbc,
                              ResultSetExtractor<User> extractor, ResultSetExtractor<List<User>> extractorToList) {
        super(jdbc, extractor, extractorToList);
    }

    @Override
    public User save(User user) {
        long id = insert(INSERT_USER_QUERY, toMapSqlParameterSource(user));
        user.setId(id);
        return user;
    }

    @Override
    public void update(User newUser) {
        MapSqlParameterSource params = toMapSqlParameterSource(newUser);
        update(UPDATE_USER_QUERY, params);
    }

    @Override
    public List<User> getAll() {
        return findAll(GET_ALL_QUERY);
    }

    @Override
    public Optional<User> getById(long userId) {
        return findOne(GET_BY_ID_QUERY, new MapSqlParameterSource("userId", userId));
    }

    @Override
    public List<User> getFriends(long userId) {
        return findMany(GET_FRIENDS_BY_USER_ID_QUERY, new MapSqlParameterSource("userId", userId));
    }

    @Override
    public void addFriend(long userId, long friendId) {
        merge(ADD_FRIEND_QUERY, new MapSqlParameterSource("userId", userId)
                .addValue("friendId", friendId));
    }

    @Override
    public void removeFriend(long userId, long friendId) {
        merge(REMOVE_FRIEND_QUERY, new MapSqlParameterSource("userId", userId)
                .addValue("friendId", friendId));
    }

    @Override
    public List<User> getCommonFriends(long id, long otherId) {
        return findMany(GET_COMMON_FRIENDS_QUERY, new MapSqlParameterSource("id", id)
                .addValue("otherId", otherId));
    }

    private MapSqlParameterSource toMapSqlParameterSource(User user) {
        MapSqlParameterSource params = new MapSqlParameterSource();

        if (user.getId() != null) {
            params.addValue("userId", user.getId());
        }

        params.addValue("login", user.getLogin());
        params.addValue("email", user.getEmail());
        params.addValue("name", user.getName());
        params.addValue("birth", user.getBirthday());

        return params;
    }
}
