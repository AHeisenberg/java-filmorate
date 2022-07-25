package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("isPositive")
    private boolean isPositive;
    @NotNull
    private long userId;
    @NotNull
    private long filmId;
    @NotNull
    private long useful;

}
