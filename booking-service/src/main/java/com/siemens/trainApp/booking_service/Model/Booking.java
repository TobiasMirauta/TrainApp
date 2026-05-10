package com.siemens.trainApp.booking_service.Model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String customerEmail;

    private String scheduleId;

    private int quantity;

    private LocalDateTime bookingTime;

    private String status;
}