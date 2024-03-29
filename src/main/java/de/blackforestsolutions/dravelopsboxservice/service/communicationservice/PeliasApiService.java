package de.blackforestsolutions.dravelopsboxservice.service.communicationservice;

import de.blackforestsolutions.dravelopsdatamodel.CallStatus;
import de.blackforestsolutions.dravelopsdatamodel.TravelPoint;
import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import reactor.core.publisher.Flux;

public interface PeliasApiService {
    Flux<CallStatus<TravelPoint>> getAutocompleteAddressesFrom(ApiToken apiToken);

    Flux<CallStatus<TravelPoint>> getNearestAddressesFrom(ApiToken apiToken);
}
