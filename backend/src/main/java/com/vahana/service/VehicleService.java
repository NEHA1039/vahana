package com.vahana.service;

import com.vahana.dto.LocationPingRequest;
import com.vahana.dto.VehicleRequest;
import com.vahana.dto.VehicleWithLocationResponse;
import com.vahana.entity.LocationPing;
import com.vahana.entity.Vehicle;
import com.vahana.entity.VehicleStatus;
import com.vahana.exception.InvalidLocationException;
import com.vahana.exception.VehicleNotFoundException;
import com.vahana.repository.LocationPingRepository;
import com.vahana.repository.VehicleRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final LocationPingRepository locationPingRepository;

    public VehicleService(VehicleRepository vehicleRepository, LocationPingRepository locationPingRepository) {
        this.vehicleRepository = vehicleRepository;
        this.locationPingRepository = locationPingRepository;
    }

    public Vehicle registerVehicle(VehicleRequest req) {
        if (vehicleRepository.existsByRegistrationNumber(req.getRegistrationNumber())) {
            throw new InvalidLocationException("A vehicle with this registration number already exists");
        }
        Vehicle vehicle = new Vehicle(req.getRegistrationNumber(), req.getDriverName(), req.getVehicleType());
        return vehicleRepository.save(vehicle);
    }

    public List<VehicleWithLocationResponse> listVehiclesWithLatestLocation() {
        return vehicleRepository.findAll().stream()
                .map(v -> {
                    Optional<LocationPing> latest =
                            locationPingRepository.findFirstByVehicleIdOrderByTimestampDesc(v.getId());
                    return latest
                            .map(p -> VehicleWithLocationResponse.from(v, p.getLatitude(), p.getLongitude(), p.getTimestamp()))
                            .orElseGet(() -> VehicleWithLocationResponse.from(v, null, null, null));
                })
                .toList();
    }

    public LocationPing recordLocation(Long vehicleId, LocationPingRequest req) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new VehicleNotFoundException(vehicleId));

        if (req.getTimestamp().isAfter(Instant.now())) {
            throw new InvalidLocationException("timestamp cannot be in the future");
        }

        LocationPing ping = new LocationPing(vehicleId, req.getLatitude(), req.getLongitude(),
                req.getTimestamp(), req.getSpeed());
        LocationPing saved = locationPingRepository.save(ping);

        if (vehicle.getStatus() != VehicleStatus.ACTIVE) {
            vehicle.setStatus(VehicleStatus.ACTIVE);
            vehicleRepository.save(vehicle);
        }

        return saved;
    }

    public List<LocationPing> getHistory(Long vehicleId, int limit) {
        if (!vehicleRepository.existsById(vehicleId)) {
            throw new VehicleNotFoundException(vehicleId);
        }
        return locationPingRepository.findByVehicleIdOrderByTimestampDesc(vehicleId, PageRequest.of(0, limit));
    }
}
