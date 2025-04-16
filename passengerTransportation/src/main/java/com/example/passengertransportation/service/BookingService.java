package com.example.passengertransportation.service;

import com.example.passengertransportation.model.BookingSeats;
import com.example.passengertransportation.model.Route;
import com.example.passengertransportation.model.User;
import com.example.passengertransportation.repository.BookingSeatsRepository;
import com.example.passengertransportation.repository.RouteRepository;
import com.example.passengertransportation.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
public class BookingService {

    @Autowired
    private BookingSeatsRepository bookingRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private UserRepository userRepository;

    // Получение всех бронирований
    public List<BookingSeats> getAllBookings() {
        return bookingRepository.findAll();
    }

    // Получение бронирования по ID
    public BookingSeats getBookingById(Long idBooking) {
        return bookingRepository.findById(idBooking).orElse(null);
    }

    // Создание нового бронирования
    public BookingSeats createBooking(Long routeId, Long userId, String bookingDateTime,
                                      boolean withBaggage, int withChild, int withPet) {
        // Валидация входных параметров
        if (routeId == null || userId == null || bookingDateTime == null || bookingDateTime.isBlank()) {
            throw new IllegalArgumentException("Заполните все поля!");
        }

        // Валидация количества детей и животных
        if (withChild < 0 || withChild > 3) {
            throw new IllegalArgumentException("Количество детей должно быть от 0 до 3.");
        }
        if (withPet < 0 || withPet > 3) {
            throw new IllegalArgumentException("Количество животных должно быть от 0 до 3.");
        }

        // Проверка маршрута
        Route route = routeRepository.findById(routeId).orElse(null);
        if (route == null) {
            throw new IllegalArgumentException("Маршрут с таким ID не найден.");
        }

        // Проверка пользователя
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new IllegalArgumentException("Пользователь с таким ID не найден.");
        }

        // Проверка доступных мест
        int seatsToBook = 1 + withChild; // Основное место + дети
        if (route.getAvailableSeats() <= 0) {
            throw new IllegalStateException("Нет доступных мест для бронирования.");
        }
        if (route.getAvailableSeats() < seatsToBook) {
            throw new IllegalStateException("Недостаточно мест для бронирования с детьми.");
        }

        // Парсинг даты и времени
        LocalDateTime bookingDateTimeParsed;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            bookingDateTimeParsed = LocalDateTime.parse(bookingDateTime, formatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Неверный формат даты и времени. Ожидается yyyy-MM-dd'T'HH:mm:ss (например, 2025-04-12T22:39:46).");
        }

        // Обновление доступных мест
        route.setAvailableSeats(route.getAvailableSeats() - seatsToBook);
        routeRepository.save(route);

        // Создание бронирования
        BookingSeats booking = new BookingSeats(null, route, user, bookingDateTimeParsed, withBaggage, withChild, withPet);
        return bookingRepository.save(booking);
    }

    // Удаление бронирования по ID
    public void deleteBooking(Long idBooking) {
        BookingSeats booking = bookingRepository.findById(idBooking).orElse(null);
        if (booking == null) {
            throw new IllegalArgumentException("Бронирования с таким ID не найдено.");
        }

        Route route = booking.getRoute();

        if (booking.getWithChild() != 0) {
            route.setAvailableSeats(route.getAvailableSeats() + booking.getWithChild());
        }

        route.setAvailableSeats(route.getAvailableSeats() + 1);
        routeRepository.save(route);

        bookingRepository.deleteById(idBooking);
    }

    // Поиск бронирований по ID пользователя
    public List<BookingSeats> getBookingsByUserId(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new IllegalArgumentException("Пользователь с таким ID не найден.");
        }
        return bookingRepository.findByUser(user);
    }
}