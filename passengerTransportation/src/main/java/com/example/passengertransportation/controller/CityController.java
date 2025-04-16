package com.example.passengertransportation.controller;

import com.example.passengertransportation.model.City;
import com.example.passengertransportation.service.CityService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cities")
public class CityController {

    @Autowired
    private CityService cityService;

    // Получение всех городов
    @Operation(summary = "Получение всех городов", description = "Возвращает список всех доступных городов")
    @GetMapping("")
    public List<City> getAllCities() {
        return cityService.getAllCities();
    }

    // Получение города по ID
    @Operation(summary = "Получение города по ID", description = "Возвращает город по указанному идентификатору")
    @GetMapping("/{id}")
    public City getCityById(@PathVariable String id) {
        return cityService.getCityById(id);
    }

    // Создание нового города
    @Operation(summary = "Добавление города", description = "Позволяет добавить новый город")
    @PostMapping("")
    public City createCity(@RequestBody City city) {
        return cityService.createCity(city);
    }

    // Обновление города
    @Operation(summary = "Обновление города", description = "Обновляет информацию о городе по указанному идентификатору")
    @PutMapping("/{id}")
    public City updateCity(@PathVariable String id, @RequestBody City city) {
        return cityService.updateCity(id, city);
    }

    // Удаление города
    @Operation(summary = "Удаление города", description = "Удаляет город по указанному идентификатору")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCity(@PathVariable String id) {
        try {
            cityService.deleteCity(id);
            return ResponseEntity.ok("Город успешно удален.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}