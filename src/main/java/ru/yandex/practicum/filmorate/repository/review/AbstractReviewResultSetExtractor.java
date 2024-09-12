package ru.yandex.practicum.filmorate.repository.review;

import org.springframework.dao.DataAccessException;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class AbstractReviewResultSetExtractor {
    protected Review mapReview(ResultSet resultSet) throws SQLException, DataAccessException {
        Review review = new Review();
        review.setReviewId(resultSet.getLong("review_id"));
        review.setContent(resultSet.getString("content"));
        review.setIsPositive(resultSet.getBoolean("is_positive"));
        review.setUserId(resultSet.getLong("user_id"));
        review.setFilmId(resultSet.getLong("film_id"));
        review.setUseful(resultSet.getInt("useful"));
        return review;
    }
}
