package com.vahana.dto;

import com.vahana.entity.Vehicle;
import com.vahana.entity.VehicleStatus;

import java.time.Instant;

public class VehicleWithLocationResponse {

    private Long id;
    private String registrationNumber;
    private String driverName;
    private String vehicleType;
    private VehicleStatus status;
    private Double latitude;
    private Double longitude;
    private Instant lastUpdated;

    public static VehicleWithLocationResponse from(Vehicle v, Double lat, Double lon, Instant lastUpdated) {
        VehicleWithLocationResponse r = new VehicleWithLocationResponse();
        r.id = v.getId();
        r.registrationNumber = v.getRegistrationNumber();
        r.driverName = v.getDriverName();
        r.vehicleType = v.getVehicleType();
        r.status = v.getStatus();
        r.latitude = lat;
        r.longitude = lon;
        r.lastUpdated = lastUpdated;
        return r;
    }

    public Long getId() { return id; }
    public String getRegistrationNumber() { return registrationNumber; }
    public String getDriverName() { return driverName; }
    public String getVehicleType() { return vehicleType; }
    public VehicleStatus getStatus() { return status; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
    public Instant getLastUpdated() { return lastUpdated; }
}
