package com.vahana.dto;

import jakarta.validation.constraints.NotBlank;

public class VehicleRequest {

    @NotBlank(message = "registrationNumber is required")
    private String registrationNumber;

    @NotBlank(message = "driverName is required")
    private String driverName;

    @NotBlank(message = "vehicleType is required")
    private String vehicleType;

    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }

    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }
}
