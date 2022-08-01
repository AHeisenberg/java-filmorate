package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.service.UserValidator.checkUser;

@Service
public class UserService {

    private final UserStorage userStorage;
    private final EventStorage eventStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage, EventStorage eventStorage) { // inMemoryUserStorage
        this.userStorage = userStorage;
        this.eventStorage = eventStorage;
    }

    public User addUser(User user) {
        checkUser(user);
        return userStorage.addUser(user);
    }

    public Optional<User> updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public Optional<User> getUser(long id) {
        return userStorage.getUser(id);
    }

    public boolean deleteUser(long id) {
        return userStorage.deleteUser(id);
    }

    public boolean addFriend(long id, long friendId) {
        Optional<User> optUser = userStorage.getUser(id);
        Optional<User> optFriend = userStorage.getUser(friendId);

        if (optUser.isPresent() && optFriend.isPresent()) {
            eventStorage.addEvent(id, friendId, Event.EventType.FRIEND, Event.Operation.ADD);
            return userStorage.addFriend(id, friendId);
        }
        return false;
    }

    public boolean removeFriend(long id, long friendId) {
        Optional<User> user = userStorage.getUser(id);
        Optional<User> friend = userStorage.getUser(friendId);

        if (user.isPresent() && friend.isPresent()) {
            eventStorage.addEvent(id, friendId, Event.EventType.FRIEND, Event.Operation.REMOVE);
            return userStorage.removeFriend(id, friendId);
        }
        return false;
    }

    public List<User> getUserFriends(long id) {
        return userStorage.getUserFriends(id);
    }

    public List<User> getCommonFriends(long id, long otherId) {
        return getUserFriends(id).stream()
                .filter(getUserFriends(otherId)::contains)
                .collect(Collectors.toList());
    }

}
