package ru.yandex.practicum.filmorate.model.feed;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "eventId")
public class Event {
    @NotNull
    private Long eventId;
    @NotNull
    private Long timestamp;
    @NotNull
    private EventType eventType;
    @NotNull
    private Operation operation;
    @NotNull
    private Long userId;
    @NotNull
    private Long entityId;
}
