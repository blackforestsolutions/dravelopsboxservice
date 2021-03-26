package de.blackforestsolutions.dravelopsboxservice.service.supportservice;

import de.blackforestsolutions.dravelopsboxservice.service.communicationservice.BackendApiService;
import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.Box;
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
        log.info("Trying to get box from StationPersistenceApi:");
        Mono.defer(() -> backendApiService.getOneBy(stationPersistenceBoxApiToken, Box.class))
                .switchIfEmpty(logNoBoxResultWith(stationPersistenceBoxApiToken))
                .onErrorResume(this::handleError)
                .subscribe(this::handleBoxResult);
    }

    private Mono<Box> logNoBoxResultWith(ApiToken apiToken) {
        Objects.requireNonNull(apiToken.getRetryTimeInMilliseconds(), "retryTimeInMilliseconds is not allowed to be null");

        long seconds = TimeUnit.MILLISECONDS.toSeconds(apiToken.getRetryTimeInMilliseconds());
        log.warn("Trying to update box failed! Next try is in: ".concat(String.valueOf(seconds)).concat(" seconds"));

        return Mono.empty();
    }

    private Mono<Box> handleError(Throwable exception) {
        log.error("Error while updating the box: ", exception);
        return Mono.empty();
    }

    private void handleBoxResult(Box stationPersistenceBox) {
        this.stationPersistenceBox = stationPersistenceBox;
        log.info("Box from StationPersistenceApi was successfully updated");
    }
}
