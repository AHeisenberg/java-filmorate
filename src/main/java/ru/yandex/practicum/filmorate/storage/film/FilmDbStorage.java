package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MPAStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class FilmDbStorage implements FilmStorage {

    private static final String SQL_ADD_FILM = "INSERT INTO films(name, description, duration, release_date, rating_id) " +
            "values (?, ?, ?, ?, ?)";
    private static final String SQL_UPDATE_FILM = "UPDATE films SET " +
            "name = ?, description = ?, duration = ?, release_date = ?, rating_id = ? " +
            "WHERE film_id = ?";
    private static final String SQL_GENRES_QUERY = "DELETE FROM films_genres WHERE film_id = ?";
    private static final String SQL_GET_ALL_FILMS = "SELECT * FROM films";
    private static final String SQL_GENRE_QUERY = "SELECT genre_id FROM films_genres WHERE film_id = ?";
    private static final String SQL_GET_FILM = "SELECT * FROM films WHERE film_id = ?";
    private static final String SQL_DELETE_FILM = "DELETE FROM films WHERE film_id = ?";
    private static final String SQL_REMOVE_LIKE = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
    private static final String SQL_LIKES_REMOVE_QUERY = "UPDATE films SET " +
            "likes_count = likes_count - 1 " +
            "WHERE film_id = ?";
    private static final String SQL_ADD_LIKE = "INSERT INTO likes(film_id, user_id) " +
            "values (?, ?)";
    private static final String SQL_LIKES_ADD_QUERY = "UPDATE films SET likes_count = ? WHERE film_id = ?";
    private static final String SQL_UPDATE_GENRES_FILM = "INSERT INTO films_genres(film_id, genre_id) values (?, ?)";
    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;
    private final MPAStorage mpaStorage;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate,
                         @Qualifier("genreDbStorage") GenreStorage genreStorage,
                         @Qualifier("MPADbStorage") MPAStorage mpaStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreStorage = genreStorage;
        this.mpaStorage = mpaStorage;
    }

    @Override
    public Film addFilm(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(SQL_ADD_FILM, new String[]{"film_id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setInt(3, film.getDuration());
            stmt.setDate(4, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        long filmId = keyHolder.getKey().longValue();

        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(SQL_UPDATE_GENRES_FILM, filmId, genre.getId());
            }
        }
        return getFilm(filmId).get();
    }

    @Override
    public Optional<Film> updateFilm(Film film) {
        boolean isUpdated = jdbcTemplate.update(SQL_UPDATE_FILM,
                film.getName(),
                film.getDescription(),
                film.getDuration(),
                film.getReleaseDate(),
                film.getMpa().getId(),
                film.getId()) > 0;

        if (isUpdated) {
            jdbcTemplate.update(SQL_GENRES_QUERY, film.getId());
        }

        if (isUpdated && film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(SQL_UPDATE_GENRES_FILM, film.getId(), genre.getId());
            }
        }
        return isUpdated ? Optional.of(getFilm(film.getId()).get()) : Optional.empty();
    }

    @Override
    public List<Film> getAllFilms() {
        List<Film> films = jdbcTemplate.query(SQL_GET_ALL_FILMS, this::mapRowToFilm);
        for (Film film : films) {
            long id = film.getId();
            Set<Genre> genreSet = jdbcTemplate.queryForList(SQL_GENRE_QUERY, Long.class, id)
                    .stream()
                    .map(genreId -> genreStorage.getGenre(genreId).get())
                    .collect(Collectors.toSet());
            film.setGenres(genreSet);
        }
        return films;
    }

    @Override
    public Optional<Film> getFilm(long id) {
        List<Film> result = jdbcTemplate.query(SQL_GET_FILM, this::mapRowToFilm, id);
        Optional<Film> optFilm = result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
        if (optFilm.isPresent()) {
            Set<Genre> genreSet = jdbcTemplate.queryForList(SQL_GENRE_QUERY, Long.class, id)
                    .stream()
                    .map(genreId -> genreStorage.getGenre(genreId).get())
                    .collect(Collectors.toSet());
            optFilm.get().setGenres(genreSet);
        }
        return optFilm;
    }

    @Override
    public boolean deleteFilm(long id) {
        return jdbcTemplate.update(SQL_DELETE_FILM, id) > 0;
    }

    @Override
    public boolean removeLike(long id, long userId) {
        boolean isRemoved = jdbcTemplate.update(SQL_REMOVE_LIKE, id, userId) > 0;
        if (isRemoved) {
            jdbcTemplate.update(SQL_LIKES_REMOVE_QUERY, id);
        }
        return isRemoved;
    }

    @Override
    public boolean addLike(long id, long userId) {
        boolean isAdded = jdbcTemplate.update(SQL_ADD_LIKE, id, userId) > 0;
        if (isAdded) {
            long likesAmount = getFilm(id).get().getLikesCount() + 1;
            jdbcTemplate.update(SQL_LIKES_ADD_QUERY, likesAmount, id);
        }
        return isAdded;
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {

        return Film.builder()
                .id(resultSet.getLong("film_id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .duration(resultSet.getInt("duration"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .mpa(mpaStorage.getRatingMPA(resultSet.getInt("rating_id")).get())
                .likesCount(resultSet.getLong("likes_count"))
                .build();
    }
}
