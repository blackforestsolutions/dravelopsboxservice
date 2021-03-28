package de.blackforestsolutions.dravelopsboxservice.service.communicationservice;

import de.blackforestsolutions.dravelopsdatamodel.CallStatus;
import de.blackforestsolutions.dravelopsdatamodel.Status;
import de.blackforestsolutions.dravelopsdatamodel.TravelPoint;
import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsgeneratedcontent.pelias.PeliasTravelPointResponse;
import de.blackforestsolutions.dravelopsboxservice.service.callbuilderservice.PeliasHttpCallBuilderService;
import de.blackforestsolutions.dravelopsboxservice.service.callbuilderservice.PeliasHttpCallBuilderServiceImpl;
import de.blackforestsolutions.dravelopsboxservice.service.communicationservice.restcalls.CallService;
import de.blackforestsolutions.dravelopsboxservice.service.communicationservice.restcalls.CallServiceImpl;
import de.blackforestsolutions.dravelopsboxservice.service.mapperservice.PeliasMapperService;
import de.blackforestsolutions.dravelopsboxservice.service.mapperservice.PeliasMapperServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.ApiTokenObjectMother.*;
import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.TravelPointObjectMother.getGermanyTravelPoint;
import static de.blackforestsolutions.dravelopsdatamodel.testutil.TestUtils.retrieveJsonToPojo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class PeliasApiServiceTest {

    private final PeliasHttpCallBuilderService peliasHttpCallBuilderService = mock(PeliasHttpCallBuilderServiceImpl.class);
    private final PeliasMapperService peliasMapperService = mock(PeliasMapperServiceImpl.class);
    private final CallService callService = mock(CallServiceImpl.class);

    private final PeliasApiService classUnderTest = new PeliasApiServiceImpl(peliasHttpCallBuilderService, peliasMapperService, callService);

    @BeforeEach
    void init() {
        when(peliasHttpCallBuilderService.buildPeliasAutocompletePathWith(any(ApiToken.class)))
                .thenReturn("");

        when(peliasHttpCallBuilderService.buildPeliasReversePathWith(any(ApiToken.class)))
                .thenReturn("");

        PeliasTravelPointResponse peliasTravelPointResponse = retrieveJsonToPojo("json/peliasAutocompleteResult.json", PeliasTravelPointResponse.class);
        when(callService.getOne(anyString(), any(HttpHeaders.class), eq(PeliasTravelPointResponse.class)))
                .thenReturn(Mono.just(peliasTravelPointResponse));

        when(peliasMapperService.extractTravelPointsFrom(any(PeliasTravelPointResponse.class)))
                .thenReturn(Flux.just(new CallStatus<>(getGermanyTravelPoint(null), Status.SUCCESS, null)));
    }

    @Test
    void test_getAutocompleteAddressesFrom_apiToken_returns_correct_travelPoints() {
        ApiToken testData = getPeliasAutocompleteApiToken();
        when(peliasMapperService.extractTravelPointsFrom(any(PeliasTravelPointResponse.class)))
                .thenReturn(Flux.just(new CallStatus<>(getGermanyTravelPoint(null), Status.SUCCESS, null)));

        Flux<CallStatus<TravelPoint>> result = classUnderTest.getAutocompleteAddressesFrom(testData);

        StepVerifier.create(result)
                .assertNext(travelPoint -> {
                    assertThat(travelPoint.getStatus()).isEqualTo(Status.SUCCESS);
                    assertThat(travelPoint.getThrowable()).isNull();
                    assertThat(travelPoint.getCalledObject()).isEqualToComparingFieldByFieldRecursively(getGermanyTravelPoint(null));
                })
                .verifyComplete();
    }

    @Test
    void test_getNearestAddressesFrom_apiToken_returns_correct_travelPoints() {
        ApiToken testData = getPeliasNearestAddressesApiToken();
        when(peliasMapperService.extractTravelPointsFrom(any(PeliasTravelPointResponse.class)))
                .thenReturn(Flux.just(new CallStatus<>(getGermanyTravelPoint(new Distance(0.0d, Metrics.KILOMETERS)), Status.SUCCESS, null)));

        Flux<CallStatus<TravelPoint>> result = classUnderTest.getNearestAddressesFrom(testData);

        StepVerifier.create(result)
                .assertNext(travelPoint -> {
                    assertThat(travelPoint.getStatus()).isEqualTo(Status.SUCCESS);
                    assertThat(travelPoint.getThrowable()).isNull();
                    assertThat(travelPoint.getCalledObject()).isEqualToComparingFieldByFieldRecursively(getGermanyTravelPoint(new Distance(0.0d, Metrics.KILOMETERS)));
                })
                .verifyComplete();
    }

    @Test
    void test_getAutocompleteAddressesFrom_apiToken_is_executed_correctly() {
        ApiToken testData = getPeliasAutocompleteApiToken();
        when(peliasMapperService.extractTravelPointsFrom(any(PeliasTravelPointResponse.class)))
                .thenReturn(Flux.just(new CallStatus<>(getGermanyTravelPoint(null), Status.SUCCESS, null)));
        ArgumentCaptor<ApiToken> apiTokenArg = ArgumentCaptor.forClass(ApiToken.class);
        ArgumentCaptor<String> urlArg = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<HttpHeaders> httpHeadersArg = ArgumentCaptor.forClass(HttpHeaders.class);
        ArgumentCaptor<PeliasTravelPointResponse> responseArg = ArgumentCaptor.forClass(PeliasTravelPointResponse.class);

        classUnderTest.getAutocompleteAddressesFrom(testData).collectList().block();

        InOrder inOrder = inOrder(peliasHttpCallBuilderService, peliasMapperService, callService);
        inOrder.verify(peliasHttpCallBuilderService, times(1)).buildPeliasAutocompletePathWith(apiTokenArg.capture());
        inOrder.verify(callService, times(1)).getOne(urlArg.capture(), httpHeadersArg.capture(), eq(PeliasTravelPointResponse.class));
        inOrder.verify(peliasMapperService, times(1)).extractTravelPointsFrom(responseArg.capture());
        inOrder.verifyNoMoreInteractions();
        assertThat(apiTokenArg.getValue()).isEqualToComparingFieldByFieldRecursively(getPeliasAutocompleteApiToken());
        assertThat(urlArg.getValue()).isEqualTo("http://localhost:4000");
        assertThat(httpHeadersArg.getValue()).isEqualTo(HttpHeaders.EMPTY);
        assertThat(responseArg.getValue()).isInstanceOf(PeliasTravelPointResponse.class);
    }

    @Test
    void test_getNearestAddressesFrom_apiToken_is_executed_correctly() {
        ApiToken testData = getPeliasNearestAddressesApiToken();
        when(peliasMapperService.extractTravelPointsFrom(any(PeliasTravelPointResponse.class)))
                .thenReturn(Flux.just(new CallStatus<>(getGermanyTravelPoint(new Distance(0.0d, Metrics.KILOMETERS)), Status.SUCCESS, null)));
        ArgumentCaptor<ApiToken> apiTokenArg = ArgumentCaptor.forClass(ApiToken.class);
        ArgumentCaptor<String> urlArg = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<HttpHeaders> httpHeadersArg = ArgumentCaptor.forClass(HttpHeaders.class);
        ArgumentCaptor<PeliasTravelPointResponse> responseArg = ArgumentCaptor.forClass(PeliasTravelPointResponse.class);

        classUnderTest.getNearestAddressesFrom(testData).collectList().block();

        InOrder inOrder = inOrder(peliasHttpCallBuilderService, peliasMapperService, callService);
        inOrder.verify(peliasHttpCallBuilderService, times(1)).buildPeliasReversePathWith(apiTokenArg.capture());
        inOrder.verify(callService, times(1)).getOne(urlArg.capture(), httpHeadersArg.capture(), eq(PeliasTravelPointResponse.class));
        inOrder.verify(peliasMapperService, times(1)).extractTravelPointsFrom(responseArg.capture());
        inOrder.verifyNoMoreInteractions();
        assertThat(apiTokenArg.getValue()).isEqualToComparingFieldByFieldRecursively(getPeliasNearestAddressesApiToken());
        assertThat(urlArg.getValue()).isEqualTo("http://localhost:4000");
        assertThat(httpHeadersArg.getValue()).isEqualTo(HttpHeaders.EMPTY);
        assertThat(responseArg.getValue()).isInstanceOf(PeliasTravelPointResponse.class);
    }

    @Test
    void test_getAutocompleteAddressesFrom_apiToken_with_host_as_null_returns_failed_call_status() {
        ApiToken testData = new ApiToken(getPeliasAutocompleteApiToken());
        testData.setHost(null);

        Flux<CallStatus<TravelPoint>> result = classUnderTest.getAutocompleteAddressesFrom(testData);

        StepVerifier.create(result)
                .assertNext(error -> {
                    assertThat(error.getStatus()).isEqualTo(Status.FAILED);
                    assertThat(error.getCalledObject()).isNull();
                    assertThat(error.getThrowable()).isInstanceOf(NullPointerException.class);
                })
                .verifyComplete();
    }

    @Test
    void test_getNearestAddressesFrom_apiToken_with_host_as_null_returns_failed_call_status() {
        ApiToken testData = new ApiToken(getPeliasNearestAddressesApiToken());
        testData.setHost(null);

        Flux<CallStatus<TravelPoint>> result = classUnderTest.getNearestAddressesFrom(testData);

        StepVerifier.create(result)
                .assertNext(error -> {
                    assertThat(error.getStatus()).isEqualTo(Status.FAILED);
                    assertThat(error.getCalledObject()).isNull();
                    assertThat(error.getThrowable()).isInstanceOf(NullPointerException.class);
                })
                .verifyComplete();
    }

    @Test
    void test_getAutocompleteAddressesFrom_apiToken_as_null_returns_exception_when_exception_is_thrown_outside_of_stream() {

        Flux<CallStatus<TravelPoint>> result = classUnderTest.getAutocompleteAddressesFrom(null);

        StepVerifier.create(result)
                .assertNext(error -> {
                    assertThat(error.getStatus()).isEqualTo(Status.FAILED);
                    assertThat(error.getCalledObject()).isNull();
                    assertThat(error.getThrowable()).isInstanceOf(NullPointerException.class);
                })
                .verifyComplete();
    }

    @Test
    void test_getNearestAddressesFrom_apiToken_as_null_returns_exception_when_exception_is_thrown_outside_of_stream() {

        Flux<CallStatus<TravelPoint>> result = classUnderTest.getNearestAddressesFrom(null);

        StepVerifier.create(result)
                .assertNext(error -> {
                    assertThat(error.getStatus()).isEqualTo(Status.FAILED);
                    assertThat(error.getCalledObject()).isNull();
                    assertThat(error.getThrowable()).isInstanceOf(NullPointerException.class);
                })
                .verifyComplete();
    }

    @Test
    void test_getAutocompleteAddressesFrom_apiToken_returns_failed_call_status_when_exception_is_thrown_by_mapperService() {
        ApiToken testData = getPeliasAutocompleteApiToken();
        when(peliasHttpCallBuilderService.buildPeliasAutocompletePathWith(any(ApiToken.class)))
                .thenThrow(NullPointerException.class);

        Flux<CallStatus<TravelPoint>> result = classUnderTest.getAutocompleteAddressesFrom(testData);

        StepVerifier.create(result)
                .assertNext(error -> {
                    assertThat(error.getStatus()).isEqualTo(Status.FAILED);
                    assertThat(error.getCalledObject()).isNull();
                    assertThat(error.getThrowable()).isInstanceOf(NullPointerException.class);
                })
                .verifyComplete();
    }

    @Test
    void test_getNearestAddressesFrom_apiToken_returns_failed_call_status_when_exception_is_thrown_by_mapperService() {
        ApiToken testData = getPeliasNearestAddressesApiToken();
        when(peliasHttpCallBuilderService.buildPeliasReversePathWith(any(ApiToken.class)))
                .thenThrow(NullPointerException.class);

        Flux<CallStatus<TravelPoint>> result = classUnderTest.getNearestAddressesFrom(testData);

        StepVerifier.create(result)
                .assertNext(error -> {
                    assertThat(error.getStatus()).isEqualTo(Status.FAILED);
                    assertThat(error.getCalledObject()).isNull();
                    assertThat(error.getThrowable()).isInstanceOf(NullPointerException.class);
                })
                .verifyComplete();
    }

    @Test
    void test_getAutocompleteAddressesFrom_apiToken_and_empty_mapper_result_returns_no_results() {
        ApiToken testData = getPeliasAutocompleteApiToken();
        when(peliasMapperService.extractTravelPointsFrom(any(PeliasTravelPointResponse.class)))
                .thenReturn(Flux.empty());

        Flux<CallStatus<TravelPoint>> result = classUnderTest.getAutocompleteAddressesFrom(testData);

        StepVerifier.create(result)
                .expectNextCount(0L)
                .verifyComplete();
    }

    @Test
    void test_getNearestAddressesFrom_apiToken_and_empty_mapper_result_returns_no_results() {
        ApiToken testData = getPeliasNearestAddressesApiToken();
        when(peliasMapperService.extractTravelPointsFrom(any(PeliasTravelPointResponse.class)))
                .thenReturn(Flux.empty());

        Flux<CallStatus<TravelPoint>> result = classUnderTest.getNearestAddressesFrom(testData);

        StepVerifier.create(result)
                .expectNextCount(0L)
                .verifyComplete();
    }

    @Test
    void test_getAutocompleteAddressesFrom_apiToken_and_error_by_callService_returns_failed_call_status() {
        ApiToken testData = getPeliasAutocompleteApiToken();
        when(callService.getOne(anyString(), any(HttpHeaders.class), eq(PeliasTravelPointResponse.class)))
                .thenReturn(Mono.error(new Exception()));

        Flux<CallStatus<TravelPoint>> result = classUnderTest.getAutocompleteAddressesFrom(testData);

        StepVerifier.create(result)
                .assertNext(error -> {
                    assertThat(error.getStatus()).isEqualTo(Status.FAILED);
                    assertThat(error.getCalledObject()).isNull();
                    assertThat(error.getThrowable()).isInstanceOf(Exception.class);
                })
                .verifyComplete();
    }

    @Test
    void test_getNearestAddressesFrom_apiToken_and_error_by_callService_returns_failed_call_status() {
        ApiToken testData = getPeliasNearestAddressesApiToken();
        when(callService.getOne(anyString(), any(HttpHeaders.class), eq(PeliasTravelPointResponse.class)))
                .thenReturn(Mono.error(new Exception()));

        Flux<CallStatus<TravelPoint>> result = classUnderTest.getNearestAddressesFrom(testData);

        StepVerifier.create(result)
                .assertNext(error -> {
                    assertThat(error.getStatus()).isEqualTo(Status.FAILED);
                    assertThat(error.getCalledObject()).isNull();
                    assertThat(error.getThrowable()).isInstanceOf(Exception.class);
                })
                .verifyComplete();
    }

    @Test
    void test_getAutocompleteAddressesFrom_apiToken_and_failed_call_status_by_mapper_returns_failed_callStatus() {
        ApiToken testData = getPeliasAutocompleteApiToken();
        when(peliasMapperService.extractTravelPointsFrom(any(PeliasTravelPointResponse.class)))
                .thenReturn(Flux.just(new CallStatus<>(null, Status.FAILED, new Exception())));

        Flux<CallStatus<TravelPoint>> result = classUnderTest.getAutocompleteAddressesFrom(testData);

        StepVerifier.create(result)
                .assertNext(error -> {
                    assertThat(error.getStatus()).isEqualTo(Status.FAILED);
                    assertThat(error.getCalledObject()).isNull();
                    assertThat(error.getThrowable()).isInstanceOf(Exception.class);
                })
                .verifyComplete();
    }

    @Test
    void test_getNearestAddressesFrom_apiToken_and_failed_call_status_by_mapper_returns_failed_callStatus() {
        ApiToken testData = getPeliasNearestAddressesApiToken();
        when(peliasMapperService.extractTravelPointsFrom(any(PeliasTravelPointResponse.class)))
                .thenReturn(Flux.just(new CallStatus<>(null, Status.FAILED, new Exception())));

        Flux<CallStatus<TravelPoint>> result = classUnderTest.getNearestAddressesFrom(testData);

        StepVerifier.create(result)
                .assertNext(error -> {
                    assertThat(error.getStatus()).isEqualTo(Status.FAILED);
                    assertThat(error.getCalledObject()).isNull();
                    assertThat(error.getThrowable()).isInstanceOf(Exception.class);
                })
                .verifyComplete();
    }

    @Test
    void test_getAutocompleteAddressesFrom_apiToken_and_thrown_exception_by_httpCallBuilder_returns_failed_callStatus() {
        ApiToken testData = getPeliasAutocompleteApiToken();
        when(peliasHttpCallBuilderService.buildPeliasAutocompletePathWith(any(ApiToken.class)))
                .thenThrow(NullPointerException.class);

        Flux<CallStatus<TravelPoint>> result = classUnderTest.getAutocompleteAddressesFrom(testData);

        StepVerifier.create(result)
                .assertNext(error -> {
                    assertThat(error.getStatus()).isEqualTo(Status.FAILED);
                    assertThat(error.getCalledObject()).isNull();
                    assertThat(error.getThrowable()).isInstanceOf(NullPointerException.class);
                })
                .verifyComplete();
    }

    @Test
    void test_getNearestAddressesFrom_apiToken_and_thrown_exception_by_httpCallBuilder_returns_failed_callStatus() {
        ApiToken testData = getPeliasNearestAddressesApiToken();
        when(peliasHttpCallBuilderService.buildPeliasReversePathWith(any(ApiToken.class)))
                .thenThrow(NullPointerException.class);

        Flux<CallStatus<TravelPoint>> result = classUnderTest.getNearestAddressesFrom(testData);

        StepVerifier.create(result)
                .assertNext(error -> {
                    assertThat(error.getStatus()).isEqualTo(Status.FAILED);
                    assertThat(error.getCalledObject()).isNull();
                    assertThat(error.getThrowable()).isInstanceOf(NullPointerException.class);
                })
                .verifyComplete();
    }

}
