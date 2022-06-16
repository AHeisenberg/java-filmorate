package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.LikeNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    final private FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {

        this.filmStorage = filmStorage;
    }

    public Film addLike(Long id, Long userId) {
        Film film = filmStorage.getFilm(id);
        film.getLikes().add(userId);
        return film;
    }

    public Film removeLike(Long filmId, Long userId) {
        Film film = filmStorage.getFilm(filmId);
        if (film.getLikes().contains(userId)) {
            film.getLikes().remove(userId);
        } else {
            throw new LikeNotFoundException();
        }
        return film;
    }

    public List<Film> findPopularFilms(Integer count) {
        return filmStorage.getAllFilms().stream()
                .sorted((o1, o2) -> (o2.getLikes().size() - o1.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }
}
