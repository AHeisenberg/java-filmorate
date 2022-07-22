package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.util.List;
import java.util.Optional;

@Service
public class DirectorService {

    private final DirectorStorage storage;

    @Autowired
    public DirectorService(DirectorStorage storage) {
        this.storage = storage;
    }

    public Director addDirector(Director director) {
        return storage.addDirector(director);
    }

    public Optional<Director> updateDirector(Director director) {
        return storage.updateDirector(director);
    }

    public List<Director> getAllDirectors() {
        return storage.getAllDirectors();
    }

    public Optional<Director> getDirector(long id) {
        return storage.getDirector(id);
    }

    public boolean deleteDirector(long id) {
        return storage.deleteDirector(id);
    }
}
