package de.blackforestsolutions.dravelopspolygonservice.service.communicationservice;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import org.springframework.data.geo.Box;
import reactor.core.publisher.Mono;

public interface OpenTripPlannerApiService {
    Mono<Box> extractBoxBy(ApiToken apiToken);
}
