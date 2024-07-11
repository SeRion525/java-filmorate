package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.time.LocalDate;

@Data
public class User {
    private Long id;

    public User() {
    }

    public User(Long id, String email, String login, String name, LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;

        if (name == null || name.isBlank()) {
            this.name = login;
        } else {
            this.name = name;
        }

        this.birthday = birthday;
    }

    @Email
    @NotNull
    @NotBlank
    private String email;

    @NotNull
    @NotBlank
    private String login;

    private String name;

    @PastOrPresent
    private LocalDate birthday;

    public void setLogin(@NotNull @NotBlank String login) {
        if (this.name == null || this.name.equals(this.login)) {
            this.name = login;
        }

        this.login = login;
    }
}
