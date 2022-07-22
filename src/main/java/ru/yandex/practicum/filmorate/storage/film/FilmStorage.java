package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Film addFilm(Film film);

    Optional<Film> updateFilm(Film film);

    List<Film> getAllFilms();

    Optional<Film> getFilm(long id);

    List<Film> getAllFilmsByDirector(long id);

    boolean deleteFilm(long id);

    boolean removeLike(long id, long userId);

    boolean addLike(long id, long userId);

}
