package com.example.passengertransportation.controller;

import com.example.passengertransportation.model.TransportType;
import com.example.passengertransportation.service.TransportTypeService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transport-types")
public class TransportTypeController {

    @Autowired
    private TransportTypeService transportTypeService;

    // Получение всех типов транспорта
    @Operation(summary = "Получение всех типов транспорта", description = "Возвращает список всех типов транспорта")
    @GetMapping("")
    public List<TransportType> getAllTransportTypes() {
        return transportTypeService.getAllTransportTypes();
    }

    // Получение типа транспорта по ID
    @Operation(summary = "Получение типа транспорта по ID", description = "Возвращает тип транспорта по указанному идентификатору")
    @GetMapping("/{id}")
    public TransportType getTransportTypeById(@PathVariable String id) {
        return transportTypeService.getTransportTypeById(id);
    }

    // Создание нового типа транспорта
    @Operation(summary = "Добавление типа транспорта", description = "Позволяет добавить новый тип транспорта")
    @PostMapping("")
    public TransportType createTransportType(@RequestBody TransportType transportType) {
        return transportTypeService.createTransportType(transportType);
    }

    // Обновление типа транспорта
    @Operation(summary = "Обновление типа транспорта", description = "Обновляет информацию о типе транспорта по указанному идентификатору")
    @PutMapping("/{id}")
    public TransportType updateTransportType(@PathVariable String id, @RequestBody TransportType transportType) {
        return transportTypeService.updateTransportType(id, transportType);
    }

    // Удаление типа транспорта
    @Operation(summary = "Удаление типа транспорта", description = "Удаляет тип транспорта по указанному идентификатору")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTransportType(@PathVariable String id) {
        try {
            transportTypeService.deleteTransportType(id);
            return ResponseEntity.ok("Тип транспорта успешно удален.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}