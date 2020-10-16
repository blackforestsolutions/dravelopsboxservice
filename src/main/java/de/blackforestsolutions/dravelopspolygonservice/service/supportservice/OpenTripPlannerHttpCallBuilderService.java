package de.blackforestsolutions.dravelopspolygonservice.service.supportservice;


import de.blackforestsolutions.dravelopsdatamodel.util.ApiToken;

public interface OpenTripPlannerHttpCallBuilderService {

    String buildOpenTripPlannerPolygonPathWith(ApiToken apiToken);
}
