package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;


@Data
@AllArgsConstructor
public class User {

    @NotNull
    private long id;

    @NotNull
    @NotBlank
    @Email
    private final String email;

    @NotNull
    @NotBlank
    @NotEmpty
    private final String login;

    private String name;

    @NotNull
    private final LocalDate birthday;

}
