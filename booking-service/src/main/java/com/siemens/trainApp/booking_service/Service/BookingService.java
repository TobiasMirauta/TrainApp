package com.siemens.trainApp.booking_service.Service;
import com.siemens.trainApp.booking_service.Client.InventoryClient;
import com.siemens.trainApp.booking_service.DTO.ReserveRequest;
import com.siemens.trainApp.booking_service.Model.Booking;
import com.siemens.trainApp.booking_service.Repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final InventoryClient inventoryClient;
    private final RabbitTemplate rabbitTemplate; // <-- Inlocuim EventPublisher cu RabbitTemplate

    @Value("${rabbitmq.queue.email.name}")
    private String emailQueueName; // Tragem numele cozii din properties

    @Transactional
    public Booking processBooking(String customerEmail, String scheduleId, int quantity) {

        // 1. Apelam Inventory Service (Sincron)
        ReserveRequest reserveRequest = new ReserveRequest();
        reserveRequest.setScheduleId(scheduleId);
        reserveRequest.setQuantity(quantity);
        inventoryClient.reserveSeats(reserveRequest);

        // 2. Salvam rezervarea in baza de date
        Booking booking = new Booking();
        booking.setCustomerEmail(customerEmail);
        booking.setScheduleId(scheduleId);
        booking.setQuantity(quantity);
        booking.setBookingTime(LocalDateTime.now());
        booking.setStatus("CONFIRMED");
        Booking savedBooking = bookingRepository.save(booking);

        // 3. Trimitem mesajul in RabbitMQ (Asincron).
        // Crezi un mesaj simplu cu datele esentiale despartite prin virgula
        // (sau poti folosi un obiect JSON, dar un String simplu e mai usor de configurat initial)
        String message = customerEmail + "," + scheduleId;
        rabbitTemplate.convertAndSend(emailQueueName, message);

        return savedBooking;
    }

    // Adauga aceasta metoda in clasa BookingService
    public List<Booking> getBookingsByScheduleId(String scheduleId) {
        return bookingRepository.findByScheduleId(scheduleId);
    }
}