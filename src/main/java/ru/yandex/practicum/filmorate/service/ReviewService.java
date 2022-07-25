package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ReviewNotFound;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {
    ReviewStorage reviewStorage;
    UserService userService;
    FilmService filmService;

    @Autowired
    public ReviewService(ReviewStorage reviewStorage, UserService userService, FilmService filmService) {
        this.reviewStorage = reviewStorage;
        this.userService = userService;
        this.filmService = filmService;
    }

    public Review addReview(Review review) {
       Optional<Film> filmOpt = filmService.getFilm(review.getFilmId());
       Optional<User> userOpt = userService.getUser(review.getUserId());

       if(filmOpt.isPresent() && userOpt.isPresent()) {
           return reviewStorage.addReview(review);
       } else if(!filmOpt.isPresent()){
           throw new FilmNotFoundException();
       } else {
           throw new UserNotFoundException();
       }


    }

    public Review editReview(Review review) {
        Optional<Review> reviewOpt = reviewStorage.getReview(review.getId());

        if(reviewOpt.isPresent()) {
            return reviewStorage.editReview(review);
        } else {
            throw new ReviewNotFound();
        }
    }

    public Optional<Review> getReview(long id) {

        Optional<Review> reviewOpt = reviewStorage.getReview(id);
        if(reviewOpt.isPresent()) {
            return reviewStorage.getReview(id);
        } else {
            throw new ReviewNotFound();
        }
    }

    public void deleteReview(long id) {
            reviewStorage.getReview(id);
            reviewStorage.deleteReview(id);
    }

    public Optional<Review> putLike(long reviewId, long userId) {

        Optional<User> userOpt = userService.getUser(userId);
        reviewStorage.getReview(reviewId);
        if(userOpt.isPresent()) {
            return reviewStorage.putLike(reviewId, userId);
        } else {
            throw new UserNotFoundException();
        }
    }

    public Optional<Review> putDislike(long reviewId, long userId) {
        Optional<User> userOpt = userService.getUser(userId);

        reviewStorage.getReview(reviewId);
        if(userOpt.isPresent()) {
            return reviewStorage.putDislike(reviewId, userId);
        } else {
            throw new UserNotFoundException();
        }
    }

    public Optional<Review> deleteLike(long reviewId, long userId) {
        Optional<User> userOpt = userService.getUser(userId);
        reviewStorage.getReview(reviewId);
        if(userOpt.isPresent()) {
            return reviewStorage.deleteLike(reviewId, userId);
        } else {
            throw new UserNotFoundException();
        }
    }

    public Optional<Review> deleteDislike(long reviewId, long userId) {
        Optional<User> userOpt = userService.getUser(userId);
        reviewStorage.getReview(reviewId);
        if(userOpt.isPresent()) {
            return reviewStorage.deleteDislike(reviewId, userId);
        } else {
            throw new UserNotFoundException();
        }
    }

    public List<Review> getReviewsOfFilm(Optional<Long> filmId, Optional<Long> countOpt) {
       long count = countOpt.orElse(10L);
       return reviewStorage.getReviewsOfFilm(filmId, count);
    }

}
