package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.film.FilmRepository;
import ru.yandex.practicum.filmorate.repository.review.ReviewRepository;
import ru.yandex.practicum.filmorate.repository.user.UserRepository;

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

    @Override
    public Review createReview(Review review) {
        User user = userRepository.getById(review.getUserId())
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_USER + review.getUserId()));
        Film film = filmRepository.getById(review.getFilmId())
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_FILM + review.getFilmId()));

        return reviewRepository.save(review);
    }

    @Override
    public Review updateReview(Review newReview) {
        Review savedReview = reviewRepository.getById(newReview.getReviewId())
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_REVIEW + newReview.getReviewId()));

        savedReview.setContent(newReview.getContent());
        savedReview.setIsPositive(newReview.getIsPositive());

        reviewRepository.update(savedReview);
        return savedReview;
    }

    @Override
    public void deleteReview(long reviewId) {
        reviewRepository.delete(reviewId);
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
        Review review = reviewRepository.getById(reviewId).orElseThrow(() -> new NotFoundException(NOT_FOUND_REVIEW + reviewId));
        User user = userRepository.getById(userId).orElseThrow(() -> new NotFoundException(NOT_FOUND_USER + userId));

        reviewRepository.addLike(review.getReviewId(), user.getId());
    }

    @Override
    public void addDislike(long reviewId, long userId) {
        Review review = reviewRepository.getById(reviewId).orElseThrow(() -> new NotFoundException(NOT_FOUND_REVIEW + reviewId));
        User user = userRepository.getById(userId).orElseThrow(() -> new NotFoundException(NOT_FOUND_USER + userId));

        reviewRepository.addDislike(review.getReviewId(), user.getId());
    }

    @Override
    public void removeLike(long reviewId, long userId) {
        Review review = reviewRepository.getById(reviewId).orElseThrow(() -> new NotFoundException(NOT_FOUND_REVIEW + reviewId));
        User user = userRepository.getById(userId).orElseThrow(() -> new NotFoundException(NOT_FOUND_USER + userId));

        reviewRepository.removeLike(review.getReviewId(), user.getId());
    }

    @Override
    public void removeDislike(long reviewId, long userId) {
        Review review = reviewRepository.getById(reviewId).orElseThrow(() -> new NotFoundException(NOT_FOUND_REVIEW + reviewId));
        User user = userRepository.getById(userId).orElseThrow(() -> new NotFoundException(NOT_FOUND_USER + userId));

        reviewRepository.removeDislike(review.getReviewId(), user.getId());
    }
}
