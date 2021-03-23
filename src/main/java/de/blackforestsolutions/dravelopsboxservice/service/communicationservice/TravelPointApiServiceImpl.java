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

import java.util.Comparator;


@Slf4j
@Service
public class TravelPointApiServiceImpl implements TravelPointApiService {

    private final RequestTokenHandlerService requestTokenHandlerService;
    private final ExceptionHandlerService exceptionHandlerService;
    private final PeliasApiService peliasApiService;
    private final ApiToken peliasAutocompleteApiToken;
    private final ApiToken peliasNearestAddressesApiToken;

    @Autowired
    public TravelPointApiServiceImpl(RequestTokenHandlerService requestTokenHandlerService, ExceptionHandlerService exceptionHandlerService, PeliasApiService peliasApiService, ApiToken peliasAutocompleteApiToken, ApiToken peliasNearestAddressesApiToken) {
        this.requestTokenHandlerService = requestTokenHandlerService;
        this.exceptionHandlerService = exceptionHandlerService;
        this.peliasApiService = peliasApiService;
        this.peliasAutocompleteApiToken = peliasAutocompleteApiToken;
        this.peliasNearestAddressesApiToken = peliasNearestAddressesApiToken;
    }

    @Override
    public Flux<TravelPoint> retrieveAutocompleteAddressesFromApiService(ApiToken userRequestToken) {
        return Mono.just(userRequestToken)
                .map(userToken -> requestTokenHandlerService.getAutocompleteApiTokenWith(userToken, peliasAutocompleteApiToken))
                .flatMapMany(peliasApiService::getAutocompleteAddressesFrom)
                .flatMap(exceptionHandlerService::handleExceptions)
                .distinct()
                .onErrorResume(exceptionHandlerService::handleExceptions);
    }

    @Override
    public Flux<TravelPoint> retrieveNearestAddressesFromApiService(ApiToken userRequestToken) {
        return Mono.just(userRequestToken)
                .map(userToken -> requestTokenHandlerService.getNearestAddressesApiTokenWith(userToken, peliasNearestAddressesApiToken))
                .flatMapMany(peliasApiService::getNearestAddressesFrom)
                .flatMap(exceptionHandlerService::handleExceptions)
                .distinct()
                .sort(Comparator.comparingDouble(travelPoint -> travelPoint.getDistanceInKilometers().getValue()))
                .onErrorResume(exceptionHandlerService::handleExceptions);
    }
}
