package de.blackforestsolutions.dravelopspolygonservice.exceptionhandling;

import de.blackforestsolutions.dravelopsdatamodel.CallStatus;
import reactor.core.publisher.Mono;

public interface ExceptionHandlerService {
    <T> Mono<T> handleExceptions(Throwable exception);

    <T> Mono<T> handleException(Throwable exception);

    <T> Mono<T> handleExceptions(CallStatus<T> callStatus);
}
