package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.RatingMPA;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class MPADbStorageTest {

    private final MPADbStorage storage;

    @Test
    public void testGetMPA_AllRight() {
        Optional<RatingMPA> ratingMPA = storage.getRatingMPA(5);
        assertThat(ratingMPA)
                .isPresent()
                .hasValueSatisfying(mpa ->
                        assertThat(mpa).hasFieldOrPropertyWithValue("id", 5)
                                .hasFieldOrPropertyWithValue("name", "NC-17")
                );
    }

    @Test
    public void testGetMPA_WrongId() {
        Optional<RatingMPA> ratingMPA = storage.getRatingMPA(100);
        assertThat(ratingMPA).isEmpty();
    }

    @Test
    public void testGetAllMPA_AllRight() {
        List<RatingMPA> mpaList = storage.getAllRatings();
        assertEquals(5, mpaList.size());
        assertThat(mpaList.get(0)).hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("name", "G");
        assertThat(mpaList.get(4)).hasFieldOrPropertyWithValue("id", 5)
                .hasFieldOrPropertyWithValue("name", "NC-17");
    }

    @Test
    public void testRemoveMPA_AllRight() {
        assertTrue(storage.deleteRatingMPA(1));
        assertEquals(4, storage.getAllRatings().size());
    }

    @Test
    public void testRemoveMPA_WrongId() {
        assertFalse(storage.deleteRatingMPA(100));
        assertEquals(5, storage.getAllRatings().size());
    }

    @Test
    public void testAddMPA_AllRight() {
        RatingMPA ratingMPA = storage.addRatingMPA(RatingMPA.builder().id(10).name("A").build());
        assertEquals(6, ratingMPA.getId());
        assertEquals(6, storage.getAllRatings().size());
    }

    @Test
    public void testUpdateMPA_AllRight() {
        storage.updateRatingMPA(RatingMPA.builder().id(1).name("A").build());
        assertThat(storage.getRatingMPA(1).get())
                .hasFieldOrPropertyWithValue("name", "A");
    }
}
