package com.siemens.trainApp.routing_service.Service;

import com.siemens.trainApp.routing_service.Controller.DTO.RouteResponse;
import com.siemens.trainApp.routing_service.Model.TrainSchedule;
import com.siemens.trainApp.routing_service.Repository.TrainScheduleRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchService {

    private final TrainScheduleRepository scheduleRepository;

    public SearchService(TrainScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    public List<RouteResponse> findRoutes(String fromStation, String toStation) {
        List<RouteResponse> results = new ArrayList<>();
        List<TrainSchedule> allSchedules = scheduleRepository.findAll();


        PriorityQueue<List<TrainSchedule>> pq = new PriorityQueue<>(
                Comparator.comparing(path -> path.get(path.size() - 1).getArrivalTime())
        );

        Map<String, LocalDateTime> earliestArrivalAtStation = new HashMap<>();

        for (TrainSchedule s : allSchedules) {
            if (s.getRoute().getSourceStation().getName().equalsIgnoreCase(fromStation)) {
                List<TrainSchedule> initialPath = new ArrayList<>();
                initialPath.add(s);
                pq.add(initialPath);

                earliestArrivalAtStation.put(s.getRoute().getDestinationStation().getName(), s.getArrivalTime());
            }
        }

        int maxTransfers = 3;

        while (!pq.isEmpty()) {
            List<TrainSchedule> currentPath = pq.poll();
            TrainSchedule lastLeg = currentPath.get(currentPath.size() - 1);
            String currentStation = lastLeg.getRoute().getDestinationStation().getName();

            if (currentStation.equalsIgnoreCase(toStation)) {
                results.add(convertToResponse(currentPath));
                // Oprim dupa primele 3 variante optime gasite, pentru a nu oferi o lista infinita de optiuni tarzii
                if (results.size() >= 3) break;
                continue;
            }

            if (currentPath.size() > maxTransfers) continue;

            for (TrainSchedule nextLeg : allSchedules) {
                if (nextLeg.getRoute().getSourceStation().getName().equalsIgnoreCase(currentStation)) {

                    if (nextLeg.getDepartureTime().isAfter(lastLeg.getArrivalTime().plusMinutes(15))) {

                        String nextStation = nextLeg.getRoute().getDestinationStation().getName();
                        LocalDateTime newArrivalTime = nextLeg.getArrivalTime();

                         LocalDateTime bestKnownArrival = earliestArrivalAtStation.getOrDefault(nextStation, LocalDateTime.MAX);

                        if (newArrivalTime.isBefore(bestKnownArrival.plusHours(2))) {

                            if (newArrivalTime.isBefore(bestKnownArrival)) {
                                earliestArrivalAtStation.put(nextStation, newArrivalTime);
                            }

                            boolean alreadyVisited = currentPath.stream()
                                    .anyMatch(leg -> leg.getRoute().getSourceStation().getName().equalsIgnoreCase(nextStation));

                            if (!alreadyVisited) {
                                List<TrainSchedule> newPath = new ArrayList<>(currentPath);
                                newPath.add(nextLeg);
                                pq.add(newPath);
                            }
                        }
                    }
                }
            }
        }

        return results;
    }

    private RouteResponse convertToResponse(List<TrainSchedule> schedules) {
        RouteResponse response = new RouteResponse();
        List<RouteResponse.SegmentDTO> segments = schedules.stream().map(s -> {
            RouteResponse.SegmentDTO dto = new RouteResponse.SegmentDTO();
            dto.setTrainId(s.getScheduleId());
            dto.setFromStation(s.getRoute().getSourceStation().getName());
            dto.setToStation(s.getRoute().getDestinationStation().getName());
            dto.setDeparture(s.getDepartureTime());
            dto.setArrival(s.getArrivalTime());
            return dto;
        }).collect(Collectors.toList());

        response.setSegments(segments);

        Duration total = Duration.between(schedules.get(0).getDepartureTime(),
                schedules.get(schedules.size() - 1).getArrivalTime());
        response.setTotalDurationMinutes(total.toMinutes());

        return response;
    }
}