package ru.yandex.practicum.filmorate.repository.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@Import({JdbcUserRepository.class, UserResultSetExtractor.class, UserListResultSetExtractor.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DisplayName("Тестировать репозиторий пользователей")
class JdbcUserRepositoryTest {
    public static final long TEST_USER1_ID = 1L;
    public static final long TEST_USER2_ID = 2L;
    public static final long TEST_USER3_ID = 3L;
    private final JdbcUserRepository userRepository;

    static User getTestUser1() {
        User user = new User();
        user.setId(TEST_USER1_ID);
        user.setLogin("user1");
        user.setName("user1");
        user.setEmail("email1@email.com");
        user.setBirthday(LocalDate.of(2000, 9, 20));
        return user;
    }

    static User getTestUser2() {
        User user = new User();
        user.setId(TEST_USER2_ID);
        user.setLogin("user2");
        user.setName("user2");
        user.setEmail("email2@email.com");
        user.setBirthday(LocalDate.of(1999, 9, 9));
        return user;
    }

    static User getTestUser3() {
        User user = new User();
        user.setId(TEST_USER3_ID);
        user.setLogin("user3");
        user.setName("user3");
        user.setEmail("email3@email.com");
        user.setBirthday(LocalDate.of(1988, 8, 8));
        return user;
    }

    @Test
    @DisplayName("Получить пользователя по ID")
    void shouldGetUserById() {
        Optional<User> user = userRepository.getById(TEST_USER1_ID);
        assertThat(user)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(getTestUser1());
    }

    @Test
    @DisplayName("Получить всех пользователей")
    void shouldGetAllUsers() {
        List<User> users = List.of(getTestUser1(), getTestUser2(), getTestUser3());

        List<User> savedUsers = userRepository.getAll();
        assertThat(savedUsers)
                .usingRecursiveComparison()
                .isEqualTo(users);
    }

    @Test
    @DisplayName("Сохранить пользователя в базу данных")
    void shouldSaveUserInDatabase() {
        User user = new User();
        user.setLogin("otherUser");
        user.setName("otherUser");
        user.setEmail("otheremail@email.com");
        user.setBirthday(LocalDate.of(1977, 7, 7));

        user = userRepository.save(user);
        Optional<User> savedUser = userRepository.getById(user.getId());
        assertThat(savedUser)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(user);
    }

    @Test
    @DisplayName("Обновить данные пользователя")
    void shouldUpdateUserData() {
        User user = getTestUser1();
        user.setEmail("newemail.email.com");

        assertDoesNotThrow(() -> userRepository.update(user));

        Optional<User> updatedUser = userRepository.getById(TEST_USER1_ID);
        assertThat(updatedUser)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(user);
    }

    @Nested
    @DisplayName("Тестировать дружбу")
    class FriendJdbcUserRepositoryTest {
        @Test
        @DisplayName("Получить друзей по ID пользователя")
        void shouldGetFriendsByUserId() {
            List<User> friends = List.of(getTestUser2());
            List<User> savedFriends = userRepository.getFriends(TEST_USER1_ID);

            assertThat(savedFriends)
                    .usingRecursiveComparison()
                    .isEqualTo(friends);
        }

        @Test
        @DisplayName("Добавить друга")
        void shouldAddFriend() {
            List<User> friends = List.of(getTestUser1());
            userRepository.addFriend(TEST_USER3_ID, TEST_USER1_ID);
            List<User> savedFriends = userRepository.getFriends(TEST_USER3_ID);
            assertThat(savedFriends)
                    .usingRecursiveComparison()
                    .isEqualTo(friends);
        }

        @Test
        @DisplayName("Удалить друга")
        void shouldRemoveFriend() {
            userRepository.removeFriend(TEST_USER1_ID, TEST_USER2_ID);
            List<User> friends = userRepository.getFriends(TEST_USER1_ID);
            assertEquals(0, friends.size());
        }

        @Test
        @DisplayName("Получить общих друзей")
        void shouldGetCommonFriends() {
            userRepository.addFriend(TEST_USER3_ID, TEST_USER1_ID);
            List<User> commonFriends = List.of(getTestUser1());
            List<User> dbCommonFriends = userRepository.getCommonFriends(TEST_USER2_ID, TEST_USER3_ID);
            assertThat(dbCommonFriends)
                    .usingRecursiveComparison()
                    .isEqualTo(commonFriends);
        }
    }
}