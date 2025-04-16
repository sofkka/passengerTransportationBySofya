package com.example.passengertransportation.service;

import com.example.passengertransportation.model.TransportType;
import com.example.passengertransportation.repository.TransportTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransportTypeService {

    @Autowired
    private TransportTypeRepository transportTypeRepository;

    // Получение всех типов транспорта
    public List<TransportType> getAllTransportTypes() {
        return transportTypeRepository.findAll();
    }

    // Получение типа транспорта по ID
    public TransportType getTransportTypeById(String idTransport) {
        return transportTypeRepository.findById(idTransport).orElse(null);
    }

    // Создание нового типа транспорта
    public TransportType createTransportType(TransportType transportType) {
        return transportTypeRepository.save(transportType);
    }

    // Обновление типа транспорта
    public TransportType updateTransportType(String idTransport, TransportType transportType) {
        TransportType existingTransportType = transportTypeRepository.findById(idTransport).orElse(null);
        if (existingTransportType != null) {
            existingTransportType.setTransportName(transportType.getTransportName());
            return transportTypeRepository.save(existingTransportType);
        }
        return null;
    }

    // Удаление типа транспорта
    public void deleteTransportType(String idTransport) {
        transportTypeRepository.deleteById(idTransport);
    }
}