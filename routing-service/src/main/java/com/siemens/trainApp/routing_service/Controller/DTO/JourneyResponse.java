package com.siemens.trainApp.routing_service.Controller.DTO;

import java.util.List;

public class JourneyResponse {
    private String journeyType;
    private List<JourneyLeg> legs;

    public JourneyResponse(String journeyType, List<JourneyLeg> legs) {
        this.journeyType = journeyType;
        this.legs = legs;
    }

    public String getJourneyType() { return journeyType; }
    public List<JourneyLeg> getLegs() { return legs; }
}