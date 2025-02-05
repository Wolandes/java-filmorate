package ru.yandex.practicum.filmorate.storage.review;


import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewStorage {
    Optional<Review> getReview(Long reviewId);

    List<Review> getAllReviews();

    Review createReview(Review review);

    Review updateReview(Review review);

    List<Review> getFilmReviews(Long filmId);

    Integer getUseful(Long reviewId);

    void deleteReview(Long reviewId);

    void deleteReviewUseful(Long reviewId);

    void addLike(Long reviewId, Long userId);

    void addDislike(Long reviewId, Long userId);

    void deleteLike(Long reviewId, Long userId);

    void deleteDislike(Long reviewId, Long userId);
}
