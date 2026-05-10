package com.siemens.trainApp.routing_service.Controller.DTO;

public class RouteRequest {
    private Long sourceStationId;
    private Long destinationStationId;
    private int durationMinutes;

    public Long getSourceStationId() { return sourceStationId; }
    public void setSourceStationId(Long sourceStationId) { this.sourceStationId = sourceStationId; }

    public Long getDestinationStationId() { return destinationStationId; }
    public void setDestinationStationId(Long destinationStationId) { this.destinationStationId = destinationStationId; }

    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }
}