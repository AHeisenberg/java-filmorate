package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserService userService;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage, UserService userService) { // inMemoryFilmStorage
        this.filmStorage = filmStorage;
        this.userService = userService;
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
            return filmStorage.addLike(id, userId);
        }
        return false;
    }

    public boolean removeLike(long filmId, long userId) {
        Optional<User> optUser = userService.getUser(userId);
        Optional<Film> optFilm = filmStorage.getFilm(filmId);

        if (optUser.isPresent() && optFilm.isPresent() && optFilm.get().getLikesCount() > 0) {
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

    public List<Film> getAllFilmsByDirectorSortedByYear(long id, String sortBy) {
        return filmStorage.getAllFilmsByDirector(id, sortBy);
    }

    public List<Film> getAllFilmsByDirectorSortedByLikes(long id, String sortBy) {
        return filmStorage.getAllFilmsByDirector(id, sortBy);
    }

    public List<Film> getFilmsBySubstring(String query, String by) {
        return filmStorage.getFilmsBySubstring(query, by);
    }
}
