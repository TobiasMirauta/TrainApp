package com.siemens.trainApp.booking_service.DTO;

import lombok.Data;
@Data
public class ReserveRequest {
    private String scheduleId;
    private int quantity;

    public String getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(String scheduleId) {
        this.scheduleId = scheduleId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
