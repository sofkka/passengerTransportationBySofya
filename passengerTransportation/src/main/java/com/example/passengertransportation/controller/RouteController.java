package com.example.passengertransportation.controller;

import com.example.passengertransportation.model.Route;
import com.example.passengertransportation.service.RouteService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/routes")
public class RouteController {

    @Autowired
    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    // Получение всех маршрутов
    @Operation(summary = "Получение всех маршрутов", description = "Возвращает список всех доступных маршрутов")
    @GetMapping("")
    public List<Route> getAllRoutes() {
        return routeService.getAllRoutes();
    }

    // Получение маршрута по ID
    @Operation(summary = "Получение маршрута по ID", description = "Возвращает маршрут по указанному идентификатору")
    @GetMapping("/{id}")
    public Route getRouteById(@PathVariable Long id) {
        return routeService.getRouteById(id);
    }

    // Получение маршрутов по типу транспорта
    @Operation(summary = "Получение маршрутов по типу транспорта", description = "Возвращает маршруты по указанному типу транспорта")
    @GetMapping("/transport/{transportId}")
    public ResponseEntity<List<Route>> getRoutesByTransportType(@PathVariable String transportId) {
        try {
            List<Route> routes = routeService.getRoutesByTransportType(transportId);
            if (routes.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(routes);
            }
            return ResponseEntity.ok(routes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    // Получение маршрутов по дате отправления
    @Operation(summary = "Получение маршрутов по дате отправления", description = "Возвращает маршруты на указанную дату отправления")
    @GetMapping("/startDate")
    public ResponseEntity<List<Route>> getRoutesByDepartureDate(@RequestParam String startDate) {
        try {
            List<Route> routes = routeService.getRoutesByDepartureDate(startDate);
            if (routes.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(routes);
            }
            return ResponseEntity.ok(routes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    // Получение маршрутов по диапазону дат
    @Operation(summary = "Получение маршрутов по диапазону дат", description = "Возвращает маршруты в указанном диапазоне дат отправления")
    @GetMapping("/intervalDates")
    public ResponseEntity<?> getRoutesByIntervalOfDates(@RequestParam String dateOne, @RequestParam String dateTwo) {
        try {
            List<Route> routes = routeService.getRoutesByIntervalOfDates(dateOne, dateTwo);
            return ResponseEntity.ok(routes);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Получение маршрутов по городам отправления и назначения
    @Operation(summary = "Получение маршрутов по городам", description = "Возвращает маршруты по указанным городам отправления и назначения")
    @GetMapping("/points")
    public ResponseEntity<List<Route>> getRoutesByDepartureAndDestinationPoint(
            @RequestParam String departureCityId,
            @RequestParam String arrivalCityId) {
        try {
            List<Route> routes = routeService.getRoutesByDepartureAndArrivalCity(departureCityId, arrivalCityId);
            if (routes.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(routes);
            }
            return ResponseEntity.ok(routes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    // Фильтрация маршрутов по параметрам
    @Operation(summary = "Фильтрация маршрутов", description = "Возвращает маршруты, отфильтрованные по указанным параметрам")
    @GetMapping("/filteredRoutes")
    public ResponseEntity<List<Route>> getFilteredRoutes(
            @RequestParam(required = false) String departureCityId,
            @RequestParam(required = false) String arrivalCityId,
            @RequestParam(required = false) String transportId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String dateOne,
            @RequestParam(required = false) String dateTwo) {
        try {
            List<Route> routes = routeService.getFilteredRoutes(departureCityId, arrivalCityId, transportId, startDate, dateOne, dateTwo);
            if (routes.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(routes);
            }
            return ResponseEntity.ok(routes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    // Обновление маршрута
    @Operation(summary = "Обновление маршрута", description = "Обновляет информацию о маршруте по указанному идентификатору")
    @PutMapping("/{id}")
    public Route updateRoute(@PathVariable Long id, @RequestBody Route route) {
        return routeService.updateRoute(id, route);
    }

    // Создание нового маршрута
    @Operation(summary = "Добавление маршрута", description = "Позволяет добавить новый маршрут")
    @PostMapping("")
    public Route createRoute(@RequestBody Route route) {
        return routeService.createRoute(route);
    }

    // Удаление маршрута
    @Operation(summary = "Удаление маршрута", description = "Удаляет маршрут по указанному идентификатору")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRoute(@PathVariable Long id) {
        try {
            routeService.deleteRoute(id);
            return ResponseEntity.ok("Маршрут успешно удален.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/paged")
    public Page<Route> getPagedRoutes(@RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int size) {
        return routeService.getRoutes(page, size);
    }
}