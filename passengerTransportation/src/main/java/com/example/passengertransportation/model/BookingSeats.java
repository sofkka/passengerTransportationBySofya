package com.example.passengertransportation.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
public class BookingSeats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_booking")
    private Long idBooking;

    @ManyToOne
    @JoinColumn(name = "id_route", nullable = false)
    private Route route; // Связь с маршрутом

    @ManyToOne
    @JoinColumn(name = "id_user", nullable = false)
    private User user; // Связь с пользователем

    @Column(name = "booking_date_time")
    private LocalDateTime bookingDateTime;

    @Column(name = "with_baggage")
    private boolean withBaggage;

    @Column(name = "with_child")
    private int withChild; // Количество детей

    @Column(name = "with_pet")
    private int withPet; // Количество животных

    public BookingSeats() {
    }

    public BookingSeats(Long idBooking, Route route, User user, LocalDateTime bookingDateTime, boolean withBaggage, int withChild, int withPet) {
        this.idBooking = idBooking;
        this.route = route;
        this.user = user;
        this.bookingDateTime = bookingDateTime;
        this.withBaggage = withBaggage;
        this.withChild = withChild;
        this.withPet = withPet;
    }

    // Геттеры и сеттеры
    public Long getIdBooking() {
        return idBooking;
    }

    public void setIdBooking(Long idBooking) {
        this.idBooking = idBooking;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getBookingDateTime() {
        return bookingDateTime;
    }

    public void setBookingDateTime(LocalDateTime bookingDateTime) {
        this.bookingDateTime = bookingDateTime;
    }

    public boolean isWithBaggage() {
        return withBaggage;
    }

    public void setWithBaggage(boolean withBaggage) {
        this.withBaggage = withBaggage;
    }

    public int getWithChild() {
        return withChild;
    }

    public void setWithChild(int withChild) {
        this.withChild = withChild;
    }

    public int getWithPet() {
        return withPet;
    }

    public void setWithPet(int withPet) {
        this.withPet = withPet;
    }
}