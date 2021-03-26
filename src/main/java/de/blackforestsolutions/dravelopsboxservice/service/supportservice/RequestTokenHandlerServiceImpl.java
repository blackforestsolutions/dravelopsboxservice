package de.blackforestsolutions.dravelopsboxservice.service.supportservice;

import de.blackforestsolutions.dravelopsboxservice.service.communicationservice.BackendApiService;
import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.Box;
import de.blackforestsolutions.dravelopsdatamodel.exception.NoResultFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
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
    @Scheduled(cron = "${stationpersistence.box.updatetime}")
    public void updateStationPersistenceBox() {
        Mono.defer(this::executeBoxApiCall)
                .switchIfEmpty(Mono.error(new NoResultFoundException()))
                .onErrorResume(this::delayError)
                .retry()
                .subscribe(this::handleBoxResult);
    }

    private Mono<Box> executeBoxApiCall() {
        log.info("Trying to get box from StationPersistenceApi");
        return backendApiService.getOneBy(stationPersistenceBoxApiToken, Box.class);
    }

    private Mono<Box> delayError(Throwable exception) {
        return Mono.defer(this::logAndDelayError)
                .then(Mono.error(exception));
    }

    private Mono<Long> logAndDelayError() {
        log.error(buildErrorMessage());
        return Mono.delay(Duration.ofSeconds(stationPersistenceBoxApiToken.getRetryTimeInSeconds()));
    }

    private String buildErrorMessage() {
        return "Error while calling box from Station PersistenceApi. Retry in "
                .concat(String.valueOf(stationPersistenceBoxApiToken.getRetryTimeInSeconds()))
                .concat(" seconds.");
    }

    private void handleBoxResult(Box stationPersistenceBox) {
        this.stationPersistenceBox = stationPersistenceBox;
        log.info("Box from StationPersistenceApi was successfully updated");
    }
}
