package com.example.passengertransportation.service;

import com.example.passengertransportation.model.City;
import com.example.passengertransportation.model.Route;
import com.example.passengertransportation.model.TransportType;
import com.example.passengertransportation.repository.BookingSeatsRepository;
import com.example.passengertransportation.repository.RouteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RouteService {

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private BookingSeatsRepository bookingSeatsRepository;

    // Получение всех маршрутов
    public List<Route> getAllRoutes() {
        return routeRepository.findAll();
    }

    public Page<Route> getRoutes(int page, int size) {
        // Фильтруем маршруты, у которых departureDateTime не раньше, чем 4 часа назад
        LocalDateTime oneDayAgo = LocalDateTime.now().minusHours(4);
        return routeRepository.findByDepartureDateTimeAfterOrderByDepartureDateTimeAsc(oneDayAgo, PageRequest.of(page, size));
    }

    // Получение маршрута по ID
    public Route getRouteById(Long idRoute) {
        return routeRepository.findById(idRoute).orElse(null);
    }

    // Создание нового маршрута
    public Route createRoute(Route route) {
        return routeRepository.save(route);
    }

    // Обновление существующего маршрута
    public Route updateRoute(Long idRoute, Route route) {
        Route existingRoute = routeRepository.findById(idRoute).orElse(null);
        if (existingRoute != null) {
            existingRoute.setTransportType(route.getTransportType());
            existingRoute.setDepartureCity(route.getDepartureCity());
            existingRoute.setArrivalCity(route.getArrivalCity());
            existingRoute.setDepartureDateTime(route.getDepartureDateTime());
            existingRoute.setArrivalDateTime(route.getArrivalDateTime());
            existingRoute.setPrice(route.getPrice());
            existingRoute.setAvailableSeats(route.getAvailableSeats());
            return routeRepository.save(existingRoute);
        }
        return null;
    }

    // Удаление маршрута по ID
    public void deleteRoute(Long idRoute) {
        Route route = routeRepository.findById(idRoute).orElse(null);
        if (route == null) {
            throw new IllegalArgumentException("Маршрут с таким ID не найден.");
        }
        if (bookingSeatsRepository.existsByRoute(route)) {
            throw new IllegalStateException("Нельзя удалить маршрут, так как на него есть бронирования.");
        }
        routeRepository.deleteById(idRoute);
    }

    // Поиск маршрутов по типу транспорта
    public List<Route> getRoutesByTransportType(String transportId) {
        TransportType transportType = new TransportType();
        transportType.setIdTransport(transportId);
        return routeRepository.findByTransportType(transportType);
    }

    // Поиск маршрутов по дате отправления
    public List<Route> getRoutesByDepartureDate(String startDate) {
        LocalDateTime date = LocalDateTime.parse(startDate, DateTimeFormatter.ISO_DATE_TIME);
        return routeRepository.findByDepartureDateTime(date);
    }

    // Поиск маршрутов по диапазону дат
    public List<Route> getRoutesByIntervalOfDates(String dateOne, String dateTwo) {
        LocalDateTime start = LocalDateTime.parse(dateOne, DateTimeFormatter.ISO_DATE_TIME);
        LocalDateTime end = LocalDateTime.parse(dateTwo, DateTimeFormatter.ISO_DATE_TIME);
        return routeRepository.findByDepartureDateTimeBetween(start, end);
    }

    // Фильтрация маршрутов по параметрам
    public List<Route> getFilteredRoutes(String departureCity,
                                         String arrivalCity,
                                         String transportType,
                                         String startDate,
                                         String dateOne,
                                         String dateTwo) {
        List<Route> routes = routeRepository.findAll();

        // 1. Фильтрация по городу отправления
        if (departureCity != null && !departureCity.isEmpty()) {
            routes = routes.stream()
                    .filter(route -> route.getDepartureCity() != null &&
                            departureCity.equals(route.getDepartureCity().getIdCity()))
                    .collect(Collectors.toList());
        }

        // 2. Фильтрация по городу назначения
        if (arrivalCity != null && !arrivalCity.isEmpty()) {
            routes = routes.stream()
                    .filter(route -> route.getArrivalCity() != null &&
                            arrivalCity.equals(route.getArrivalCity().getIdCity()))
                    .collect(Collectors.toList());
        }

        // 3. Фильтрация по типу транспорта
        if (transportType != null && !transportType.isEmpty()) {
            routes = routes.stream()
                    .filter(route -> route.getTransportType() != null &&
                            transportType.equals(route.getTransportType().getIdTransport()))
                    .collect(Collectors.toList());
        }

        // 4. Фильтрация по точной дате (обязательно, если startDate передан)
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if (startDate != null && !startDate.isEmpty()) {
            LocalDate filterDate = LocalDate.parse(startDate, dateFormatter);
            routes = routes.stream()
                    .filter(route -> {
                        LocalDate routeDate = route.getDepartureDateTime().toLocalDate();
                        return routeDate.equals(filterDate);
                    })
                    .collect(Collectors.toList());
        } else if (dateOne != null && !dateOne.isEmpty() &&
                dateTwo != null && !dateTwo.isEmpty()) {
            // Фильтрация по диапазону дат (если нужно оставить эту функциональность)
            LocalDate dateStart = LocalDate.parse(dateOne, dateFormatter);
            LocalDate dateEnd = LocalDate.parse(dateTwo, dateFormatter);
            routes = routes.stream()
                    .filter(route -> {
                        LocalDate routeDate = route.getDepartureDateTime().toLocalDate();
                        return !routeDate.isBefore(dateStart) && !routeDate.isAfter(dateEnd);
                    })
                    .collect(Collectors.toList());
        } else {
            // Если startDate не передан, возвращаем пустой список или можно выбросить исключение
            return List.of();
        }

        return routes;
    }

    // Поиск маршрутов по городам отправления и прибытия
    public List<Route> getRoutesByDepartureAndArrivalCity(String departureCityId, String arrivalCityId) {
        City departureCity = new City();
        departureCity.setIdCity(departureCityId);
        City arrivalCity = new City();
        arrivalCity.setIdCity(arrivalCityId);
        return routeRepository.findByDepartureCityAndArrivalCity(departureCity, arrivalCity);
    }
}