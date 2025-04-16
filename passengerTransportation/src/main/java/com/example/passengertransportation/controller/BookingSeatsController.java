package com.example.passengertransportation.controller;

import com.example.passengertransportation.model.BookingSeats;
import com.example.passengertransportation.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
public class BookingSeatsController {

    @Autowired
    private BookingService bookingService;

    // Получение всех бронирований
    @Operation(summary = "Получение всех бронирований", description = "Возвращает список всех доступных бронирований")
    @GetMapping("")
    public List<BookingSeats> getAllBookings() {
        return bookingService.getAllBookings();
    }

    // Получение бронирования по ID
    @Operation(summary = "Получение бронирования по ID", description = "Возвращает информацию о бронировании по указанному идентификатору")
    @GetMapping("/{id}")
    public BookingSeats getBookingById(@PathVariable Long id) {
        return bookingService.getBookingById(id);
    }

    // Получение бронирований по ID пользователя
    @Operation(summary = "Поиск бронирований по ID пользователя", description = "Возвращает список бронирований для указанного пользователя")
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getBookingsByUserId(@PathVariable Long userId) {
        List<BookingSeats> bookings = bookingService.getBookingsByUserId(userId);
        if (bookings.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Бронирования не найдены.");
        }
        return ResponseEntity.ok(bookings);
    }

    // Создание нового бронирования
    @Operation(summary = "Добавление брони", description = "Позволяет добавить бронь")
    @PostMapping("/createNewBooking")
    public ResponseEntity<?> createBooking(
            @RequestParam Long routeId,
            @RequestParam Long userId,
            @RequestParam String bookingDateTime,
            @RequestParam boolean withBaggage,
            @RequestParam int withChild,
            @RequestParam int withPet) {
        try {
            BookingSeats booking = bookingService.createBooking(routeId, userId, bookingDateTime, withBaggage, withChild, withPet);
            return ResponseEntity.ok(booking);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Удаление бронирования
    @Operation(summary = "Удаление бронирования", description = "Удаляет бронирование по указанному идентификатору")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBooking(@PathVariable Long id) {
        try {
            bookingService.deleteBooking(id);
            return ResponseEntity.ok("Бронирование успешно удалено.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}