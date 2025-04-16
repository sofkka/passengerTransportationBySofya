package com.example.passengertransportation;

import com.example.passengertransportation.controller.RouteController;
import com.example.passengertransportation.model.City;
import com.example.passengertransportation.model.Route;
import com.example.passengertransportation.model.TransportType;
import com.example.passengertransportation.service.RouteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты для RouteController")
public class RouteControllerTest {

    @Mock
    private RouteService routeService;

    @InjectMocks
    private RouteController routeController;

    private Route route;
    private City departureCity;
    private City arrivalCity;
    private TransportType transportType;

    @BeforeEach
    void setUp() {
        // Инициализация сущностей City
        departureCity = new City();
        departureCity.setIdCity("1");
        departureCity.setCityName("Москва");

        arrivalCity = new City();
        arrivalCity.setIdCity("2");
        arrivalCity.setCityName("Санкт-Петербург");

        // Инициализация сущности TransportType
        transportType = new TransportType();
        transportType.setIdTransport("1");
        transportType.setTransportName("Автобус");

        // Инициализация Route
        route = new Route();
        route.setIdRoute(1L);
        route.setDepartureCity(departureCity);
        route.setArrivalCity(arrivalCity);
        route.setTransportType(transportType);
        route.setDepartureDateTime(LocalDateTime.parse("2025-04-20T10:00:00"));
        route.setArrivalDateTime(LocalDateTime.parse("2025-04-20T15:00:00"));
        route.setPrice(new BigDecimal("1500.00"));
        route.setAvailableSeats(50);
    }

    @Nested
    @DisplayName("Тесты для метода получения всех маршрутов")
    class GetAllRoutesTests {

        @Test
        @DisplayName("Должен вернуть список маршрутов, если маршруты существуют")
        void testGetAllRoutes_Success() {
            List<Route> expectedRoutes = Arrays.asList(route);
            when(routeService.getAllRoutes()).thenReturn(expectedRoutes);

            List<Route> actualRoutes = routeController.getAllRoutes();

            assertEquals(1, actualRoutes.size(), "Размер списка маршрутов должен быть 1");
            Route actualRoute = actualRoutes.get(0);
            assertEquals(route.getIdRoute(), actualRoute.getIdRoute(), "ID маршрута должен совпадать");
            assertEquals(route.getDepartureCity().getIdCity(), actualRoute.getDepartureCity().getIdCity(), "ID города отправления должен совпадать");
            assertEquals(route.getArrivalCity().getIdCity(), actualRoute.getArrivalCity().getIdCity(), "ID города назначения должен совпадать");
            assertEquals(route.getTransportType().getIdTransport(), actualRoute.getTransportType().getIdTransport(), "ID типа транспорта должен совпадать");
            verify(routeService, times(1)).getAllRoutes();
        }

        @Test
        @DisplayName("Должен вернуть пустой список, если маршрутов нет")
        void testGetAllRoutes_Empty() {
            when(routeService.getAllRoutes()).thenReturn(Collections.emptyList());

            List<Route> actualRoutes = routeController.getAllRoutes();

            assertTrue(actualRoutes.isEmpty(), "Список маршрутов должен быть пустым");
            verify(routeService, times(1)).getAllRoutes();
        }
    }

    @Nested
    @DisplayName("Тесты для метода получения маршрута по ID")
    class GetRouteByIdTests {

        @Test
        @DisplayName("Должен вернуть маршрут, если ID существует")
        void testGetRouteById_Success() {
            Long routeId = 1L;
            when(routeService.getRouteById(routeId)).thenReturn(route);

            Route actualRoute = routeController.getRouteById(routeId);

            assertNotNull(actualRoute, "Маршрут не должен быть null");
            assertEquals(route.getIdRoute(), actualRoute.getIdRoute(), "ID маршрута должен совпадать");
            assertEquals(route.getDepartureCity().getIdCity(), actualRoute.getDepartureCity().getIdCity(), "ID города отправления должен совпадать");
            assertEquals(route.getTransportType().getIdTransport(), actualRoute.getTransportType().getIdTransport(), "ID типа транспорта должен совпадать");
            verify(routeService, times(1)).getRouteById(routeId);
        }

        @Test
        @DisplayName("Должен выбросить IllegalArgumentException, если маршрут с ID не существует")
        void testGetRouteById_NotFound() {
            Long routeId = 999L;
            when(routeService.getRouteById(routeId)).thenThrow(new IllegalArgumentException("Маршрут не найден"));

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> routeController.getRouteById(routeId),
                    "Ожидается IllegalArgumentException для несуществующего ID маршрута");
            assertEquals("Маршрут не найден", exception.getMessage());
            verify(routeService, times(1)).getRouteById(routeId);
        }

