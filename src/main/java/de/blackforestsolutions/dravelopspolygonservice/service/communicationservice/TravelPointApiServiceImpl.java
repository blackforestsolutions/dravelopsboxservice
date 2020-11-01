package de.blackforestsolutions.dravelopspolygonservice.service.communicationservice;

import de.blackforestsolutions.dravelopsdatamodel.util.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.util.DravelOpsJsonMapper;
import de.blackforestsolutions.dravelopspolygonservice.exceptionhandling.ExceptionHandlerService;
import de.blackforestsolutions.dravelopspolygonservice.service.supportservice.RequestTokenHandlerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Slf4j
@Service
public class TravelPointApiServiceImpl implements TravelPointApiService {

    private final DravelOpsJsonMapper dravelOpsJsonMapper = new DravelOpsJsonMapper();
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
    public Flux<String> retrieveTravelPointsFromApiService(String userRequestToken) {
        return Mono.just(userRequestToken)
                .flatMap(dravelOpsJsonMapper::mapJsonToApiToken)
                .map(userToken -> requestTokenHandlerService.getRequestApiTokenWith(userToken, peliasApiToken))
                .flatMapMany(peliasApiService::extractTravelPointsFrom)
                .flatMap(exceptionHandlerService::handleExceptions)
                .flatMap(dravelOpsJsonMapper::map)
                .onErrorResume(exceptionHandlerService::handleExceptions);
    }
}
