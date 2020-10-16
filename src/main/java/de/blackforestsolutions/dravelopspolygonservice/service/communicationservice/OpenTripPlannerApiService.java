package de.blackforestsolutions.dravelopspolygonservice.service.communicationservice;

import de.blackforestsolutions.dravelopsdatamodel.util.ApiToken;
import org.springframework.data.geo.Polygon;
import reactor.core.publisher.Mono;

public interface OpenTripPlannerApiService {
    Mono<Polygon> extractPolygonBy(ApiToken apiToken);
}
