package com.siemens.trainApp.routing_service.Repository;

import com.siemens.trainApp.routing_service.Model.TrainSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface TrainScheduleRepository extends JpaRepository<TrainSchedule, String> {
}