package de.blackforestsolutions.dravelopsboxservice.service.communicationservice;

import de.blackforestsolutions.dravelopsboxservice.exceptionhandling.ExceptionHandlerService;
import de.blackforestsolutions.dravelopsboxservice.service.supportservice.RequestTokenHandlerService;
import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.TravelPoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Slf4j
@Service
public class TravelPointApiServiceImpl implements TravelPointApiService {

    private final RequestTokenHandlerService requestTokenHandlerService;
    private final ExceptionHandlerService exceptionHandlerService;
    private final ApiToken peliasApiToken;
    private final PeliasApiService peliasApiService;

    @Autowired
    public TravelPointApiServiceImpl(RequestTokenHandlerService requestTokenHandlerService, ExceptionHandlerService exceptionHandlerService, ApiToken peliasApiToken, PeliasApiService peliasApiService) {
        this.requestTokenHandlerService = requestTokenHandlerService;
        this.exceptionHandlerService = exceptionHandlerService;
        this.peliasApiToken = peliasApiToken;
        this.peliasApiService = peliasApiService;
    }

    @Override
    public Flux<TravelPoint> retrieveAutocompleteAddressesFromApiService(ApiToken userRequestToken) {
        return Mono.just(userRequestToken)
                .map(userToken -> requestTokenHandlerService.getAutocompleteApiTokenWith(userToken, peliasApiToken))
                .flatMapMany(peliasApiService::getAutocompleteAddressesFrom)
                .flatMap(exceptionHandlerService::handleExceptions)
                .onErrorResume(exceptionHandlerService::handleExceptions);
    }

    @Override
    public Flux<TravelPoint> retrieveNearestAddressesFromApiService(ApiToken userRequestToken) {
        return Mono.just(userRequestToken)
                .map(userToken -> requestTokenHandlerService.getNearestAddressesApiTokenWith(userToken, peliasApiToken))
                .flatMapMany(peliasApiService::getNearestAddressesFrom)
                .flatMap(exceptionHandlerService::handleExceptions)
                .onErrorResume(exceptionHandlerService::handleExceptions);
    }
}
