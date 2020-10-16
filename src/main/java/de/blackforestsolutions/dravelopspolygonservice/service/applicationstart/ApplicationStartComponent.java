package de.blackforestsolutions.dravelopspolygonservice.service.applicationstart;

import de.blackforestsolutions.dravelopspolygonservice.service.polygonservice.PolygonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartComponent implements ApplicationListener<ApplicationReadyEvent> {

    private final PolygonService polygonService;

    @Autowired
    public ApplicationStartComponent(PolygonService polygonService) {
        this.polygonService = polygonService;
    }

    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {
        polygonService.updateOpenTripPlannerPolygon();
    }
}
