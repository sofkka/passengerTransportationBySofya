package com.example.passengertransportation.model;

import jakarta.persistence.*;

@Entity
@Table(name = "transport_types")
public class TransportType {

    @Id
    @Column(name = "id_transport")
    private String idTransport;

    @Column(name = "transport_name")
    private String transportName;

    public TransportType() {
    }

    public TransportType(String idTransport, String transportName) {
        this.idTransport = idTransport;
        this.transportName = transportName;
    }

    // Геттеры и сеттеры
    public String getIdTransport() {
        return idTransport;
    }

    public void setIdTransport(String idTransport) {
        this.idTransport = idTransport;
    }

    public String getTransportName() {
        return transportName;
    }

    public void setTransportName(String transportName) {
        this.transportName = transportName;
    }
}