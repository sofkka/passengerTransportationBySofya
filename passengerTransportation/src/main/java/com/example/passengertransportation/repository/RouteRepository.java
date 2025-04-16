package com.example.passengertransportation.repository;

import com.example.passengertransportation.model.City;
import com.example.passengertransportation.model.Route;
import com.example.passengertransportation.model.TransportType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
    // Поиск маршрутов по типу транспорта
    List<Route> findByTransportType(TransportType transportType);

    // Поиск маршрутов по городам отправления и прибытия
    List<Route> findByDepartureCityAndArrivalCity(City departureCity, City arrivalCity);

    // Поиск маршрутов по дате отправления
    List<Route> findByDepartureDateTime(LocalDateTime departureDateTime);

    // Поиск маршрутов в диапазоне дат
    List<Route> findByDepartureDateTimeBetween(LocalDateTime start, LocalDateTime end);

    Page<Route> findAll(Pageable pageable); // стандартная пагинация

    // Пагинация маршрутов с фильтром по дате (не раньше, чем 24 часа назад) и сортировкой по departureDateTime
    Page<Route> findByDepartureDateTimeAfterOrderByDepartureDateTimeAsc(LocalDateTime dateTime, Pageable pageable);
}