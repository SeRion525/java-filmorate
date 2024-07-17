package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import ru.yandex.practicum.filmorate.validator.annotation.HasNotWhiteSpace;
import ru.yandex.practicum.filmorate.validator.annotation.NullOrNotBlank;
import ru.yandex.practicum.filmorate.validator.group.Create;
import ru.yandex.practicum.filmorate.validator.group.Default;
import ru.yandex.practicum.filmorate.validator.group.Update;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    @NotNull(groups = Update.class)
    @Positive(groups = Update.class)
    private Long id;

    @Email(groups = Default.class)
    @NotBlank(groups = Create.class)
    @NullOrNotBlank(groups = Update.class)
    private String email;

    @NotBlank(groups = Create.class)
    @HasNotWhiteSpace(groups = Default.class)
    @NullOrNotBlank(groups = Update.class)
    private String login;

    private String name;

    @PastOrPresent(groups = Default.class)
    private LocalDate birthday;

    private Set<Long> friends = new HashSet<>();

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

    public void setLogin(@NotNull @NotBlank String login) {
        if (this.name == null || this.name.equals(this.login)) {
            this.name = login;
        }

        this.login = login;
    }
}
