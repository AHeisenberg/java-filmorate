package ru.yandex.practicum.filmorate.exception;


public class ReviewNotFoundException extends RuntimeException {
    public ReviewNotFoundException() {
        super("Review has not been found!");
    }
}
