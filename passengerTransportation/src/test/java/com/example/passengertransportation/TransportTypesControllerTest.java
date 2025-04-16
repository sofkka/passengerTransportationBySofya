package com.example.passengertransportation;

import com.example.passengertransportation.controller.TransportTypeController;
import com.example.passengertransportation.model.TransportType;
import com.example.passengertransportation.service.TransportTypeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
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
@DisplayName("Тесты для TransportTypeController")
public class TransportTypesControllerTest {

    @Mock
    private TransportTypeService transportTypeService;

    @InjectMocks
    private TransportTypeController transportTypeController;

    private TransportType transportType;

    @BeforeEach
    void setUp() {
        transportType = new TransportType();
        transportType.setIdTransport("1");
        transportType.setTransportName("Автобус");
    }

    @Nested
    @DisplayName("Тесты для метода получения всех типов транспорта")
    class GetAllTransportTypesTests {

        @Test
        @DisplayName("Должен вернуть список типов транспорта, если типы существуют")
        void testGetAllTransportTypes_Success() {
            List<TransportType> expectedTypes = Arrays.asList(transportType);
            when(transportTypeService.getAllTransportTypes()).thenReturn(expectedTypes);

            List<TransportType> actualTypes = transportTypeController.getAllTransportTypes();

            assertEquals(1, actualTypes.size(), "Размер списка типов транспорта должен быть 1");
            TransportType actualType = actualTypes.get(0);
            assertEquals(transportType.getIdTransport(), actualType.getIdTransport(), "ID типа транспорта должен совпадать");
            assertEquals(transportType.getTransportName(), actualType.getTransportName(), "Название типа транспорта должно совпадать");
            verify(transportTypeService, times(1)).getAllTransportTypes();
        }

        @Test
        @DisplayName("Должен вернуть пустой список, если типов транспорта нет")
        void testGetAllTransportTypes_Empty() {
            when(transportTypeService.getAllTransportTypes()).thenReturn(Collections.emptyList());

            List<TransportType> actualTypes = transportTypeController.getAllTransportTypes();

            assertTrue(actualTypes.isEmpty(), "Список типов транспорта должен быть пустым");
            verify(transportTypeService, times(1)).getAllTransportTypes();
        }
    }

    @Nested
    @DisplayName("Тесты для метода получения типа транспорта по ID")
    class GetTransportTypeByIdTests {

        @Test
        @DisplayName("Должен вернуть тип транспорта, если ID существует")
        void testGetTransportTypeById_Success() {
            String typeId = "1";
            when(transportTypeService.getTransportTypeById(typeId)).thenReturn(transportType);

            TransportType actualType = transportTypeController.getTransportTypeById(typeId);

            assertNotNull(actualType, "Тип транспорта не должен быть null");
            assertEquals(transportType.getIdTransport(), actualType.getIdTransport(), "ID типа транспорта должен совпадать");
            assertEquals(transportType.getTransportName(), actualType.getTransportName(), "Название типа транспорта должно совпадать");
            verify(transportTypeService, times(1)).getTransportTypeById(typeId);
        }

