package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.Getter;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.RatingMPA;

import java.util.*;


@Repository
public class InMemoryMPAStorage implements MPAStorage {
    private static int id;

    private int getId() {
        return ++id;
    }

    @Getter
    private final Map<Integer, RatingMPA> ratings;

    public InMemoryMPAStorage() {
        ratings = new HashMap<>();
        ratings.put(getId(), RatingMPA.builder().id(id).name("G").build());
        ratings.put(getId(), RatingMPA.builder().id(id).name("PG").build());
        ratings.put(getId(), RatingMPA.builder().id(id).name("PG-13").build());
        ratings.put(getId(), RatingMPA.builder().id(id).name("R").build());
        ratings.put(getId(), RatingMPA.builder().id(id).name("NC-17").build());
    }

    @Override
    public RatingMPA addRatingMPA(RatingMPA mpa) {
        mpa.setId(getId());
        ratings.put(id, mpa);
        return mpa;
    }

    @Override
    public Optional<RatingMPA> updateRatingMPA(RatingMPA mpa) {
        if (ratings.containsKey(mpa.getId())) {
            ratings.put(mpa.getId(), mpa);
            return Optional.of(mpa);
        }
        return Optional.empty();
    }

    @Override
    public List<RatingMPA> getAllRatings() {
        return new ArrayList<>(ratings.values());
    }

    @Override
    public Optional<RatingMPA> getRatingMPA(long id) {
        return Optional.ofNullable(ratings.get(id));
    }

    @Override
    public boolean deleteRatingMPA(long id) {
        return ratings.remove(id) != null;
    }
}
