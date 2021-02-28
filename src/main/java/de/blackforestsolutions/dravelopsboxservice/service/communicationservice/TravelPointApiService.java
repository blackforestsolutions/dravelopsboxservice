package de.blackforestsolutions.dravelopsboxservice.service.communicationservice;

import de.blackforestsolutions.dravelopsdatamodel.TravelPoint;
import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import reactor.core.publisher.Flux;

public interface TravelPointApiService {
    Flux<TravelPoint> retrieveTravelPointsFromApiService(ApiToken request);
}
