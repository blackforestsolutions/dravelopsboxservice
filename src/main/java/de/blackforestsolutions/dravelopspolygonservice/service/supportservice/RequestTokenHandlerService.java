package de.blackforestsolutions.dravelopspolygonservice.service.supportservice;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import org.springframework.scheduling.annotation.Scheduled;

public interface RequestTokenHandlerService {
    ApiToken getRequestApiTokenWith(ApiToken request, ApiToken configuredPeliasApiToken);

    @Scheduled(cron = "${otp.polygonupdatetime}")
    void updateOpenTripPlannerBox();
}
