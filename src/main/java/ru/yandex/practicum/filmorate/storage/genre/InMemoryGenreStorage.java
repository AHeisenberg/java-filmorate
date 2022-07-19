package ru.yandex.practicum.filmorate.storage.genre;


import lombok.Getter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;

@Component
public class InMemoryGenreStorage implements GenreStorage {
    @Getter
    private final Map<Integer, Genre> genres;
    private static int id;

    private int getId() {
        return ++id;
    }

    public InMemoryGenreStorage() {
        genres = new HashMap<>();
        genres.put(getId(), Genre.builder().id(id).name("Комедия").build());
        genres.put(getId(), Genre.builder().id(id).name("Драма").build());
        genres.put(getId(), Genre.builder().id(id).name("Мультфильм").build());
        genres.put(getId(), Genre.builder().id(id).name("Триллер").build());
        genres.put(getId(), Genre.builder().id(id).name("Документальный").build());
        genres.put(getId(), Genre.builder().id(id).name("Боевик").build());
    }

    @Override
    public Genre addGenre(Genre genre) {
        genre.setId(getId());
        genres.put(id, genre);
        return genre;
    }

    @Override
    public Optional<Genre> updateGenre(Genre genre) {
        if (genres.containsKey(genre.getId())) {
            genres.put(genre.getId(), genre);
            return Optional.of(genre);
        }
        return Optional.empty();
    }

    @Override
    public List<Genre> getAllGenre() {
        return new ArrayList<>(genres.values());
    }

    @Override
    public Optional<Genre> getGenre(long id) {
        return Optional.ofNullable(genres.get(id));
    }

    @Override
    public boolean deleteGenre(long id) {
        return genres.remove(id) != null;
    }
}
