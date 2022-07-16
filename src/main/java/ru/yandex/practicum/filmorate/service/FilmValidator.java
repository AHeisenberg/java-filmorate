package ru.yandex.practicum.filmorate.service;

import org.springframework.util.StringUtils;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.InMemoryGenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.InMemoryMPAStorage;

import java.time.LocalDate;
import java.time.Month;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class FilmValidator {

    private static final LocalDate DATE_OF_FILM_RELEASE = LocalDate.of(1895, Month.DECEMBER, 28);
    private static InMemoryMPAStorage mpaStorage;
    private static InMemoryGenreStorage genreStorage;

    public FilmValidator() {
        mpaStorage = new InMemoryMPAStorage();
        genreStorage = new InMemoryGenreStorage();
    }

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

    private static void checkRating(Film film) {
        film.setMpa(mpaStorage.getRatings().getOrDefault(film.getMpa().getId(), null));
        if (film.getMpa() == null) {
            throw new RuntimeException("Rating should be valid");
        }
    }

    private static void checkGenres(Film film) {
        if (film.getGenres() != null) {
            Set<Genre> treeSet = new TreeSet<>(Comparator.comparing(Genre::getId));
            treeSet.addAll(film.getGenres().stream()
                    .filter(genre -> genreStorage.getGenres().containsKey(genre.getId()))
                    .collect(Collectors.toSet()));
            for (Genre genre : treeSet) {
                genre.setName(genreStorage.getGenres().get(genre.getId()).getName());
            }
            film.setGenres(treeSet);
        }
    }

    public static void checkFilm(Film film) {
        checkValidNameOfFilm(film);
        checkValidDescriptionOfFilm(film);
        checkValidReleaseDate(film);
        checkDurationOfFilm(film);
        checkGenres(film);
        checkRating(film);
    }

}
