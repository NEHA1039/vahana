package com.vahana.controller;

import com.vahana.dto.LocationPingRequest;
import com.vahana.dto.VehicleRequest;
import com.vahana.dto.VehicleWithLocationResponse;
import com.vahana.entity.LocationPing;
import com.vahana.entity.Vehicle;
import com.vahana.service.VehicleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@CrossOrigin(origins = "*")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @PostMapping
    public ResponseEntity<Vehicle> registerVehicle(@Valid @RequestBody VehicleRequest request) {
        Vehicle saved = vehicleService.registerVehicle(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public ResponseEntity<List<VehicleWithLocationResponse>> listVehicles() {
        return ResponseEntity.ok(vehicleService.listVehiclesWithLatestLocation());
    }

    @PostMapping("/{id}/location")
    public ResponseEntity<LocationPing> ingestLocation(@PathVariable Long id,
                                                         @Valid @RequestBody LocationPingRequest request) {
        LocationPing saved = vehicleService.recordLocation(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<LocationPing>> getHistory(@PathVariable Long id,
                                                           @RequestParam(defaultValue = "50") int limit) {
        return ResponseEntity.ok(vehicleService.getHistory(id, limit));
    }
}
