package com.example.passengertransportation;

import com.example.passengertransportation.controller.BookingSeatsController;
import com.example.passengertransportation.model.BookingSeats;
import com.example.passengertransportation.model.Route;
import com.example.passengertransportation.model.User;
import com.example.passengertransportation.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты для BookingSeatsController")
public class BookingControllerTest {

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingSeatsController bookingController;

    private Route route;
    private User user;
    private BookingSeats booking;

    @BeforeEach
    public void setUp() {
        route = new Route();
        route.setIdRoute(1L);
        route.setAvailableSeats(5);

        user = new User();
        user.setIdUser(1L);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime bookingDateTime = LocalDate.parse("2025-04-03", dateFormatter).atStartOfDay();

        booking = new BookingSeats();
        booking.setIdBooking(1L);
        booking.setRoute(route);
        booking.setUser(user);
        booking.setBookingDateTime(bookingDateTime);
        booking.setWithBaggage(true);
        booking.setWithChild(1); // 1 ребенок
        booking.setWithPet(2);   // 2 животных
    }

    @Nested
    @DisplayName("Тесты для метода получения всех бронирований")
    class GetAllBookingsTests {

        @Test
        @DisplayName("Должен вернуть список бронирований, если бронирования существуют")
        void testGetAllBookings_Success() {
            List<BookingSeats> expectedBookings = Arrays.asList(booking);
            when(bookingService.getAllBookings()).thenReturn(expectedBookings);

            List<BookingSeats> actualBookings = bookingController.getAllBookings();

            assertEquals(1, actualBookings.size(), "Размер списка бронирований должен быть 1");
            BookingSeats actualBooking = actualBookings.get(0);
            assertEquals(booking.getIdBooking(), actualBooking.getIdBooking(), "ID бронирования должен совпадать");
            assertEquals(booking.getRoute().getIdRoute(), actualBooking.getRoute().getIdRoute(), "ID маршрута должен совпадать");
            assertEquals(booking.getUser().getIdUser(), actualBooking.getUser().getIdUser(), "ID пользователя должен совпадать");
            assertTrue(actualBooking.isWithBaggage(), "Флаг багажа должен быть true");
            assertEquals(1, actualBooking.getWithChild(), "Количество детей должно быть 1");
            assertEquals(2, actualBooking.getWithPet(), "Количество животных должно быть 2");
            verify(bookingService, times(1)).getAllBookings();
        }

        @Test
        @DisplayName("Должен вернуть пустой список, если бронирований нет")
        void testGetAllBookings_Empty() {
            when(bookingService.getAllBookings()).thenReturn(Collections.emptyList());

            List<BookingSeats> actualBookings = bookingController.getAllBookings();

            assertTrue(actualBookings.isEmpty(), "Список бронирований должен быть пустым");
            verify(bookingService, times(1)).getAllBookings();
        }
    }

    @Nested
    @DisplayName("Тесты для метода получения бронирования по ID")
    class GetBookingByIdTests {

        @Test
        @DisplayName("Должен вернуть бронирование, если ID существует")
        void testGetBookingById_Success() {
            Long bookingId = 1L;
            when(bookingService.getBookingById(bookingId)).thenReturn(booking);

            BookingSeats actualBooking = bookingController.getBookingById(bookingId);

            assertNotNull(actualBooking, "Бронирование не должно быть null");
            assertEquals(booking.getIdBooking(), actualBooking.getIdBooking(), "ID бронирования должен совпадать");
            assertEquals(booking.getRoute().getIdRoute(), actualBooking.getRoute().getIdRoute(), "ID маршрута должен совпадать");
            assertEquals(booking.getUser().getIdUser(), actualBooking.getUser().getIdUser(), "ID пользователя должен совпадать");
            verify(bookingService, times(1)).getBookingById(bookingId);
        }

        @Test
        @DisplayName("Должен выбросить IllegalArgumentException, если бронирование с ID не существует")
        void testGetBookingById_NotFound() {
            Long bookingId = 999L;
            when(bookingService.getBookingById(bookingId)).thenThrow(new IllegalArgumentException("Бронирование не найдено"));

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> bookingController.getBookingById(bookingId));
            assertEquals("Бронирование не найдено", exception.getMessage());
            verify(bookingService, times(1)).getBookingById(bookingId);
        }

