package de.blackforestsolutions.dravelopspolygonservice.service.communicationservice;

import de.blackforestsolutions.dravelopsdatamodel.CallStatus;
import de.blackforestsolutions.dravelopsdatamodel.Status;
import de.blackforestsolutions.dravelopsdatamodel.TravelPoint;
import de.blackforestsolutions.dravelopsdatamodel.exception.NoExternalResultFoundException;
import de.blackforestsolutions.dravelopsdatamodel.util.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.util.DravelOpsJsonMapper;
import de.blackforestsolutions.dravelopsgeneratedcontent.pelias.PeliasTravelPointResponse;
import de.blackforestsolutions.dravelopspolygonservice.service.callbuilderservice.PeliasHttpCallBuilderService;
import de.blackforestsolutions.dravelopspolygonservice.service.communicationservice.restcalls.CallService;
import de.blackforestsolutions.dravelopspolygonservice.service.mapperservice.PeliasMapperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URL;

import static de.blackforestsolutions.dravelopsdatamodel.util.DravelOpsHttpCallBuilder.buildUrlWith;

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
            return executeCallWith(apiToken)
                    .onErrorResume(error -> Mono.just(new CallStatus<>(null, Status.FAILED, error)));
        } catch (Exception e) {
            return Flux.just(new CallStatus<>(null, Status.FAILED, e));
        }

    }

    private Flux<CallStatus<TravelPoint>> executeCallWith(ApiToken apiToken) {
        return Mono.just(apiToken)
                .map(this::getRequestStringFrom)
                .flatMap(url -> callService.get(url, HttpHeaders.EMPTY))
                .flatMap(httpResponse -> convertToPojo(httpResponse.getBody()))
                .flatMap(this::handleEmptyResponse)
                .flatMapMany(peliasMapperService::extractTravelPointsFrom);
    }

    private String getRequestStringFrom(ApiToken apiToken) {
        ApiToken.ApiTokenBuilder builder = new ApiToken.ApiTokenBuilder(apiToken);
        builder.setPath(peliasHttpCallBuilderService.buildPeliasAutocompletePathWith(apiToken));
        URL requestUrl = buildUrlWith(builder.build());
        return requestUrl.toString();
    }

    private Mono<PeliasTravelPointResponse> convertToPojo(String json) {
        DravelOpsJsonMapper mapper = new DravelOpsJsonMapper();
        return mapper.mapJsonToPojo(json, PeliasTravelPointResponse.class);
    }

    private Mono<PeliasTravelPointResponse> handleEmptyResponse(PeliasTravelPointResponse response) {
        if (response.getFeatures().size() == 0) {
            return Mono.error(new NoExternalResultFoundException());
        }
        return Mono.just(response);
    }

}