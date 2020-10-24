package de.blackforestsolutions.dravelopspolygonservice.service.communicationservice;

import de.blackforestsolutions.dravelopsdatamodel.util.ApiToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Box;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TravelPointApiServiceImpl implements TravelPointApiService {

    private Box openTripPlannerBox;
    private final ApiToken openTripPlannerApiToken;
    private final OpenTripPlannerApiService openTripPlannerApiService;

    @Autowired
    public TravelPointApiServiceImpl(Box openTripPlannerBox, ApiToken openTripPlannerApiToken, OpenTripPlannerApiService openTripPlannerApiService) {
        this.openTripPlannerBox = openTripPlannerBox;
        this.openTripPlannerApiToken = openTripPlannerApiToken;
        this.openTripPlannerApiService = openTripPlannerApiService;
    }

    @Override
    @Scheduled(cron = "${otp.polygonupdatetime}")
    public void updateOpenTripPlannerBox() {
        openTripPlannerApiService.extractBoxBy(openTripPlannerApiToken)
                .doOnError(e -> log.error("Error while updating Polygon: ", e))
                .onErrorStop()
                .subscribe(box -> {
                    openTripPlannerBox = box;
//                   05.10.2020 Placeholder to defend pmd violation until pelias is implemented
                    log.info("Polygon from OpenTripPlanner was updated: ", openTripPlannerBox);
                });
    }
}
