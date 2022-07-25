package ru.yandex.practicum.filmorate.exception;


public class ReviewNotFound extends RuntimeException{
    public ReviewNotFound() {
        super("Review has not been found!");
    }

}
