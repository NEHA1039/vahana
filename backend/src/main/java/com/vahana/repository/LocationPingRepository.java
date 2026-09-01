package com.vahana.repository;

import com.vahana.entity.LocationPing;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LocationPingRepository extends JpaRepository<LocationPing, Long> {

    List<LocationPing> findByVehicleIdOrderByTimestampDesc(Long vehicleId, Pageable pageable);

    Optional<LocationPing> findFirstByVehicleIdOrderByTimestampDesc(Long vehicleId);
}
