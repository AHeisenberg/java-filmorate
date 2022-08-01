package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface FilmStorage {
    Film addFilm(Film film);

    Optional<Film> updateFilm(Film film);

    List<Film> getAllFilms();

    Optional<Film> getFilm(long id);

    List<Film> getAllFilmsByDirector(long id, String sortBy);

    boolean deleteFilm(long id);

    boolean removeLike(long id, long userId);

    boolean addLike(long id, long userId);

    Map<Long, Set<Long>> getUserLikes();
    List<Film> getTopLikeableFilms(long count);

    List<Film> getTopFilmsByYear(long count, int year);

    List<Film> getTopFilmsByGenre(long count, int genreId);

    List<Film> getTopFilmsByGenreAndYear(long count, int genreId, int year);

    List<Film> getTopFilmsByUser(long userId);

    List<Film> getFilmsBySubstring(String query, String by);


}
