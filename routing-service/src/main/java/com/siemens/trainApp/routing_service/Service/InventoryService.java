package com.siemens.trainApp.routing_service.Service;

import com.siemens.trainApp.routing_service.Model.TrainInventory;
import com.siemens.trainApp.routing_service.Repository.InventoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Transactional
    public void reserveSeats(String scheduleId, int quantity) {
        TrainInventory inventory = inventoryRepository.findByScheduleIdForUpdate(scheduleId);

        if (inventory == null) {
            throw new RuntimeException("Schedule not found in inventory!");
        }

        if (inventory.getAvailableSeats() < quantity) {
            throw new RuntimeException("Not enough seats available! (Overbooking prevented)");
        }

        inventory.setAvailableSeats(inventory.getAvailableSeats() - quantity);

        inventoryRepository.save(inventory);
    }
}
