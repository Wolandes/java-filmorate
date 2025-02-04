package ru.yandex.practicum.filmorate.storage.review;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.DbException;
import ru.yandex.practicum.filmorate.exception.ExceptionMessages;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage {
    private static final String GET_REVIEW = """
            select r.id, r.content, r.user_id, r.film_id, r.is_positive, COALESCE(SUM(ru.weight), 0) as useful
            from public.reviews r
            left outer join public.review_useful ru on r.id = ru.review_id
            where r.id = :id
            group by ru.review_id
            """;
    private static final String GET_REVIEWS = """
            select r.id, r.content, r.user_id, r.film_id, r.is_positive, COALESCE(SUM(ru.weight), 0) as useful
            from public.reviews r
            left outer join public.review_useful ru on r.id = ru.review_id
            group by ru.review_id, r.id
            order by useful desc
            """;
    private static final String GET_FILM_REVIEWS = """
            select r.id, r.content, r.user_id, r.film_id, r.is_positive, COALESCE(SUM(ru.weight), 0) as useful
            from public.reviews r
            left outer join public.review_useful ru on r.id = ru.review_id
            where r.film_id = :film_id
            group by ru.review_id, r.id
            order by useful desc
            """;
    private static final String INSERT_REVIEW = """
            insert into public.reviews (content, user_id, film_id, is_positive)
            values (:content, :user_id, :film_id, :is_positive)
            """;
    private static final String UPDATE_REVIEW = """
            update public.reviews
            set content = :content,
            is_positive = :is_positive
            where id = :id
            """;
    private static final String GET_USEFUL = """
            select SUM(weight)
            from review_useful
            where review_id = :review_id
            group by review_id
            """;
    private static final String DELETE_REVIEW = """
            delete from reviews
            where id = :id
            """;
    private static final String DELETE_REVIEW_USEFUL = """
            delete from review_useful
            where review_id = :review_id
            """;
    private static final String INSERT_REVIEW_LIKE = """
            merge into public.review_useful (review_id, user_id, weight)
            values (:review_id, :user_id, 1)
            """;
    private static final String INSERT_REVIEW_DISLIKE = """
            merge into public.review_useful (review_id, user_id, weight)
            values (:review_id, :user_id, -1)
            """;
    private static final String DELETE_REVIEW_LIKE = """
            delete from review_useful
            where review_id = :review_id
            and user_id = :user_id
            and weight = 1
            """;
    private static final String DELETE_REVIEW_DISLIKE = """
            delete from review_useful
            where review_id = :review_id
            and user_id = :user_id
            and weight = -1
            """;

    private final NamedParameterJdbcOperations jdbc;

    @Override
    public Optional<Review> getReview(Long reviewId) {
        SqlParameterSource params = new MapSqlParameterSource("id", reviewId);
        try {
            Review result = jdbc.query(GET_REVIEW, params, (ResultSet rs) -> {
                Review review = new Review();
                while (rs.next()) {
                    review.setReviewId(rs.getLong("id"));
                    review.setContent(rs.getString("content"));
                    review.setUserId(rs.getLong("user_id"));
                    review.setFilmId(rs.getLong("film_id"));
                    review.setIsPositive(rs.getBoolean("is_positive"));
                    review.setUseful(rs.getInt("useful"));
                }
                return review;
            });

            if (result.getReviewId() != null) {
                return Optional.ofNullable(result);
            } else {
                return Optional.empty();
            }

        } catch (DataAccessException ignored) {
            throw new DbException(String.format(ExceptionMessages.SELECT_ERROR));
        }
    }

    @Override
    public List<Review> getAllReviews() {
        try {
            return jdbc.query(GET_REVIEWS, (ResultSet rs) -> {
                List<Review> reviews = new ArrayList<>();
                while (rs.next()) {
                    Review review = new Review();
                    review.setReviewId(rs.getLong("id"));
                    review.setContent(rs.getString("content"));
                    review.setUserId(rs.getLong("user_id"));
                    review.setFilmId(rs.getLong("film_id"));
                    review.setIsPositive(rs.getBoolean("is_positive"));
                    review.setUseful(rs.getInt("useful"));
                    reviews.add(review);
                }
                return reviews;
            });
        } catch (DataAccessException ignored) {
            throw new DbException(String.format(ExceptionMessages.SELECT_ERROR));
        }
    }

    @Override
    public Review createReview(Review review) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("content", review.getContent());
        params.addValue("user_id", review.getUserId());
        params.addValue("film_id", review.getFilmId());
        params.addValue("is_positive", review.getIsPositive());
        try {
            jdbc.update(INSERT_REVIEW, params, keyHolder, new String[]{"id"});
            review.setReviewId(keyHolder.getKeyAs(Integer.class).longValue());
            return review;
        } catch (DataAccessException ignored) {
            throw new DbException(String.format(ExceptionMessages.INSERT_REVIEW_ERROR, review));
        }
    }

    @Override
    public Review updateReview(Review review) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("content", review.getContent());
        params.addValue("is_positive", review.getIsPositive());
        params.addValue("id", review.getReviewId());
        try {
            jdbc.update(UPDATE_REVIEW, params);
            return review;
        } catch (DataAccessException ignored) {
            throw new DbException(String.format(ExceptionMessages.UPDATE_REVIEW_ERROR, review));
        }
    }

    @Override
    public List<Review> getFilmReviews(Long filmId) {
        SqlParameterSource params = new MapSqlParameterSource("film_id", filmId);
        try {
            return jdbc.query(GET_FILM_REVIEWS, params, (ResultSet rs) -> {
                List<Review> reviews = new ArrayList<>();
                while (rs.next()) {
                    Review review = new Review();
                    review.setReviewId(rs.getLong("id"));
                    review.setContent(rs.getString("content"));
                    review.setUserId(rs.getLong("user_id"));
                    review.setFilmId(rs.getLong("film_id"));
                    review.setIsPositive(rs.getBoolean("is_positive"));
                    review.setUseful(rs.getInt("useful"));
                    reviews.add(review);
                }
                return reviews;
            });
        } catch (DataAccessException ignored) {
            throw new DbException(String.format(ExceptionMessages.GET_FILM_REVIEWS_ERROR, filmId));
        }
    }

    @Override
    public Integer getUseful(Long reviewId) {
        SqlParameterSource params = new MapSqlParameterSource("review_id", reviewId);
        try {
            return jdbc.queryForObject(GET_USEFUL, params, Integer.class);
        } catch (EmptyResultDataAccessException empt) {
            return 0;
        } catch (DataAccessException ignored) {
            throw new DbException(String.format(ExceptionMessages.SELECT_ERROR));
        }
    }

    @Override
    public void deleteReview(Long reviewId) {
        SqlParameterSource params = new MapSqlParameterSource("id", reviewId);
        try {
            jdbc.update(DELETE_REVIEW, params);
        } catch (DataAccessException ignored) {
            throw new DbException(String.format(ExceptionMessages.DELETE_REVIEW_ERROR, reviewId));
        }
    }

    @Override
    public void deleteReviewUseful(Long reviewId) {
        SqlParameterSource params = new MapSqlParameterSource("review_id", reviewId);
        try {
            jdbc.update(DELETE_REVIEW_USEFUL, params);
        } catch (DataAccessException ignored) {
            throw new DbException(String.format(ExceptionMessages.DELETE_REVIEW_ERROR, reviewId));
        }
    }

    @Override
    public void addLike(Long reviewId, Long userId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("review_id", reviewId);
        params.addValue("user_id", userId);
        try {
            jdbc.update(INSERT_REVIEW_LIKE, params);
        } catch (DataAccessException ignored) {
            throw new DbException(String.format(ExceptionMessages.INSERT_REVIEWLIKE_ERROR, userId, reviewId));
        }
    }

    @Override
    public void addDislike(Long reviewId, Long userId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("review_id", reviewId);
        params.addValue("user_id", userId);
        try {
            jdbc.update(INSERT_REVIEW_DISLIKE, params);
        } catch (DataAccessException ignored) {
            throw new DbException(String.format(ExceptionMessages.INSERT_REVIEWDISLIKE_ERROR, userId, reviewId));
        }
    }

    @Override
    public void deleteLike(Long reviewId, Long userId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("review_id", reviewId);
        params.addValue("user_id", userId);
        try {
            jdbc.update(DELETE_REVIEW_LIKE, params);
        } catch (DataAccessException ignored) {
            throw new DbException(String.format(ExceptionMessages.DELETE_REVIEWLIKE_ERROR, userId, reviewId));
        }
    }

    @Override
    public void deleteDislike(Long reviewId, Long userId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("review_id", reviewId);
        params.addValue("user_id", userId);
        try {
            jdbc.update(DELETE_REVIEW_DISLIKE, params);
        } catch (DataAccessException ignored) {
            throw new DbException(String.format(ExceptionMessages.DELETE_REVIEWDISLIKE_ERROR, userId, reviewId));
        }
    }
}
