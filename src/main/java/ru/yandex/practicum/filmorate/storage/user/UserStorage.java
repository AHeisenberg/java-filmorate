package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {

    User addUser(User user);

    Optional<User> updateUser(User user);

    List<User> getAllUsers();

    Optional<User> getUser(long id);

    boolean deleteUser(long id);

    boolean addFriend(long id, long friendId);

    boolean removeFriend(long id, long friendId);

    List<User> getUserFriends(long id);

}
