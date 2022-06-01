package ru.yandex.practicum.filmorate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.yandex.practicum.filmorate.model.User;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class UserNotFoundException extends Exception {

    public UserNotFoundException(User user) {
        super("User not found: id=" + user.getId() + ", login=" + user.getLogin());
    }

}
