package com.example.passengertransportation;

import com.example.passengertransportation.controller.CityController;
import com.example.passengertransportation.model.City;
import com.example.passengertransportation.service.CityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CityController Tests")
public class CityControllerTest {

    @Mock
    private CityService cityService;

    @InjectMocks
    private CityController cityController;

    private City city;

    @BeforeEach
    void setUp() {
        city = new City();
        city.setIdCity("1");
        city.setCityName("Москва");
    }

    @Nested
    @DisplayName("Tests for getAllCities")
    class GetAllCitiesTests {

        @Test
        @DisplayName("Should return list of cities when cities exist")
        void testGetAllCities_Success() {
            List<City> expectedCities = Arrays.asList(city);
            when(cityService.getAllCities()).thenReturn(expectedCities);

            List<City> actualCities = cityController.getAllCities();

            assertEquals(1, actualCities.size(), "Size of cities list should be 1");
            City actualCity = actualCities.get(0);
            assertEquals(city.getIdCity(), actualCity.getIdCity(), "City ID should match");
            assertEquals(city.getCityName(), actualCity.getCityName(), "City name should match");
            verify(cityService, times(1)).getAllCities();
        }

        @Test
        @DisplayName("Should return empty list when no cities exist")
        void testGetAllCities_Empty() {
            when(cityService.getAllCities()).thenReturn(Collections.emptyList());

            List<City> actualCities = cityController.getAllCities();

            assertTrue(actualCities.isEmpty(), "Cities list should be empty");
            verify(cityService, times(1)).getAllCities();
        }
    }

    @Nested
    @DisplayName("Tests for getCityById")
    class GetCityByIdTests {

        @Test
        @DisplayName("Should return city when ID exists")
        void testGetCityById_Success() {
            String cityId = "1";
            when(cityService.getCityById(cityId)).thenReturn(city);

            City actualCity = cityController.getCityById(cityId);

            assertNotNull(actualCity, "City should not be null");
            assertEquals(city.getIdCity(), actualCity.getIdCity(), "City ID should match");
            assertEquals(city.getCityName(), actualCity.getCityName(), "City name should match");
            verify(cityService, times(1)).getCityById(cityId);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when city ID does not exist")
        void testGetCityById_NotFound() {
            String cityId = "999";
            when(cityService.getCityById(cityId)).thenThrow(new IllegalArgumentException("Город не найден"));

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> cityController.getCityById(cityId),
                    "Expected IllegalArgumentException for non-existent city ID");
            assertEquals("Город не найден", exception.getMessage());
            verify(cityService, times(1)).getCityById(cityId);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("Should throw IllegalArgumentException when city ID is null or empty")
        void testGetCityById_NullOrEmptyId(String cityId) {
            when(cityService.getCityById(cityId)).thenThrow(new IllegalArgumentException("ID города не может быть пустым или null"));

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> cityController.getCityById(cityId),
                    "Expected IllegalArgumentException for null or empty city ID");
            assertEquals("ID города не может быть пустым или null", exception.getMessage());
            verify(cityService, times(1)).getCityById(cityId);
        }
    }

    @Nested
    @DisplayName("Tests for createCity")
    class CreateCityTests {

        @Test
        @DisplayName("Should create city successfully")
        void testCreateCity_Success() {
            City newCity = new City();
            newCity.setIdCity("2");
            newCity.setCityName("Санкт-Петербург");
            when(cityService.createCity(any(City.class))).thenReturn(newCity);

            City actualCity = cityController.createCity(newCity);

            assertNotNull(actualCity, "Created city should not be null");
            assertEquals("2", actualCity.getIdCity(), "City ID should match");
            assertEquals("Санкт-Петербург", actualCity.getCityName(), "City name should match");
            verify(cityService, times(1)).createCity(newCity);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when city is null")
        void testCreateCity_NullCity() {
            when(cityService.createCity(null)).thenThrow(new IllegalArgumentException("Город не может быть null"));

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> cityController.createCity(null),
                    "Expected IllegalArgumentException for null city");
            assertEquals("Город не может быть null", exception.getMessage());
            verify(cityService, times(1)).createCity(null);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when city name is empty")
        void testCreateCity_EmptyCityName() {
            City invalidCity = new City();
            invalidCity.setIdCity("3");
            invalidCity.setCityName("");
            when(cityService.createCity(any(City.class))).thenThrow(new IllegalArgumentException("Название города не может быть пустым"));

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> cityController.createCity(invalidCity),
                    "Expected IllegalArgumentException for empty city name");
            assertEquals("Название города не может быть пустым", exception.getMessage());
            verify(cityService, times(1)).createCity(invalidCity);
        }
    }

    @Nested
    @DisplayName("Tests for updateCity")
    class UpdateCityTests {

