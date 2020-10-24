package de.blackforestsolutions.dravelopspolygonservice.service.communicationservice;

import org.springframework.scheduling.annotation.Scheduled;

public interface TravelPointApiService {
    @Scheduled(cron = "${otp.polygonupdatetime}")
    void updateOpenTripPlannerBox();
}
