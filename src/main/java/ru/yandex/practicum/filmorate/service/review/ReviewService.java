package ru.yandex.practicum.filmorate.service.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewService {
    Review getReview(Long reviewId);

    Review createReview(Review review);

    Review updateReview(Review review);

    void removeReview(Long reviewId);

    List<Review> getReviewsForFilm(Long filmId, Integer count);

    void addReviewLike(Long reviewId, Long userId);

    void addReviewDislike(Long reviewId, Long userId);

    void deleteReviewLike(Long reviewId, Long userId);

    void deleteReviewDislike(Long reviewId, Long userId);
}
