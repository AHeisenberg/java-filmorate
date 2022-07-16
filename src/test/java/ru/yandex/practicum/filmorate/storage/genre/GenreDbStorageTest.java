package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class GenreDbStorageTest {

    private final GenreDbStorage genreDbStorage;

    @Test
    public void testGetGenre_AllRight() {
        Optional<Genre> optionalGenre = genreDbStorage.getGenre(1);
        assertThat(optionalGenre)
                .isPresent()
                .hasValueSatisfying(genre ->
                        assertThat(genre).hasFieldOrPropertyWithValue("id", 1)
                                .hasFieldOrPropertyWithValue("name", "Комедия")
                );
    }

    @Test
    public void testGetGenre_WrongId() {
        Optional<Genre> genre = genreDbStorage.getGenre(10);
        assertThat(genre).isEmpty();
    }

    @Test
    public void testGetAllGenres_AllRight() {
        List<Genre> genres = genreDbStorage.getAllGenre();
        assertEquals(6, genres.size());
        assertThat(genres.get(0)).hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("name", "Комедия");
        assertThat(genres.get(1)).hasFieldOrPropertyWithValue("id", 2)
                .hasFieldOrPropertyWithValue("name", "Драма");
    }

    @Test
    public void testDeleteGenre_AllRight() {
        assertTrue(genreDbStorage.deleteGenre(1));
        assertEquals(5, genreDbStorage.getAllGenre().size());
    }

    @Test
    public void testDeleteGenre_WrongId() {
        assertFalse(genreDbStorage.deleteGenre(100));
        assertEquals(6, genreDbStorage.getAllGenre().size());
    }

    @Test
    public void testAddGenre_AllRight() {
        Genre genre = genreDbStorage.addGenre(Genre.builder().id(10).name("Мыльная опера").build());
        assertEquals(7, genre.getId());
        assertEquals(7, genreDbStorage.getAllGenre().size());
    }

    @Test
    public void testUpdateGenre() {
        genreDbStorage.updateGenre(Genre.builder().id(1).name("Мыльная опера").build());
        assertThat(genreDbStorage.getGenre(1).get())
                .hasFieldOrPropertyWithValue("name", "Мыльная опера");
        assertEquals(6, genreDbStorage.getAllGenre().size());
    }
}
