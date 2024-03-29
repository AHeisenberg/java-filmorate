package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.service.FilmValidator.DATE_OF_FILM_RELEASE;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserService userService;
    private final GenreStorage genreStorage;
    private final EventStorage eventStorage;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage, UserService userService, @Qualifier("genreDbStorage") GenreStorage genreStorage, EventStorage eventStorage) { // inMemoryFilmStorage
        this.filmStorage = filmStorage;
        this.userService = userService;
        this.genreStorage = genreStorage;
        this.eventStorage = eventStorage;
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Optional<Film> updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Optional<Film> getFilm(long id) {
        return filmStorage.getFilm(id);
    }

    public boolean deleteFilm(long id) {
        return filmStorage.deleteFilm(id);
    }

    public boolean addLike(long id, long userId) {
        Optional<User> optUser = userService.getUser(userId);
        Optional<Film> optFilm = filmStorage.getFilm(id);

        if (optUser.isPresent() && optFilm.isPresent()) {
            eventStorage.addEvent(userId, id, Event.EventType.LIKE, Event.Operation.ADD);
            return filmStorage.addLike(id, userId);
        }
        return false;
    }

    public boolean removeLike(long filmId, long userId) {
        Optional<User> optUser = userService.getUser(userId);
        Optional<Film> optFilm = filmStorage.getFilm(filmId);

        if (optUser.isPresent() && optFilm.isPresent() && optFilm.get().getLikesCount() > 0) {
            eventStorage.addEvent(userId, filmId, Event.EventType.LIKE, Event.Operation.REMOVE);
            return filmStorage.removeLike(filmId, userId);
        }
        return false;
    }

    public List<Film> findPopularFilms(Integer count) {
        return filmStorage.getAllFilms().stream()
                .sorted((o1, o2) -> (int) (o2.getLikesCount() - o1.getLikesCount()))
                .limit(count)
                .collect(Collectors.toList());
    }

    public List<Film> getAllFilmsByDirector(long id, String sortBy) {
        return filmStorage.getAllFilmsByDirector(id, sortBy);
    }

    public List<Film> getFilmsBySubstring(String query, String by) {
        return filmStorage.getFilmsBySubstring(query, by);
    }

    public Optional<List<Film>> getTopCommonFilms(long userId, long friendId) {
        return userService.getUser(userId).isPresent() && userService.getUser(friendId).isPresent()
                ? Optional.of(filmStorage.getTopFilmsByUser(userId).stream().filter(filmStorage.getTopFilmsByUser(friendId)::contains).collect(Collectors.toList()))
                : Optional.empty();
    }

    public Optional<List<Film>> getPopularFilms(long count, int genreId, int year) {
        if (genreId != -1 && year != -1) {
            return getTopFilmsByGenreAndYear(count, genreId, year);

        } else if (genreId != -1) {
            return getTopFilmsByGenre(count, genreId);

        } else if (year != -1) {
            return getTopFilmsByYear(count, year);

        } else {
            return Optional.of(getTopLikableFilms(count));
        }
    }

    private Optional<List<Film>> getTopFilmsByGenreAndYear(long count, int genreId, int year) {
        return genreStorage.getGenre(genreId).isPresent() && year > DATE_OF_FILM_RELEASE.getYear()
                ? Optional.of(filmStorage.getTopFilmsByGenreAndYear(count, genreId, year))
                : Optional.empty();
    }

    private Optional<List<Film>> getTopFilmsByGenre(long count, int genreId) {
        return genreStorage.getGenre(genreId).isPresent()
                ? Optional.of(filmStorage.getTopFilmsByGenre(count, genreId))
                : Optional.empty();
    }

    private Optional<List<Film>> getTopFilmsByYear(long count, int year) {
        return year > DATE_OF_FILM_RELEASE.getYear()
                ? Optional.of(filmStorage.getTopFilmsByYear(count, year))
                : Optional.empty();
    }

    private List<Film> getTopLikableFilms(long count) {
        return filmStorage.getTopLikeableFilms(count);
    }
}
