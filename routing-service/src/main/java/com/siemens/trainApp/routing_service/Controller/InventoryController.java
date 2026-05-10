package com.siemens.trainApp.routing_service.Controller;

import com.siemens.trainApp.routing_service.Controller.DTO.ReserveRequest;
import com.siemens.trainApp.routing_service.Service.InventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/inventory")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }


    @PostMapping("/reserve")
    public ResponseEntity<String> reserve(@RequestBody ReserveRequest request) {
        inventoryService.reserveSeats(request.getScheduleId(), request.getQuantity());
        return ResponseEntity.ok("Seats reserved successfully");
    }
}
