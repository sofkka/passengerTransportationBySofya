package com.example.passengertransportation.service;

import com.example.passengertransportation.model.User;
import com.example.passengertransportation.repository.UserRepository;
import jakarta.xml.bind.DatatypeConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Метод для хеширования пароля с SHA-256
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes("UTF-8"));
            return DatatypeConverter.printHexBinary(hashBytes).toLowerCase();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при хешировании пароля", e);
        }
    }

    // Получение всех пользователей
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Получение пользователя по ID
    public User getUserById(Long idUser) {
        return userRepository.findById(idUser).orElse(null);
    }

    // Создание нового пользователя
    public User createUser(String login, String password, String phoneNumber, String userName, String userSurname, String userPatronymic) {
        // Проверка на пустые или пробельные строки для обязательных полей
        if (login.isBlank() || password.isBlank() || phoneNumber.isBlank() || userName.isBlank() || userSurname.isBlank()) {
            throw new IllegalArgumentException("Заполните все обязательные поля!");
        }

        // Проверка длины логина (3–20 символов)
        if (login.length() < 3 || login.length() > 20) {
            throw new IllegalArgumentException("Логин должен содержать от 3 до 20 символов");
        }

        // Допустимые символы в логине: латинские буквы, цифры, -, _, !, ?
        if (!login.matches("[a-zA-Z0-9-_!?]+")) {
            throw new IllegalArgumentException("Логин может содержать только латинские буквы, цифры и символы: -, _, !, ?");
        }

        // Проверка длины пароля (5–20 символов)
        if (password.length() < 5 || password.length() > 20) {
            throw new IllegalArgumentException("Пароль должен содержать от 5 до 20 символов");
        }

        // Формат номера телефона: +7 и 10 цифр после
        if (!phoneNumber.matches("\\+7[0-9]{10}")) {
            throw new IllegalArgumentException("Номер телефона должен быть в формате +79991234567");
        }

        // Проверка длины имени и фамилии (до 45 символов)
        if (userName.length() > 45 || userSurname.length() > 45) {
            throw new IllegalArgumentException("Имя или фамилия не могут превышать 45 символов");
        }

        // Проверка символов имени и фамилии (только буквы)
        if (!userName.matches("[a-zA-Zа-яА-Я]+") || !userSurname.matches("[a-zA-Zа-яА-Я]+")) {
            throw new IllegalArgumentException("Имя и фамилия могут содержать только буквы");
        }

        // Отчество — необязательное поле
        if (userPatronymic != null && !userPatronymic.isBlank()) {
            if (userPatronymic.length() > 45) {
                throw new IllegalArgumentException("Отчество не может превышать 45 символов");
            }
            if (!userPatronymic.matches("[a-zA-Zа-яА-Я]+")) {
                throw new IllegalArgumentException("Отчество может содержать только буквы");
            }
        } else {
            // Если отчество не указано, сохраняем пустую строку
            userPatronymic = "";
        }

        // Проверка уникальности логина
        if (userRepository.findByLogin(login).isPresent()) {
            throw new IllegalArgumentException("Пользователь с таким логином уже зарегистрирован");
        }

        // Проверка уникальности номера телефона
        if (userRepository.findByPhoneNumber(phoneNumber) != null) {
            throw new IllegalArgumentException("Пользователь с таким номером телефона уже зарегистрирован");
        }

        // Создание нового пользователя с хешированным паролем
        User userNew = new User();
        userNew.setLogin(login);
        userNew.setPassword(hashPassword(password)); // Используется SHA-256
        userNew.setPhoneNumber(phoneNumber);
        userNew.setUserName(userName);
        userNew.setUserSurname(userSurname);
        userNew.setUserPatronymic(userPatronymic); // Пустая строка, если отчество не указано

        // Сохраняем в БД и возвращаем
        return userRepository.save(userNew);
    }

    // Обновление пользователя
    public User updateUser(Long idUser, User user) {
        User existingUser = userRepository.findById(idUser)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с ID " + idUser + " не найден"));

        // Обновляем только разрешённые поля
        if (user.getLogin() != null) {
            existingUser.setLogin(user.getLogin());
        }
        if (user.getPhoneNumber() != null) {
            existingUser.setPhoneNumber(user.getPhoneNumber());
        }
        if (user.getUserName() != null) {
            existingUser.setUserName(user.getUserName());
        }
        if (user.getUserSurname() != null) {
            existingUser.setUserSurname(user.getUserSurname());
        }
        existingUser.setUserPatronymic(user.getUserPatronymic()); // Может быть null

        // Пароль не обновляем (для этого нужен отдельный эндпоинт)

        return userRepository.save(existingUser);
    }

    // Удаление пользователя
    public void deleteUser(Long idUser) {
        userRepository.deleteById(idUser);
    }

    // Аутентификация пользователя с SHA-256
    public User authenticateUser(String login, String password) {
        // Теперь findByLogin возвращает Optional и orElseThrow будет работать
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new IllegalArgumentException("Неверный логин или пароль"));

        String hashedInputPassword = hashPassword(password);

        if (!hashedInputPassword.equals(user.getPassword())) {
            throw new IllegalArgumentException("Неверный логин или пароль");
        }

        return user;
    }
}