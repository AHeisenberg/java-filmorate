package ru.yandex.practicum.filmorate.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RequestMapping(value = "/reviews")
@RestController
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping()
    public ResponseEntity<Optional<Review>> addReview(@RequestBody Review review) {
        return new ResponseEntity<>(reviewService.addReview(review), HttpStatus.OK);
    }

    @PutMapping()
    public ResponseEntity<Optional<Review>> editReview(@RequestBody Review review) {
        return new ResponseEntity<>(reviewService.editReview(review), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Review>> getReview(@PathVariable long id) {
        Optional<Review> review = reviewService.getReview(id);
        return new ResponseEntity<>(review, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteReview(@PathVariable long id) {
        reviewService.deleteReview(id);
        return new ResponseEntity<>( HttpStatus.OK);
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<Optional<Review>> putLike(@PathVariable long id, @PathVariable long userId) {
        return new ResponseEntity<>(reviewService.putLike(id, userId), HttpStatus.OK);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public ResponseEntity<Optional<Review>> putDislike(@PathVariable long id, @PathVariable long userId) {
        return new ResponseEntity<>(reviewService.putDislike(id, userId), HttpStatus.OK);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<Optional<Review>> deleteLike(@PathVariable long id, @PathVariable long userId) {
        return new ResponseEntity<>(reviewService.deleteLike(id, userId), HttpStatus.OK);
    }

    @DeleteMapping("/{id}/Dislike/{userId}")
    public ResponseEntity<Optional<Review>> deleteDislike(@PathVariable long id, @PathVariable long userId) {
        return new ResponseEntity<>(reviewService.deleteDislike(id, userId), HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<List<Review>> getReviewsOfFilm (@RequestParam(required = false) Optional<Long> filmId,
                                                          @RequestParam(required = false) Optional<Long> countOpt) {

        return new ResponseEntity<>(reviewService.getReviewsOfFilm(filmId, countOpt), HttpStatus.OK);
    }
}
