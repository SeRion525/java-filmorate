package ru.yandex.practicum.filmorate.model;

public enum Rating {
    G, PG, PG13, R, NC17;

    public static Rating of(String rating) {
        return switch (rating.toLowerCase()) {
            case "g" -> G;
            case "pg" -> PG;
            case "pg13", "pg-13" -> PG13;
            case "r" -> R;
            case "nc17", "nc-17" -> NC17;
            default -> null;
        };
    }
}
