package de.blackforestsolutions.dravelopspolygonservice.service.supportservice;

import de.blackforestsolutions.dravelopsdatamodel.util.ApiToken;
import de.blackforestsolutions.dravelopspolygonservice.service.communicationservice.OpenTripPlannerApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Box;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RequestTokenHandlerServiceImpl implements RequestTokenHandlerService {

    private Box openTripPlannerBox;
    private final ApiToken openTripPlannerApiToken;
    private final OpenTripPlannerApiService openTripPlannerApiService;

    @Autowired
    public RequestTokenHandlerServiceImpl(Box openTripPlannerBox, ApiToken openTripPlannerApiToken, OpenTripPlannerApiService openTripPlannerApiService) {
        this.openTripPlannerBox = openTripPlannerBox;
        this.openTripPlannerApiToken = openTripPlannerApiToken;
        this.openTripPlannerApiService = openTripPlannerApiService;
    }

    @Override
    public ApiToken getRequestApiTokenWith(ApiToken request, ApiToken configuredPeliasApiToken) {
        return new ApiToken.ApiTokenBuilder(configuredPeliasApiToken)
                .setLanguage(request.getLanguage())
                .setDeparture(request.getDeparture())
                .setBox(openTripPlannerBox)
                .build();
    }

    @Override
    @Scheduled(cron = "${otp.polygonupdatetime}")
    public void updateOpenTripPlannerBox() {
        openTripPlannerApiService.extractBoxBy(openTripPlannerApiToken)
                .doOnError(e -> log.error("Error while updating Polygon: ", e))
                .onErrorStop()
                .subscribe(box -> {
                    openTripPlannerBox = box;
                    log.info("Polygon from OpenTripPlanner was successfully updated");
                });
    }
}
