package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class UserControllerTest {

    @Autowired
    private UserController userController;

    private User user;

    @AfterEach
    private void afterEach() {
        userController.deleteAllUsers();
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
        createUser("Login", 2000);
        user.setEmail("user.host.com");
        userController.add(user);

        assertThrows(ValidationException.class, () -> userController.add(user), "Wrong test");
    }

    @Test
    void testAddUser_EmailIsExist() {
        createUser("Login", 2000);
        userController.add(user);
        User user2 = new User(2, "user@host.com", "Login", "Name",
                LocalDate.of(2000, 1, 1));

        userController.add(user2);
        assertEquals(userController.getAll().size(), 1, "Wrong test");
    }


    @Test
    void testUpdateUser_EmailIsOccupiedByAnotherUser() {
        createUser("Login", 2000);
        userController.add(user);
        User user2 = new User(2, "user2@host.com", "Login", "Name",
                LocalDate.of(2000, 1, 1));
        user2.setEmail("user@host.com");

        assertThrows(ValidationException.class, () -> userController.update(user2), "Wrong test");
    }

    @Test
    void testUpdateUser_AllRight() {
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