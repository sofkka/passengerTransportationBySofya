package com.example.passengertransportation.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

// Конфигурация Swagger/OpenAPI для автоматической генерации документации API системы бронирования билетов

@OpenAPIDefinition(
        info = @Info(
                title = "Passenger Transportation Booking System API",
                description = "API для системы бронирования билетов на пассажирские перевозки. Этот API позволяет управлять бронированием билетов, маршрутами, расписаниями и данными пассажиров.",
                version = "1.0.0"
        )
)

public class OpenApiConfig {

}
