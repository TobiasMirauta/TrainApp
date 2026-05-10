package com.siemens.trainApp.routing_service.Client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;
import java.util.Map;

@FeignClient(name = "booking-service", url = "${booking.service.url}")
public interface BookingClient {
    @GetMapping("/api/v1/bookings/train/{scheduleId}")
    List<Map<String, Object>> getBookingsForTrain(@PathVariable String scheduleId);
}