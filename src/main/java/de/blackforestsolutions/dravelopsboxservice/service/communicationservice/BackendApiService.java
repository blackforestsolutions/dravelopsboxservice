package de.blackforestsolutions.dravelopsboxservice.service.communicationservice;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import reactor.core.publisher.Mono;

public interface BackendApiService {
    <T> Mono<T> getOneBy(ApiToken serviceApiToken, Class<T> returnType);
}
