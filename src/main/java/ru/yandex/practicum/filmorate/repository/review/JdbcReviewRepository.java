package ru.yandex.practicum.filmorate.repository.review;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.repository.JdbcBaseRepository;

import java.util.List;
import java.util.Optional;

@Repository
public class JdbcReviewRepository extends JdbcBaseRepository<Review> implements ReviewRepository {
    private static final String ADD_REVIEW_QUERY = """
            INSERT INTO reviews(content, is_positive, user_id, film_id)
            VALUES (:content, :isPositive, :userId, :filmId);
            """;

    private static final String UPDATE_REVIEW_QUERY = """
            UPDATE reviews
            SET content = :content, is_positive = :isPositive
            WHERE review_id = :reviewId;
            """;

    private static final String DELETE_REVIEW_QUERY = "DELETE FROM reviews WHERE review_id = :reviewId";

    private static final String GET_ALL_QUERY = "SELECT * FROM reviews LIMIT :count;";

    private static final String GET_BY_ID = "SELECT * FROM reviews WHERE review_id = :reviewId;";

    private static final String GET_BY_FILM_ID = "SELECT * FROM reviews WHERE film_id = :filmId LIMIT :count;";

    private static final String ADD_LIKE_QUERY = """
            MERGE INTO reviews_likes AS rl
            USING (VALUES(:reviewId, :userId)) AS v(review_id, user_id) ON v.review_id = rl.review_id AND v.user_id = rl.user_id
            WHEN NOT MATCHED THEN INSERT VALUES(v.review_id, v.user_id, TRUE)
            WHEN MATCHED AND is_like IS FALSE THEN UPDATE SET is_like = TRUE;
            """;

    private static final String ADD_DISLIKE_QUERY = """
            MERGE INTO reviews_likes AS rl
            USING (VALUES(:reviewId, :userId)) AS v(review_id, user_id) ON v.review_id = rl.review_id AND v.user_id = rl.user_id
            WHEN NOT MATCHED THEN INSERT VALUES(v.review_id, v.user_id, FALSE)
            WHEN MATCHED AND is_like IS TRUE THEN UPDATE SET is_like = FALSE;
            """;

    private static final String DELETE_LIKE_QUERY = """
            DELETE FROM reviews_likes
            WHERE review_id = :reviewId AND user_id = :userId AND is_like IS TRUE;
            """;

    private static final String DELETE_DISLIKE_QUERY = """
            DELETE FROM reviews_likes
            WHERE review_id = :reviewId AND user_id = :userId AND is_like IS FALSE;
            """;

    private static final String COUNT_USEFUL_QUERY = """
            UPDATE reviews
            SET useful = (
            	SELECT (COUNT(*) FILTER (WHERE is_like IS TRUE)) - (COUNT(*) FILTER(WHERE is_like IS FALSE)) FROM reviews_likes
            	WHERE review_id = :reviewId
            )
            WHERE review_id = :reviewId;
            """;

    public JdbcReviewRepository(NamedParameterJdbcOperations jdbc,
                                ResultSetExtractor<Review> extractor, ResultSetExtractor<List<Review>> extractorToMany) {
        super(jdbc, extractor, extractorToMany);
    }

    @Override
    public Review save(Review review) {
        long id = insert(ADD_REVIEW_QUERY, toMapSqlParameterSource(review));
        review.setReviewId(id);
        return review;
    }

    @Override
    public void update(Review newReview) {
        update(UPDATE_REVIEW_QUERY, toMapSqlParameterSource(newReview));
    }

    @Override
    public void delete(long reviewId) {
        update(DELETE_REVIEW_QUERY, new MapSqlParameterSource("reviewId", reviewId));
    }

    @Override
    public Optional<Review> getById(long reviewId) {
        return findOne(GET_BY_ID, new MapSqlParameterSource("reviewId", reviewId));
    }

    @Override
    public List<Review> getAll(int count) {
        return findMany(GET_ALL_QUERY, new MapSqlParameterSource("count", count));
    }

    @Override
    public List<Review> getAllByFilmId(long filmId, int count) {
        return findMany(GET_BY_FILM_ID, new MapSqlParameterSource("filmId", filmId)
                .addValue("count", count));
    }

    @Override
    public void addLike(long reviewId, long userId) {
        int rowsUpdated = merge(ADD_LIKE_QUERY, new MapSqlParameterSource("reviewId", reviewId)
                .addValue("userId", userId));

        if (rowsUpdated > 0) {
            update(COUNT_USEFUL_QUERY, new MapSqlParameterSource("reviewId", reviewId));
        }
    }

    @Override
    public void addDislike(long reviewId, long userId) {
        int rowsUpdated = merge(ADD_DISLIKE_QUERY, new MapSqlParameterSource("reviewId", reviewId)
                .addValue("userId", userId));

        if (rowsUpdated > 0) {
            update(COUNT_USEFUL_QUERY, new MapSqlParameterSource("reviewId", reviewId));
        }
    }

    @Override
    public void removeLike(long reviewId, long userId) {
        int rowsUpdated = merge(DELETE_LIKE_QUERY, new MapSqlParameterSource("reviewId", reviewId)
                .addValue("userId", userId));

        if (rowsUpdated > 0) {
            update(COUNT_USEFUL_QUERY, new MapSqlParameterSource("reviewId", reviewId));
        }
    }

    @Override
    public void removeDislike(long reviewId, long userId) {
        int rowsUpdated = merge(DELETE_DISLIKE_QUERY, new MapSqlParameterSource("reviewId", reviewId)
                .addValue("userId", userId));

        if (rowsUpdated > 0) {
            update(COUNT_USEFUL_QUERY, new MapSqlParameterSource("reviewId", reviewId));
        }
    }

    private MapSqlParameterSource toMapSqlParameterSource(Review review) {
        MapSqlParameterSource params = new MapSqlParameterSource();

        if (review.getReviewId() != null) {
            params.addValue("reviewId", review.getReviewId());
        }

        params.addValue("content", review.getContent());
        params.addValue("isPositive", review.getIsPositive());
        params.addValue("userId", review.getUserId());
        params.addValue("filmId", review.getFilmId());

        return params;
    }
}
