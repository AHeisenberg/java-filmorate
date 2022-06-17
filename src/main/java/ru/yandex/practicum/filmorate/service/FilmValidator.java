package ru.yandex.practicum.filmorate.service;

import org.springframework.util.StringUtils;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;

public class FilmValidator {

    private static final LocalDate DATE_OF_FILM_RELEASE = LocalDate.of(1895, Month.DECEMBER, 28);

    private static void checkValidNameOfFilm(Film film) {
        if (!(StringUtils.hasLength(film.getName()))) {
            throw new ValidationException("The film's name is blank");
        }
    }

    private static void checkValidDescriptionOfFilm(Film film) {
        if (!(StringUtils.hasLength(film.getDescription()))) {
            throw new ValidationException("The film's description is blank");
        }
        if (film.getDescription().length() > 200) {
            throw new ValidationException("The film's description is too long");
        }
    }

    private static void checkValidReleaseDate(Film film) {
        LocalDate releaseDate = film.getReleaseDate();
        if (releaseDate == null
                || releaseDate.isBefore(DATE_OF_FILM_RELEASE)) {
            throw new ValidationException("The film's release date should be after: " + DATE_OF_FILM_RELEASE);
        }
    }

    private static void checkDurationOfFilm(Film film) {
        if (film.getDuration() <= 0) {
            throw new ValidationException("The film's duration is negative or 0");
        }
    }

    public static void checkFilm(Film film) {
        checkValidNameOfFilm(film);
        checkValidDescriptionOfFilm(film);
        checkValidReleaseDate(film);
        checkDurationOfFilm(film);
    }

}
