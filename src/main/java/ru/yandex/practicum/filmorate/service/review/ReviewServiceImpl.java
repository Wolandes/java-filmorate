package ru.yandex.practicum.filmorate.service.review;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DbException;
import ru.yandex.practicum.filmorate.exception.ExceptionMessages;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.event.EventOperation;
import ru.yandex.practicum.filmorate.model.event.EventType;
import ru.yandex.practicum.filmorate.service.event.EventServiceImpl;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewDbStorage reviewStorage;
    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;
    private final EventServiceImpl eventService;

    @Override
    public Review getReview(Long reviewId) {
        return reviewStorage.getReview(reviewId)
                .orElseThrow(() -> new NotFoundException("Отзыв не найден с ID: " + reviewId));
    }

    @Override
    public Review createReview(Review review) {
        if (review.getUserId() != null && userStorage.getUser(review.getUserId()) == null) {
            throw new NotFoundException(String.format(ExceptionMessages.USER_NOT_FOUND_ERROR, review.getUserId()));
        }
        if (review.getFilmId() != null && filmStorage.getFilm(review.getFilmId()) == null) {
            throw new NotFoundException(String.format(ExceptionMessages.FILM_NOT_FOUND_ERROR, review.getFilmId()));
        }
        Review newReview = Optional.ofNullable(reviewStorage.createReview(review))
                .orElseThrow(() -> new DbException(String.format(ExceptionMessages.INSERT_REVIEW_ERROR, review)));
        eventService.createEvent(newReview.getUserId(), EventType.REVIEW, EventOperation.ADD, newReview.getReviewId());
        return newReview;
    }

    @Override
    public Review updateReview(Review review) {
        if (reviewStorage.getReview(review.getReviewId()) == null) {
            throw new NotFoundException(String.format(ExceptionMessages.REVIEW_NOT_FOUND_ERROR, review.getReviewId()));
        }
        if (userStorage.getUser(review.getUserId()) == null) {
            throw new NotFoundException(String.format(ExceptionMessages.USER_NOT_FOUND_ERROR, review.getUserId()));
        }
        if (filmStorage.getFilm(review.getFilmId()) == null) {
            throw new NotFoundException(String.format(ExceptionMessages.FILM_NOT_FOUND_ERROR, review.getFilmId()));
        }
        Review newReview = Optional.ofNullable(reviewStorage.updateReview(review))
                .orElseThrow(() -> new DbException(String.format(ExceptionMessages.UPDATE_REVIEW_ERROR, review)));
        newReview.setUseful(reviewStorage.getUseful(review.getReviewId()));
        eventService.createEvent(newReview.getUserId(), EventType.REVIEW, EventOperation.UPDATE, newReview.getReviewId());
        return newReview;
    }

    @Override
    public void removeReview(Long reviewId) {
        if (reviewStorage.getReview(reviewId) == null) {
            throw new NotFoundException(String.format(ExceptionMessages.REVIEW_NOT_FOUND_ERROR, reviewId));
        }
        reviewStorage.deleteReview(reviewId);
        reviewStorage.deleteReviewUseful(reviewId);
        eventService.createEvent(getReview(reviewId).getUserId(), EventType.REVIEW, EventOperation.REMOVE, reviewId);
    }

    @Override
    public List<Review> getReviewsForFilm(Long filmId, Integer count) {
        if (filmId == 0) {
            return reviewStorage.getAllReviews()
                    .stream()
                    .limit(count)
                    .toList();
        } else {
            Film film = Optional.ofNullable(filmStorage.getFilm(filmId))
                    .orElseThrow(() -> new NotFoundException("Фильм с идентификатором " + filmId + " не найден."));
            return reviewStorage.getFilmReviews(filmId)
                    .stream()
                    .limit(count)
                    .toList();
        }
    }

    @Override
    public void addReviewLike(Long reviewId, Long userId) {
        if (reviewStorage.getReview(reviewId) == null) {
            throw new NotFoundException(String.format(ExceptionMessages.REVIEW_NOT_FOUND_ERROR, reviewId));
        }
        if (userStorage.getUser(userId) == null) {
            throw new NotFoundException(String.format(ExceptionMessages.USER_NOT_FOUND_ERROR, userId));
        }
        reviewStorage.addLike(reviewId, userId);
    }

    @Override
    public void addReviewDislike(Long reviewId, Long userId) {
        if (reviewStorage.getReview(reviewId) == null) {
            throw new NotFoundException(String.format(ExceptionMessages.REVIEW_NOT_FOUND_ERROR, reviewId));
        }
        if (userStorage.getUser(userId) == null) {
            throw new NotFoundException(String.format(ExceptionMessages.USER_NOT_FOUND_ERROR, userId));
        }
        reviewStorage.addDislike(reviewId, userId);
    }

    @Override
    public void deleteReviewLike(Long reviewId, Long userId) {
        if (reviewStorage.getReview(reviewId) == null) {
            throw new NotFoundException(String.format(ExceptionMessages.REVIEW_NOT_FOUND_ERROR, reviewId));
        }
        if (userStorage.getUser(userId) == null) {
            throw new NotFoundException(String.format(ExceptionMessages.USER_NOT_FOUND_ERROR, userId));
        }
        reviewStorage.deleteLike(reviewId, userId);
    }

    @Override
    public void deleteReviewDislike(Long reviewId, Long userId) {
        if (reviewStorage.getReview(reviewId) == null) {
            throw new NotFoundException(String.format(ExceptionMessages.REVIEW_NOT_FOUND_ERROR, reviewId));
        }
        if (userStorage.getUser(userId) == null) {
            throw new NotFoundException(String.format(ExceptionMessages.USER_NOT_FOUND_ERROR, userId));
        }
        reviewStorage.deleteDislike(reviewId, userId);
    }
}
