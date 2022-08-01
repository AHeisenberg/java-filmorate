package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

public interface DirectorStorage {
    Director addDirector(Director director);

    Optional<Director> updateDirector(Director director);

    List<Director> getAllDirectors();

    Optional<Director> getDirector(long id);

    boolean deleteDirector(long id);
}
