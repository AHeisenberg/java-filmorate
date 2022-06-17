package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.yandex.practicum.filmorate.service.UserValidator.checkUser;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private static long id = 0;

    public static long getId() {
        return ++id;
    }

    private final Map<Long, User> users = new HashMap<>();

    public void deleteAllUsers() {
        log.info("All users have been deleted");
        users.clear();
    }

    @Override
    public User addUser(User user) {
        checkUser(user);
        user.setId(getId());
        users.put(user.getId(), user);
        log.info("Added user {}", user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            throw new UserNotFoundException();
        }
        checkUser(user);
        log.info("Update user. New data {}", user);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUser(Long id) {
        User user = users.get(id);
        if (user == null) {
            throw new UserNotFoundException();
        }
        return user;
    }

}
