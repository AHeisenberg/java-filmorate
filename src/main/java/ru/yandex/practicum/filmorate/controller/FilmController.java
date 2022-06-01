package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {

    private static long id;

    public static long getId() {
        return ++id;
    }
    private final Map<Long, Film> films = new HashMap<>();
    private static final LocalDate DATE_OF_FILM_RELEASE = LocalDate.of(1895, Month.DECEMBER, 28);

    @GetMapping
    public List<Film> getAll() {
        return new ArrayList<>(films.values());
    }

    public void deleteAllFilms() {
        films.clear();
    }

    @PostMapping
    public Film add(@Valid @NotNull @RequestBody Film film) {
        if (!films.containsKey(film.getId())) {
            if (isValidReleaseDate(film) && isValidNameAndDescription(film) && isValidDuration(film)) {
                film.setId(getId());
                films.put(film.getId(), film);
                log.debug("The movie was added with id={}", film.getId());
            }
        } else {
            throw new ValidationException("This movie already exists");
        }
        return film;
    }

    @PutMapping
    public Film update(@Valid @NotNull @RequestBody Film film) throws FilmNotFoundException {
        if (films.containsKey(film.getId())) {
            if (isValidReleaseDate(film) && isValidNameAndDescription(film) && isValidDuration(film)) {
                films.put(film.getId(), film);
                log.debug("The movie was updated with id={}", film.getId());
            }
        } else {
            log.error("This movie doesn't exist");

            throw new FilmNotFoundException(film);
        }
        return film;
    }

    private boolean isValidReleaseDate(Film film) {
        if (film.getReleaseDate().isBefore(DATE_OF_FILM_RELEASE)) {
            throw new ValidationException("The film's release date should be after: " + DATE_OF_FILM_RELEASE);
        } else {
            return true;
        }
    }

    private boolean isValidNameAndDescription(Film film) {
        if (film.getDescription().isEmpty() || film.getDescription().isBlank()) {
            throw new ValidationException("The film's description is blank");
        }
        if (film.getDescription().length() > 200) {
            throw new ValidationException("The film's description is too long");
        }
        if (film.getName().isBlank() || film.getName().isEmpty()) {
            throw new ValidationException("The film's name is blank");
        } else {
            return true;
        }
    }

    private boolean isValidDuration(Film film) {
        if (film.getDuration() <= 0) {
            throw new ValidationException("The film's duration is negative or 0");
        } else {
            return true;
        }
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<String> exceptionHandler(ValidationException e) {
        log.debug(e.getMessage());
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
