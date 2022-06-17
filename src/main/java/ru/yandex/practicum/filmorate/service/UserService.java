package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    final private UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addFriend(Long id, Long friendId) {
        User user = userStorage.getUser(id);
        User friend = userStorage.getUser(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(id);
        return user;
    }

    public User removeFriend(Long id, Long friendId) {
        User user = userStorage.getUser(id);
        User friend = userStorage.getUser(friendId);

        if (user.getFriends().contains(friendId)) {
            user.getFriends().remove(friendId);
        } else {
            throw new UserNotFoundException();
        }

        if (friend.getFriends().contains(id)) {
            friend.getFriends().remove(id);
        } else {
            throw new UserNotFoundException();
        }

        return user;
    }

    public List<User> getUserFriends(Long id) {
        return userStorage.getUser(id).getFriends().stream().map(userStorage::getUser).collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Long id, Long otherId) {

        Set<Long> userFriends = userStorage.getUser(id).getFriends();
        Set<Long> friendFriends = userStorage.getUser(otherId).getFriends();

        return userFriends.stream()
                .filter(friendFriends::contains)
                .map(userStorage::getUser).collect(Collectors.toList());
    }
}
