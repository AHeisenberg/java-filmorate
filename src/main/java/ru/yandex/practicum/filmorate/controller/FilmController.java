package ru.yandex.practicum.filmorate.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;


@RestController
@RequestMapping(value = "/films")
@Slf4j
public class FilmController {

    private final FilmService filmService;
    private final DirectorService directorService;

    @Autowired
    public FilmController(FilmService filmService, DirectorService directorService) {
        this.filmService = filmService;
        this.directorService = directorService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Film> addFilm(@RequestBody Film film) {
        return new ResponseEntity<>(filmService.addFilm(film), HttpStatus.CREATED);
    }


    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Film> updateFilm(@RequestBody Film film) {
        return filmService.updateFilm(film).map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Film> getFilm(@PathVariable long id) {
        return filmService.getFilm(id).map(film -> new ResponseEntity<>(film, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity<List<Film>> getAll() {
        return new ResponseEntity<>(filmService.getAllFilms(), HttpStatus.OK);
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<Film> addLike(@PathVariable long id, @PathVariable long userId) {
        return filmService.addLike(id, userId) ? new ResponseEntity<>(null, HttpStatus.OK)
                : new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<Film> removeLike(@PathVariable long id, @PathVariable long userId) {
        return filmService.removeLike(id, userId) ? new ResponseEntity<>(null, HttpStatus.OK)
                : new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Film> deleteFilm(@PathVariable long id) {
        return filmService.deleteFilm(id) ? new ResponseEntity<>(null, HttpStatus.OK)
                : new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @GetMapping("/director/{directorId}")
    public ResponseEntity<List<Film>> getDirectorsFilmSortedByYearOrLikes(@PathVariable long directorId,
                                                                          @RequestParam(defaultValue = "id")
                                                                          String sortBy) {
        if (directorService.getDirector(directorId).isPresent()) {
            return new ResponseEntity<>(filmService.getAllFilmsByDirector(directorId, sortBy), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<Film>> getFilmsBySubstring(@RequestParam String query, @RequestParam String by) {
        return new ResponseEntity<>(filmService.getFilmsBySubstring(query, by), HttpStatus.OK);
    }

    @GetMapping("/popular")
    public ResponseEntity<List<Film>> getPopularFilms(@RequestParam(defaultValue = "10") long count,
                                                      @RequestParam(required = false, defaultValue = "-1") int genreId,
                                                      @RequestParam(required = false, defaultValue = "-1") int year) {
        return new ResponseEntity<>(filmService.getPopularFilms(count, genreId, year).get(), HttpStatus.OK);
    }

    @GetMapping("/common")
    public ResponseEntity<List<Film>> getTopCommonFilms(@RequestParam long userId, @RequestParam long friendId) {
        return filmService.getTopCommonFilms(userId, friendId).map(film -> new ResponseEntity<>(film, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

}