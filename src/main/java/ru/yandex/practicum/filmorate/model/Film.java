package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor

public class Film {

    @NotNull
    private final long id;

    @NotNull
    @NotBlank
    private String name;

    @NotNull
    @Size(max = 200)
    @NotBlank
    private String description;

    @NotNull
    private LocalDate releaseDate;

    @NotNull
//    @Positive
    private int duration;

}
