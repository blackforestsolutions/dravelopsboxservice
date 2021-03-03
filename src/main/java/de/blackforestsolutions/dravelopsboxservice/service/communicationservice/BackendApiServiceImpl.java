package de.blackforestsolutions.dravelopsboxservice.service.communicationservice;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsboxservice.exceptionhandling.ExceptionHandlerService;
import de.blackforestsolutions.dravelopsboxservice.service.communicationservice.restcalls.CallService;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.net.URL;

import static de.blackforestsolutions.dravelopsdatamodel.util.DravelOpsHttpCallBuilder.buildUrlWith;

@Service
public class BackendApiServiceImpl implements BackendApiService {

    private final CallService callService;
    private final ExceptionHandlerService exceptionHandlerService;

    public BackendApiServiceImpl(CallService callService, ExceptionHandlerService exceptionHandlerService) {
        this.callService = callService;
        this.exceptionHandlerService = exceptionHandlerService;
    }

    @Override
    public <T> Mono<T> getOneBy(ApiToken serviceApiToken, Class<T> returnType) {
        try {
            return executeRequestWithOneResult(serviceApiToken, returnType)
                    .onErrorResume(exceptionHandlerService::handleException);
        } catch (Exception e) {
            return exceptionHandlerService.handleException(e);
        }
    }

    private <T> Mono<T> executeRequestWithOneResult(ApiToken serviceApiToken, Class<T> returnType) {
        return Mono.just(serviceApiToken)
                .flatMap(this::getRequestString)
                .flatMap(url -> callService.getOneReactive(url, HttpHeaders.EMPTY, returnType));
    }

    private Mono<String> getRequestString(ApiToken apiToken) {
        URL requestUrl = buildUrlWith(apiToken);
        return Mono.just(requestUrl.toString());
    }
}
