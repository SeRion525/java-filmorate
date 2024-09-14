package ru.yandex.practicum.filmorate.repository.review;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class ReviewListResultSetExtractor extends AbstractReviewResultSetExtractor implements ResultSetExtractor<List<Review>> {
    @Override
    public List<Review> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
        List<Review> reviews = new ArrayList<>();
        while (resultSet.next()) {
            reviews.add(mapReview(resultSet));
        }
        return reviews;
    }
}