        @Test
        @DisplayName("Should update city successfully")
        void testUpdateCity_Success() {
            String cityId = "1";
            City updatedCity = new City();
            updatedCity.setIdCity("1");
            updatedCity.setCityName("Москва (обновлено)");
            when(cityService.updateCity(eq(cityId), any(City.class))).thenReturn(updatedCity);

            City actualCity = cityController.updateCity(cityId, updatedCity);

            assertNotNull(actualCity, "Updated city should not be null");
            assertEquals("1", actualCity.getIdCity(), "City ID should match");
            assertEquals("Москва (обновлено)", actualCity.getCityName(), "City name should match");
            verify(cityService, times(1)).updateCity(cityId, updatedCity);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when city ID does not exist")
        void testUpdateCity_NotFound() {
            String cityId = "999";
            City updatedCity = new City();
            updatedCity.setIdCity("999");
            updatedCity.setCityName("Неизвестный город");
            when(cityService.updateCity(eq(cityId), any(City.class)))
                    .thenThrow(new IllegalArgumentException("Город не найден"));

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> cityController.updateCity(cityId, updatedCity),
                    "Expected IllegalArgumentException for non-existent city ID");
            assertEquals("Город не найден", exception.getMessage());
            verify(cityService, times(1)).updateCity(cityId, updatedCity);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("Should throw IllegalArgumentException when city ID is null or empty")
        void testUpdateCity_NullOrEmptyId(String cityId) {
            City updatedCity = new City();
            updatedCity.setCityName("Москва");
            when(cityService.updateCity(eq(cityId), any(City.class)))
                    .thenThrow(new IllegalArgumentException("ID города не может быть пустым или null"));

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> cityController.updateCity(cityId, updatedCity),
                    "Expected IllegalArgumentException for null or empty city ID");
            assertEquals("ID города не может быть пустым или null", exception.getMessage());
            verify(cityService, times(1)).updateCity(cityId, updatedCity);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when city name is empty")
        void testUpdateCity_EmptyCityName() {
            String cityId = "1";
            City invalidCity = new City();
            invalidCity.setIdCity("1");
            invalidCity.setCityName("");
            when(cityService.updateCity(eq(cityId), any(City.class)))
                    .thenThrow(new IllegalArgumentException("Название города не может быть пустым"));

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> cityController.updateCity(cityId, invalidCity),
                    "Expected IllegalArgumentException for empty city name");
            assertEquals("Название города не может быть пустым", exception.getMessage());
            verify(cityService, times(1)).updateCity(cityId, invalidCity);
        }
    }

    @Nested
    @DisplayName("Tests for deleteCity")
    class DeleteCityTests {

        @Test
        @DisplayName("Should delete city successfully")
        void testDeleteCity_Success() {
            String cityId = "1";
            doNothing().when(cityService).deleteCity(cityId);

            ResponseEntity<String> response = cityController.deleteCity(cityId);

            assertEquals(HttpStatus.OK, response.getStatusCode(), "Status should be OK");
            assertEquals("Город успешно удален.", response.getBody(), "Response body should match");
            verify(cityService, times(1)).deleteCity(cityId);
        }

        @Test
        @DisplayName("Should return NOT_FOUND when city ID does not exist")
        void testDeleteCity_NotFound() {
            String cityId = "999";
            doThrow(new IllegalArgumentException("Город не найден")).when(cityService).deleteCity(cityId);

            ResponseEntity<String> response = cityController.deleteCity(cityId);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "Status should be NOT_FOUND");
            assertEquals("Город не найден", response.getBody(), "Response body should match");
            verify(cityService, times(1)).deleteCity(cityId);
        }

        @Test
        @DisplayName("Should return BAD_REQUEST when city cannot be deleted")
        void testDeleteCity_CannotDelete() {
            String cityId = "1";
            doThrow(new IllegalStateException("Город не может быть удален")).when(cityService).deleteCity(cityId);

            ResponseEntity<String> response = cityController.deleteCity(cityId);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Status should be BAD_REQUEST");
            assertEquals("Город не может быть удален", response.getBody(), "Response body should match");
            verify(cityService, times(1)).deleteCity(cityId);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("Should throw IllegalArgumentException when city ID is null or empty")
        void testDeleteCity_NullOrEmptyId(String cityId) {
            doThrow(new IllegalArgumentException("ID города не может быть пустым или null")).when(cityService).deleteCity(cityId);

            ResponseEntity<String> response = cityController.deleteCity(cityId);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "Status should be NOT_FOUND");
            assertEquals("ID города не может быть пустым или null", response.getBody(), "Response body should match");
            verify(cityService, times(1)).deleteCity(cityId);
        }
    }
}
