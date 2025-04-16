package com.example.passengertransportation.controller;

import com.example.passengertransportation.model.User;
import com.example.passengertransportation.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    // Получение всех пользователей
    @Operation(summary = "Получение всех пользователей", description = "Возвращает список всех пользователей")
    @GetMapping("")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // Получение пользователя по ID
    @Operation(summary = "Получение пользователя по ID", description = "Возвращает пользователя по указанному идентификатору")
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    // Создание нового пользователя
    @Operation(summary = "Добавление пользователя", description = "Позволяет добавить нового пользователя")
    @PostMapping("/createNewUser")
    public ResponseEntity<?> createUser(
            @RequestParam String login,
            @RequestParam String password,
            @RequestParam String phoneNumber,
            @RequestParam String userName,
            @RequestParam String userSurname,
            @RequestParam String userPatronymic) {
        try {
            User user = userService.createUser(login, password, phoneNumber, userName, userSurname, userPatronymic);
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка сервера: " + e.getMessage());
        }
    }

    // Обновление пользователя
    @Operation(summary = "Обновление пользователя", description = "Обновляет информацию о пользователе по указанному идентификатору")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable("id") Long id, @RequestBody User user) {
        try {
            User updatedUser = userService.updateUser(id, user);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Удаление пользователя
    @Operation(summary = "Удаление пользователя", description = "Удаляет пользователя по указанному идентификатору")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok("Пользователь успешно удален.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Аутентификация пользователя (вход в аккаунт)
    @Operation(summary = "Аутентификация пользователя", description = "Позволяет пользователю войти в систему")
    @GetMapping("/login")
    public ResponseEntity<?> loginUser(
            @RequestParam String login,
            @RequestParam String password) {
        try {
            User user = userService.authenticateUser(login, password);
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка сервера: " + e.getMessage());
        }
    }
}