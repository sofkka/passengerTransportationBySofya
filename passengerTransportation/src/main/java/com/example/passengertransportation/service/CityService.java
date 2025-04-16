package com.example.passengertransportation.service;

import com.example.passengertransportation.model.City;
import com.example.passengertransportation.repository.CityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CityService {

    @Autowired
    private CityRepository cityRepository;

    // Получение всех городов
    public List<City> getAllCities() {
        return cityRepository.findAll();
    }

    // Получение города по ID
    public City getCityById(String idCity) {
        return cityRepository.findById(idCity).orElse(null);
    }

    // Создание нового города
    public City createCity(City city) {
        return cityRepository.save(city);
    }

    // Обновление города
    public City updateCity(String idCity, City city) {
        City existingCity = cityRepository.findById(idCity).orElse(null);
        if (existingCity != null) {
            existingCity.setCityName(city.getCityName());
            return cityRepository.save(existingCity);
        }
        return null;
    }

    // Удаление города
    public void deleteCity(String idCity) {
        cityRepository.deleteById(idCity);
    }
}