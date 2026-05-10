package com.siemens.trainApp.booking_service.Controller;

import com.siemens.trainApp.booking_service.DTO.BookingRequest;
import com.siemens.trainApp.booking_service.Model.Booking;
import com.siemens.trainApp.booking_service.Service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/bookings")
public class BookingController {
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }
    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<?> bookTicket(@RequestBody BookingRequest request) {
        try {
            Booking booking = bookingService.processBooking(
                    request.getCustomerEmail(),
                    request.getScheduleId(),
                    request.getQuantity()
            );
            return ResponseEntity.ok(booking);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/train/{scheduleId}")
    public ResponseEntity<List<Booking>> getBookingsForTrain(@PathVariable String scheduleId) {
        List<Booking> bookings = bookingService.getBookingsByScheduleId(scheduleId);
        return ResponseEntity.ok(bookings);
    }
}