package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserDbStorageTest {
    private final UserDbStorage userDbStorage;

    private void createUser() {
        userDbStorage.addUser(User.builder().id(1L)
                .email("user@host.com")
                .login("Login")
                .name("Name")
                .birthday(LocalDate.of(1980, 12, 31))
                .build());
    }

    @Test
    public void testGetUser_AllRight() {
        createUser();
        Optional<User> userOptional = userDbStorage.getUser(1);
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(User ->
                        assertThat(User).hasFieldOrPropertyWithValue("id", 1L)
                                .hasFieldOrPropertyWithValue("email", "user@host.com")
                                .hasFieldOrPropertyWithValue("login", "Login")
                                .hasFieldOrPropertyWithValue("name", "Name")
                );
    }

    @Test
    public void testGetUser_WrongId() {
        Optional<User> userOptional = userDbStorage.getUser(100);
        assertThat(userOptional).isEmpty();
    }

    @Test
    public void testGetAllUsers_AllRight() {
        createUser();
        userDbStorage.addUser(User.builder()
                .email("user2@host.com")
                .login("Login2")
                .name("Name2")
                .birthday(LocalDate.of(1981, 1, 1))
                .build());
        List<User> users = userDbStorage.getAllUsers();
        assertEquals(2, users.size());
        assertThat(users.get(0)).hasFieldOrPropertyWithValue("email", "user@host.com");
        assertThat(users.get(1)).hasFieldOrPropertyWithValue("email", "user2@host.com");

    }

    @Test
    public void testDeleteUser_AllRight() {
        createUser();
        assertTrue(userDbStorage.deleteUser(1));
        assertEquals(0, userDbStorage.getAllUsers().size());
    }

    @Test
    public void testDeleteUser_WrongId() {
        createUser();
        boolean isDeleted = userDbStorage.deleteUser(10);
        assertFalse(isDeleted);
        assertEquals(1, userDbStorage.getAllUsers().size());
    }

    @Test
    public void testCreateUser_AllRight() {
        createUser();
        assertEquals(1, userDbStorage.getAllUsers().size());
    }

    @Test
    public void testUpdate() {
        createUser();
        userDbStorage.updateUser(User.builder()
                .id(1L)
                .email("user@host.com")
                .login("Login")
                .name("UpdatedName")
                .birthday(LocalDate.of(1980, 12, 31))
                .build());
        assertThat(userDbStorage.getUser(1).get())
                .hasFieldOrPropertyWithValue("name", "UpdatedName");
        assertEquals(1, userDbStorage.getAllUsers().size());
    }

    @Test
    public void testAddFriends_AllRight() {
        createUser();
        User friend = userDbStorage.addUser(User.builder()
                .email("friend@host.com")
                .login("Login2")
                .name("Name2")
                .birthday(LocalDate.of(1994, 1, 7))
                .build());
        assertTrue(userDbStorage.addFriend(1, 2));
        assertEquals(1, userDbStorage.getUserFriends(1).size());
        assertTrue(userDbStorage.getUserFriends(1).contains(friend));
        assertEquals(0, userDbStorage.getUserFriends(2).size());
    }

}
