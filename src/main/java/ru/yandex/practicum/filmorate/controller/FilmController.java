package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;

@RestController
@RequestMapping("/")
@Slf4j
public class FilmController {

    private final FilmStorage filmStorage;
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmStorage filmStorage, FilmService filmService) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
    }

    @PostMapping(value = "/films")
    public Film add(@RequestBody Film film) {
        return filmStorage.addFilm(film);
    }

    @PutMapping(value = "/films")
    public Film update(@RequestBody Film film) {
        return filmStorage.updateFilm(film);
    }

    @GetMapping("/films/{id}")
    public Film getFilm(@PathVariable String id) {
        return filmStorage.getFilm(Long.valueOf(id));
    }

    @GetMapping(value = "/films")
    public List<Film> getAll() {
        return filmStorage.getAllFilms();
    }


    @PutMapping("/films/{id}/like/{userId}")
    public Film addLike(
            @PathVariable Long id,
            @PathVariable Long userId) {
        if ((id != null) && (userId != null)) {
            return filmService.addLike(id, userId);
        } else {
            return null;
        }
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public Film removeLike(
            @PathVariable Long id,
            @PathVariable Long userId) {
        if ((id != null) && (userId != null)) {
            return filmService.removeLike(id, userId);
        } else {
            return null;
        }
    }

    @GetMapping("/films/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") Integer count) {
        return filmService.findPopularFilms(count);
    }
}
