package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
@AllArgsConstructor

public class Film {

    @NotNull
    private long id;

    @NotNull
    @NotBlank
    private String name;

    @NotNull
    @Size(max = 200)
    @NotBlank
    private String description;

    @NotNull
    private final LocalDate releaseDate;

    @NotNull
    private int duration;

}
