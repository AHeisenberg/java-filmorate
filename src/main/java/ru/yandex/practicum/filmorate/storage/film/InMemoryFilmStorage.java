package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.yandex.practicum.filmorate.service.FilmValidator.checkFilm;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private static long id;

    public static long getId() {
        return ++id;
    }

    private final Map<Long, Film> films = new HashMap<>();

    public void deleteAllFilms() {
        log.info("All films have been deleted");
        films.clear();
    }

    @Override
    public Film addFilm(Film film) {
        checkFilm(film);
        film.setId(getId());
        films.put(film.getId(), film);
        log.info("The movie was added with id={}", film.getId());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new FilmNotFoundException();
        }
        checkFilm(film);
        log.info("The movie was updated with id={}", film.getId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getFilm(Long id) {
        Film film = films.get(id);
        if (film == null) {
            throw new FilmNotFoundException();
        }
        return film;
    }
}
