package com.siemens.trainApp.routing_service.Controller;

import com.siemens.trainApp.routing_service.Controller.DTO.*;
import com.siemens.trainApp.routing_service.Model.*;
import com.siemens.trainApp.routing_service.Service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // --- STATIONS ---

    // CREATE
    @PostMapping("/stations")
    public ResponseEntity<Station> createStation(@RequestBody StationRequest request) {
        return ResponseEntity.ok(adminService.addStation(request));
    }
    @GetMapping("/ping")
    public String ping() {
        return "PONG! Controllerul functioneaza.";
    }

    // READ
    @GetMapping("/stations")
    public List<Station> getStations() { return adminService.getAllStations(); }

    // UPDATE
    @PutMapping("/stations/{id}")
    public ResponseEntity<Station> updateStation(@PathVariable Long id, @RequestBody StationRequest request) {
        return ResponseEntity.ok(adminService.updateStation(id, request));
    }

    // DELETE
    @DeleteMapping("/stations/{id}")
    public ResponseEntity<Void> deleteStation(@PathVariable Long id) {
        adminService.deleteStation(id);
        return ResponseEntity.noContent().build();
    }

    // --- ROUTES ---
    //CREATE
    @PostMapping("/routes")
    public ResponseEntity<Route> createRoute(@RequestBody RouteRequest request) {
        return ResponseEntity.ok(adminService.addRoute(request));
    }

    // READ
    @GetMapping("/routes")
    public List<Route> getRoutes() { return adminService.getAllRoutes(); }

    // UPDATE
    @PutMapping("/routes/{id}")
    public ResponseEntity<Route> updateRoute(@PathVariable Long id, @RequestBody RouteRequest request) {
        return ResponseEntity.ok(adminService.updateRoute(id, request));
    }

    // DELETE
    @DeleteMapping("/routes/{id}")
    public ResponseEntity<Void> deleteRoute(@PathVariable Long id) {
        adminService.deleteRoute(id);
        return ResponseEntity.noContent().build();
    }

    // --- SCHEDULES ---

    @PostMapping("/schedules")
    public ResponseEntity<TrainSchedule> createSchedule(@RequestBody ScheduleRequest request) {
        return ResponseEntity.ok(adminService.addSchedule(request));
    }

    // READ
    @GetMapping("/schedules")
    public List<TrainSchedule> getSchedules() { return adminService.getAllSchedules(); }

    // UPDATE
    @PutMapping("/schedules/{id}")
    public ResponseEntity<TrainSchedule> updateSchedule(@PathVariable String id, @RequestBody ScheduleRequest request) {
        return ResponseEntity.ok(adminService.updateSchedule(id, request));
    }

    // DELETE
    @DeleteMapping("/schedules/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable String id) {
        adminService.deleteSchedule(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchTrainConnections(
            @RequestParam("from") String from,
            @RequestParam("to") String to) {
        try {
            System.out.println("Căutare începută pentru: " + from + " -> " + to);

            List<JourneyResponse> results = adminService.searchJourneys(from, to);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }
    @PatchMapping("/schedules/{id}/delay")
    public ResponseEntity<?> addDelay(
            @PathVariable("id") String scheduleId,
            @RequestParam("minutes") int minutes) {
        try {
            System.out.println("=== START REQUEST ÎNTÂRZIERE ===");
            System.out.println("Tren: " + scheduleId + " | Minute: " + minutes);

            adminService.addDelay(scheduleId, minutes);

            return ResponseEntity.ok("Întârziere salvată și notificări trimise cu succes!");
        } catch (Exception e) {
            System.err.println("!!! EROARE LA ÎNTÂRZIERE !!!");
            e.printStackTrace();
            return ResponseEntity.status(500).body("Eroare server: " + e.getMessage());
        }
    }
}