package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.review.ReviewServiceImpl;
import ru.yandex.practicum.filmorate.validation.ValidatorGroups;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewServiceImpl reviewService;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Review getReview(@PathVariable("id") Long reviewId) {
        log.info("Вызван метод GET /reviews/{}", reviewId);
        Review review = reviewService.getReview(reviewId);
        log.info("Метод GET /reviews/{} вернул ответ {}", reviewId, review);
        return review;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Validated({ValidatorGroups.Create.class})
    public Review createReview(@RequestBody @Valid Review review) {
        log.info("Вызван метод POST /reviews {}", review);
        Review newReview = reviewService.createReview(review);
        log.info("Метод POST /reviews вернул ответ {}", newReview);
        return newReview;
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    @Validated({ValidatorGroups.Update.class})
    public Review updateReview(@RequestBody @Valid Review review) {
        log.info("Вызван метод PUT /reviews {}", review);
        Review newReview = reviewService.updateReview(review);
        log.info("Метод PUT /reviews вернул ответ {}", newReview);
        return newReview;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReview(@PathVariable("id") Long reviewId) {
        log.info("Вызван метод DELETE /reviews/{}", reviewId);
        reviewService.removeReview(reviewId);
        log.info("Метод DELETE /reviews/{} выполнен успешно", reviewId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Review> getReviewsForFilm(@RequestParam(defaultValue = "0") Long filmId,
                                          @Positive @RequestParam(defaultValue = "10") Integer count) {
        log.info("Вызван метод GET /reviews?filmId={}&count={}", filmId, count);
        List<Review> filmReviews = reviewService.getReviewsForFilm(filmId, count);
        log.info("Метод GET /reviews?filmId={}&count={} вернул ответ {}", filmId, count, filmReviews);
        return filmReviews;
    }

    @PutMapping("{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void addReviewLike(@PathVariable("id") Long reviewId,
                              @PathVariable("userId") Long userId) {
        log.info("Вызван метод PUT /reviews/{id}/like/{userId} c id = {} и userId = {}", reviewId, userId);
        reviewService.addReviewLike(reviewId, userId);
        log.info("Метод PUT /reviews/{id}/like/{userId} успешно выполнен");
    }

    @PutMapping("{id}/dislike/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void addReviewDislike(@PathVariable("id") Long reviewId,
                                 @PathVariable("userId") Long userId) {
        log.info("Вызван метод PUT /reviews/{id}/dislike/{userId} c id = {} и userId = {}", reviewId, userId);
        reviewService.addReviewDislike(reviewId, userId);
        log.info("Метод PUT /reviews/{id}/dislike/{userId} успешно выполнен");
    }

    @DeleteMapping("{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteReviewLike(@PathVariable("id") Long reviewId,
                                 @PathVariable("userId") Long userId) {
        log.info("Вызван метод DELETE /reviews/{id}/like/{userId} c id = {} и userId = {}", reviewId, userId);
        reviewService.deleteReviewLike(reviewId, userId);
        log.info("Метод DELETE /reviews/{id}/like/{userId} успешно выполнен");
    }

    @DeleteMapping("{id}/dislike/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteReviewDislike(@PathVariable("id") Long reviewId,
                                    @PathVariable("userId") Long userId) {
        log.info("Вызван метод DELETE /reviews/{id}/dislike/{userId} c id = {} и userId = {}", reviewId, userId);
        reviewService.deleteReviewDislike(reviewId, userId);
        log.info("Метод DELETE /reviews/{id}/dislike/{userId} успешно выполнен");
    }
}
