package com.siemens.trainApp.booking_service.Client;

import com.siemens.trainApp.booking_service.DTO.ReserveRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


    @FeignClient(name = "inventory-service", url = "http://localhost:8080")
    public interface InventoryClient {

        @PostMapping("/api/v1/inventory/reserve")
        void reserveSeats(@RequestBody ReserveRequest request);
    }

