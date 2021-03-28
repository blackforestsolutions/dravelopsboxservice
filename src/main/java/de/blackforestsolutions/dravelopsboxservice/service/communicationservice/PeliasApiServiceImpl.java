package de.blackforestsolutions.dravelopsboxservice.service.communicationservice;

import de.blackforestsolutions.dravelopsboxservice.service.callbuilderservice.PeliasHttpCallBuilderService;
import de.blackforestsolutions.dravelopsboxservice.service.communicationservice.restcalls.CallService;
import de.blackforestsolutions.dravelopsboxservice.service.mapperservice.PeliasMapperService;
import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.CallStatus;
import de.blackforestsolutions.dravelopsdatamodel.Status;
import de.blackforestsolutions.dravelopsdatamodel.TravelPoint;
import de.blackforestsolutions.dravelopsgeneratedcontent.pelias.PeliasTravelPointResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URL;
import java.util.Optional;

import static de.blackforestsolutions.dravelopsdatamodel.util.DravelOpsHttpCallBuilder.buildUrlWith;

@Slf4j
@Service
public class PeliasApiServiceImpl implements PeliasApiService {

    private static final int EMPTY_RESPONSE_SIZE = 0;

    private final PeliasHttpCallBuilderService peliasHttpCallBuilderService;
    private final PeliasMapperService peliasMapperService;
    private final CallService callService;

    @Autowired
    public PeliasApiServiceImpl(PeliasHttpCallBuilderService peliasHttpCallBuilderService, PeliasMapperService peliasMapperService, CallService callService) {
        this.peliasHttpCallBuilderService = peliasHttpCallBuilderService;
        this.peliasMapperService = peliasMapperService;
        this.callService = callService;
    }

    @Override
    public Flux<CallStatus<TravelPoint>> getAutocompleteAddressesFrom(ApiToken apiToken) {
        try {
            return executeAutocompleteCallWith(apiToken)
                    .onErrorResume(error -> Mono.just(new CallStatus<>(null, Status.FAILED, error)));
        } catch (Exception e) {
            return Flux.just(new CallStatus<>(null, Status.FAILED, e));
        }

    }

    @Override
    public Flux<CallStatus<TravelPoint>> getNearestAddressesFrom(ApiToken apiToken) {
        try {
            return executeReverseCallWith(apiToken)
                    .onErrorResume(error -> Mono.just(new CallStatus<>(null, Status.FAILED, error)));
        } catch (Exception e) {
            return Flux.just(new CallStatus<>(null, Status.FAILED, e));
        }
    }

    private Flux<CallStatus<TravelPoint>> executeAutocompleteCallWith(ApiToken apiToken) {
        return Mono.just(apiToken)
                .map(this::getAutocompleteRequestString)
                .flatMap(url -> callService.getOne(url, HttpHeaders.EMPTY, PeliasTravelPointResponse.class))
                .flatMap(this::handleEmptyAutocompleteResponse)
                .flatMapMany(peliasMapperService::extractTravelPointsFrom);
    }

    private Flux<CallStatus<TravelPoint>> executeReverseCallWith(ApiToken apiToken) {
        return Mono.just(apiToken)
                .map(this::getReverseRequestString)
                .flatMap(url -> callService.getOne(url, HttpHeaders.EMPTY, PeliasTravelPointResponse.class))
                .flatMap(this::handleEmptyReverseResponse)
                .flatMapMany(peliasMapperService::extractTravelPointsFrom);
    }

    private String getAutocompleteRequestString(ApiToken apiToken) {
        ApiToken builder = new ApiToken(apiToken);
        builder.setPath(peliasHttpCallBuilderService.buildPeliasAutocompletePathWith(apiToken));
        URL requestUrl = buildUrlWith(builder);
        return requestUrl.toString();
    }

    private String getReverseRequestString(ApiToken apiToken) {
        ApiToken builder = new ApiToken(apiToken);
        builder.setPath(peliasHttpCallBuilderService.buildPeliasReversePathWith(apiToken));
        URL requestUrl = buildUrlWith(builder);
        return requestUrl.toString();
    }

    private Mono<PeliasTravelPointResponse> handleEmptyAutocompleteResponse(PeliasTravelPointResponse response) {
        if (response.getFeatures().size() == EMPTY_RESPONSE_SIZE) {
            Optional<String> optionalSearchText = Optional.ofNullable(response.getGeocoding().getQuery().getText());
            optionalSearchText.ifPresent(searchText -> log.info("No result found in pelias for searchText: ".concat(searchText)));
            return Mono.empty();
        }
        return Mono.just(response);
    }

    private Mono<PeliasTravelPointResponse> handleEmptyReverseResponse(PeliasTravelPointResponse response) {
        if (response.getFeatures().size() == EMPTY_RESPONSE_SIZE) {
            Optional<Double> optionalLongitude = Optional.ofNullable(response.getGeocoding().getQuery().getPointLon());
            Optional<Double> optionalLatitude = Optional.ofNullable(response.getGeocoding().getQuery().getPointLat());

            if (optionalLongitude.isPresent() && optionalLatitude.isPresent()) {
                logEmptyReverseResponse(optionalLongitude.get(), optionalLatitude.get());
            }
        }
        return Mono.just(response);
    }

    private void logEmptyReverseResponse(double longitude, double latitude) {
        log.info("No result found in pelias for Lon/Lat:"
                .concat(String.valueOf(longitude))
                .concat("/")
                .concat(String.valueOf(latitude))
        );
    }

}
