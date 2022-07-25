package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.List;

@RestController
@RequestMapping(value = "/directors")
@Slf4j
public class DirectorController {

    private final DirectorService directorService;

    @Autowired
    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Director> addDirector(@RequestBody Director director) {
        return new ResponseEntity<>(directorService.addDirector(director), HttpStatus.CREATED);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Director> updateDirector(@RequestBody Director director) {
        return directorService.updateDirector(director).map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Director> getDirectorById(@PathVariable long id) {
        return directorService.getDirector(id).map(director -> new ResponseEntity<>(director, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity<List<Director>> getAllDirectors() {
        return new ResponseEntity<>(directorService.getAllDirectors(), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Director> deleteDirector(@PathVariable long id) {
        return directorService.deleteDirector(id) ? new ResponseEntity<>(null, HttpStatus.OK)
                : new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }
}
