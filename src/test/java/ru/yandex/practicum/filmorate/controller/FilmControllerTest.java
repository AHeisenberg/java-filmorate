package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class FilmControllerTest {
    @Autowired
    private FilmController filmController;

    @Autowired
    private FilmStorage filmStorage;

    private Film film;

    @AfterEach
    private void afterEach() {
        filmStorage.deleteAllFilms();
    }

    private void createFilm(String description, int yearOfRelease) {
        film = new Film(1, "Titanic", description,
                LocalDate.of(yearOfRelease, 1, 1), 320);
    }

    @Test
    void testAddFilm_AllRight() {
        createFilm("Sad movie about a shipwreck", 2000);
        filmController.add(film);

        assertEquals(film, filmController.getAll().get(0), "Wrong test");
    }

    @Test
    void testAddFilm_WrongDateOfRelease() {
        createFilm("Sad movie about a shipwreck", 1800);

        assertThrows(ValidationException.class, () -> filmController.add(film), "Wrong test");
        assertEquals(filmController.getAll().size(), 0, "Wrong test");
    }

    @Test
    void testAddFilm_DescriptionIsZero() {
        createFilm("", 2020);

        assertThrows(Exception.class, () -> filmController.add(film));
        assertEquals(filmController.getAll().size(), 0, "Wrong test");
    }

    @Test
    void testAddFilm_DescriptionIsTooLong() {
        char[] array = new char[201];
        Arrays.fill(array, 'a');
        String description = new String(array);
        createFilm(description, 2020);

        assertThrows(ValidationException.class, () -> filmController.add(film), "Wrong test");
        assertEquals(filmController.getAll().size(), 0, "Wrong test");
    }

    @Test
    void testAddFilm_NameIsEmpty() {
        createFilm("Sad movie about a shipwreck", 2000);
        film.setName("");

        assertThrows(ValidationException.class, () -> filmController.add(film), "Wrong test");
    }

    @Test
    void testAddFilm_WrongDuration() {
        createFilm("Sad movie about a shipwreck", 2000);
        film.setDuration(-300);

        assertThrows(ValidationException.class, () -> filmController.add(film), "Wrong test");
    }
}