package de.blackforestsolutions.dravelopspolygonservice.service.polygonservice;

import org.springframework.scheduling.annotation.Scheduled;

public interface PolygonService {
    @Scheduled(cron = "${otp.polygonupdatetime}")
    void updateOpenTripPlannerPolygon();
}
