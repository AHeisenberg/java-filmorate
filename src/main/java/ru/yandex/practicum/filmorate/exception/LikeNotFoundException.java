package ru.yandex.practicum.filmorate.exception;

public class LikeNotFoundException extends RuntimeException {
    public LikeNotFoundException() {
        super("This like doesn't exist");
    }
}



