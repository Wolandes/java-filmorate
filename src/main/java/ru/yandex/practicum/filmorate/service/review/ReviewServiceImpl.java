package ru.yandex.practicum.filmorate.service.review;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DbException;
import ru.yandex.practicum.filmorate.exception.ExceptionMessages;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.event.Event;
import ru.yandex.practicum.filmorate.model.event.EventOperation;
import ru.yandex.practicum.filmorate.model.event.EventType;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewDbStorage reviewStorage;
    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;
    private final EventStorage eventStorage;

    @Override
    public Review getReview(Long reviewId) {
        return reviewStorage.getReview(reviewId)
                .orElseThrow(() -> new NotFoundException("Отзыв не найден с ID: " + reviewId));
    }

    @Override
    public Review createReview(Review review) {
        checkUserById(review.getUserId());
        checkFilmById(review.getFilmId());
        Review newReview = Optional.ofNullable(reviewStorage.createReview(review))
                .orElseThrow(() -> new DbException(String.format(ExceptionMessages.INSERT_REVIEW_ERROR, review)));
        Event event = Event.builder()
                .timestamp(Instant.now().toEpochMilli())
                .userId(newReview.getUserId())
                .eventType(EventType.REVIEW)
                .operation(EventOperation.ADD)
                .entityId(newReview.getReviewId())
                .build();
        eventStorage.createEvent(event);
        return newReview;
    }

    @Override
    public Review updateReview(Review review) {
        getReview(review.getReviewId());
        checkUserById(review.getUserId());
        checkFilmById(review.getFilmId());
        Review newReview = Optional.ofNullable(reviewStorage.updateReview(review))
                .orElseThrow(() -> new DbException(
                        String.format(ExceptionMessages.UPDATE_REVIEW_ERROR, review.getReviewId())));
        newReview = reviewStorage.getReview(newReview.getReviewId()).get();
        newReview.setUseful(reviewStorage.getUseful(review.getReviewId()));
        Event event = Event.builder()
                .timestamp(Instant.now().toEpochMilli())
                .userId(newReview.getUserId())
                .eventType(EventType.REVIEW)
                .operation(EventOperation.UPDATE)
                .entityId(newReview.getReviewId())
                .build();
        eventStorage.createEvent(event);
        return newReview;
    }

    @Override
    public void removeReview(Long reviewId) {
        getReview(reviewId);
        Event event = Event.builder()
                .timestamp(Instant.now().toEpochMilli())
                .userId(getReview(reviewId).getUserId())
                .eventType(EventType.REVIEW)
                .operation(EventOperation.REMOVE)
                .entityId(reviewId)
                .build();
        eventStorage.createEvent(event);
        reviewStorage.deleteReviewUseful(reviewId);
        reviewStorage.deleteReview(reviewId);
    }

    @Override
    public List<Review> getReviewsForFilm(Long filmId, Integer count) {
        if (filmId == 0) {
            return reviewStorage.getAllReviews()
                    .stream()
                    .limit(count)
                    .toList();
        } else {
            checkFilmById(filmId);
            return reviewStorage.getFilmReviews(filmId)
                    .stream()
                    .limit(count)
                    .toList();
        }
    }

    @Override
    public void addReviewLike(Long reviewId, Long userId) {
        getReview(reviewId);
        checkUserById(userId);
        reviewStorage.addLike(reviewId, userId);
    }

    @Override
    public void addReviewDislike(Long reviewId, Long userId) {
        getReview(reviewId);
        checkUserById(userId);
        reviewStorage.addDislike(reviewId, userId);
    }

    @Override
    public void deleteReviewLike(Long reviewId, Long userId) {
        getReview(reviewId);
        checkUserById(userId);
        reviewStorage.deleteLike(reviewId, userId);
    }

    @Override
    public void deleteReviewDislike(Long reviewId, Long userId) {
        getReview(reviewId);
        checkUserById(userId);
        reviewStorage.deleteDislike(reviewId, userId);
    }

    private void checkUserById(Long userId) {
        Optional.ofNullable(userStorage.getUser(userId))
                .orElseThrow(() -> new NotFoundException(String.format(ExceptionMessages.USER_NOT_FOUND_ERROR, userId)));
    }

    private void checkFilmById(Long filmId) {
        Optional.ofNullable(filmStorage.getFilm(filmId))
                .orElseThrow(() -> new NotFoundException(String.format(ExceptionMessages.FILM_NOT_FOUND_ERROR, filmId)));
    }
}
