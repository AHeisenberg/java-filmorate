package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RecommendationService {
    private Map<Long, Set<Long>> allLikes;
    private final FilmStorage filmStorage;

    @Autowired
    public RecommendationService(@Qualifier("filmDbStorage") FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Set<Film> findRecommendedFilmsForUser(long id) {
        Map<Long, Set<Long>> crossingMoviesWithOtherUsers = findOtherUsersWithIntersectionsByUserId(id);
        Set<Long> resultFilmIds = new HashSet<>();

        for (var entry : crossingMoviesWithOtherUsers.entrySet()) {
            Set<Long> userFilms = allLikes.get(id);
            Set<Long> otherUserFilms = entry.getValue();
            otherUserFilms.removeAll(userFilms);
            resultFilmIds.addAll(otherUserFilms);
        }
        return resultFilmIds.stream().map(filmId -> filmStorage.getFilm(filmId).get()).collect(Collectors.toSet());
    }


    private Map<Long, Set<Long>> findOtherUsersWithIntersectionsByUserId(long id) {
        this.allLikes = filmStorage.getUserLikes();
        Set<Long> userLikes = allLikes.get(id);
        Map<Long, Set<Long>> result = new HashMap<>();
        Set<Long> crossroads = new HashSet<>();

        for (var fstEntry : allLikes.entrySet()) {
            if (fstEntry.getKey().equals(id)) {
                continue;
            }
            if (fstEntry.getValue().size() > crossroads.size()) {
                crossroads = fstEntry.getValue();
                Set<Long> crossroadsCopy = new HashSet<>(crossroads);
                crossroads.retainAll(userLikes);
                if (crossroads.size() > 0 && result.size() < crossroads.size()) {
                    result.put(fstEntry.getKey(), crossroadsCopy);
                }
            }
        }
        return result;
    }
}