        @Test
        @DisplayName("Должен выбросить IllegalArgumentException, если тип транспорта с ID не существует")
        void testGetTransportTypeById_NotFound() {
            String typeId = "999";
            when(transportTypeService.getTransportTypeById(typeId))
                    .thenThrow(new IllegalArgumentException("Тип транспорта не найден"));

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> transportTypeController.getTransportTypeById(typeId),
                    "Ожидается IllegalArgumentException для несуществующего ID типа транспорта");
            assertEquals("Тип транспорта не найден", exception.getMessage());
            verify(transportTypeService, times(1)).getTransportTypeById(typeId);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("Должен выбросить IllegalArgumentException, если ID типа транспорта null или пустой")
        void testGetTransportTypeById_NullOrEmptyId(String typeId) {
            when(transportTypeService.getTransportTypeById(typeId))
                    .thenThrow(new IllegalArgumentException("ID типа транспорта не может быть пустым или null"));

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> transportTypeController.getTransportTypeById(typeId),
                    "Ожидается IllegalArgumentException для null или пустого ID типа транспорта");
            assertEquals("ID типа транспорта не может быть пустым или null", exception.getMessage());
            verify(transportTypeService, times(1)).getTransportTypeById(typeId);
        }
    }

    @Nested
    @DisplayName("Тесты для метода создания типа транспорта")
    class CreateTransportTypeTests {

        @Test
        @DisplayName("Должен успешно создать тип транспорта")
        void testCreateTransportType_Success() {
            TransportType newType = new TransportType();
            newType.setIdTransport("2");
            newType.setTransportName("Поезд");
            when(transportTypeService.createTransportType(any(TransportType.class))).thenReturn(newType);

            TransportType actualType = transportTypeController.createTransportType(newType);

            assertNotNull(actualType, "Созданный тип транспорта не должен быть null");
            assertEquals("2", actualType.getIdTransport(), "ID типа транспорта должен совпадать");
            assertEquals("Поезд", actualType.getTransportName(), "Название типа транспорта должно совпадать");
            verify(transportTypeService, times(1)).createTransportType(newType);
        }

        @Test
        @DisplayName("Должен выбросить IllegalArgumentException, если тип транспорта null")
        void testCreateTransportType_NullType() {
            when(transportTypeService.createTransportType(null))
                    .thenThrow(new IllegalArgumentException("Тип транспорта не может быть null"));

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> transportTypeController.createTransportType(null),
                    "Ожидается IllegalArgumentException для null типа транспорта");
            assertEquals("Тип транспорта не может быть null", exception.getMessage());
            verify(transportTypeService, times(1)).createTransportType(null);
        }

        @Test
        @DisplayName("Должен выбросить IllegalArgumentException, если название типа пустое")
        void testCreateTransportType_EmptyTypeName() {
            TransportType invalidType = new TransportType();
            invalidType.setIdTransport("3");
            invalidType.setTransportName("");
            when(transportTypeService.createTransportType(any(TransportType.class)))
                    .thenThrow(new IllegalArgumentException("Название типа транспорта не может быть пустым"));

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> transportTypeController.createTransportType(invalidType),
                    "Ожидается IllegalArgumentException для пустого названия типа");
            assertEquals("Название типа транспорта не может быть пустым", exception.getMessage());
            verify(transportTypeService, times(1)).createTransportType(invalidType);
        }
    }

    @Nested
    @DisplayName("Тесты для метода обновления типа транспорта")
    class UpdateTransportTypeTests {

        @Test
        @DisplayName("Должен успешно обновить тип транспорта")
        void testUpdateTransportType_Success() {
            String typeId = "1";
            TransportType updatedType = new TransportType();
            updatedType.setIdTransport("1");
            updatedType.setTransportName("Автобус (обновлено)");
            when(transportTypeService.updateTransportType(eq(typeId), any(TransportType.class))).thenReturn(updatedType);

            TransportType actualType = transportTypeController.updateTransportType(typeId, updatedType);

            assertNotNull(actualType, "Обновленный тип транспорта не должен быть null");
            assertEquals("1", actualType.getIdTransport(), "ID типа транспорта должен совпадать");
            assertEquals("Автобус (обновлено)", actualType.getTransportName(), "Название типа транспорта должно совпадать");
            verify(transportTypeService, times(1)).updateTransportType(typeId, updatedType);
        }

        @Test
        @DisplayName("Должен выбросить IllegalArgumentException, если тип транспорта с ID не существует")
        void testUpdateTransportType_NotFound() {
            String typeId = "999";
            TransportType updatedType = new TransportType();
            updatedType.setIdTransport("999");
            updatedType.setTransportName("Неизвестный тип");
            when(transportTypeService.updateTransportType(eq(typeId), any(TransportType.class)))
                    .thenThrow(new IllegalArgumentException("Тип транспорта не найден"));

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> transportTypeController.updateTransportType(typeId, updatedType),
                    "Ожидается IllegalArgumentException для несуществующего ID типа транспорта");
            assertEquals("Тип транспорта не найден", exception.getMessage());
            verify(transportTypeService, times(1)).updateTransportType(typeId, updatedType);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("Должен выбросить IllegalArgumentException, если ID типа транспорта null или пустой")
        void testUpdateTransportType_NullOrEmptyId(String typeId) {
            TransportType updatedType = new TransportType();
            updatedType.setTransportName("Автобус");
            when(transportTypeService.updateTransportType(eq(typeId), any(TransportType.class)))
                    .thenThrow(new IllegalArgumentException("ID типа транспорта не может быть пустым или null"));

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> transportTypeController.updateTransportType(typeId, updatedType),
                    "Ожидается IllegalArgumentException для null или пустого ID типа транспорта");
            assertEquals("ID типа транспорта не может быть пустым или null", exception.getMessage());
            verify(transportTypeService, times(1)).updateTransportType(typeId, updatedType);
        }

        @Test
        @DisplayName("Должен выбросить IllegalArgumentException, если название типа пустое")
        void testUpdateTransportType_EmptyTypeName() {
            String typeId = "1";
            TransportType invalidType = new TransportType();
            invalidType.setIdTransport("1");
            invalidType.setTransportName("");
            when(transportTypeService.updateTransportType(eq(typeId), any(TransportType.class)))
                    .thenThrow(new IllegalArgumentException("Название типа транспорта не может быть пустым"));

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> transportTypeController.updateTransportType(typeId, invalidType),
                    "Ожидается IllegalArgumentException для пустого названия типа");
            assertEquals("Название типа транспорта не может быть пустым", exception.getMessage());
            verify(transportTypeService, times(1)).updateTransportType(typeId, invalidType);
        }
    }
    @Nested
    @DisplayName("Тесты для метода удаления типа транспорта")
    class DeleteTransportTypeTests {

        @Test
        @DisplayName("Должен успешно удалить тип транспорта")
        void testDeleteTransportType_Success() {
            String typeId = "1";
            doNothing().when(transportTypeService).deleteTransportType(typeId);

            ResponseEntity<String> response = transportTypeController.deleteTransportType(typeId);

            assertEquals(HttpStatus.OK, response.getStatusCode(), "Статус должен быть OK");
            assertEquals("Тип транспорта успешно удален.", response.getBody(), "Тело ответа должно совпадать");
            verify(transportTypeService, times(1)).deleteTransportType(typeId);
        }

        @Test
        @DisplayName("Должен вернуть NOT_FOUND, если тип транспорта с ID не существует")
        void testDeleteTransportType_NotFound() {
            String typeId = "999";
            doThrow(new IllegalArgumentException("Тип транспорта не найден"))
                    .when(transportTypeService).deleteTransportType(typeId);

            ResponseEntity<String> response = transportTypeController.deleteTransportType(typeId);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "Статус должен быть NOT_FOUND");
            assertEquals("Тип транспорта не найден", response.getBody(), "Тело ответа должно совпадать");
            verify(transportTypeService, times(1)).deleteTransportType(typeId);
        }

        @Test
        @DisplayName("Должен вернуть BAD_REQUEST, если тип транспорта не может быть удален")
        void testDeleteTransportType_CannotDelete() {
            String typeId = "1";
            doThrow(new IllegalStateException("Тип транспорта не может быть удален"))
                    .when(transportTypeService).deleteTransportType(typeId);

            ResponseEntity<String> response = transportTypeController.deleteTransportType(typeId);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Статус должен быть BAD_REQUEST");
            assertEquals("Тип транспорта не может быть удален", response.getBody(), "Тело ответа должно совпадать");
            verify(transportTypeService, times(1)).deleteTransportType(typeId);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("Должен вернуть NOT_FOUND, если ID типа транспорта null или пустой")
        void testDeleteTransportType_NullOrEmptyId(String typeId) {
            doThrow(new IllegalArgumentException("ID типа транспорта не может быть пустым или null"))
                    .when(transportTypeService).deleteTransportType(typeId);

            ResponseEntity<String> response = transportTypeController.deleteTransportType(typeId);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "Статус должен быть NOT_FOUND");
            assertEquals("ID типа транспорта не может быть пустым или null", response.getBody(), "Тело ответа должно совпадать");
            verify(transportTypeService, times(1)).deleteTransportType(typeId);
        }
    }
}