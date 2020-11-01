package de.blackforestsolutions.dravelopspolygonservice.service.callbuilderservice;


import de.blackforestsolutions.dravelopsdatamodel.util.ApiToken;

public interface OpenTripPlannerHttpCallBuilderService {

    String buildOpenTripPlannerPolygonPathWith(ApiToken apiToken);
}
