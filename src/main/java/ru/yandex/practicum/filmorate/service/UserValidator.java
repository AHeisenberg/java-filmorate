package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

@Slf4j
public class UserValidator {

    private static void checkUserEmail(String email) {
        if (!email.contains("@")) {
            log.debug("The email address is incorrect - {}", email);
            throw new ValidationException("The email address is incorrect");
        }
    }

    private static void checkValidUserLogin(String login) {
        if (!(StringUtils.hasLength(login))
                || StringUtils.containsWhitespace(login)) {
            throw new ValidationException("Login is not correct");
        }
    }

    private static void checkValidUserName(User user) {
        if (!(StringUtils.hasLength(user.getName()))) {
            user.setName(user.getLogin());
        }
    }

    private static void checkValidDateOfBirthday(User user) {
        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("The date of birth is wrong - it should be earlier today");
        }
    }

    public static void checkUser(User user) {
        checkUserEmail(user.getEmail());
        checkValidUserLogin(user.getLogin());
        checkValidUserName(user);
        checkValidDateOfBirthday(user);
    }
}

