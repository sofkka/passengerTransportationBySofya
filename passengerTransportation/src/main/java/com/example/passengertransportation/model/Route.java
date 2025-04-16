package com.example.passengertransportation.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "routes")
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_route")
    private Long idRoute;

    @ManyToOne
    @JoinColumn(name = "id_transport", nullable = false)
    private TransportType transportType; // Связь с типом транспорта

    @ManyToOne
    @JoinColumn(name = "id_city_departure", nullable = false)
    private City departureCity;

    @ManyToOne
    @JoinColumn(name = "id_city_arrival", nullable = false)
    private City arrivalCity;

    @Column(name = "departure_date_time")
    private LocalDateTime departureDateTime;

    @Column(name = "arrival_date_time")
    private LocalDateTime arrivalDateTime;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "available_seats")
    private int availableSeats;

    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<BookingSeats> bookings; // Список бронирований

    // Конструкторы
    public Route() {
    }

    public Route(Long idRoute, TransportType transportType, City departureCity, City arrivalCity,
                 LocalDateTime departureDateTime, LocalDateTime arrivalDateTime, BigDecimal price, int availableSeats) {
        this.idRoute = idRoute;
        this.transportType = transportType;
        this.departureCity = departureCity;
        this.arrivalCity = arrivalCity;
        this.departureDateTime = departureDateTime;
        this.arrivalDateTime = arrivalDateTime;
        this.price = price;
        this.availableSeats = availableSeats;
    }

    // Геттеры и сеттеры
    public Long getIdRoute() {
        return idRoute;
    }

    public void setIdRoute(Long idRoute) {
        this.idRoute = idRoute;
    }

    public TransportType getTransportType() {
        return transportType;
    }

    public void setTransportType(TransportType transportType) {
        this.transportType = transportType;
    }

    public City getDepartureCity() {
        return departureCity;
    }

    public void setDepartureCity(City departureCity) {
        this.departureCity = departureCity;
    }

    public City getArrivalCity() {
        return arrivalCity;
    }

    public void setArrivalCity(City arrivalCity) {
        this.arrivalCity = arrivalCity;
    }

    public LocalDateTime getDepartureDateTime() {
        return departureDateTime;
    }

    public void setDepartureDateTime(LocalDateTime departureDateTime) {
        this.departureDateTime = departureDateTime;
    }

    public LocalDateTime getArrivalDateTime() {
        return arrivalDateTime;
    }

    public void setArrivalDateTime(LocalDateTime arrivalDateTime) {
        this.arrivalDateTime = arrivalDateTime;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public List<BookingSeats> getBookings() {
        return bookings;
    }

    public void setBookings(List<BookingSeats> bookings) {
        this.bookings = bookings;
    }
}