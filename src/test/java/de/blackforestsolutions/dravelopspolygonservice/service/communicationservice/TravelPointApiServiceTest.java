package de.blackforestsolutions.dravelopspolygonservice.service.communicationservice;

import de.blackforestsolutions.dravelopsdatamodel.CallStatus;
import de.blackforestsolutions.dravelopsdatamodel.Status;
import de.blackforestsolutions.dravelopsdatamodel.TravelPoint;
import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopspolygonservice.exceptionhandling.ExceptionHandlerService;
import de.blackforestsolutions.dravelopspolygonservice.exceptionhandling.ExceptionHandlerServiceImpl;
import de.blackforestsolutions.dravelopspolygonservice.service.supportservice.RequestTokenHandlerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.ApiTokenObjectMother.*;
import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.TravelPointObjectMother.getGermanyTravelPoint;
import static de.blackforestsolutions.dravelopsdatamodel.testutil.TestUtils.toJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TravelPointApiServiceTest {

    private final ExceptionHandlerService exceptionHandlerService = spy(ExceptionHandlerServiceImpl.class);
    private final RequestTokenHandlerService requestTokenHandlerService = spy(RequestTokenHandlerService.class);
    private final ApiToken peliasApiToken = getConfiguredPeliasAutocompleteApiToken();
    private final PeliasApiService peliasApiService = mock(PeliasApiServiceImpl.class);

    private final TravelPointApiService classUnderTest = new TravelPointApiServiceImpl(requestTokenHandlerService, exceptionHandlerService, peliasApiToken, peliasApiService);

    @BeforeEach
    void init() {
        when(requestTokenHandlerService.getRequestApiTokenWith(any(ApiToken.class), any(ApiToken.class)))
                .thenReturn(getPeliasAutocompleteApiToken());

        when(peliasApiService.extractTravelPointsFrom(any(ApiToken.class))).thenReturn(Flux.just(
                new CallStatus<>(getGermanyTravelPoint(), Status.SUCCESS, null),
                new CallStatus<>(null, Status.FAILED, new Exception())
        ));
    }

    @Test
    void test_retrieveTravelPointsFromApiService_with_polygonToken_requestTokenHandler_exceptionHandler_and_apiService_returns_json_travelPoints() {
        ApiToken testData = getPolygonApiToken();

        Flux<TravelPoint> result = classUnderTest.retrieveTravelPointsFromApiService(testData);

        StepVerifier.create(result)
                .assertNext(travelPoint -> assertThat(toJson(travelPoint)).isEqualTo(toJson(getGermanyTravelPoint())))
                .verifyComplete();
    }

    @Test
    void test_retrieveTravelPointsFromApiService_with_polygonToken_requestTokenHandler_exceptionHandler_and_apiService_is_executed_correctly() {
        ArgumentCaptor<ApiToken> polygonTokenArg = ArgumentCaptor.forClass(ApiToken.class);
        ArgumentCaptor<ApiToken> configuredTokenArg = ArgumentCaptor.forClass(ApiToken.class);
        ArgumentCaptor<ApiToken> mergedTokenArg = ArgumentCaptor.forClass(ApiToken.class);
        ArgumentCaptor<CallStatus<TravelPoint>> callStatusArg = ArgumentCaptor.forClass(CallStatus.class);
        ApiToken polygonTestToken = getPolygonApiToken();

        classUnderTest.retrieveTravelPointsFromApiService(polygonTestToken).collectList().block();

        InOrder inOrder = inOrder(requestTokenHandlerService, peliasApiService, exceptionHandlerService);
        inOrder.verify(requestTokenHandlerService, times(1)).getRequestApiTokenWith(polygonTokenArg.capture(), configuredTokenArg.capture());
        inOrder.verify(peliasApiService, times(1)).extractTravelPointsFrom(mergedTokenArg.capture());
        inOrder.verify(exceptionHandlerService, times(2)).handleExceptions(callStatusArg.capture());
        inOrder.verifyNoMoreInteractions();
        assertThat(polygonTokenArg.getValue()).isEqualToComparingFieldByField(getPolygonApiToken());
        assertThat(configuredTokenArg.getValue()).isEqualToComparingFieldByField(getConfiguredPeliasAutocompleteApiToken());
        assertThat(mergedTokenArg.getValue()).isEqualToComparingFieldByField(getPeliasAutocompleteApiToken());
        assertThat(callStatusArg.getAllValues().size()).isEqualTo(2);
        assertThat(callStatusArg.getAllValues().get(0).getStatus()).isEqualTo(Status.SUCCESS);
        assertThat(callStatusArg.getAllValues().get(0).getThrowable()).isNull();
        assertThat(callStatusArg.getAllValues().get(0).getCalledObject()).isInstanceOf(TravelPoint.class);
        assertThat(callStatusArg.getAllValues().get(1).getStatus()).isEqualTo(Status.FAILED);
        assertThat(callStatusArg.getAllValues().get(1).getCalledObject()).isNull();
        assertThat(callStatusArg.getAllValues().get(1).getThrowable()).isInstanceOf(Exception.class);
    }

    @Test
    void test_retrieveTravelPointsFromApiService_with_polygonToken_and_thrown_exception_returns_zero_travelPoints() {
        ApiToken polygonTestToken = getPolygonApiToken();
        when(requestTokenHandlerService.getRequestApiTokenWith(any(ApiToken.class), any(ApiToken.class)))
                .thenThrow(new NullPointerException());

        Flux<TravelPoint> result = classUnderTest.retrieveTravelPointsFromApiService(polygonTestToken);

        StepVerifier.create(result)
                .expectNextCount(0L)
                .verifyComplete();
        verify(exceptionHandlerService, times(1)).handleExceptions(any(Throwable.class));
    }

    @Test
    void test_retrieveTravelPointsFromApiService_with_polygonToken_and_error_returns_zero_travelPoints_when_apiService_failed() {
        ApiToken polygonTestToken = getPolygonApiToken();
        when(peliasApiService.extractTravelPointsFrom(any(ApiToken.class)))
                .thenReturn(Flux.error(new Exception()));

        Flux<TravelPoint> result = classUnderTest.retrieveTravelPointsFromApiService(polygonTestToken);

        StepVerifier.create(result)
                .expectNextCount(0L)
                .verifyComplete();
        verify(exceptionHandlerService, times(1)).handleExceptions(any(Throwable.class));
    }

    @Test
    void test_retrieveTravelPointsFromApiService_with_polygonToken_and_error_call_status_returns_zero_travelPoints_when_apiService_failed() {
        ApiToken polygonTestToken = getPolygonApiToken();
        when(peliasApiService.extractTravelPointsFrom(any(ApiToken.class)))
                .thenReturn(Flux.just(new CallStatus<>(null, Status.FAILED, new Exception())));

        Flux<TravelPoint> result = classUnderTest.retrieveTravelPointsFromApiService(polygonTestToken);

        StepVerifier.create(result)
                .expectNextCount(0L)
                .verifyComplete();
        verify(exceptionHandlerService, times(1)).handleExceptions(any(CallStatus.class));
    }

}
