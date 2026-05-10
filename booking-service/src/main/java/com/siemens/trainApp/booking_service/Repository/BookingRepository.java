package com.siemens.trainApp.booking_service.Repository;

import com.siemens.trainApp.booking_service.Model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {
    List<Booking> findByScheduleId(String scheduleId);
}
