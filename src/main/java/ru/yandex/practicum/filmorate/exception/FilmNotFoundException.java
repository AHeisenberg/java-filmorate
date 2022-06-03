package ru.yandex.practicum.filmorate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.yandex.practicum.filmorate.model.Film;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class FilmNotFoundException extends Exception {

    public FilmNotFoundException(Film film) {
        super("Film not found: id=" + film.getId() + ", name=" + film.getName());
    }
}



