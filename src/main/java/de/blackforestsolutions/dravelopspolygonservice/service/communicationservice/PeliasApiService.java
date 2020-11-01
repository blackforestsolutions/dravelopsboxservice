package de.blackforestsolutions.dravelopspolygonservice.service.communicationservice;

import de.blackforestsolutions.dravelopsdatamodel.CallStatus;
import de.blackforestsolutions.dravelopsdatamodel.TravelPoint;
import de.blackforestsolutions.dravelopsdatamodel.util.ApiToken;
import reactor.core.publisher.Flux;

public interface PeliasApiService {
    Flux<CallStatus<TravelPoint>> extractTravelPointsFrom(ApiToken apiToken);
}
