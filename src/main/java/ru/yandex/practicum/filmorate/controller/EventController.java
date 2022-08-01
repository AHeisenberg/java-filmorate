package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.service.EventService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class EventController {

    private final EventService eventService;

    @GetMapping("/{id}/feed")
    public ResponseEntity<List<Event>> showEventFeed(@PathVariable long id) {
        return new ResponseEntity<>(eventService.showEventFeed(id), HttpStatus.OK);
    }
}
