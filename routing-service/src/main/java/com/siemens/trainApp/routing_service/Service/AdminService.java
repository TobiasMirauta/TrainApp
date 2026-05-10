package com.siemens.trainApp.routing_service.Service;

import com.siemens.trainApp.routing_service.Controller.DTO.*;
import com.siemens.trainApp.routing_service.Model.*;
import com.siemens.trainApp.routing_service.Repository.*;
import com.siemens.trainApp.routing_service.Client.BookingClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final StationRepository stationRepository;
    private final RouteRepository routeRepository;
    private final TrainScheduleRepository scheduleRepository;
    private final InventoryRepository inventoryRepository;
    private final BookingClient bookingClient;
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.queue.email.name:email_queue}")
    private String emailQueue;

    @Autowired
    public AdminService(StationRepository stationRepository,
                        RouteRepository routeRepository,
                        TrainScheduleRepository scheduleRepository,
                        InventoryRepository inventoryRepository,
                        BookingClient bookingClient,
                        RabbitTemplate rabbitTemplate) {
        this.stationRepository = stationRepository;
        this.routeRepository = routeRepository;
        this.scheduleRepository = scheduleRepository;
        this.inventoryRepository = inventoryRepository;
        this.bookingClient = bookingClient;
        this.rabbitTemplate = rabbitTemplate;
    }

    // ==========================================
    // --- STATION OPERATIONS ---
    // ==========================================
    public Station addStation(StationRequest request) {
        Station station = new Station();
        station.setName(request.getName());
        return stationRepository.save(station);
    }

    public List<Station> getAllStations() {
        return stationRepository.findAll();
    }

    public Station updateStation(Long id, StationRequest request) {
        Station station = stationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Station not found"));
        station.setName(request.getName());
        return stationRepository.save(station);
    }

    public void deleteStation(Long id) {
        stationRepository.deleteById(id);
    }

    // ==========================================
    // --- ROUTE OPERATIONS ---
    // ==========================================
    public Route addRoute(RouteRequest request) {
        Station source = stationRepository.findById(request.getSourceStationId())
                .orElseThrow(() -> new RuntimeException("Source station not found"));
        Station destination = stationRepository.findById(request.getDestinationStationId())
                .orElseThrow(() -> new RuntimeException("Destination station not found"));

        Route route = new Route();
        route.setSourceStation(source);
        route.setDestinationStation(destination);
        route.setDurationMinutes(request.getDurationMinutes());
        return routeRepository.save(route);
    }

    public List<Route> getAllRoutes() {
        return routeRepository.findAll();
    }

    public Route updateRoute(Long id, RouteRequest request) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Route not found"));
        Station source = stationRepository.findById(request.getSourceStationId())
                .orElseThrow(() -> new RuntimeException("Source station not found"));
        Station destination = stationRepository.findById(request.getDestinationStationId())
                .orElseThrow(() -> new RuntimeException("Destination station not found"));

        route.setSourceStation(source);
        route.setDestinationStation(destination);
        route.setDurationMinutes(request.getDurationMinutes());
        return routeRepository.save(route);
    }

    public void deleteRoute(Long id) {
        routeRepository.deleteById(id);
    }

    // ==========================================
    // --- SCHEDULE OPERATIONS ---
    // ==========================================
    @Transactional
    public TrainSchedule addSchedule(ScheduleRequest request) {
        Route route = routeRepository.findById(request.getRouteId())
                .orElseThrow(() -> new RuntimeException("Route not found"));

        TrainSchedule schedule = new TrainSchedule();
        schedule.setScheduleId(request.getScheduleId());
        schedule.setRoute(route);
        schedule.setDepartureTime(request.getDepartureTime());
        schedule.setArrivalTime(request.getArrivalTime());
        TrainSchedule savedSchedule = scheduleRepository.save(schedule);

        TrainInventory inventory = new TrainInventory();
        inventory.setScheduleId(request.getScheduleId());
        inventory.setTotalSeats(request.getTotalSeats());
        inventory.setAvailableSeats(request.getTotalSeats());
        inventoryRepository.save(inventory);

        return savedSchedule;
    }

    public List<TrainSchedule> getAllSchedules() {
        return scheduleRepository.findAll();
    }

    @Transactional
    public TrainSchedule updateSchedule(String id, ScheduleRequest request) {
        TrainSchedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));
        Route route = routeRepository.findById(request.getRouteId())
                .orElseThrow(() -> new RuntimeException("Route not found"));

        schedule.setRoute(route);
        schedule.setDepartureTime(request.getDepartureTime());
        schedule.setArrivalTime(request.getArrivalTime());

        TrainInventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory not found"));
        inventory.setTotalSeats(request.getTotalSeats());

        inventoryRepository.save(inventory);
        return scheduleRepository.save(schedule);
    }

    @Transactional
    public void deleteSchedule(String id) {
        inventoryRepository.deleteById(id);
        scheduleRepository.deleteById(id);
    }

    // ==========================================
    // --- DELAY & NOTIFICATIONS (CERINȚA C) ---
    // ==========================================
    @Transactional
    public TrainSchedule addDelay(String scheduleId, int delayMinutes) {
        TrainSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Trenul nu exista!"));

        schedule.setDelayMinutes(schedule.getDelayMinutes() + delayMinutes);
        TrainSchedule saved = scheduleRepository.save(schedule);
        System.out.println("-> Baza de date actualizata cu intarzierea.");

        try {
            // Luam clientii care au rezervat pe acest tren
            List<Map<String, Object>> bookings = bookingClient.getBookingsForTrain(scheduleId);

            for (Map<String, Object> booking : bookings) {
                String email = (String) booking.get("customerEmail");
                String mesajNotificare = "ATENȚIE: Trenul " + scheduleId + " are o întârziere de " + delayMinutes + " minute.";

                // Trimitem un mesaj combinat pe care NotificationService sa il poata recunoaste usor
                rabbitTemplate.convertAndSend(emailQueue, mesajNotificare + " Pentru: " + email);
            }
            System.out.println("-> Notificari trimise in RabbitMQ.");
        } catch (Exception e) {
            System.err.println("-> Eroare la preluarea rezervarilor din BookingService: " + e.getMessage());
        }

        return saved;
    }

    // ==========================================
    // --- SEARCH LOGIC (CERINȚA B) ---
    // ==========================================
    public List<JourneyResponse> searchJourneys(String from, String to) {
        List<TrainSchedule> allSchedules = scheduleRepository.findAll();
        List<JourneyResponse> validJourneys = new ArrayList<>();

        // 1. RUTE DIRECTE
        for (TrainSchedule s : allSchedules) {
            if (s.getRoute().getSourceStation().getName().equalsIgnoreCase(from) &&
                    s.getRoute().getDestinationStation().getName().equalsIgnoreCase(to)) {

                validJourneys.add(new JourneyResponse("DIRECT", List.of(
                        new JourneyLeg(s.getScheduleId(), from, to, s.getDepartureTime(), s.getArrivalTime())
                )));
            }
        }

        // 2. RUTE CU ESCALĂ (Logica de tranzitie corectată)
        for (TrainSchedule t1 : allSchedules) {
            // Trenul 1 pleacă din Sursa noastră
            if (t1.getRoute().getSourceStation().getName().equalsIgnoreCase(from)) {

                for (TrainSchedule t2 : allSchedules) {
                    // Trenul 2 ajunge la Destinația noastră
                    // ȘI Punctul de tranzitie (Sosire T1 == Plecare T2)
                    if (t2.getRoute().getDestinationStation().getName().equalsIgnoreCase(to) &&
                            t1.getRoute().getDestinationStation().getName().equalsIgnoreCase(t2.getRoute().getSourceStation().getName())) {

                        // Verificăm dacă trenurile nu sunt identice și dacă timpul e logic
                        if (!t1.getScheduleId().equals(t2.getScheduleId()) &&
                                t1.getArrivalTime().isBefore(t2.getDepartureTime())) {

                            String stationMiddle = t1.getRoute().getDestinationStation().getName();

                            validJourneys.add(new JourneyResponse("ESCALA", List.of(
                                    new JourneyLeg(t1.getScheduleId(), from, stationMiddle, t1.getDepartureTime(), t1.getArrivalTime()),
                                    new JourneyLeg(t2.getScheduleId(), stationMiddle, to, t2.getDepartureTime(), t2.getArrivalTime())
                            )));
                        }
                    }
                }
            }
        }

        if (validJourneys.isEmpty()) {
            throw new RuntimeException("Nu s-au găsit legături între " + from + " și " + to);
        }

        // Sortăm rezultatele după ora de plecare a primului tren
        validJourneys.sort(Comparator.comparing(j -> j.getLegs().get(0).getDepartureTime()));

        return validJourneys;
    }
}