        @Test
        @DisplayName("Должен выбросить IllegalArgumentException, если ID бронирования null")
        void testGetBookingById_NullId() {
            Long bookingId = null;
            when(bookingService.getBookingById(bookingId)).thenThrow(new IllegalArgumentException("ID бронирования не может быть null"));

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> bookingController.getBookingById(bookingId));
            assertEquals("ID бронирования не может быть null", exception.getMessage());
            verify(bookingService, times(1)).getBookingById(bookingId);
        }
    }

    @Nested
    @DisplayName("Тесты для метода получения бронирований по ID пользователя")
    class GetBookingsByUserIdTests {

        @Test
        @DisplayName("Должен вернуть бронирования, если ID пользователя существует")
        void testGetBookingsByUserId_Success() {
            Long userId = 1L;
            List<BookingSeats> expectedBookings = Arrays.asList(booking);
            when(bookingService.getBookingsByUserId(userId)).thenReturn(expectedBookings);

            ResponseEntity<?> response = bookingController.getBookingsByUserId(userId);

            assertEquals(HttpStatus.OK, response.getStatusCode(), "Статус должен быть OK");
            assertEquals(expectedBookings, response.getBody(), "Список бронирований должен совпадать");
            verify(bookingService, times(1)).getBookingsByUserId(userId);
        }

        @Test
        @DisplayName("Должен вернуть NOT_FOUND, если бронирований для пользователя нет")
        void testGetBookingsByUserId_NotFound() {
            Long userId = 999L;
            when(bookingService.getBookingsByUserId(userId)).thenReturn(Collections.emptyList());

            ResponseEntity<?> response = bookingController.getBookingsByUserId(userId);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "Статус должен быть NOT_FOUND");
            assertEquals("Бронирования не найдены.", response.getBody(), "Тело ответа должно совпадать");
            verify(bookingService, times(1)).getBookingsByUserId(userId);
        }

        @Test
        @DisplayName("Должен выбросить IllegalArgumentException, если ID пользователя null")
        void testGetBookingsByUserId_NullId() {
            Long userId = null;
            when(bookingService.getBookingsByUserId(userId)).thenThrow(new IllegalArgumentException("ID пользователя не может быть null"));

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> bookingController.getBookingsByUserId(userId));
            assertEquals("ID пользователя не может быть null", exception.getMessage());
            verify(bookingService, times(1)).getBookingsByUserId(userId);
        }
    }

    @Nested
    @DisplayName("Тесты для метода создания бронирования")
    class CreateBookingTests {

        @Test
        @DisplayName("Должен успешно создать бронирование")
        void testCreateBooking_Success() {
            Long routeId = 1L;
            Long userId = 1L;
            String bookingDateTime = "2025-04-03T00:00:00";
            boolean withBaggage = true;
            int withChild = 1;
            int withPet = 2;

            when(bookingService.createBooking(routeId, userId, bookingDateTime, withBaggage, withChild, withPet))
                    .thenReturn(booking);

            ResponseEntity<?> response = bookingController.createBooking(routeId, userId, bookingDateTime, withBaggage, withChild, withPet);

            assertEquals(HttpStatus.OK, response.getStatusCode(), "Статус должен быть OK");
            assertEquals(booking, response.getBody(), "Созданное бронирование должно совпадать");
            verify(bookingService, times(1)).createBooking(routeId, userId, bookingDateTime, withBaggage, withChild, withPet);
        }

        @Test
        @DisplayName("Должен вернуть NOT_FOUND, если маршрут не существует")
        void testCreateBooking_RouteNotFound() {
            Long routeId = 999L;
            Long userId = 1L;
            String bookingDateTime = "2025-04-03T00:00:00";
            boolean withBaggage = true;
            int withChild = 1;
            int withPet = 2;

            when(bookingService.createBooking(routeId, userId, bookingDateTime, withBaggage, withChild, withPet))
                    .thenThrow(new IllegalArgumentException("Маршрут не найден"));

            ResponseEntity<?> response = bookingController.createBooking(routeId, userId, bookingDateTime, withBaggage, withChild, withPet);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "Статус должен быть NOT_FOUND");
            assertEquals("Маршрут не найден", response.getBody(), "Тело ответа должно совпадать");
            verify(bookingService, times(1)).createBooking(routeId, userId, bookingDateTime, withBaggage, withChild, withPet);
        }

        @Test
        @DisplayName("Должен вернуть NOT_FOUND, если пользователь не существует")
        void testCreateBooking_UserNotFound() {
            Long routeId = 1L;
            Long userId = 999L;
            String bookingDateTime = "2025-04-03T00:00:00";
            boolean withBaggage = true;
            int withChild = 1;
            int withPet = 2;

            when(bookingService.createBooking(routeId, userId, bookingDateTime, withBaggage, withChild, withPet))
                    .thenThrow(new IllegalArgumentException("Пользователь не найден"));

            ResponseEntity<?> response = bookingController.createBooking(routeId, userId, bookingDateTime, withBaggage, withChild, withPet);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "Статус должен быть NOT_FOUND");
            assertEquals("Пользователь не найден", response.getBody(), "Тело ответа должно совпадать");
            verify(bookingService, times(1)).createBooking(routeId, userId, bookingDateTime, withBaggage, withChild, withPet);
        }

        @Test
        @DisplayName("Должен вернуть BAD_REQUEST, если нет доступных мест")
        void testCreateBooking_NoSeatsAvailable() {
            Long routeId = 1L;
            Long userId = 1L;
            String bookingDateTime = "2025-04-03T00:00:00";
            boolean withBaggage = true;
            int withChild = 1;
            int withPet = 2;

            when(bookingService.createBooking(routeId, userId, bookingDateTime, withBaggage, withChild, withPet))
                    .thenThrow(new IllegalStateException("Нет доступных мест"));

            ResponseEntity<?> response = bookingController.createBooking(routeId, userId, bookingDateTime, withBaggage, withChild, withPet);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Статус должен быть BAD_REQUEST");
            assertEquals("Нет доступных мест", response.getBody(), "Тело ответа должно совпадать");
            verify(bookingService, times(1)).createBooking(routeId, userId, bookingDateTime, withBaggage, withChild, withPet);
        }

        @Test
        @DisplayName("Должен вернуть BAD_REQUEST, если формат даты некорректен")
        void testCreateBooking_InvalidDateTime() {
            Long routeId = 1L;
            Long userId = 1L;
            String bookingDateTime = "invalid-date";
            boolean withBaggage = true;
            int withChild = 1;
            int withPet = 2;

            when(bookingService.createBooking(routeId, userId, bookingDateTime, withBaggage, withChild, withPet))
                    .thenThrow(new IllegalArgumentException("Некорректный формат даты"));

            ResponseEntity<?> response = bookingController.createBooking(routeId, userId, bookingDateTime, withBaggage, withChild, withPet);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "Статус должен быть NOT_FOUND");
            assertEquals("Некорректный формат даты", response.getBody(), "Тело ответа должно совпадать");
            verify(bookingService, times(1)).createBooking(routeId, userId, bookingDateTime, withBaggage, withChild, withPet);
        }

        @ParameterizedTest
        @CsvSource({
                "-1, 0, Количество детей не может быть отрицательным",
                "0, -1, Количество животных не может быть отрицательным"
        })
        @DisplayName("Должен вернуть BAD_REQUEST, если количество детей или животных отрицательное")
        void testCreateBooking_NegativeChildOrPet(int withChild, int withPet, String expectedMessage) {
            Long routeId = 1L;
            Long userId = 1L;
            String bookingDateTime = "2025-04-03T00:00:00";
            boolean withBaggage = true;

            when(bookingService.createBooking(routeId, userId, bookingDateTime, withBaggage, withChild, withPet))
                    .thenThrow(new IllegalArgumentException(expectedMessage));

            ResponseEntity<?> response = bookingController.createBooking(routeId, userId, bookingDateTime, withBaggage, withChild, withPet);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "Статус должен быть NOT_FOUND");
            assertEquals(expectedMessage, response.getBody(), "Тело ответа должно совпадать");
            verify(bookingService, times(1)).createBooking(routeId, userId, bookingDateTime, withBaggage, withChild, withPet);
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " ", "null"})
        @DisplayName("Должен вернуть BAD_REQUEST, если дата бронирования пустая или null")
        void testCreateBooking_EmptyOrNullDateTime(String bookingDateTime) {
            Long routeId = 1L;
            Long userId = 1L;
            boolean withBaggage = true;
            int withChild = 1;
            int withPet = 2;

            when(bookingService.createBooking(routeId, userId, bookingDateTime, withBaggage, withChild, withPet))
                    .thenThrow(new IllegalArgumentException("Дата бронирования не может быть пустой"));

            ResponseEntity<?> response = bookingController.createBooking(routeId, userId, bookingDateTime, withBaggage, withChild, withPet);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "Статус должен быть NOT_FOUND");
            assertEquals("Дата бронирования не может быть пустой", response.getBody(), "Тело ответа должно совпадать");
            verify(bookingService, times(1)).createBooking(routeId, userId, bookingDateTime, withBaggage, withChild, withPet);
        }

        @Test
        @DisplayName("Должен создать бронирование с нулевым количеством детей и животных")
        void testCreateBooking_ZeroChildAndPet() {
            Long routeId = 1L;
            Long userId = 1L;
            String bookingDateTime = "2025-04-03T00:00:00";
            boolean withBaggage = false;
            int withChild = 0;
            int withPet = 0;

            BookingSeats zeroChildPetBooking = new BookingSeats();
            zeroChildPetBooking.setIdBooking(2L);
            zeroChildPetBooking.setRoute(route);
            zeroChildPetBooking.setUser(user);
            zeroChildPetBooking.setBookingDateTime(LocalDateTime.parse(bookingDateTime));
            zeroChildPetBooking.setWithBaggage(false);
            zeroChildPetBooking.setWithChild(0);
            zeroChildPetBooking.setWithPet(0);

            when(bookingService.createBooking(routeId, userId, bookingDateTime, withBaggage, withChild, withPet))
                    .thenReturn(zeroChildPetBooking);

            ResponseEntity<?> response = bookingController.createBooking(routeId, userId, bookingDateTime, withBaggage, withChild, withPet);

            assertEquals(HttpStatus.OK, response.getStatusCode(), "Статус должен быть OK");
            BookingSeats actualBooking = (BookingSeats) response.getBody();
            assertEquals(0, actualBooking.getWithChild(), "Количество детей должно быть 0");
            assertEquals(0, actualBooking.getWithPet(), "Количество животных должно быть 0");
            assertFalse(actualBooking.isWithBaggage(), "Флаг багажа должен быть false");
            verify(bookingService, times(1)).createBooking(routeId, userId, bookingDateTime, withBaggage, withChild, withPet);
        }
    }

    @Nested
    @DisplayName("Тесты для метода удаления бронирования")
    class DeleteBookingTests {

        @Test
        @DisplayName("Должен успешно удалить бронирование")
        void testDeleteBooking_Success() {
            Long bookingId = 1L;
            doNothing().when(bookingService).deleteBooking(bookingId);

            ResponseEntity<String> response = bookingController.deleteBooking(bookingId);

            assertEquals(HttpStatus.OK, response.getStatusCode(), "Статус должен быть OK");
            assertEquals("Бронирование успешно удалено.", response.getBody(), "Тело ответа должно совпадать");
            verify(bookingService, times(1)).deleteBooking(bookingId);
        }

        @Test
        @DisplayName("Должен вернуть NOT_FOUND, если бронирование не существует")
        void testDeleteBooking_NotFound() {
            Long bookingId = 999L;
            doThrow(new IllegalArgumentException("Бронирование не найдено")).when(bookingService).deleteBooking(bookingId);

            ResponseEntity<String> response = bookingController.deleteBooking(bookingId);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "Статус должен быть NOT_FOUND");
            assertEquals("Бронирование не найдено", response.getBody(), "Тело ответа должно совпадать");
            verify(bookingService, times(1)).deleteBooking(bookingId);
        }

        @Test
        @DisplayName("Должен вернуть BAD_REQUEST, если бронирование не может быть удалено")
        void testDeleteBooking_CannotDelete() {
            Long bookingId = 1L;
            doThrow(new IllegalStateException("Бронирование не может быть удалено")).when(bookingService).deleteBooking(bookingId);

            ResponseEntity<String> response = bookingController.deleteBooking(bookingId);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Статус должен быть BAD_REQUEST");
            assertEquals("Бронирование не может быть удалено", response.getBody(), "Тело ответа должно совпадать");
            verify(bookingService, times(1)).deleteBooking(bookingId);
        }
    }
}