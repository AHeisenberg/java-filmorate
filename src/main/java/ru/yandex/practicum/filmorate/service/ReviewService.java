package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReviewService {
private final ReviewStorage reviewStorage;
    private final UserService userService;
    private final FilmService filmService;
    private final EventStorage eventStorage;

    @Autowired
    public ReviewService(ReviewStorage reviewStorage, UserService userService, FilmService filmService, EventStorage eventStorage) {
        this.reviewStorage = reviewStorage;
        this.userService = userService;
        this.filmService = filmService;
        this.eventStorage = eventStorage;
    }

    public Review addReview(Review review) {
        Optional<Film> filmOpt = filmService.getFilm(review.getFilmId());
        Optional<User> userOpt = userService.getUser(review.getUserId());

        if (review.getFilmId() == null || review.getUserId() == null) {
            throw new ValidationException("film ID or user ID is empty");
        }

        if (review.getIsPositive() == null) {
            throw new ValidationException("variable isPositive is empty");
        }

        if (filmOpt.isPresent() && userOpt.isPresent()) {
            Optional<Review> addedReview = reviewStorage.addReview(review);
            eventStorage.addEvent(review.getUserId(), addedReview.get().getReviewId(), Event.EventType.REVIEW, Event.Operation.ADD);
            return addedReview
                    .stream()
                    .collect(Collectors.toList()).get(0);
        } else if (filmOpt.isEmpty()) {
            throw new FilmNotFoundException();
        } else {
            throw new UserNotFoundException();
        }
    }

    public Review editReview(Review review) {
        Optional<Review> reviewOpt = reviewStorage.getReview(review.getReviewId());
        if (reviewOpt.isPresent()) {
            Optional<Review> updatedReview = reviewStorage.editReview(review);
            eventStorage.addEvent(updatedReview.get().getUserId(), updatedReview.get().getReviewId(),
                    Event.EventType.REVIEW, Event.Operation.UPDATE);
            return updatedReview
                    .stream()
                    .collect(Collectors.toList()).get(0);
        } else {
            throw new ReviewNotFoundException();
        }
    }

    public Review getReview(long id) {
        try {
            return reviewStorage.getReview(id)
                    .stream()
                    .collect(Collectors.toList()).get(0);
        } catch (Exception e) {
            throw new ReviewNotFoundException();
        }
    }

    public void deleteReview(long id) {
        Optional<Review> reviewOpt = reviewStorage.getReview(id);
        if (reviewOpt.isPresent()) {
            eventStorage.addEvent(reviewOpt.get().getUserId(), id, Event.EventType.REVIEW, Event.Operation.REMOVE);
            reviewStorage.deleteReview(id);
        } else {
            throw new ReviewNotFoundException();
        }
    }

    public Review putLike(long reviewId, long userId) {

        Optional<User> userOpt = userService.getUser(userId);
        reviewStorage.getReview(reviewId);
        if (userOpt.isPresent()) {
            return reviewStorage.putLike(reviewId, userId)
                    .stream()
                    .collect(Collectors.toList()).get(0);
        } else {
            throw new UserNotFoundException();
        }
    }

    public Review putDislike(long reviewId, long userId) {
        Optional<User> userOpt = userService.getUser(userId);

        reviewStorage.getReview(reviewId);
        if (userOpt.isPresent()) {
            return reviewStorage.putDislike(reviewId, userId)
                    .stream()
                    .collect(Collectors.toList()).get(0);
        } else {
            throw new UserNotFoundException();
        }
    }

    public Review deleteLike(long reviewId, long userId) {
        Optional<User> userOpt = userService.getUser(userId);
        reviewStorage.getReview(reviewId);
        if (userOpt.isPresent()) {
            return reviewStorage.deleteLike(reviewId, userId)
                    .stream()
                    .collect(Collectors.toList()).get(0);
        } else {
            throw new UserNotFoundException();
        }
    }

    public Review deleteDislike(long reviewId, long userId) {
        Optional<User> userOpt = userService.getUser(userId);
        reviewStorage.getReview(reviewId);
        if (userOpt.isPresent()) {
            return reviewStorage.deleteDislike(reviewId, userId)
                    .stream()
                    .collect(Collectors.toList()).get(0);
        } else {
            throw new UserNotFoundException();
        }
    }

    public List<Review> getReviewsOfFilm(Optional<Long> filmIdOpt, Optional<Long> countOpt) {
        Long count = countOpt.orElse(10L);
        Long filmId = filmIdOpt.orElse(null);
        return reviewStorage.getReviewsOfFilm(filmId, count);
    }

}
