package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class UserControllerTest {

    @Autowired
    private UserController userController;

    @Autowired
    private UserStorage userStorage;

    private User user;

    @AfterEach
    private void afterEach() {
        userStorage.deleteAllUsers();
    }

    private void createUser(String login, int yearOfBirthday) {
        user = new User(1, "user@host.com", login, "Name",
                LocalDate.of(yearOfBirthday, 1, 1));
    }

    @Test
    void testAddUser_AllRight() {
        createUser("Login", 2000);
        userController.add(user);
        assertEquals(user, userController.getAll().get(0), "Wrong test");
    }

    @Test
    void testAddUser_EmailIsWrong() {
        user = new User(1, "user.host.com", "Login", "Name",
                LocalDate.of(2020, 1, 1));
        assertThrows(ValidationException.class, () -> userController.add(user), "Wrong test");
    }


    @Test
    void testUpdateUser_AllRight() throws UserNotFoundException {
        createUser("Login", 2000);
        userController.add(user);
        user.setName("NewName");
        userController.update(user);

        assertEquals(user.getName(), userController.getAll().get(0).getName(), "Wrong test");

    }

    @Test
    void testAddUser_DateOfBirthdayIsNotValid() {
        createUser("Login", 3000);
        assertThrows(ValidationException.class, () -> userController.add(user), "Wrong test");
        assertEquals(userController.getAll().size(), 0, "Wrong test");
    }


    @Test
    void testAddUser_NameIsBlank() {
        createUser("Login", 2000);
        user.setName("");
        userController.add(user);
        assertEquals(user.getLogin(), user.getName(), "Wrong test");
    }
}