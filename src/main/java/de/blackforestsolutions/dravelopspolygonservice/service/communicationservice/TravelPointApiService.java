package de.blackforestsolutions.dravelopspolygonservice.service.communicationservice;

import reactor.core.publisher.Flux;

public interface TravelPointApiService {
    Flux<String> retrieveTravelPointsFromApiService(String request);
}
