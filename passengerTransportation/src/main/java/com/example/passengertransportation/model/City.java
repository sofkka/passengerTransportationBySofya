package com.example.passengertransportation.model;

import jakarta.persistence.*;

@Entity
@Table(name = "cities")
public class City {

    @Id
    @Column(name = "id_city")
    private String idCity;

    @Column(name = "city_name")
    private String cityName;

    public City() {
    }

    public City(String idCity, String cityName) {
        this.idCity = idCity;
        this.cityName = cityName;
    }

    // Геттеры и сеттеры
    public String getIdCity() {
        return idCity;
    }

    public void setIdCity(String idCity) {
        this.idCity = idCity;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
}