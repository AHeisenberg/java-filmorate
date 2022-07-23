package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

import static ru.yandex.practicum.filmorate.service.FilmValidator.checkFilm;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private static long id;

    private long getId() {
        return ++id;
    }

    private final Map<Long, Film> films;

    private final Map<Long, Set<Long>> likes;

    private final Map<Long, Director> directors;

    public InMemoryFilmStorage() {
        films = new HashMap<>();
        likes = new HashMap<>();
        directors = new HashMap<>();
    }

    @Override
    public Film addFilm(Film film) {
        checkFilm(film);
        film.setId(getId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Optional<Film> updateFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new FilmNotFoundException();
        }
        checkFilm(film);
        log.info("The movie was updated with id={}", film.getId());
        films.put(film.getId(), film);
        return Optional.of(film);
    }

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Optional<Film> getFilm(long id) {
        Film film = films.get(id);
        if (film == null) {
            throw new FilmNotFoundException();
        }
        return Optional.of(film);
    }

    @Override
    public boolean deleteFilm(long id) {
        return films.remove(id) != null;
    }

    @Override
    public boolean removeLike(long id, long userId) {
        if (likes.containsKey(id)) {
            likes.get(id).remove(userId);
            films.get(id).setLikesCount(likes.get(id).size());
            return true;
        }
        return false;
    }

    @Override
    public boolean addLike(long id, long userId) {
        if (likes.containsKey(id)) {
            likes.get(id).add(userId);
        } else {
            Set<Long> filmLikes = new HashSet<>();
            filmLikes.add(userId);
            likes.put(id, filmLikes);
        }
        films.get(id).setLikesCount(likes.get(id).size());
        return true;
    }

    @Override
    public List<Film> getAllFilmsByDirector(long id, String sortBy) {
        List<Film> filmsByDirector = new ArrayList<>();
        for (Long filmId : directors.keySet()) {
            if (directors.get(filmId).getId() == id) {
                filmsByDirector.add(films.get(filmId));
            }
        }
        return filmsByDirector;
    }
}
