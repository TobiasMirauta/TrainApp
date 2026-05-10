package com.siemens.trainApp.routing_service.Controller;

import com.siemens.trainApp.routing_service.Controller.DTO.JourneyResponse;
import com.siemens.trainApp.routing_service.Service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/search")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class SearchController {

    private final AdminService adminService; // Apelăm logica deja funcțională de escală

    public SearchController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping
    public ResponseEntity<?> search(@RequestParam("from") String from, @RequestParam("to") String to) {
        try {
            System.out.println("Căutare PUBLICĂ începută pentru: " + from + " -> " + to);
            // Apelăm metoda care știe să facă tranzitul (escala) și formatarea corectă
            List<JourneyResponse> results = adminService.searchJourneys(from, to);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            System.err.println("Eroare la căutare: " + e.getMessage());
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }
}