        @Test
        @DisplayName("Должен выбросить IllegalArgumentException, если ID маршрута null")
        void testGetRouteById_NullId() {
            Long routeId = null;
            when(routeService.getRouteById(null)).thenThrow(new IllegalArgumentException("ID маршрута не может быть null"));

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> routeController.getRouteById(routeId),
                    "Ожидается IllegalArgumentException для null ID маршрута");
            assertEquals("ID маршрута не может быть null", exception.getMessage());
            verify(routeService, times(1)).getRouteById(routeId);
        }
    }

    @Nested
    @DisplayName("Тесты для метода получения маршрутов по типу транспорта")
    class GetRoutesByTransportTypeTests {

        @Test
        @DisplayName("Должен вернуть маршруты, если ID транспорта существует и маршруты найдены")
        void testGetRoutesByTransportType_Success() {
            String transportId = "1";
            List<Route> expectedRoutes = Arrays.asList(route);
            when(routeService.getRoutesByTransportType(transportId)).thenReturn(expectedRoutes);

            ResponseEntity<List<Route>> response = routeController.getRoutesByTransportType(transportId);

            assertEquals(HttpStatus.OK, response.getStatusCode(), "Статус должен быть OK");
            assertEquals(expectedRoutes, response.getBody(), "Список маршрутов должен совпадать");
            verify(routeService, times(1)).getRoutesByTransportType(transportId);
        }

        @Test
        @DisplayName("Должен вернуть NOT_FOUND, если маршруты не найдены")
        void testGetRoutesByTransportType_NotFound() {
            String transportId = "999";
            when(routeService.getRoutesByTransportType(transportId)).thenReturn(Collections.emptyList());

            ResponseEntity<List<Route>> response = routeController.getRoutesByTransportType(transportId);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "Статус должен быть NOT_FOUND");
            assertTrue(response.getBody().isEmpty(), "Список маршрутов должен быть пустым");
            verify(routeService, times(1)).getRoutesByTransportType(transportId);
        }

        @Test
        @DisplayName("Должен вернуть INTERNAL_SERVER_ERROR, если сервис выбрасывает исключение")
        void testGetRoutesByTransportType_Exception() {
            String transportId = "1";
            when(routeService.getRoutesByTransportType(transportId)).thenThrow(new RuntimeException("Ошибка сервера"));

            ResponseEntity<List<Route>> response = routeController.getRoutesByTransportType(transportId);

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode(), "Статус должен быть INTERNAL_SERVER_ERROR");
            assertTrue(response.getBody().isEmpty(), "Список маршрутов должен быть пустым");
            verify(routeService, times(1)).getRoutesByTransportType(transportId);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("Должен вернуть INTERNAL_SERVER_ERROR, если ID транспорта null или пустой")
        void testGetRoutesByTransportType_NullOrEmptyId(String transportId) {
            when(routeService.getRoutesByTransportType(transportId))
                    .thenThrow(new IllegalArgumentException("ID типа транспорта не может быть пустым или null"));

            ResponseEntity<List<Route>> response = routeController.getRoutesByTransportType(transportId);

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode(), "Статус должен быть INTERNAL_SERVER_ERROR");
            assertTrue(response.getBody().isEmpty(), "Список маршрутов должен быть пустым");
            verify(routeService, times(1)).getRoutesByTransportType(transportId);
        }
    }

    @Nested
    @DisplayName("Тесты для метода получения маршрутов по дате отправления")
    class GetRoutesByDepartureDateTests {

        @Test
        @DisplayName("Должен вернуть маршруты, если указана корректная дата и маршруты найдены")
        void testGetRoutesByDepartureDate_Success() {
            String startDate = "2025-04-20T10:00:00";
            List<Route> expectedRoutes = Arrays.asList(route);
            when(routeService.getRoutesByDepartureDate(startDate)).thenReturn(expectedRoutes);

            ResponseEntity<List<Route>> response = routeController.getRoutesByDepartureDate(startDate);

            assertEquals(HttpStatus.OK, response.getStatusCode(), "Статус должен быть OK");
            assertEquals(expectedRoutes, response.getBody(), "Список маршрутов должен совпадать");
            verify(routeService, times(1)).getRoutesByDepartureDate(startDate);
        }

        @Test
        @DisplayName("Должен вернуть NOT_FOUND, если маршруты не найдены")
        void testGetRoutesByDepartureDate_NotFound() {
            String startDate = "2025-04-21T10:00:00";
            when(routeService.getRoutesByDepartureDate(startDate)).thenReturn(Collections.emptyList());

            ResponseEntity<List<Route>> response = routeController.getRoutesByDepartureDate(startDate);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "Статус должен быть NOT_FOUND");
            assertTrue(response.getBody().isEmpty(), "Список маршрутов должен быть пустым");
            verify(routeService, times(1)).getRoutesByDepartureDate(startDate);
        }

        @Test
        @DisplayName("Должен вернуть INTERNAL_SERVER_ERROR, если сервис выбрасывает исключение")
        void testGetRoutesByDepartureDate_Exception() {
            String startDate = "2025-04-20T10:00:00";
            when(routeService.getRoutesByDepartureDate(startDate)).thenThrow(new RuntimeException("Ошибка сервера"));

            ResponseEntity<List<Route>> response = routeController.getRoutesByDepartureDate(startDate);

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode(), "Статус должен быть INTERNAL_SERVER_ERROR");
            assertTrue(response.getBody().isEmpty(), "Список маршрутов должен быть пустым");
            verify(routeService, times(1)).getRoutesByDepartureDate(startDate);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("Должен вернуть INTERNAL_SERVER_ERROR, если дата null или пустая")
        void testGetRoutesByDepartureDate_NullOrEmptyDate(String startDate) {
            when(routeService.getRoutesByDepartureDate(startDate))
                    .thenThrow(new IllegalArgumentException("Дата отправления не может быть пустой или null"));

            ResponseEntity<List<Route>> response = routeController.getRoutesByDepartureDate(startDate);

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode(), "Статус должен быть INTERNAL_SERVER_ERROR");
            assertTrue(response.getBody().isEmpty(), "Список маршрутов должен быть пустым");
            verify(routeService, times(1)).getRoutesByDepartureDate(startDate);
        }

        @Test
        @DisplayName("Должен вернуть INTERNAL_SERVER_ERROR, если формат даты некорректен")
        void testGetRoutesByDepartureDate_InvalidDateFormat() {
            String startDate = "invalid-date";
            when(routeService.getRoutesByDepartureDate(startDate))
                    .thenThrow(new IllegalArgumentException("Некорректный формат даты"));

            ResponseEntity<List<Route>> response = routeController.getRoutesByDepartureDate(startDate);

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode(), "Статус должен быть INTERNAL_SERVER_ERROR");
            assertTrue(response.getBody().isEmpty(), "Список маршрутов должен быть пустым");
            verify(routeService, times(1)).getRoutesByDepartureDate(startDate);
        }
    }

    @Nested
    @DisplayName("Тесты для метода получения маршрутов по интервалу дат")
    class GetRoutesByIntervalOfDatesTests {

        @Test
        @DisplayName("Должен вернуть маршруты, если указан корректный диапазон дат")
        void testGetRoutesByIntervalOfDates_Success() {
            String dateOne = "2025-04-20T00:00:00";
            String dateTwo = "2025-04-25T23:59:59";
            List<Route> expectedRoutes = Arrays.asList(route);
            when(routeService.getRoutesByIntervalOfDates(dateOne, dateTwo)).thenReturn(expectedRoutes);

            ResponseEntity<?> response = routeController.getRoutesByIntervalOfDates(dateOne, dateTwo);

            assertEquals(HttpStatus.OK, response.getStatusCode(), "Статус должен быть OK");
            assertEquals(expectedRoutes, response.getBody(), "Список маршрутов должен совпадать");
            verify(routeService, times(1)).getRoutesByIntervalOfDates(dateOne, dateTwo);
        }

        @Test
        @DisplayName("Должен вернуть NOT_FOUND, если сервис выбрасывает IllegalStateException")
        void testGetRoutesByIntervalOfDates_NotFound() {
            String dateOne = "2025-04-20T00:00:00";
            String dateTwo = "2025-04-25T23:59:59";
            when(routeService.getRoutesByIntervalOfDates(dateOne, dateTwo))
                    .thenThrow(new IllegalStateException("Маршруты не найдены"));

            ResponseEntity<?> response = routeController.getRoutesByIntervalOfDates(dateOne, dateTwo);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "Статус должен быть NOT_FOUND");
            assertEquals("Маршруты не найдены", response.getBody(), "Тело ответа должно совпадать");
            verify(routeService, times(1)).getRoutesByIntervalOfDates(dateOne, dateTwo);
        }

        @ParameterizedTest
        @CsvSource({
                "'', '2025-04-25T23:59:59', Дата начала не может быть пустой",
                "'2025-04-20T00:00:00', '', Дата окончания не может быть пустой",
                "'invalid-date', '2025-04-25T23:59:59', Некорректный формат даты начала",
                "'2025-04-20T00:00:00', 'invalid-date', Некорректный формат даты окончания"
        })
        @DisplayName("Должен вернуть NOT_FOUND, если даты некорректны")
        void testGetRoutesByIntervalOfDates_InvalidDates(String dateOne, String dateTwo, String errorMessage) {
            when(routeService.getRoutesByIntervalOfDates(dateOne, dateTwo))
                    .thenThrow(new IllegalStateException(errorMessage));

            ResponseEntity<?> response = routeController.getRoutesByIntervalOfDates(dateOne, dateTwo);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "Статус должен быть NOT_FOUND");
            assertEquals(errorMessage, response.getBody(), "Тело ответа должно совпадать");
            verify(routeService, times(1)).getRoutesByIntervalOfDates(dateOne, dateTwo);
        }
    }

    @Nested
    @DisplayName("Тесты для метода получения маршрутов по городам отправления и назначения")
    class GetRoutesByDepartureAndDestinationPointTests {

        @Test
        @DisplayName("Должен вернуть маршруты, если города существуют и маршруты найдены")
        void testGetRoutesByDepartureAndDestinationPoint_Success() {
            String departureCityId = "1";
            String arrivalCityId = "2";
            List<Route> expectedRoutes = Arrays.asList(route);
            when(routeService.getRoutesByDepartureAndArrivalCity(departureCityId, arrivalCityId)).thenReturn(expectedRoutes);

            ResponseEntity<List<Route>> response = routeController.getRoutesByDepartureAndDestinationPoint(departureCityId, arrivalCityId);

            assertEquals(HttpStatus.OK, response.getStatusCode(), "Статус должен быть OK");
            assertEquals(expectedRoutes, response.getBody(), "Список маршрутов должен совпадать");
            verify(routeService, times(1)).getRoutesByDepartureAndArrivalCity(departureCityId, arrivalCityId);
        }

        @Test
        @DisplayName("Должен вернуть NOT_FOUND, если маршруты не найдены")
        void testGetRoutesByDepartureAndDestinationPoint_NotFound() {
            String departureCityId = "999";
            String arrivalCityId = "998";
            when(routeService.getRoutesByDepartureAndArrivalCity(departureCityId, arrivalCityId))
                    .thenReturn(Collections.emptyList());

            ResponseEntity<List<Route>> response = routeController.getRoutesByDepartureAndDestinationPoint(departureCityId, arrivalCityId);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "Статус должен быть NOT_FOUND");
            assertTrue(response.getBody().isEmpty(), "Список маршрутов должен быть пустым");
            verify(routeService, times(1)).getRoutesByDepartureAndArrivalCity(departureCityId, arrivalCityId);
        }

        @Test
        @DisplayName("Должен вернуть INTERNAL_SERVER_ERROR, если сервис выбрасывает исключение")
        void testGetRoutesByDepartureAndDestinationPoint_Exception() {
            String departureCityId = "1";
            String arrivalCityId = "2";
            when(routeService.getRoutesByDepartureAndArrivalCity(departureCityId, arrivalCityId))
                    .thenThrow(new RuntimeException("Ошибка сервера"));

            ResponseEntity<List<Route>> response = routeController.getRoutesByDepartureAndDestinationPoint(departureCityId, arrivalCityId);

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode(), "Статус должен быть INTERNAL_SERVER_ERROR");
            assertTrue(response.getBody().isEmpty(), "Список маршрутов должен быть пустым");
            verify(routeService, times(1)).getRoutesByDepartureAndArrivalCity(departureCityId, arrivalCityId);
        }

        @ParameterizedTest
        @CsvSource({
                "'', '2', ID города отправления не может быть пустым",
                "'1', '', ID города назначения не может быть пустым"
        })
        @DisplayName("Должен вернуть INTERNAL_SERVER_ERROR, если ID городов пустые")
        void testGetRoutesByDepartureAndDestinationPoint_EmptyCityIds(String departureCityId, String arrivalCityId, String errorMessage) {
            when(routeService.getRoutesByDepartureAndArrivalCity(departureCityId, arrivalCityId))
                    .thenThrow(new IllegalArgumentException(errorMessage));

            ResponseEntity<List<Route>> response = routeController.getRoutesByDepartureAndDestinationPoint(departureCityId, arrivalCityId);

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode(), "Статус должен быть INTERNAL_SERVER_ERROR");
            assertTrue(response.getBody().isEmpty(), "Список маршрутов должен быть пустым");
            verify(routeService, times(1)).getRoutesByDepartureAndArrivalCity(departureCityId, arrivalCityId);
        }
    }

    @Nested
    @DisplayName("Тесты для метода получения отфильтрованных маршрутов")
    class GetFilteredRoutesTests {

        @Test
        @DisplayName("Должен вернуть маршруты, если все параметры корректны")
        void testGetFilteredRoutes_Success() {
            String departureCityId = "1";
            String arrivalCityId = "2";
            String transportId = "1";
            String startDate = "2025-04-20T10:00:00";
            String dateOne = "2025-04-20T00:00:00";
            String dateTwo = "2025-04-25T23:59:59";
            List<Route> expectedRoutes = Arrays.asList(route);
            when(routeService.getFilteredRoutes(departureCityId, arrivalCityId, transportId, startDate, dateOne, dateTwo))
                    .thenReturn(expectedRoutes);

            ResponseEntity<List<Route>> response = routeController.getFilteredRoutes(departureCityId, arrivalCityId, transportId, startDate, dateOne, dateTwo);

            assertEquals(HttpStatus.OK, response.getStatusCode(), "Статус должен быть OK");
            assertEquals(expectedRoutes, response.getBody(), "Список маршрутов должен совпадать");
            verify(routeService, times(1)).getFilteredRoutes(departureCityId, arrivalCityId, transportId, startDate, dateOne, dateTwo);
        }

        @Test
        @DisplayName("Должен вернуть NOT_FOUND, если маршруты не найдены")
        void testGetFilteredRoutes_NotFound() {
            String departureCityId = "999";
            String arrivalCityId = "998";
            String transportId = "999";
            String startDate = "2025-04-21T10:00:00";
            String dateOne = "2025-04-21T00:00:00";
            String dateTwo = "2025-04-26T23:59:59";
            when(routeService.getFilteredRoutes(departureCityId, arrivalCityId, transportId, startDate, dateOne, dateTwo))
                    .thenReturn(Collections.emptyList());

            ResponseEntity<List<Route>> response = routeController.getFilteredRoutes(departureCityId, arrivalCityId, transportId, startDate, dateOne, dateTwo);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "Статус должен быть NOT_FOUND");
            assertTrue(response.getBody().isEmpty(), "Список маршрутов должен быть пустым");
            verify(routeService, times(1)).getFilteredRoutes(departureCityId, arrivalCityId, transportId, startDate, dateOne, dateTwo);
        }

        @Test
        @DisplayName("Должен вернуть INTERNAL_SERVER_ERROR, если сервис выбрасывает исключение")
        void testGetFilteredRoutes_Exception() {
            String departureCityId = "1";
            String arrivalCityId = "2";
            String transportId = "1";
            String startDate = "2025-04-20T10:00:00";
            String dateOne = "2025-04-20T00:00:00";
            String dateTwo = "2025-04-25T23:59:59";
            when(routeService.getFilteredRoutes(departureCityId, arrivalCityId, transportId, startDate, dateOne, dateTwo))
                    .thenThrow(new RuntimeException("Ошибка сервера"));

            ResponseEntity<List<Route>> response = routeController.getFilteredRoutes(departureCityId, arrivalCityId, transportId, startDate, dateOne, dateTwo);

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode(), "Статус должен быть INTERNAL_SERVER_ERROR");
            assertTrue(response.getBody().isEmpty(), "Список маршрутов должен быть пустым");
            verify(routeService, times(1)).getFilteredRoutes(departureCityId, arrivalCityId, transportId, startDate, dateOne, dateTwo);
        }

        @Test
        @DisplayName("Должен вернуть маршруты, если некоторые параметры null")
        void testGetFilteredRoutes_SomeParametersNull() {
            List<Route> expectedRoutes = Arrays.asList(route);
            when(routeService.getFilteredRoutes(null, null, null, null, null, null)).thenReturn(expectedRoutes);

            ResponseEntity<List<Route>> response = routeController.getFilteredRoutes(null, null, null, null, null, null);

            assertEquals(HttpStatus.OK, response.getStatusCode(), "Статус должен быть OK");
            assertEquals(expectedRoutes, response.getBody(), "Список маршрутов должен совпадать");
            verify(routeService, times(1)).getFilteredRoutes(null, null, null, null, null, null);
        }
    }

    @Nested
    @DisplayName("Тесты для метода обновления маршрута")
    class UpdateRouteTests {

        @Test
        @DisplayName("Должен успешно обновить маршрут")
        void testUpdateRoute_Success() {
            Long routeId = 1L;
            Route updatedRoute = new Route();
            updatedRoute.setIdRoute(1L);
            updatedRoute.setDepartureCity(new City());
            updatedRoute.getDepartureCity().setIdCity("3");
            updatedRoute.setArrivalCity(new City());
            updatedRoute.getArrivalCity().setIdCity("4");
            updatedRoute.setTransportType(new TransportType());
            updatedRoute.getTransportType().setIdTransport("2");
            updatedRoute.setDepartureDateTime(LocalDateTime.parse("2025-04-21T12:00:00"));
            updatedRoute.setArrivalDateTime(LocalDateTime.parse("2025-04-21T18:00:00"));
            updatedRoute.setPrice(new BigDecimal("2000.00"));
            updatedRoute.setAvailableSeats(40);
            when(routeService.updateRoute(eq(routeId), any(Route.class))).thenReturn(updatedRoute);

            Route actualRoute = routeController.updateRoute(routeId, updatedRoute);

            assertNotNull(actualRoute, "Обновленный маршрут не должен быть null");
            assertEquals(routeId, actualRoute.getIdRoute(), "ID маршрута должен совпадать");
            assertEquals("3", actualRoute.getDepartureCity().getIdCity(), "ID города отправления должен совпадать");
            assertEquals("4", actualRoute.getArrivalCity().getIdCity(), "ID города назначения должен совпадать");
            assertEquals("2", actualRoute.getTransportType().getIdTransport(), "ID типа транспорта должен совпадать");
            assertEquals(new BigDecimal("2000.00"), actualRoute.getPrice(), "Цена должна совпадать");
            verify(routeService, times(1)).updateRoute(routeId, updatedRoute);
        }

        @Test
        @DisplayName("Должен выбросить IllegalArgumentException, если маршрут с ID не существует")
        void testUpdateRoute_NotFound() {
            Long routeId = 999L;
            Route updatedRoute = new Route();
            when(routeService.updateRoute(eq(routeId), any(Route.class)))
                    .thenThrow(new IllegalArgumentException("Маршрут не найден"));

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> routeController.updateRoute(routeId, updatedRoute),
                    "Ожидается IllegalArgumentException для несуществующего ID маршрута");
            assertEquals("Маршрут не найден", exception.getMessage());
            verify(routeService, times(1)).updateRoute(routeId, updatedRoute);
        }

        @Test
        @DisplayName("Должен выбросить IllegalArgumentException, если ID маршрута null")
        void testUpdateRoute_NullId() {
            Long routeId = null;
            Route updatedRoute = new Route();
            when(routeService.updateRoute(isNull(), any(Route.class)))
                    .thenThrow(new IllegalArgumentException("ID маршрута не может быть null"));

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> routeController.updateRoute(routeId, updatedRoute),
                    "Ожидается IllegalArgumentException для null ID маршрута");
            assertEquals("ID маршрута не может быть null", exception.getMessage());
            verify(routeService, times(1)).updateRoute(routeId, updatedRoute);
        }

        @Test
        @DisplayName("Должен выбросить IllegalArgumentException, если город отправления null")
        void testUpdateRoute_NullDepartureCity() {
            Long routeId = 1L;
            Route updatedRoute = new Route();
            updatedRoute.setIdRoute(routeId);
            updatedRoute.setArrivalCity(arrivalCity);
            updatedRoute.setTransportType(transportType);
            updatedRoute.setDepartureDateTime(LocalDateTime.now());
            updatedRoute.setArrivalDateTime(LocalDateTime.now().plusHours(5));
            when(routeService.updateRoute(eq(routeId), any(Route.class)))
                    .thenThrow(new IllegalArgumentException("Город отправления не может быть null"));

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> routeController.updateRoute(routeId, updatedRoute),
                    "Ожидается IllegalArgumentException для null города отправления");
            assertEquals("Город отправления не может быть null", exception.getMessage());
            verify(routeService, times(1)).updateRoute(routeId, updatedRoute);
        }
    }

    @Nested
    @DisplayName("Тесты для метода создания маршрута")
    class CreateRouteTests {

        @Test
        @DisplayName("Должен успешно создать маршрут")
        void testCreateRoute_Success() {
            Route newRoute = new Route();
            newRoute.setIdRoute(2L);
            newRoute.setDepartureCity(new City());
            newRoute.getDepartureCity().setIdCity("3");
            newRoute.setArrivalCity(new City());
            newRoute.getArrivalCity().setIdCity("4");
            newRoute.setTransportType(new TransportType());
            newRoute.getTransportType().setIdTransport("2");
            newRoute.setDepartureDateTime(LocalDateTime.parse("2025-04-21T12:00:00"));
            newRoute.setArrivalDateTime(LocalDateTime.parse("2025-04-21T18:00:00"));
            newRoute.setPrice(new BigDecimal("2000.00"));
            newRoute.setAvailableSeats(40);
            when(routeService.createRoute(any(Route.class))).thenReturn(newRoute);

            Route actualRoute = routeController.createRoute(newRoute);

            assertNotNull(actualRoute, "Созданный маршрут не должен быть null");
            assertEquals(2L, actualRoute.getIdRoute(), "ID маршрута должен совпадать");
            assertEquals("3", actualRoute.getDepartureCity().getIdCity(), "ID города отправления должен совпадать");
            assertEquals("4", actualRoute.getArrivalCity().getIdCity(), "ID города назначения должен совпадать");
            assertEquals("2", actualRoute.getTransportType().getIdTransport(), "ID типа транспорта должен совпадать");
            assertEquals(new BigDecimal("2000.00"), actualRoute.getPrice(), "Цена должна совпадать");
            verify(routeService, times(1)).createRoute(newRoute);
        }

        @Test
        @DisplayName("Должен выбросить IllegalArgumentException, если маршрут null")
        void testCreateRoute_NullRoute() {
            when(routeService.createRoute(null))
                    .thenThrow(new IllegalArgumentException("Маршрут не может быть null"));

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> routeController.createRoute(null),
                    "Ожидается IllegalArgumentException для null маршрута");
            assertEquals("Маршрут не может быть null", exception.getMessage());
            verify(routeService, times(1)).createRoute(null);
        }

        @Test
        @DisplayName("Должен выбросить IllegalArgumentException, если город отправления null")
        void testCreateRoute_NullDepartureCity() {
            Route invalidRoute = new Route();
            invalidRoute.setIdRoute(2L);
            invalidRoute.setArrivalCity(arrivalCity);
            invalidRoute.setTransportType(transportType);
            invalidRoute.setDepartureDateTime(LocalDateTime.now());
            invalidRoute.setArrivalDateTime(LocalDateTime.now().plusHours(5));
            when(routeService.createRoute(any(Route.class)))
                    .thenThrow(new IllegalArgumentException("Город отправления не может быть null"));

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> routeController.createRoute(invalidRoute),
                    "Ожидается IllegalArgumentException для null города отправления");
            assertEquals("Город отправления не может быть null", exception.getMessage());
            verify(routeService, times(1)).createRoute(invalidRoute);
        }

        @Test
        @DisplayName("Должен выбросить IllegalArgumentException, если тип транспорта null")
        void testCreateRoute_NullTransportType() {
            Route invalidRoute = new Route();
            invalidRoute.setIdRoute(2L);
            invalidRoute.setDepartureCity(departureCity);
            invalidRoute.setArrivalCity(arrivalCity);
            invalidRoute.setDepartureDateTime(LocalDateTime.now());
            invalidRoute.setArrivalDateTime(LocalDateTime.now().plusHours(5));
            when(routeService.createRoute(any(Route.class)))
                    .thenThrow(new IllegalArgumentException("Тип транспорта не может быть null"));

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> routeController.createRoute(invalidRoute),
                    "Ожидается IllegalArgumentException для null типа транспорта");
            assertEquals("Тип транспорта не может быть null", exception.getMessage());
            verify(routeService, times(1)).createRoute(invalidRoute);
        }

        @Test
        @DisplayName("Должен выбросить IllegalArgumentException, если цена отрицательная")
        void testCreateRoute_NegativePrice() {
            Route invalidRoute = new Route();
            invalidRoute.setIdRoute(2L);
            invalidRoute.setDepartureCity(departureCity);
            invalidRoute.setArrivalCity(arrivalCity);
            invalidRoute.setTransportType(transportType);
            invalidRoute.setDepartureDateTime(LocalDateTime.now());
            invalidRoute.setArrivalDateTime(LocalDateTime.now().plusHours(5));
            invalidRoute.setPrice(new BigDecimal("-100.00"));
            when(routeService.createRoute(any(Route.class)))
                    .thenThrow(new IllegalArgumentException("Цена не может быть отрицательной"));

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> routeController.createRoute(invalidRoute),
                    "Ожидается IllegalArgumentException для отрицательной цены");
            assertEquals("Цена не может быть отрицательной", exception.getMessage());
            verify(routeService, times(1)).createRoute(invalidRoute);
        }
    }

    @Nested
    @DisplayName("Тесты для метода удаления маршрута")
    class DeleteRouteTests {

        @Test
        @DisplayName("Должен успешно удалить маршрут")
        void testDeleteRoute_Success() {
            Long routeId = 1L;
            doNothing().when(routeService).deleteRoute(routeId);

            ResponseEntity<String> response = routeController.deleteRoute(routeId);

            assertEquals(HttpStatus.OK, response.getStatusCode(), "Статус должен быть OK");
            assertEquals("Маршрут успешно удален.", response.getBody(), "Тело ответа должно совпадать");
            verify(routeService, times(1)).deleteRoute(routeId);
        }

        @Test
        @DisplayName("Должен вернуть NOT_FOUND, если маршрут с ID не существует")
        void testDeleteRoute_NotFound() {
            Long routeId = 999L;
            doThrow(new IllegalArgumentException("Маршрут не найден"))
                    .when(routeService).deleteRoute(routeId);

            ResponseEntity<String> response = routeController.deleteRoute(routeId);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "Статус должен быть NOT_FOUND");
            assertEquals("Маршрут не найден", response.getBody(), "Тело ответа должно совпадать");
            verify(routeService, times(1)).deleteRoute(routeId);
        }

        @Test
        @DisplayName("Должен вернуть BAD_REQUEST, если маршрут не может быть удален")
        void testDeleteRoute_CannotDelete() {
            Long routeId = 1L;
            doThrow(new IllegalStateException("Маршрут не может быть удален"))
                    .when(routeService).deleteRoute(routeId);

            ResponseEntity<String> response = routeController.deleteRoute(routeId);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Статус должен быть BAD_REQUEST");
            assertEquals("Маршрут не может быть удален", response.getBody(), "Тело ответа должно совпадать");
            verify(routeService, times(1)).deleteRoute(routeId);
        }

        @Test
        @DisplayName("Должен вернуть NOT_FOUND, если ID маршрута null")
        void testDeleteRoute_NullId() {
            Long routeId = null;
            doThrow(new IllegalArgumentException("ID маршрута не может быть null"))
                    .when(routeService).deleteRoute(isNull());

            ResponseEntity<String> response = routeController.deleteRoute(routeId);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "Статус должен быть NOT_FOUND");
            assertEquals("ID маршрута не может быть null", response.getBody(), "Тело ответа должно совпадать");
            verify(routeService, times(1)).deleteRoute(routeId);
        }
    }

    @Nested
    @DisplayName("Тесты для метода получения маршрутов с пагинацией")
    class GetPagedRoutesTests {

        @Test
        @DisplayName("Должен вернуть маршруты с пагинацией, если страница и размер корректны")
        void testGetPagedRoutes_Success() {
            int page = 0;
            int size = 10;
            List<Route> routes = Arrays.asList(route);
            Page<Route> expectedPage = new PageImpl<>(routes, PageRequest.of(page, size), 1);
            when(routeService.getRoutes(page, size)).thenReturn(expectedPage);

            Page<Route> actualPage = routeController.getPagedRoutes(page, size);

            assertEquals(1, actualPage.getContent().size(), "Страница должна содержать 1 маршрут");
            assertEquals(route.getIdRoute(), actualPage.getContent().get(0).getIdRoute(), "ID маршрута должен совпадать");
            assertEquals(route.getDepartureCity().getIdCity(), actualPage.getContent().get(0).getDepartureCity().getIdCity(), "ID города отправления должен совпадать");
            assertEquals(1, actualPage.getTotalElements(), "Общее количество элементов должно быть 1");
            verify(routeService, times(1)).getRoutes(page, size);
        }

        @Test
        @DisplayName("Должен вернуть пустую страницу, если маршрутов нет")
        void testGetPagedRoutes_Empty() {
            int page = 0;
            int size = 10;
            Page<Route> expectedPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(page, size), 0);
            when(routeService.getRoutes(page, size)).thenReturn(expectedPage);

            Page<Route> actualPage = routeController.getPagedRoutes(page, size);

            assertTrue(actualPage.getContent().isEmpty(), "Содержимое страницы должно быть пустым");
            assertEquals(0, actualPage.getTotalElements(), "Общее количество элементов должно быть 0");
            verify(routeService, times(1)).getRoutes(page, size);
        }

        @ParameterizedTest
        @ValueSource(ints = {-1, -10})
        @DisplayName("Должен обработать отрицательный номер страницы")
        void testGetPagedRoutes_NegativePage(int page) {
            int size = 10;
            Page<Route> expectedPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, size), 0);
            when(routeService.getRoutes(page, size)).thenReturn(expectedPage);

            Page<Route> actualPage = routeController.getPagedRoutes(page, size);

            assertTrue(actualPage.getContent().isEmpty(), "Содержимое страницы должно быть пустым для отрицательной страницы");
            verify(routeService, times(1)).getRoutes(page, size);
        }

        @ParameterizedTest
        @ValueSource(ints = {0, -1})
        @DisplayName("Должен обработать некорректный размер страницы")
        void testGetPagedRoutes_InvalidSize(int size) {
            int page = 0;
            Page<Route> expectedPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(page, Math.max(1, size)), 0);
            when(routeService.getRoutes(page, size)).thenReturn(expectedPage);

            Page<Route> actualPage = routeController.getPagedRoutes(page, size);

            assertTrue(actualPage.getContent().isEmpty(), "Содержимое страницы должно быть пустым для некорректного размера");
            verify(routeService, times(1)).getRoutes(page, size);
        }
    }
}