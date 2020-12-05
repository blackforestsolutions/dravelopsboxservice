package de.blackforestsolutions.dravelopspolygonservice.service.callbuilderservice;


import de.blackforestsolutions.dravelopsdatamodel.ApiToken;

public interface OpenTripPlannerHttpCallBuilderService {

    String buildOpenTripPlannerPolygonPathWith(ApiToken apiToken);
}
