package de.blackforestsolutions.dravelopspolygonservice.service.applicationstart;

import de.blackforestsolutions.dravelopspolygonservice.service.communicationservice.TravelPointApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartComponent implements ApplicationListener<ApplicationReadyEvent> {

    private final TravelPointApiService travelPointApiService;

    @Autowired
    public ApplicationStartComponent(TravelPointApiService travelPointApiService) {
        this.travelPointApiService = travelPointApiService;
    }

    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {
        travelPointApiService.updateOpenTripPlannerBox();
    }
}
