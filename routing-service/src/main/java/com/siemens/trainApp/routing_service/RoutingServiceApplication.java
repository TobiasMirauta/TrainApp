package com.siemens.trainApp.routing_service;

import com.siemens.trainApp.routing_service.Model.TrainInventory;
import com.siemens.trainApp.routing_service.Repository.InventoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EnableFeignClients
public class RoutingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RoutingServiceApplication.class, args);
	}
	@Bean
	CommandLineRunner initDatabase(InventoryRepository repository) {
		return args -> {
			if (repository.findById("TREN-123").isEmpty()) {
				TrainInventory trenTest = new TrainInventory();
				trenTest.setScheduleId("TREN-123");
				trenTest.setTotalSeats(50);
				trenTest.setAvailableSeats(50);
				repository.save(trenTest);
				System.out.println(" Trenul de test TREN-123 a fost adaugat in baza de date cu 50 de locuri.");
			}
		};
	}
	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
						.allowedOrigins("*")
						.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
						.allowedHeaders("*");
			}
		};
	}
}
