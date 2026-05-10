package com.siemens.trainApp.routing_service.Repository;

import com.siemens.trainApp.routing_service.Model.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
}