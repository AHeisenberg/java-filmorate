package ru.yandex.practicum.filmorate.storage.mpa;

import ru.yandex.practicum.filmorate.model.RatingMPA;

import java.util.List;
import java.util.Optional;

public interface MPAStorage {
    RatingMPA addRatingMPA(RatingMPA ratingMPA);

    Optional<RatingMPA> updateRatingMPA(RatingMPA ratingMPA);

    List<RatingMPA> getAllRatings();

    Optional<RatingMPA> getRatingMPA(long id);

    boolean deleteRatingMPA(long id);

}
