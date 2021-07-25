package de.blackforestsolutions.dravelopsboxservice.service.supportservice;

import de.blackforestsolutions.dravelopsboxservice.service.communicationservice.BackendApiService;
import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.Box;
import de.blackforestsolutions.dravelopsdatamodel.exception.NoResultFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@RefreshScope
@Service
public class RequestTokenHandlerServiceImpl implements RequestTokenHandlerService {

    private Box stationPersistenceBox;
    private final ApiToken stationPersistenceBoxApiToken;
    private final BackendApiService backendApiService;

    @Autowired
    public RequestTokenHandlerServiceImpl(Box stationPersistenceBox, ApiToken stationPersistenceBoxApiToken, BackendApiService backendApiService) {
        this.stationPersistenceBox = stationPersistenceBox;
        this.stationPersistenceBoxApiToken = stationPersistenceBoxApiToken;
        this.backendApiService = backendApiService;
    }

    @Override
    public ApiToken getAutocompleteApiTokenWith(ApiToken requestApiToken, ApiToken configuredPeliasApiToken) {
        ApiToken autocompleteApiToken = new ApiToken(configuredPeliasApiToken);

        autocompleteApiToken.setLanguage(requestApiToken.getLanguage());
        autocompleteApiToken.setDeparture(requestApiToken.getDeparture());
        autocompleteApiToken.setBox(stationPersistenceBox);

        return autocompleteApiToken;
    }

    @Override
    public ApiToken getNearestAddressesApiTokenWith(ApiToken requestApiToken, ApiToken configuredPeliasApiToken) {
        ApiToken nearestAddressesApiToken = new ApiToken(configuredPeliasApiToken);

        nearestAddressesApiToken.setArrivalCoordinate(requestApiToken.getArrivalCoordinate());
        nearestAddressesApiToken.setRadiusInKilometers(requestApiToken.getRadiusInKilometers());
        nearestAddressesApiToken.setLanguage(requestApiToken.getLanguage());

        return nearestAddressesApiToken;
    }

    @Override
    @Scheduled(fixedRateString = "${stationpersistence.get.box.retryTimeInMilliseconds}")
    public void updateStationPersistenceBox() {
        log.info("Trying to get box from StationPersistenceApi!");
        Mono.defer(() -> backendApiService.getOneBy(stationPersistenceBoxApiToken, Box.class))
                .switchIfEmpty(Mono.error(new NoResultFoundException()))
                .onErrorResume(error -> logBoxUpdateError())
                .subscribe(this::handleBoxResult);
    }

    private Mono<Box> logBoxUpdateError() {
        Objects.requireNonNull(stationPersistenceBoxApiToken.getRetryTimeInMilliseconds(), "retryTimeInMilliseconds is not allowed to be null");

        long seconds = TimeUnit.MILLISECONDS.toSeconds(stationPersistenceBoxApiToken.getRetryTimeInMilliseconds());
        log.warn("Trying to update box failed! Next try is in: ".concat(String.valueOf(seconds)).concat(" seconds"));

        return Mono.empty();
    }

    private void handleBoxResult(Box stationPersistenceBox) {
        this.stationPersistenceBox = stationPersistenceBox;
        log.info("Box from StationPersistenceApi was successfully updated.");
    }
}
