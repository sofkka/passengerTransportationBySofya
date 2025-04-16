package com.example.passengertransportation.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    private Long idUser;

    @Column(name = "user_login")
    private String login; // Изменено с userLogin на login

    @Column(name = "password")
    private String password;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "user_surname")
    private String userSurname;

    @Column(name = "user_patronymic")
    private String userPatronymic;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<BookingSeats> bookings;

    // Конструкторы
    public User() {
    }

    public User(Long idUser, String login, String password, String phoneNumber, String userName, String userSurname, String userPatronymic) {
        this.idUser = idUser;
        this.login = login;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.userName = userName;
        this.userSurname = userSurname;
        this.userPatronymic = userPatronymic;
    }

    // Геттеры и сеттеры
    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

    public String getLogin() { // Изменено с getUserLogin
        return login;
    }

    public void setLogin(String login) { // Изменено с setUserLogin
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserSurname() {
        return userSurname;
    }

    public void setUserSurname(String userSurname) {
        this.userSurname = userSurname;
    }

    public String getUserPatronymic() {
        return userPatronymic;
    }

    public void setUserPatronymic(String userPatronymic) {
        this.userPatronymic = userPatronymic;
    }

    public List<BookingSeats> getBookings() {
        return bookings;
    }

    public void setBookings(List<BookingSeats> bookings) {
        this.bookings = bookings;
    }
}