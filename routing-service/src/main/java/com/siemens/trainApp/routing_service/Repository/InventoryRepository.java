package com.siemens.trainApp.routing_service.Repository;

import com.siemens.trainApp.routing_service.Model.TrainInventory;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface InventoryRepository extends JpaRepository<TrainInventory, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM TrainInventory i WHERE i.scheduleId = :scheduleId")
    TrainInventory findByScheduleIdForUpdate(String scheduleId);
}
