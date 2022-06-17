package ru.yandex.practicum.filmorate.exception;

public class FriendNotFoundException extends RuntimeException {
    public FriendNotFoundException() {
        super("This friend doesn't exist");
    }
}

