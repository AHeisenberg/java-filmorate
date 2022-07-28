package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MPAStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
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
    private static final String SQL_UPDATE_GENRES_FILM = "INSERT INTO films_genres(film_id, genre_id) values (?, ?)";
    private static final String SQL_GET_FILM = "SELECT * FROM films WHERE film_id = ?";
    private static final String SQL_DELETE_FILM = "DELETE FROM films WHERE film_id = ?";
    private static final String SQL_REMOVE_LIKE = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
    private static final String SQL_LIKES_REMOVE_QUERY = "UPDATE films SET " +
            "likes_count = likes_count - 1 " +
            "WHERE film_id = ?";
    private static final String SQL_ADD_LIKE = "INSERT INTO likes(film_id, user_id) " +
            "values (?, ?)";
    private static final String SQL_LIKES_ADD_QUERY = "UPDATE films SET likes_count = ? WHERE film_id = ?";
    private static final String GET_USER_LIKES = "SELECT user_id, film_id FROM likes";
    private static final String SQL_UPDATE_DIRECTORS_FILM = "INSERT INTO film_directors(film_id, director_id) " +
            "VALUES (?, ?)";
    private static final String SQL_GET_DIRECTOR_BY_ID = "SELECT * FROM directors JOIN film_directors " +
            "AS fd ON directors.director_id = fd.director_id WHERE fd.film_id = ?";
    private static final String SQL_DELETE_DIRECTORS = "DELETE FROM film_directors WHERE film_id = ?";
    private static final String SQL_DIRECTORS_FILM_ORDER_BY_FILM_ID = "SELECT f.*, fd.director_id FROM films AS f " +
            "JOIN film_directors AS fd ON f.film_id = fd.film_id WHERE fd.director_id = ? ORDER BY f.film_id";
    private static final String SQL_DIRECTORS_FILM_ORDER_BY_YEAR = "SELECT f.*, fd.director_id FROM films AS f " +
            "JOIN film_directors AS fd ON f.film_id = fd.film_id WHERE fd.director_id = ? ORDER BY f.release_date";
    private static final String SQL_DIRECTORS_FILM_ORDER_BY_LIKES = "SELECT f.*, fd.director_id FROM films AS f " +
            "JOIN film_directors AS fd ON f.film_id = fd.film_id WHERE fd.director_id = ? ORDER BY f.likes_count";

    private static final String SQL_GET_TOP_LIKEBLE_FILMS = "SELECT * FROM films ORDER BY likes_count DESC LIMIT ?";
    private static final String FIND_TOP_FILMS_BY_GENRE = "SELECT * FROM films WHERE film_id IN " +
            "(SELECT film_id FROM films_genres WHERE genre_id = ?) ORDER BY likes_count DESC LIMIT ?";
    private static final String FIND_TOP_FILMS_BY_YEAR = "SELECT * FROM films WHERE " +
            "EXTRACT(YEAR FROM release_date) = ? ORDER BY likes_count DESC LIMIT ?";
    private static final String FIND_TOP_FILMS_BY_YEAR_AND_GENRE = "SELECT * FROM films WHERE film_id IN " +
            "(SELECT film_id FROM films_genres WHERE genre_id = ?) AND EXTRACT(YEAR FROM release_date) = ? " +
            "ORDER BY likes_count DESC LIMIT ?";
    private static final String GET_FILM_BY_USER = "SELECT * FROM films WHERE film_id IN " +
            "(SELECT film_id FROM likes WHERE user_id = ?) ORDER BY likes_count DESC";

    private static final String SQL_GET_FILMS_BY_SUBSTRING_NAME = "SELECT * FROM films WHERE LOWER(name) LIKE ?";

    private static final String SQL_GET_FILMS_BY_SUBSTRING_DIRECTOR = "SELECT f.* FROM films AS f JOIN " +
            "film_directors AS fd ON f.film_id = fd.film_id JOIN directors AS d ON fd.director_id = d.director_id " +
            "WHERE LOWER(d.director_name) LIKE ?";

    private static final String SQL_GET_FILMS_BY_SUBSTRING_NAME_DIR = "SELECT f.* FROM films AS f JOIN " +
            "film_directors AS fd ON f.film_id = fd.film_id JOIN directors AS d ON d.director_id = fd.director_id " +
            "WHERE LOWER(d.director_name) LIKE ? OR LOWER(f.name) LIKE ?";

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
        if (film.getDirectors() != null) {
            for (Director director : film.getDirectors()) {
                jdbcTemplate.update(SQL_UPDATE_DIRECTORS_FILM, filmId, director.getId());
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
            jdbcTemplate.update(SQL_DELETE_DIRECTORS, film.getId());
        }

        if (isUpdated && film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(SQL_UPDATE_GENRES_FILM, film.getId(), genre.getId());
            }
        }

        if (film.getDirectors() != null) {
            for (Director director : film.getDirectors()) {
                jdbcTemplate.update(SQL_UPDATE_DIRECTORS_FILM, film.getId(), director.getId());
            }
            film.setDirectors(getDirectorsByFilmId(film.getId()));
        }
        film.setGenres(setGenresToFilm(film.getId()));
        return isUpdated ? Optional.of(film) : Optional.empty();

    }

    @Override
    public List<Film> getAllFilms() {
        List<Film> films = jdbcTemplate.query(SQL_GET_ALL_FILMS, this::mapRowToFilm);
        for (Film film : films) {
            long id = film.getId();
            film.setGenres(setGenresToFilm(id));
            film.setDirectors(getDirectorsByFilmId(id));
        }
        return films;
    }

    @Override
    public Optional<Film> getFilm(long id) {
        List<Film> result = jdbcTemplate.query(SQL_GET_FILM, this::mapRowToFilm, id);
        Optional<Film> optFilm = result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
        if (optFilm.isPresent()) {
            optFilm.get().setGenres(setGenresToFilm(id));
            optFilm.get().setDirectors(getDirectorsByFilmId(optFilm.get().getId()));
        }
        return optFilm;
    }

    @Override
    public List<Film> getAllFilmsByDirector(long id, String sortBy) {
        List<Film> films;
        if (sortBy.equals("id")) {
            films = jdbcTemplate.query(SQL_DIRECTORS_FILM_ORDER_BY_FILM_ID, this::mapRowToFilm, id);
        } else if (sortBy.equals("year")) {
            films = jdbcTemplate.query(SQL_DIRECTORS_FILM_ORDER_BY_YEAR, this::mapRowToFilm, id);
        } else {
            films = jdbcTemplate.query(SQL_DIRECTORS_FILM_ORDER_BY_LIKES, this::mapRowToFilm, id);
        }
        for (Film film : films) {
            long filmId = film.getId();
            film.setGenres(setGenresToFilm(filmId));
            film.setDirectors(getDirectorsByFilmId(filmId));
        }
        return films;
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


    private Set<Director> getDirectorsByFilmId(long id) {
        return new HashSet<>(jdbcTemplate.query(SQL_GET_DIRECTOR_BY_ID, this::mapRowToDirector, id));
    }

    private Set<Genre> setGenresToFilm(long id) {
        return jdbcTemplate.queryForList(SQL_GENRE_QUERY, Long.class, id)
                .stream()
                .map(genreId -> genreStorage.getGenre(genreId).get())

                .collect(Collectors.toSet());
    }


    @Override
    public Map<Long, Set<Long>> getUserLikes() {

        Map<Long, Set<Long>> likes = new HashMap<>();
        jdbcTemplate.query(GET_USER_LIKES, (rs) -> {
            long userId = rs.getInt("user_id");
            long filmId = rs.getInt("film_id");
            likes.merge(userId, new HashSet<>(Set.of(filmId)), (oldValue, newValue) -> {
                oldValue.add(filmId);
                return oldValue;
            });
        });
        return likes;
    }


    private Director mapRowToDirector(ResultSet resultSet, int rowNum) throws SQLException {

        return Director.builder()
                .id(resultSet.getInt("director_id"))
                .name(resultSet.getString("director_name"))
                .build();
    }


    @Override
    public List<Film> getTopLikableFilms(long count) {
        List<Film> films = jdbcTemplate.query(SQL_GET_TOP_LIKEBLE_FILMS, this::mapRowToFilm, count);
        films.forEach(f -> f.setGenres(setGenresToFilm(f.getId())));
        return films;
    }

    @Override
    public List<Film> getTopFilmsByYear(long count, int year) {
        List<Film> films = jdbcTemplate.query(FIND_TOP_FILMS_BY_YEAR, this::mapRowToFilm, year, count);
        films.forEach(f -> f.setGenres(setGenresToFilm(f.getId())));
        return films;
    }

    @Override
    public List<Film> getTopFilmsByGenre(long count, int genreId) {
        List<Film> films = jdbcTemplate.query(FIND_TOP_FILMS_BY_GENRE, this::mapRowToFilm, genreId, count);
        films.forEach(f -> f.setGenres(setGenresToFilm(f.getId())));
        return films;
    }

    @Override
    public List<Film> getTopFilmsByGenreAndYear(long count, int genreId, int year) {
        List<Film> films = jdbcTemplate.query(FIND_TOP_FILMS_BY_YEAR_AND_GENRE,
                this::mapRowToFilm, genreId, year, count);
        films.forEach(f -> f.setGenres(setGenresToFilm(f.getId())));
        return films;
    }

    @Override
    public List<Film> getFilmsBySubstring(String query, String by) {
        List<Film> films = new ArrayList<>();
        String stringSearch = "%" + query.toLowerCase() + "%";
        if (by.contains("title")) {
            films.addAll(jdbcTemplate.query(SQL_GET_FILMS_BY_SUBSTRING_NAME, this::mapRowToFilm, stringSearch));
        }
        if (by.contains("director")) {
            films.addAll(0, jdbcTemplate.query(SQL_GET_FILMS_BY_SUBSTRING_DIRECTOR, this::mapRowToFilm,
                    stringSearch));
        }
        for (Film film : films) {
            long id = film.getId();
            film.setGenres(setGenresToFilm(id));
            film.setDirectors(getDirectorsByFilmId(id));
        }
        return films;
    }

    @Override
    public List<Film> getTopFilmsByUser(long userId) {
        List<Film> films = jdbcTemplate.query(GET_FILM_BY_USER, this::mapRowToFilm, userId);
        films.forEach(f -> f.setGenres(setGenresToFilm(f.getId())));
        return films;
    }

}


