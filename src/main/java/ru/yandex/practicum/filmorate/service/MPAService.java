package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.RatingMPA;
import ru.yandex.practicum.filmorate.storage.mpa.MPAStorage;

import java.util.List;
import java.util.Optional;

@Service
public class MPAService {

    private final MPAStorage storage;

    public MPAService(@Qualifier("MPADbStorage") MPAStorage storage) {
        this.storage = storage;
    }

    public List<RatingMPA> getAllRatings() {
        return storage.getAllRatings();
    }

    public Optional<RatingMPA> getRating(int id) {
        return storage.getRatingMPA(id);
    }
}
