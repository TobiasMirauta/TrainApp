package com.siemens.trainApp.routing_service.Controller.DTO;

import java.time.LocalDateTime;
import java.util.List;

public class RouteResponse {
    private List<SegmentDTO> segments;
    private long totalDurationMinutes;

    public List<SegmentDTO> getSegments() { return segments; }
    public void setSegments(List<SegmentDTO> segments) { this.segments = segments; }

    public long getTotalDurationMinutes() { return totalDurationMinutes; }
    public void setTotalDurationMinutes(long totalDurationMinutes) { this.totalDurationMinutes = totalDurationMinutes; }

    public static class SegmentDTO {
        private String trainId;
        private String fromStation;
        private String toStation;
        private LocalDateTime departure;
        private LocalDateTime arrival;

        public String getTrainId() { return trainId; }
        public void setTrainId(String trainId) { this.trainId = trainId; }
        public String getFromStation() { return fromStation; }
        public void setFromStation(String fromStation) { this.fromStation = fromStation; }
        public String getToStation() { return toStation; }
        public void setToStation(String toStation) { this.toStation = toStation; }
        public LocalDateTime getDeparture() { return departure; }
        public void setDeparture(LocalDateTime departure) { this.departure = departure; }
        public LocalDateTime getArrival() { return arrival; }
        public void setArrival(LocalDateTime arrival) { this.arrival = arrival; }
    }
}