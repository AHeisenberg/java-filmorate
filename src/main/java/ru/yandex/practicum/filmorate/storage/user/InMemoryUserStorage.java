package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.service.UserValidator.checkUser;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private static long id = 0;

    private long getId() {
        return ++id;
    }

    private final Map<Long, User> users;

    private final Map<Long, Set<Long>> friends;

    public InMemoryUserStorage() {
        users = new HashMap<>();
        friends = new HashMap<>();
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
    public Optional<User> updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            throw new UserNotFoundException();

        }
        checkUser(user);
        log.info("Update user. New data {}", user);
        users.put(user.getId(), user);
        return Optional.of(user);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> getUser(long id) {
        User user = users.get(id);
        if (user == null) {
            throw new UserNotFoundException();
        }
        return Optional.of(user);
    }

    @Override
    public boolean deleteUser(long id) {
        return users.remove(id) != null;
    }

    @Override
    public boolean addFriend(long id, long friendId) {
        if (friends.containsKey(id)) {
            return friends.get(id).add(friendId);
        }
        Set<Long> userFriends = new HashSet<>();
        userFriends.add(friendId);
        friends.put(id, userFriends);
        return true;
    }

    @Override
    public boolean removeFriend(long id, long friendId) {
        if (friends.containsKey(id)) {
            return friends.get(id).remove(friendId);
        }
        return false;
    }

    @Override
    public List<User> getUserFriends(long id) {
        if (friends.containsKey(id)) {
            return friends.get(id).stream().map(users::get).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
