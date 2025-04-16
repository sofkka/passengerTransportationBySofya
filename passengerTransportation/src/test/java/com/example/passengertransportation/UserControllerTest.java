package com.example.passengertransportation;

import com.example.passengertransportation.controller.UserController;
import com.example.passengertransportation.model.User;
import com.example.passengertransportation.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты для UserController")
public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setIdUser(1L);
        user.setLogin("testuser");
        user.setPassword("password123");
        user.setPhoneNumber("+79991234567");
        user.setUserName("Иван");
        user.setUserSurname("Иванов");
        user.setUserPatronymic("Иванович");
    }

    @Nested
    @DisplayName("Тесты для метода получения всех пользователей")
    class GetAllUsersTests {

        @Test
        @DisplayName("Должен вернуть список пользователей, если пользователи существуют")
        void testGetAllUsers_Success() {
            List<User> expectedUsers = Arrays.asList(user);
            when(userService.getAllUsers()).thenReturn(expectedUsers);

            List<User> actualUsers = userController.getAllUsers();

            assertEquals(1, actualUsers.size(), "Размер списка пользователей должен быть 1");
            User actualUser = actualUsers.get(0);
            assertEquals(user.getIdUser(), actualUser.getIdUser(), "ID пользователя должен совпадать");
            assertEquals(user.getLogin(), actualUser.getLogin(), "Логин должен совпадать");
            verify(userService, times(1)).getAllUsers();
        }

        @Test
        @DisplayName("Должен вернуть пустой список, если пользователей нет")
        void testGetAllUsers_Empty() {
            when(userService.getAllUsers()).thenReturn(Collections.emptyList());

            List<User> actualUsers = userController.getAllUsers();

            assertTrue(actualUsers.isEmpty(), "Список пользователей должен быть пустым");
            verify(userService, times(1)).getAllUsers();
        }
    }

    @Nested
    @DisplayName("Тесты для метода получения пользователя по ID")
    class GetUserByIdTests {

        @Test
        @DisplayName("Должен вернуть пользователя, если ID существует")
        void testGetUserById_Success() {
            Long userId = 1L;
            when(userService.getUserById(userId)).thenReturn(user);

            User actualUser = userController.getUserById(userId);

            assertNotNull(actualUser, "Пользователь не должен быть null");
            assertEquals(user.getIdUser(), actualUser.getIdUser(), "ID пользователя должен совпадать");
            assertEquals(user.getLogin(), actualUser.getLogin(), "Логин должен совпадать");
            verify(userService, times(1)).getUserById(userId);
        }

        @Test
        @DisplayName("Должен выбросить IllegalArgumentException, если пользователь с ID не существует")
        void testGetUserById_NotFound() {
            Long userId = 999L;
            when(userService.getUserById(userId)).thenThrow(new IllegalArgumentException("Пользователь не найден"));

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> userController.getUserById(userId),
                    "Ожидается IllegalArgumentException для несуществующего ID пользователя");
            assertEquals("Пользователь не найден", exception.getMessage());
            verify(userService, times(1)).getUserById(userId);
        }

        @Test
        @DisplayName("Должен выбросить IllegalArgumentException, если ID пользователя null")
        void testGetUserById_NullId() {
            Long userId = null;
            when(userService.getUserById(null)).thenThrow(new IllegalArgumentException("ID пользователя не может быть null"));

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> userController.getUserById(userId),
                    "Ожидается IllegalArgumentException для null ID пользователя");
            assertEquals("ID пользователя не может быть null", exception.getMessage());
            verify(userService, times(1)).getUserById(userId);
        }
    }

    @Nested
    @DisplayName("Тесты для метода создания пользователя")
    class CreateUserTests {

        @Test
        @DisplayName("Должен успешно создать пользователя")
        void testCreateUser_Success() {
            String login = "newuser";
            String password = "pass123";
            String phoneNumber = "+79991234568";
            String userName = "Петр";
            String userSurname = "Петров";
            String userPatronymic = "Петрович";
            when(userService.createUser(login, password, phoneNumber, userName, userSurname, userPatronymic))
                    .thenReturn(user);

            ResponseEntity<?> response = userController.createUser(login, password, phoneNumber, userName, userSurname, userPatronymic);

            assertEquals(HttpStatus.OK, response.getStatusCode(), "Статус должен быть OK");
            assertEquals(user, response.getBody(), "Созданный пользователь должен совпадать");
            verify(userService, times(1)).createUser(login, password, phoneNumber, userName, userSurname, userPatronymic);
        }

        @ParameterizedTest
        @CsvSource({
                "'', 'pass123', '+79991234568', 'Петр', 'Петров', 'Петрович', Логин не может быть пустым",
                "'newuser', '', '+79991234568', 'Петр', 'Петров', 'Петрович', Пароль не может быть пустым",
                "'newuser', 'pass123', '', 'Петр', 'Петров', 'Петрович', Номер телефона не может быть пустым",
                "'newuser', 'pass123', '+79991234568', '', 'Петров', 'Петрович', Имя не может быть пустым",
                "'newuser', 'pass123', '+79991234568', 'Петр', '', 'Петрович', Фамилия не может быть пустым",
                "'newuser', 'pass123', '+79991234568', 'Петр', 'Петров', '', Отчество не может быть пустым"
        })
        @DisplayName("Должен вернуть BAD_REQUEST, если обязательные параметры пустые")
        void testCreateUser_EmptyParameters(String login, String password, String phoneNumber,
                                            String userName, String userSurname, String userPatronymic,
                                            String errorMessage) {
            when(userService.createUser(login, password, phoneNumber, userName, userSurname, userPatronymic))
                    .thenThrow(new IllegalArgumentException(errorMessage));

            ResponseEntity<?> response = userController.createUser(login, password, phoneNumber, userName, userSurname, userPatronymic);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Статус должен быть BAD_REQUEST");
            assertEquals(errorMessage, response.getBody(), "Тело ответа должно совпадать с сообщением об ошибке");
            verify(userService, times(1)).createUser(login, password, phoneNumber, userName, userSurname, userPatronymic);
        }

        @Test
        @DisplayName("Должен вернуть INTERNAL_SERVER_ERROR при возникновении непредвиденной ошибки")
        void testCreateUser_ServerError() {
            String login = "newuser";
            String password = "pass123";
            String phoneNumber = "+79991234568";
            String userName = "Петр";
            String userSurname = "Петров";
            String userPatronymic = "Петрович";
            when(userService.createUser(login, password, phoneNumber, userName, userSurname, userPatronymic))
                    .thenThrow(new RuntimeException("Ошибка базы данных"));

            ResponseEntity<?> response = userController.createUser(login, password, phoneNumber, userName, userSurname, userPatronymic);

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode(), "Статус должен быть INTERNAL_SERVER_ERROR");
            assertEquals("Ошибка сервера: Ошибка базы данных", response.getBody(), "Тело ответа должно совпадать");
            verify(userService, times(1)).createUser(login, password, phoneNumber, userName, userSurname, userPatronymic);
        }

        @Test
        @DisplayName("Должен вернуть BAD_REQUEST, если логин уже занят")
        void testCreateUser_DuplicateLogin() {
            String login = "testuser";
            String password = "pass123";
            String phoneNumber = "+79991234568";
            String userName = "Петр";
            String userSurname = "Петров";
            String userPatronymic = "Петрович";
            when(userService.createUser(login, password, phoneNumber, userName, userSurname, userPatronymic))
                    .thenThrow(new IllegalArgumentException("Логин уже занят"));

            ResponseEntity<?> response = userController.createUser(login, password, phoneNumber, userName, userSurname, userPatronymic);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Статус должен быть BAD_REQUEST");
            assertEquals("Логин уже занят", response.getBody(), "Тело ответа должно совпадать");
            verify(userService, times(1)).createUser(login, password, phoneNumber, userName, userSurname, userPatronymic);
        }
    }

    @Nested
    @DisplayName("Тесты для метода обновления пользователя")
    class UpdateUserTests {

        @Test
        @DisplayName("Должен успешно обновить пользователя")
        void testUpdateUser_Success() {
            Long userId = 1L;
            User updatedUser = new User();
            updatedUser.setIdUser(userId);
            updatedUser.setLogin("updateduser");
            when(userService.updateUser(eq(userId), any(User.class))).thenReturn(updatedUser);

            ResponseEntity<?> response = userController.updateUser(userId, updatedUser);

            assertEquals(HttpStatus.OK, response.getStatusCode(), "Статус должен быть OK");
            assertEquals(updatedUser, response.getBody(), "Обновленный пользователь должен совпадать");
            verify(userService, times(1)).updateUser(userId, updatedUser);
        }

        @Test
        @DisplayName("Должен вернуть NOT_FOUND, если пользователь с ID не существует")
        void testUpdateUser_NotFound() {
            Long userId = 999L;
            User updatedUser = new User();
            when(userService.updateUser(eq(userId), any(User.class)))
                    .thenThrow(new IllegalArgumentException("Пользователь не найден"));

            ResponseEntity<?> response = userController.updateUser(userId, updatedUser);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "Статус должен быть NOT_FOUND");
            assertEquals("Пользователь не найден", response.getBody(), "Тело ответа должно совпадать");
            verify(userService, times(1)).updateUser(userId, updatedUser);
        }

        @Test
        @DisplayName("Должен вернуть NOT_FOUND, если ID пользователя null")
        void testUpdateUser_NullId() {
            Long userId = null;
            User updatedUser = new User();
            when(userService.updateUser(isNull(), any(User.class)))
                    .thenThrow(new IllegalArgumentException("ID пользователя не может быть null"));

            ResponseEntity<?> response = userController.updateUser(userId, updatedUser);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "Статус должен быть NOT_FOUND");
            assertEquals("ID пользователя не может быть null", response.getBody(), "Тело ответа должно совпадать");
            verify(userService, times(1)).updateUser(userId, updatedUser);
        }

        @Test
        @DisplayName("Должен вернуть NOT_FOUND, если объект пользователя null")
        void testUpdateUser_NullUser() {
            Long userId = 1L;
            when(userService.updateUser(eq(userId), isNull()))
                    .thenThrow(new IllegalArgumentException("Пользователь не может быть null"));

            ResponseEntity<?> response = userController.updateUser(userId, null);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "Статус должен быть NOT_FOUND");
            assertEquals("Пользователь не может быть null", response.getBody(), "Тело ответа должно совпадать");
            verify(userService, times(1)).updateUser(userId, null);
        }
    }

    @Nested
    @DisplayName("Тесты для метода удаления пользователя")
    class DeleteUserTests {

        @Test
        @DisplayName("Должен успешно удалить пользователя")
        void testDeleteUser_Success() {
            Long userId = 1L;
            doNothing().when(userService).deleteUser(userId);

            ResponseEntity<String> response = userController.deleteUser(userId);

            assertEquals(HttpStatus.OK, response.getStatusCode(), "Статус должен быть OK");
            assertEquals("Пользователь успешно удален.", response.getBody(), "Тело ответа должно совпадать");
            verify(userService, times(1)).deleteUser(userId);
        }

        @Test
        @DisplayName("Должен вернуть NOT_FOUND, если пользователь с ID не существует")
        void testDeleteUser_NotFound() {
            Long userId = 999L;
            doThrow(new IllegalArgumentException("Пользователь не найден"))
                    .when(userService).deleteUser(userId);

            ResponseEntity<String> response = userController.deleteUser(userId);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "Статус должен быть NOT_FOUND");
            assertEquals("Пользователь не найден", response.getBody(), "Тело ответа должно совпадать");
            verify(userService, times(1)).deleteUser(userId);
        }

        @Test
        @DisplayName("Должен вернуть BAD_REQUEST, если пользователь не может быть удален")
        void testDeleteUser_CannotDelete() {
            Long userId = 1L;
            doThrow(new IllegalStateException("Пользователь не может быть удален"))
                    .when(userService).deleteUser(userId);

            ResponseEntity<String> response = userController.deleteUser(userId);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Статус должен быть BAD_REQUEST");
            assertEquals("Пользователь не может быть удален", response.getBody(), "Тело ответа должно совпадать");
            verify(userService, times(1)).deleteUser(userId);
        }

        @Test
        @DisplayName("Должен вернуть NOT_FOUND, если ID пользователя null")
        void testDeleteUser_NullId() {
            Long userId = null;
            doThrow(new IllegalArgumentException("ID пользователя не может быть null"))
                    .when(userService).deleteUser(isNull());

            ResponseEntity<String> response = userController.deleteUser(userId);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "Статус должен быть NOT_FOUND");
            assertEquals("ID пользователя не может быть null", response.getBody(), "Тело ответа должно совпадать");
            verify(userService, times(1)).deleteUser(userId);
        }
    }

    @Nested
    @DisplayName("Тесты для метода аутентификации пользователя")
    class LoginUserTests {

        @Test
        @DisplayName("Должен успешно аутентифицировать пользователя")
        void testLoginUser_Success() {
            String login = "testuser";
            String password = "password123";
            when(userService.authenticateUser(login, password)).thenReturn(user);

            ResponseEntity<?> response = userController.loginUser(login, password);

            assertEquals(HttpStatus.OK, response.getStatusCode(), "Статус должен быть OK");
            assertEquals(user, response.getBody(), "Аутентифицированный пользователь должен совпадать");
            verify(userService, times(1)).authenticateUser(login, password);
        }

        @Test
        @DisplayName("Должен вернуть UNAUTHORIZED, если учетные данные неверные")
        void testLoginUser_InvalidCredentials() {
            String login = "testuser";
            String password = "wrongpassword";
            when(userService.authenticateUser(login, password))
                    .thenThrow(new IllegalArgumentException("Неверный логин или пароль"));

            ResponseEntity<?> response = userController.loginUser(login, password);

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode(), "Статус должен быть UNAUTHORIZED");
            assertEquals("Неверный логин или пароль", response.getBody(), "Тело ответа должно совпадать");
            verify(userService, times(1)).authenticateUser(login, password);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("Должен вернуть UNAUTHORIZED, если логин null или пустой")
        void testLoginUser_NullOrEmptyLogin(String login) {
            String password = "password123";
            when(userService.authenticateUser(login, password))
                    .thenThrow(new IllegalArgumentException("Логин не может быть пустым или null"));

            ResponseEntity<?> response = userController.loginUser(login, password);

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode(), "Статус должен быть UNAUTHORIZED");
            assertEquals("Логин не может быть пустым или null", response.getBody(), "Тело ответа должно совпадать");
            verify(userService, times(1)).authenticateUser(login, password);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("Должен вернуть UNAUTHORIZED, если пароль null или пустой")
        void testLoginUser_NullOrEmptyPassword(String password) {
            String login = "testuser";
            when(userService.authenticateUser(login, password))
                    .thenThrow(new IllegalArgumentException("Пароль не может быть пустым или null"));

            ResponseEntity<?> response = userController.loginUser(login, password);

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode(), "Статус должен быть UNAUTHORIZED");
            assertEquals("Пароль не может быть пустым или null", response.getBody(), "Тело ответа должно совпадать");
            verify(userService, times(1)).authenticateUser(login, password);
        }

        @Test
        @DisplayName("Должен вернуть INTERNAL_SERVER_ERROR при возникновении непредвиденной ошибки")
        void testLoginUser_ServerError() {
            String login = "testuser";
            String password = "password123";
            when(userService.authenticateUser(login, password))
                    .thenThrow(new RuntimeException("Ошибка базы данных"));

            ResponseEntity<?> response = userController.loginUser(login, password);

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode(), "Статус должен быть INTERNAL_SERVER_ERROR");
            assertEquals("Ошибка сервера: Ошибка базы данных", response.getBody(), "Тело ответа должно совпадать");
            verify(userService, times(1)).authenticateUser(login, password);
        }
    }
}