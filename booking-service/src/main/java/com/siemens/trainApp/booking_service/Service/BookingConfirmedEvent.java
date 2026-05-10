package com.siemens.trainApp.booking_service.Service;

import lombok.AllArgsConstructor;
import lombok.Getter;
@Getter
@AllArgsConstructor
public class BookingConfirmedEvent {

        private final String customerEmail;
        private final String scheduleId;
}
