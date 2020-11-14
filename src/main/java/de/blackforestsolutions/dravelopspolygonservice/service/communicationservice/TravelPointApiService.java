package de.blackforestsolutions.dravelopspolygonservice.service.communicationservice;

import de.blackforestsolutions.dravelopsdatamodel.TravelPoint;
import de.blackforestsolutions.dravelopsdatamodel.util.ApiToken;
import reactor.core.publisher.Flux;

public interface TravelPointApiService {
    Flux<TravelPoint> retrieveTravelPointsFromApiService(ApiToken request);
}
