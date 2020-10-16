package de.blackforestsolutions.dravelopspolygonservice.service.supportservice;

import de.blackforestsolutions.dravelopsdatamodel.util.ApiToken;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class OpenTripPlannerHttpCallBuilderServiceImpl implements OpenTripPlannerHttpCallBuilderService {

    private static final String OPEN_TRIP_PLANNER_PATH = "otp";
    private static final String ROUTER_PATH = "routers";

    @Override
    public String buildOpenTripPlannerPolygonPathWith(ApiToken apiToken) {
        Objects.requireNonNull(apiToken.getRouter(), "router is not allowed to be null");
        return "/"
                .concat(OPEN_TRIP_PLANNER_PATH)
                .concat("/")
                .concat(ROUTER_PATH)
                .concat("/")
                .concat(apiToken.getRouter());
    }
}
