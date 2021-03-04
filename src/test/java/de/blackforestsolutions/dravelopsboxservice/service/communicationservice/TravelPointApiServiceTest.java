package de.blackforestsolutions.dravelopsboxservice.service.communicationservice;

import de.blackforestsolutions.dravelopsboxservice.exceptionhandling.ExceptionHandlerService;
import de.blackforestsolutions.dravelopsboxservice.exceptionhandling.ExceptionHandlerServiceImpl;
import de.blackforestsolutions.dravelopsboxservice.service.supportservice.RequestTokenHandlerService;
import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.CallStatus;
import de.blackforestsolutions.dravelopsdatamodel.Status;
import de.blackforestsolutions.dravelopsdatamodel.TravelPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.ApiTokenObjectMother.*;
import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.TravelPointObjectMother.getGermanyTravelPoint;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class TravelPointApiServiceTest {

    private final ExceptionHandlerService exceptionHandlerService = spy(ExceptionHandlerServiceImpl.class);
    private final RequestTokenHandlerService requestTokenHandlerService = spy(RequestTokenHandlerService.class);
    private final ApiToken peliasApiToken = getConfiguredPeliasApiToken();
    private final PeliasApiService peliasApiService = mock(PeliasApiServiceImpl.class);

    private final TravelPointApiService classUnderTest = new TravelPointApiServiceImpl(requestTokenHandlerService, exceptionHandlerService, peliasApiToken, peliasApiService);

    @BeforeEach
    void init() {
        when(requestTokenHandlerService.getAutocompleteApiTokenWith(any(ApiToken.class), any(ApiToken.class)))
                .thenReturn(getPeliasAutocompleteApiToken());
        when(peliasApiService.getAutocompleteAddressesFrom(any(ApiToken.class))).thenReturn(Flux.just(
                new CallStatus<>(getGermanyTravelPoint(null), Status.SUCCESS, null),
                new CallStatus<>(null, Status.FAILED, new Exception())
        ));

        when(requestTokenHandlerService.getNearestAddressesApiTokenWith(any(ApiToken.class), any(ApiToken.class)))
                .thenReturn(getPeliasNearestAddressesApiToken());
        when(peliasApiService.getNearestAddressesFrom(any(ApiToken.class))).thenReturn(Flux.just(
                new CallStatus<>(getGermanyTravelPoint(new Distance(0.0d, Metrics.KILOMETERS)), Status.SUCCESS, null),
                new CallStatus<>(null, Status.FAILED, new Exception())
        ));
    }

    @Test
    void test_retrieveAutocompleteAddressesFromApiService_with_autocompleteToken_requestTokenHandler_exceptionHandler_and_apiService_returns_travelPoints() {
        ApiToken testData = getAutocompleteBoxServiceApiToken();

        Flux<TravelPoint> result = classUnderTest.retrieveAutocompleteAddressesFromApiService(testData);

        StepVerifier.create(result)
                .assertNext(travelPoint -> assertThat(travelPoint).isEqualToComparingFieldByFieldRecursively(getGermanyTravelPoint(null)))
                .verifyComplete();
    }

    @Test
    void test_retrieveNearestAddressesFromApiService_with_addressToken_requestTokenHandler_exceptionHandler_and_apiService_returns_travelPoints() {
        ApiToken testData = getNearestAddressesBoxServiceApiToken();

        Flux<TravelPoint> result = classUnderTest.retrieveNearestAddressesFromApiService(testData);

        StepVerifier.create(result)
                .assertNext(travelPoint -> assertThat(travelPoint).isEqualToComparingFieldByFieldRecursively(getGermanyTravelPoint(new Distance(0.0d, Metrics.KILOMETERS))))
                .verifyComplete();
    }

    @Test
    void test_retrieveAutocompleteAddressesFromApiService_with_autocompleteToken_requestTokenHandler_exceptionHandler_and_apiService_is_executed_correctly() {
        ArgumentCaptor<ApiToken> boxServiceTokenArg = ArgumentCaptor.forClass(ApiToken.class);
        ArgumentCaptor<ApiToken> configuredTokenArg = ArgumentCaptor.forClass(ApiToken.class);
        ArgumentCaptor<ApiToken> mergedTokenArg = ArgumentCaptor.forClass(ApiToken.class);
        ArgumentCaptor<CallStatus<TravelPoint>> callStatusArg = ArgumentCaptor.forClass(CallStatus.class);
        ApiToken testData = getAutocompleteBoxServiceApiToken();

        classUnderTest.retrieveAutocompleteAddressesFromApiService(testData).collectList().block();

        InOrder inOrder = inOrder(requestTokenHandlerService, peliasApiService, exceptionHandlerService);
        inOrder.verify(requestTokenHandlerService, times(1)).getAutocompleteApiTokenWith(boxServiceTokenArg.capture(), configuredTokenArg.capture());
        inOrder.verify(peliasApiService, times(1)).getAutocompleteAddressesFrom(mergedTokenArg.capture());
        inOrder.verify(exceptionHandlerService, times(2)).handleExceptions(callStatusArg.capture());
        inOrder.verifyNoMoreInteractions();
        assertThat(boxServiceTokenArg.getValue()).isEqualToComparingFieldByField(getAutocompleteBoxServiceApiToken());
        assertThat(configuredTokenArg.getValue()).isEqualToComparingFieldByField(getConfiguredPeliasApiToken());
        assertThat(mergedTokenArg.getValue()).isEqualToComparingFieldByFieldRecursively(getPeliasAutocompleteApiToken());
        assertThat(callStatusArg.getAllValues().size()).isEqualTo(2);
        assertThat(callStatusArg.getAllValues().get(0).getStatus()).isEqualTo(Status.SUCCESS);
        assertThat(callStatusArg.getAllValues().get(0).getThrowable()).isNull();
        assertThat(callStatusArg.getAllValues().get(0).getCalledObject()).isInstanceOf(TravelPoint.class);
        assertThat(callStatusArg.getAllValues().get(1).getStatus()).isEqualTo(Status.FAILED);
        assertThat(callStatusArg.getAllValues().get(1).getCalledObject()).isNull();
        assertThat(callStatusArg.getAllValues().get(1).getThrowable()).isInstanceOf(Exception.class);
    }

    @Test
    void test_retrieveAutocompleteAddressesFromApiService_with_addressToken_requestTokenHandler_exceptionHandler_and_apiService_is_executed_correctly() {
        ArgumentCaptor<ApiToken> boxServiceTokenArg = ArgumentCaptor.forClass(ApiToken.class);
        ArgumentCaptor<ApiToken> configuredTokenArg = ArgumentCaptor.forClass(ApiToken.class);
        ArgumentCaptor<ApiToken> mergedTokenArg = ArgumentCaptor.forClass(ApiToken.class);
        ArgumentCaptor<CallStatus<TravelPoint>> callStatusArg = ArgumentCaptor.forClass(CallStatus.class);
        ApiToken testData = getNearestAddressesBoxServiceApiToken();

        classUnderTest.retrieveNearestAddressesFromApiService(testData).collectList().block();

        InOrder inOrder = inOrder(requestTokenHandlerService, peliasApiService, exceptionHandlerService);
        inOrder.verify(requestTokenHandlerService, times(1)).getNearestAddressesApiTokenWith(boxServiceTokenArg.capture(), configuredTokenArg.capture());
        inOrder.verify(peliasApiService, times(1)).getNearestAddressesFrom(mergedTokenArg.capture());
        inOrder.verify(exceptionHandlerService, times(2)).handleExceptions(callStatusArg.capture());
        inOrder.verifyNoMoreInteractions();
        assertThat(boxServiceTokenArg.getValue()).isEqualToComparingFieldByField(getNearestAddressesBoxServiceApiToken());
        assertThat(configuredTokenArg.getValue()).isEqualToComparingFieldByField(getConfiguredPeliasApiToken());
        assertThat(mergedTokenArg.getValue()).isEqualToComparingFieldByFieldRecursively(getPeliasNearestAddressesApiToken());
        assertThat(callStatusArg.getAllValues().size()).isEqualTo(2);
        assertThat(callStatusArg.getAllValues().get(0).getStatus()).isEqualTo(Status.SUCCESS);
        assertThat(callStatusArg.getAllValues().get(0).getThrowable()).isNull();
        assertThat(callStatusArg.getAllValues().get(0).getCalledObject()).isInstanceOf(TravelPoint.class);
        assertThat(callStatusArg.getAllValues().get(1).getStatus()).isEqualTo(Status.FAILED);
        assertThat(callStatusArg.getAllValues().get(1).getCalledObject()).isNull();
        assertThat(callStatusArg.getAllValues().get(1).getThrowable()).isInstanceOf(Exception.class);
    }

    @Test
    void test_retrieveAutocompleteAddressesFromApiService_with_autocompleteToken_and_thrown_exception_returns_zero_travelPoints() {
        ApiToken testData = getAutocompleteBoxServiceApiToken();
        when(requestTokenHandlerService.getAutocompleteApiTokenWith(any(ApiToken.class), any(ApiToken.class)))
                .thenThrow(new NullPointerException());

        Flux<TravelPoint> result = classUnderTest.retrieveAutocompleteAddressesFromApiService(testData);

        StepVerifier.create(result)
                .expectNextCount(0L)
                .verifyComplete();
        verify(exceptionHandlerService, times(1)).handleExceptions(any(Throwable.class));
    }

    @Test
    void test_retrieveNearestAddressesFromApiService_with_addressToken_and_thrown_exception_returns_zero_travelPoints() {
        ApiToken testData = getNearestAddressesBoxServiceApiToken();
        when(requestTokenHandlerService.getNearestAddressesApiTokenWith(any(ApiToken.class), any(ApiToken.class)))
                .thenThrow(new NullPointerException());

        Flux<TravelPoint> result = classUnderTest.retrieveNearestAddressesFromApiService(testData);

        StepVerifier.create(result)
                .expectNextCount(0L)
                .verifyComplete();
        verify(exceptionHandlerService, times(1)).handleExceptions(any(Throwable.class));
    }

    @Test
    void test_retrieveAutocompleteAddressesFromApiService_with_autocompleteToken_and_error_returns_zero_travelPoints_when_apiService_failed() {
        ApiToken testData = getAutocompleteBoxServiceApiToken();
        when(peliasApiService.getAutocompleteAddressesFrom(any(ApiToken.class)))
                .thenReturn(Flux.error(new Exception()));

        Flux<TravelPoint> result = classUnderTest.retrieveAutocompleteAddressesFromApiService(testData);

        StepVerifier.create(result)
                .expectNextCount(0L)
                .verifyComplete();
        verify(exceptionHandlerService, times(1)).handleExceptions(any(Throwable.class));
    }

    @Test
    void test_retrieveNearestAddressesFromApiService_with_addressToken_and_error_returns_zero_travelPoints_when_apiService_failed() {
        ApiToken testData = getNearestAddressesBoxServiceApiToken();
        when(peliasApiService.getNearestAddressesFrom(any(ApiToken.class)))
                .thenReturn(Flux.error(new Exception()));

        Flux<TravelPoint> result = classUnderTest.retrieveNearestAddressesFromApiService(testData);

        StepVerifier.create(result)
                .expectNextCount(0L)
                .verifyComplete();
        verify(exceptionHandlerService, times(1)).handleExceptions(any(Throwable.class));
    }

    @Test
    void test_retrieveAutocompleteAddressesFromApiService_with_autocompleteToken_and_error_call_status_returns_zero_travelPoints_when_apiService_failed() {
        ApiToken testData = getAutocompleteBoxServiceApiToken();
        when(peliasApiService.getAutocompleteAddressesFrom(any(ApiToken.class)))
                .thenReturn(Flux.just(new CallStatus<>(null, Status.FAILED, new Exception())));

        Flux<TravelPoint> result = classUnderTest.retrieveAutocompleteAddressesFromApiService(testData);

        StepVerifier.create(result)
                .expectNextCount(0L)
                .verifyComplete();
        verify(exceptionHandlerService, times(1)).handleExceptions(any(CallStatus.class));
    }

    @Test
    void test_retrieveNearestAddressesFromApiService_with_addressToken_and_error_call_status_returns_zero_travelPoints_when_apiService_failed() {
        ApiToken testData = getNearestAddressesBoxServiceApiToken();
        when(peliasApiService.getNearestAddressesFrom(any(ApiToken.class)))
                .thenReturn(Flux.just(new CallStatus<>(null, Status.FAILED, new Exception())));

        Flux<TravelPoint> result = classUnderTest.retrieveNearestAddressesFromApiService(testData);

        StepVerifier.create(result)
                .expectNextCount(0L)
                .verifyComplete();
        verify(exceptionHandlerService, times(1)).handleExceptions(any(CallStatus.class));
    }

}
