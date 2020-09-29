package de.blackforestsolutions.dravelopspolygonservice.service.polygonservice;

import de.blackforestsolutions.dravelopsdatamodel.util.ApiToken;
import de.blackforestsolutions.dravelopspolygonservice.service.communicationservice.OpenTripPlannerApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Polygon;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PolygonServiceImpl implements PolygonService {

    private Polygon openTripPlannerPolygon;
    private final ApiToken openTripPlannerApiToken;
    private final OpenTripPlannerApiService openTripPlannerApiService;

    @Autowired
    public PolygonServiceImpl(Polygon openTripPlannerPolygon, ApiToken openTripPlannerApiToken, OpenTripPlannerApiService openTripPlannerApiService) {
        this.openTripPlannerPolygon = openTripPlannerPolygon;
        this.openTripPlannerApiToken = openTripPlannerApiToken;
        this.openTripPlannerApiService = openTripPlannerApiService;
    }

    @Override
    @Scheduled(cron = "${otp.polygonupdatetime}")
    public void updateOpenTripPlannerPolygon() {
        openTripPlannerApiService.extractPolygonBy(openTripPlannerApiToken)
                .doOnError(e -> log.error("Error while updating Polygon: ", e))
                .onErrorStop()
                .subscribe(polygon -> {
                    openTripPlannerPolygon = polygon;
                    log.info("Polygon from OpenTripPlanner was updated.");
                });
    }
}
