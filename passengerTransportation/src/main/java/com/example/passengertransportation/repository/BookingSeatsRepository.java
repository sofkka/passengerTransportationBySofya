package com.example.passengertransportation.repository;

import com.example.passengertransportation.model.BookingSeats;
import com.example.passengertransportation.model.Route;
import com.example.passengertransportation.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingSeatsRepository extends JpaRepository<BookingSeats, Long> {
    // Проверка наличия бронирований для маршрута
    boolean existsByRoute(Route route);

    // Поиск бронирований по пользователю
    List<BookingSeats> findByUser(User user);
}