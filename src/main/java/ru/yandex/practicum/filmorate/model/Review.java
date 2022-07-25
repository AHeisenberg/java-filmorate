package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class Review {
    @NotNull
    private long reviewId;
    @NotNull
    private String content;
    @NotNull
    private boolean isPositive;
    @NotNull
    private long userId;
    @NotNull
    private long filmId;
    @NotNull
    private long useful;
}
