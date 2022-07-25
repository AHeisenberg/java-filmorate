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

    private String content;
    @JsonProperty("isPositive")
    private Boolean isPositive;

    private Long userId;

    private Long filmId;
    @NotNull
    private long useful;

}
