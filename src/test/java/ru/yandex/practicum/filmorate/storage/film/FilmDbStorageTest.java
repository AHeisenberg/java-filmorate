package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RatingMPA;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FilmDbStorageTest {

    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;

    private void createFilm() {
        filmDbStorage.addFilm(Film.builder().id(1L)
                .name("Titanic")
                .description("Sad movie about a shipwreck")
                .duration(320)
                .mpa(RatingMPA.builder().id(1).name("G").build())
                .releaseDate(LocalDate.of(2000, 1, 1))
                .build());
    }

    @Test
    public void testGetFilm_AllRight() {
        createFilm();
        Optional<Film> filmOptional = filmDbStorage.getFilm(1);
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 1L)
                                .hasFieldOrPropertyWithValue("name",
                                        "Titanic")
                                .hasFieldOrPropertyWithValue("description",
                                        "Sad movie about a shipwreck")
                                .hasFieldOrPropertyWithValue("duration", 320)
                );
        assertEquals(1, filmOptional.get().getMpa().getId());
        assertEquals("G", filmOptional.get().getMpa().getName());

    }

    @Test
    public void testAddGenre() {
        Set<Genre> genreSet = new HashSet<>();
        Genre genre = Genre.builder().id(1).name("Драма").build();
        genreSet.add(genre);
        filmDbStorage.addFilm(Film.builder()
                .name("Titanic")
                .description("Sad movie about a shipwreck")
                .duration(143)
                .mpa(RatingMPA.builder().id(2).name("PG").build())
                .releaseDate(LocalDate.of(2003, 7, 9))
                .genres(genreSet)
                .build());

        assertEquals(1, genreSet.size());
        assertTrue(genreSet.contains(genre));
    }

    @Test
    public void testGetFilm_WrongId() {
        Optional<Film> filmOptional = filmDbStorage.getFilm(10);
        assertThat(filmOptional).isEmpty();
    }

    @Test
    public void testGetAllFilms() {
        createFilm();
        filmDbStorage.addFilm(Film.builder()
                .name("Last Samurai")
                .description("Epic period action drama film")
                .duration(150)
                .mpa(RatingMPA.builder().id(1).name("G").build())
                .releaseDate(LocalDate.of(2000, 1, 1))
                .build());
        List<Film> films = filmDbStorage.getAllFilms();
        assertEquals(2, films.size());
        assertThat(films.get(0)).hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name",
                        "Titanic");
        assertThat(films.get(1)).hasFieldOrPropertyWithValue("id", 2L)
                .hasFieldOrPropertyWithValue("name", "Last Samurai");
    }

    @Test
    public void testDeleteFilm() {
        createFilm();
        assertTrue(filmDbStorage.deleteFilm(1));
        assertEquals(0, filmDbStorage.getAllFilms().size());
    }

    @Test
    public void testDeleteFilm_WrongId() {
        createFilm();
        assertFalse(filmDbStorage.deleteFilm(10));
        assertEquals(1, filmDbStorage.getAllFilms().size());
    }

    @Test
    public void testCreate() {
        createFilm();
        assertEquals(1, filmDbStorage.getAllFilms().size());
    }

    @Test
    public void testUpdate() {
        createFilm();
        filmDbStorage.updateFilm(Film.builder()
                .id(1)
                .name("Titanic - Submarine")
                .description("Sad movie about a shipwreck")
                .duration(320)
                .mpa(RatingMPA.builder().id(3).name("PG-13").build())
                .releaseDate(LocalDate.of(2000, 1, 1))
                .build());
        assertThat(filmDbStorage.getFilm(1).get())
                .hasFieldOrPropertyWithValue("name", "Titanic - Submarine");
        assertEquals(1, filmDbStorage.getAllFilms().size());
    }

    @Test
    public void testAddLike() {
        userDbStorage.addUser(User.builder()
                .email("user@host.com")
                .login("Login")
                .name("Name")
                .birthday(LocalDate.of(1980, 1, 1))
                .build());
        createFilm();
        assertTrue(filmDbStorage.addLike(1, 1));
        assertEquals(1, filmDbStorage.getFilm(1).get().getLikesCount());
    }

    @Test
    public void testRemoveLike() {
        testAddLike();
        assertTrue(filmDbStorage.removeLike(1, 1));
        assertEquals(0, filmDbStorage.getFilm(1).get().getLikesCount());
    }
}
