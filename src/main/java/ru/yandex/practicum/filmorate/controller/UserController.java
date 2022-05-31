package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    public void deleteAllUsers() {
        users.clear();
    }

    @PostMapping
    public User add(@Valid @NotNull @RequestBody User user) {
        if (!users.containsKey(user.getId())) {
            if (isValidDateOfBirthday(user) && isExistsEmail(user)) {
                if (user.getName().isBlank()) {
                    user.setName(user.getLogin());
                    users.put(user.getId(), user);
                    log.debug("The user has been added with name=login and id={}", user.getId());
                } else {
                    users.put(user.getId(), user);
                    log.debug("The user has been added with id={}", user.getId());
                }
            }
        } else {
            throw new ValidationException("This user  or email already exists");
        }
        return user;
    }


    @PutMapping
    public User update(@Valid @NotNull @RequestBody User user) {
        if (users.containsKey(user.getId())
                && (isExistsEmail(user) || users.get(user.getId()).getEmail().equals(user.getEmail()))) {
// The user should not be able to update his email to a mail already existing in the system and belonging to the OTHER user
            if (isValidDateOfBirthday(user)
                   ) {
                if (user.getName().isBlank()) {
                    user.setName(user.getLogin());
                    users.put(user.getId(), user);
                    log.debug("The user has been updated with name=login and id={}", user.getId());
                } else {
                    users.put(user.getId(), user);
                    log.debug("The user has been updated with id={}", user.getId());
                }
            }
        } else {
            throw new ValidationException("This user or email doesn't exist");
        }
        return user;
    }


    private boolean isValidDateOfBirthday(User user) {
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("The date of birth is wrong - it should be earlier today");
        } else {
            return true;
        }
    }

    private boolean isExistsEmail(User user) {
        boolean res = true;
        for (User u : users.values()) {
            if (u.getEmail().equals(user.getEmail())) {
                res = false;
                log.debug("The user  with E-Mail " + user.getEmail() + " already exists");
//                throw new ValidationException("The E-Mail already exists");
            }
        }
        return res;
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<String> exceptionHandler(ValidationException e) {
        log.debug(e.getMessage());
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

}