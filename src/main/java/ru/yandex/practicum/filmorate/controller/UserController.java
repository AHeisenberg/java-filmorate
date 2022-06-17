package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@RestController
@RequestMapping("/")
@Slf4j
public class UserController {

    private final UserStorage userStorage;
    private final UserService userService;

    @Autowired
    public UserController(UserStorage userStorage, UserService userService) {
        this.userStorage = userStorage;
        this.userService = userService;
    }

    @GetMapping(value = "/users")
    public List<User> getAll() {
        return userStorage.getAllUsers();
    }

    @GetMapping("/users/{id}")
    public User getUser(@PathVariable String id) {
        return userStorage.getUser(Long.valueOf(id));
    }

    @PostMapping(value = "/users")
    public User add(@RequestBody User user) {
        return userStorage.addUser(user);
    }

    @PutMapping(value = "/users")
    public User update(@RequestBody User user) {
        return userStorage.updateUser(user);
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public User addFriend(
            @PathVariable Long id,
            @PathVariable Long friendId) {
        if ((id != null) && (friendId != null)) {

            return userService.addFriend(id, friendId);
        } else {
            return null;
        }
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public User removeFriend(
            @PathVariable Long id,
            @PathVariable Long friendId) {
        if ((id != null) && (friendId != null)) {

            return userService.removeFriend(id, friendId);
        } else {
            return null;
        }
    }

    @GetMapping("/users/{id}/friends")
    public List<User> getUserFriends(@PathVariable Long id) {
        return userService.getUserFriends(id);
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(
            @PathVariable Long id,
            @PathVariable Long otherId) {
        if ((id != null) && (otherId != null)) {

            return userService.getCommonFriends(id, otherId);
        } else {
            return null;
        }
    }

}
