package de.blackforestsolutions.dravelopspolygonservice.service.communicationservice;

import com.fasterxml.jackson.core.JsonParseException;
import de.blackforestsolutions.dravelopsdatamodel.CallStatus;
import de.blackforestsolutions.dravelopsdatamodel.Status;
import de.blackforestsolutions.dravelopsdatamodel.TravelPoint;
import de.blackforestsolutions.dravelopsdatamodel.exception.NoExternalResultFoundException;
import de.blackforestsolutions.dravelopsdatamodel.util.ApiToken;
import de.blackforestsolutions.dravelopsgeneratedcontent.pelias.PeliasTravelPointResponse;
import de.blackforestsolutions.dravelopspolygonservice.service.callbuilderservice.PeliasHttpCallBuilderService;
import de.blackforestsolutions.dravelopspolygonservice.service.callbuilderservice.PeliasHttpCallBuilderServiceImpl;
import de.blackforestsolutions.dravelopspolygonservice.service.communicationservice.restcalls.CallService;
import de.blackforestsolutions.dravelopspolygonservice.service.communicationservice.restcalls.CallServiceImpl;
import de.blackforestsolutions.dravelopspolygonservice.service.mapperservice.PeliasMapperService;
import de.blackforestsolutions.dravelopspolygonservice.service.mapperservice.PeliasMapperServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.ApiTokenObjectMother.getPeliasAutocompleteApiToken;
import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.TravelPointObjectMother.getGermanyTravelPoint;
import static de.blackforestsolutions.dravelopsdatamodel.testutil.TestUtils.getResourceFileAsString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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

        when(callService.get(anyString(), any(HttpHeaders.class)))
                .thenReturn(Mono.just(new ResponseEntity<>(getResourceFileAsString("json/peliasResult.json"), HttpStatus.OK)));

        when(peliasMapperService.extractTravelPointsFrom(any(PeliasTravelPointResponse.class)))
                .thenReturn(Flux.just(new CallStatus<>(getGermanyTravelPoint(), Status.SUCCESS, null)));
    }

    @Test
    void test_extractTravelPointsFrom_apiToken_returns_correct_travelPoints() {
        ApiToken testData = getPeliasAutocompleteApiToken();

        Flux<CallStatus<TravelPoint>> result = classUnderTest.extractTravelPointsFrom(testData);

        StepVerifier.create(result)
                .assertNext(travelPoint -> {
                    assertThat(travelPoint.getStatus()).isEqualTo(Status.SUCCESS);
                    assertThat(travelPoint.getThrowable()).isNull();
                    assertThat(travelPoint.getCalledObject()).isEqualToComparingFieldByField(getGermanyTravelPoint());
                })
                .verifyComplete();
    }

    @Test
    void test_extractTravelPointsFrom_apiToken_is_executed_correctly() {
        ApiToken testData = getPeliasAutocompleteApiToken();
        ArgumentCaptor<ApiToken> apiTokenArg = ArgumentCaptor.forClass(ApiToken.class);
        ArgumentCaptor<String> urlArg = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<HttpHeaders> httpHeadersArg = ArgumentCaptor.forClass(HttpHeaders.class);
        ArgumentCaptor<PeliasTravelPointResponse> responseArg = ArgumentCaptor.forClass(PeliasTravelPointResponse.class);

        classUnderTest.extractTravelPointsFrom(testData).collectList().block();

        InOrder inOrder = inOrder(peliasHttpCallBuilderService, peliasMapperService, callService);
        inOrder.verify(peliasHttpCallBuilderService, times(1)).buildPeliasAutocompletePathWith(apiTokenArg.capture());
        inOrder.verify(callService, times(1)).get(urlArg.capture(), httpHeadersArg.capture());
        inOrder.verify(peliasMapperService, times(1)).extractTravelPointsFrom(responseArg.capture());
        inOrder.verifyNoMoreInteractions();
        assertThat(apiTokenArg.getValue()).isEqualToComparingFieldByField(getPeliasAutocompleteApiToken());
        assertThat(urlArg.getValue()).isEqualTo("http://localhost:4000");
        assertThat(httpHeadersArg.getValue()).isEqualTo(HttpHeaders.EMPTY);
        assertThat(responseArg.getValue()).isInstanceOf(PeliasTravelPointResponse.class);
    }

    @Test
    void test_extractTravelPointsFrom_apiToken_with_host_as_null_returns_failed_call_status() {
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(getPeliasAutocompleteApiToken());
        testData.setHost(null);

        Flux<CallStatus<TravelPoint>> result = classUnderTest.extractTravelPointsFrom(testData.build());

        StepVerifier.create(result)
                .assertNext(error -> {
                    assertThat(error.getStatus()).isEqualTo(Status.FAILED);
                    assertThat(error.getCalledObject()).isNull();
                    assertThat(error.getThrowable()).isInstanceOf(NullPointerException.class);
                })
                .verifyComplete();
    }

    @Test
    void test_extractTravelPointsFrom_apiToken_and_failed_call_returns_failed_call_status() {
        ApiToken testData = getPeliasAutocompleteApiToken();
        when(callService.get(anyString(), any(HttpHeaders.class)))
                .thenReturn(Mono.just(new ResponseEntity<>("error", HttpStatus.BAD_REQUEST)));

        Flux<CallStatus<TravelPoint>> result = classUnderTest.extractTravelPointsFrom(testData);

        StepVerifier.create(result)
                .assertNext(error -> {
                    assertThat(error.getStatus()).isEqualTo(Status.FAILED);
                    assertThat(error.getCalledObject()).isNull();
                    assertThat(error.getThrowable()).isInstanceOf(JsonParseException.class);
                })
                .verifyComplete();
    }

    @Test
    void test_extractTravelPointsFrom_apiToken_returns_failed_call_status_when_exception_is_thrown_inside_stream() {
        ApiToken testData = getPeliasAutocompleteApiToken();
        when(callService.get(anyString(), any(HttpHeaders.class)))
                .thenReturn(Mono.just(new ResponseEntity<>(null, HttpStatus.OK)));

        Flux<CallStatus<TravelPoint>> result = classUnderTest.extractTravelPointsFrom(testData);

        StepVerifier.create(result)
                .assertNext(error -> {
                    assertThat(error.getStatus()).isEqualTo(Status.FAILED);
                    assertThat(error.getCalledObject()).isNull();
                    assertThat(error.getThrowable()).isInstanceOf(IllegalArgumentException.class);
                })
                .verifyComplete();
    }

    @Test
    void test_extractTravelPointsFrom_apiToken_as_null_returns_exception_when_exception_is_thrown_outside_of_stream() {

        Flux<CallStatus<TravelPoint>> result = classUnderTest.extractTravelPointsFrom(null);

        StepVerifier.create(result)
                .assertNext(error -> {
                    assertThat(error.getStatus()).isEqualTo(Status.FAILED);
                    assertThat(error.getCalledObject()).isNull();
                    assertThat(error.getThrowable()).isInstanceOf(NullPointerException.class);
                })
                .verifyComplete();
    }

    @Test
    void test_extractTravelPointsFrom_apiToken_returns_failed_call_status_when_exception_is_thrown_by_mapperService() {
        ApiToken testData = getPeliasAutocompleteApiToken();
        when(peliasHttpCallBuilderService.buildPeliasAutocompletePathWith(any(ApiToken.class)))
                .thenThrow(NullPointerException.class);

        Flux<CallStatus<TravelPoint>> result = classUnderTest.extractTravelPointsFrom(testData);

        StepVerifier.create(result)
                .assertNext(error -> {
                    assertThat(error.getStatus()).isEqualTo(Status.FAILED);
                    assertThat(error.getCalledObject()).isNull();
                    assertThat(error.getThrowable()).isInstanceOf(NullPointerException.class);
                })
                .verifyComplete();
    }

    @Test
    void test_extractTravelPointsFrom_apiToken_and_no_result_json_returns_failed_call_status_with_noExternalResultFoundException() {
        ApiToken testData = getPeliasAutocompleteApiToken();
        when(callService.get(anyString(), any(HttpHeaders.class)))
                .thenReturn(Mono.just(new ResponseEntity<>(getResourceFileAsString("json/peliasNoResult.json"), HttpStatus.OK)));

        Flux<CallStatus<TravelPoint>> result = classUnderTest.extractTravelPointsFrom(testData);

        StepVerifier.create(result)
                .assertNext(error -> {
                    assertThat(error.getStatus()).isEqualTo(Status.FAILED);
                    assertThat(error.getCalledObject()).isNull();
                    assertThat(error.getThrowable()).isInstanceOf(NoExternalResultFoundException.class);
                })
                .verifyComplete();
    }

    @Test
    void test_extractTravelPointsFrom_apiToken_and_error_by_callService_returns_failed_call_status() {
        ApiToken testData = getPeliasAutocompleteApiToken();
        when(callService.get(anyString(), any(HttpHeaders.class)))
                .thenReturn(Mono.error(new Exception()));

        Flux<CallStatus<TravelPoint>> result = classUnderTest.extractTravelPointsFrom(testData);

        StepVerifier.create(result)
                .assertNext(error -> {
                    assertThat(error.getStatus()).isEqualTo(Status.FAILED);
                    assertThat(error.getCalledObject()).isNull();
                    assertThat(error.getThrowable()).isInstanceOf(Exception.class);
                })
                .verifyComplete();
    }

    @Test
    void test_extractTravelPointsFrom_apiToken_and_failed_call_status_by_mapper_returns_failed_callStatus() {
        ApiToken testData = getPeliasAutocompleteApiToken();
        when(peliasMapperService.extractTravelPointsFrom(any(PeliasTravelPointResponse.class)))
                .thenReturn(Flux.just(new CallStatus<>(null, Status.FAILED, new Exception())));

        Flux<CallStatus<TravelPoint>> result = classUnderTest.extractTravelPointsFrom(testData);

        StepVerifier.create(result)
                .assertNext(error -> {
                    assertThat(error.getStatus()).isEqualTo(Status.FAILED);
                    assertThat(error.getCalledObject()).isNull();
                    assertThat(error.getThrowable()).isInstanceOf(Exception.class);
                })
                .verifyComplete();
    }

    @Test
    void test_extractTravelPointsFrom_apiToken_and_thrown_exception_by_httpCallBuilder_returns_failed_callStatus() {
        ApiToken testData = getPeliasAutocompleteApiToken();
        when(peliasHttpCallBuilderService.buildPeliasAutocompletePathWith(any(ApiToken.class)))
                .thenThrow(NullPointerException.class);

        Flux<CallStatus<TravelPoint>> result = classUnderTest.extractTravelPointsFrom(testData);

        StepVerifier.create(result)
                .assertNext(error -> {
                    assertThat(error.getStatus()).isEqualTo(Status.FAILED);
                    assertThat(error.getCalledObject()).isNull();
                    assertThat(error.getThrowable()).isInstanceOf(NullPointerException.class);
                })
                .verifyComplete();
    }


}
