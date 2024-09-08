package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.feed.Event;
import ru.yandex.practicum.filmorate.model.feed.EventType;
import ru.yandex.practicum.filmorate.model.feed.Operation;
import ru.yandex.practicum.filmorate.repository.event.EventRepository;
import ru.yandex.practicum.filmorate.repository.film.FilmRepository;
import ru.yandex.practicum.filmorate.repository.review.ReviewRepository;
import ru.yandex.practicum.filmorate.repository.user.UserRepository;

import java.time.Instant;
import java.util.List;

import static ru.yandex.practicum.filmorate.service.BaseFilmService.NOT_FOUND_FILM;
import static ru.yandex.practicum.filmorate.service.BaseUserService.NOT_FOUND_USER;

@Service
@RequiredArgsConstructor
public class BaseReviewService implements ReviewService {
    public static final String NOT_FOUND_REVIEW = "Не найден отзыв с ID = ";

    private final ReviewRepository reviewRepository;
    private final FilmRepository filmRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public Review createReview(Review review) {
        User user = userRepository.getById(review.getUserId())
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_USER + review.getUserId()));
        Film film = filmRepository.getById(review.getFilmId())
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_FILM + review.getFilmId()));
        Review savedReview = reviewRepository.save(review);

        eventRepository.save(createEvent(Operation.ADD, savedReview.getUserId(), savedReview.getReviewId()));
        return savedReview;
    }

    @Override
    public Review updateReview(Review newReview) {
        Review savedReview = getReviewById(newReview.getReviewId());

        savedReview.setContent(newReview.getContent());
        savedReview.setIsPositive(newReview.getIsPositive());

        reviewRepository.update(savedReview);

        eventRepository.save(createEvent(Operation.UPDATE, savedReview.getUserId(), savedReview.getReviewId()));
        return savedReview;
    }

    @Override
    public void deleteReview(long reviewId) {
        Review review = getReviewById(reviewId);
        reviewRepository.delete(reviewId);

        eventRepository.save(createEvent(Operation.REMOVE, review.getUserId(), review.getReviewId()));
    }

    @Override
    public Review getReviewById(long reviewId) {
        return reviewRepository.getById(reviewId).orElseThrow(() -> new NotFoundException(NOT_FOUND_REVIEW + reviewId));
    }

    @Override
    public List<Review> getReviewsByFilmId(Long filmId, int count) {
        if (filmId == null) {
            return reviewRepository.getAll(count);
        }

        Film film = filmRepository.getById(filmId).orElseThrow(() -> new NotFoundException(NOT_FOUND_FILM + filmId));
        return reviewRepository.getAllByFilmId(film.getId(), count);
    }

    @Override
    public void addLike(long reviewId, long userId) {
        Review review = getReviewById(reviewId);
        User user = userRepository.getById(userId).orElseThrow(() -> new NotFoundException(NOT_FOUND_USER + userId));

        reviewRepository.addLike(review.getReviewId(), user.getId());
    }

    @Override
    public void addDislike(long reviewId, long userId) {
        Review review = getReviewById(reviewId);
        User user = userRepository.getById(userId).orElseThrow(() -> new NotFoundException(NOT_FOUND_USER + userId));

        reviewRepository.addDislike(review.getReviewId(), user.getId());
    }

    @Override
    public void removeLike(long reviewId, long userId) {
        Review review = getReviewById(reviewId);
        User user = userRepository.getById(userId).orElseThrow(() -> new NotFoundException(NOT_FOUND_USER + userId));

        reviewRepository.removeLike(review.getReviewId(), user.getId());
    }

    @Override
    public void removeDislike(long reviewId, long userId) {
        Review review = getReviewById(reviewId);
        User user = userRepository.getById(userId).orElseThrow(() -> new NotFoundException(NOT_FOUND_USER + userId));

        reviewRepository.removeDislike(review.getReviewId(), user.getId());
    }

    private Event createEvent(Operation operation, Long userId, Long entityId) {
        Event event = new Event();
        event.setEventType(EventType.REVIEW);
        event.setOperation(operation);
        event.setUserId(userId);
        event.setEntityId(entityId);
        event.setTimestamp(Instant.now().toEpochMilli());
        return event;
    }
}
