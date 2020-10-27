package de.blackforestsolutions.dravelopspolygonservice.service.communicationservice;

import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Flux;

public interface TravelPointApiService {
    Flux<String> retrieveTravelPointsFromApiService(String request);
}
