package com.siemens.trainApp.booking_service.DTO;

import lombok.Data;

@Data
public class BookingRequest {
    private String customerEmail;
    private String scheduleId;
    private int quantity;
}