package de.blackforestsolutions.dravelopsboxservice.service.communicationservice;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.CallStatus;
import de.blackforestsolutions.dravelopsdatamodel.Status;
import de.blackforestsolutions.dravelopsdatamodel.TravelPoint;
import de.blackforestsolutions.dravelopsgeneratedcontent.pelias.PeliasTravelPointResponse;
import de.blackforestsolutions.dravelopsboxservice.service.callbuilderservice.PeliasHttpCallBuilderService;
import de.blackforestsolutions.dravelopsboxservice.service.communicationservice.restcalls.CallService;
import de.blackforestsolutions.dravelopsboxservice.service.mapperservice.PeliasMapperService;
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
    public Flux<CallStatus<TravelPoint>> extractTravelPointsFrom(ApiToken apiToken) {
        try {
            return executeAutocompleteCallWith(apiToken)
                    .onErrorResume(error -> Mono.just(new CallStatus<>(null, Status.FAILED, error)));
        } catch (Exception e) {
            return Flux.just(new CallStatus<>(null, Status.FAILED, e));
        }

    }

    public Mono<CallStatus<TravelPoint>> extractTravelPointFrom(ApiToken apiToken) {
//        try {
//            return executeAutocompleteCallWith(apiToken)
//                    .doOnNext(response -> response.getFeatures().sort(Comparator.comparing(feature -> feature.getProperties().getConfidence()).reversed()))
//                    .flatMapMany(peliasMapperService::extractTravelPointsFrom)
//                    .onErrorResume(error -> Mono.just(new CallStatus<>(null, Status.FAILED, error)))
//                    .take(1);
//        } catch (Exception e) {
//            return Mono.just(new CallStatus<>(null, Status.FAILED, e));
//        }
    }

//    private Mono<PeliasTravelPointResponse> ex

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
                .flatMap()
    }

    private String getAutocompleteRequestString(ApiToken apiToken) {
        ApiToken.ApiTokenBuilder builder = new ApiToken.ApiTokenBuilder(apiToken);
        builder.setPath(peliasHttpCallBuilderService.buildPeliasAutocompletePathWith(apiToken));
        URL requestUrl = buildUrlWith(builder.build());
        return requestUrl.toString();
    }

    private String getReverseRequestString(ApiToken apiToken) {
        ApiToken.ApiTokenBuilder builder = new ApiToken.ApiTokenBuilder(apiToken);
        builder.setPath(peliasHttpCallBuilderService.buildPeliasReversePathWith(apiToken));
        URL requestUrl = buildUrlWith(builder.build());
        return requestUrl.toString();
    }

    private Mono<PeliasTravelPointResponse> handleEmptyAutocompleteResponse(PeliasTravelPointResponse response) {
        if (response.getFeatures().size() == 0) {
            Optional<String> optionalSearchText = Optional.ofNullable(response.getGeocoding().getQuery().getText());
            optionalSearchText.ifPresent(searchText -> log.info("No result found in pelias for searchText: ".concat(searchText)));
            return Mono.empty();
        }
        return Mono.just(response);
    }

    private Mono<PeliasTravelPointResponse> handleEmptyCoordinateResponse(PeliasTravelPointResponse response) {
        if (response.getFeatures().size() == 0) {
            Optional<String> optionalLongitude = Optional.ofNullable()
        }
    }

}
