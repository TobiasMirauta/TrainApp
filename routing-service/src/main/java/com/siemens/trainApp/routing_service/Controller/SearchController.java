package com.siemens.trainApp.routing_service.Controller;

import com.siemens.trainApp.routing_service.Controller.DTO.RouteResponse;
import com.siemens.trainApp.routing_service.Service.SearchService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/search")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping
    public List<RouteResponse> search(@RequestParam String from, @RequestParam String to) {
        return searchService.findRoutes(from, to);
    }
}