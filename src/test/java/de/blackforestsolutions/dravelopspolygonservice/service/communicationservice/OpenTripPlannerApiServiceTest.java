package de.blackforestsolutions.dravelopspolygonservice.service.communicationservice;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.blackforestsolutions.dravelopsdatamodel.util.ApiToken;
import de.blackforestsolutions.dravelopsgeneratedcontent.opentripplanner.polygon.OpenTripPlannerPolygonResponse;
import de.blackforestsolutions.dravelopspolygonservice.service.communicationservice.restcalls.CallService;
import de.blackforestsolutions.dravelopspolygonservice.service.supportservice.OpenTripPlannerHttpCallBuilderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.springframework.data.geo.Box;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.ApiTokenObjectMother.getOpenTripPlannerApiToken;
import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.BoxObjectMother.getVrsBox;
import static de.blackforestsolutions.dravelopsdatamodel.testutil.TestUtils.getResourceFileAsString;
import static de.blackforestsolutions.dravelopsdatamodel.testutil.TestUtils.retrieveJsonToPojo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class OpenTripPlannerApiServiceTest {

    private final OpenTripPlannerHttpCallBuilderService openTripPlannerHttpCallBuilderService = mock(OpenTripPlannerHttpCallBuilderService.class);
    private final CallService callService = mock(CallService.class);

    private final OpenTripPlannerApiService classUnderTest = new OpenTripPlannerApiServiceImpl(callService, openTripPlannerHttpCallBuilderService);

    @BeforeEach
    void init() {
        when(openTripPlannerHttpCallBuilderService.buildOpenTripPlannerPolygonPathWith(any(ApiToken.class)))
                .thenReturn("");

        String json = getResourceFileAsString("json/openTripPlannerPolygonJson.json");
        when(callService.get(anyString(), any(HttpHeaders.class)))
                .thenReturn(Mono.just(new ResponseEntity<>(json, HttpStatus.OK)));
    }

    @Test
    void test_extractBoxBy_apiToken_returns_box_correctly() {
        ApiToken testData = getOpenTripPlannerApiToken();

        Mono<Box> result = classUnderTest.extractBoxBy(testData);

        StepVerifier.create(result)
                .assertNext(box -> assertThat(box).isEqualTo(getVrsBox()))
                .verifyComplete();
    }

    @Test
    void test_extractBoxBy_apiToken_is_executed_correctly() {
        ApiToken testData = getOpenTripPlannerApiToken();
        ArgumentCaptor<ApiToken> apiTokenArg = ArgumentCaptor.forClass(ApiToken.class);
        ArgumentCaptor<String> urlArg = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<HttpHeaders> httpHeadersArg = ArgumentCaptor.forClass(HttpHeaders.class);

        classUnderTest.extractBoxBy(testData).block();

        InOrder inOrder = inOrder(openTripPlannerHttpCallBuilderService, callService);
        inOrder.verify(openTripPlannerHttpCallBuilderService, times(1)).buildOpenTripPlannerPolygonPathWith(apiTokenArg.capture());
        inOrder.verify(callService, times(1)).get(urlArg.capture(), httpHeadersArg.capture());
        inOrder.verifyNoMoreInteractions();
        assertThat(apiTokenArg.getValue()).isEqualToComparingFieldByField(getOpenTripPlannerApiToken());
        assertThat(urlArg.getValue()).isEqualTo("http://localhost:8089");
        assertThat(httpHeadersArg.getValue()).isEqualTo(HttpHeaders.EMPTY);
    }

    @Test
    void test_extractBoxBy_apiToken_with_error_returns_nullPointerException() {
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(getOpenTripPlannerApiToken());
        testData.setHost(null);

        Mono<Box> result = classUnderTest.extractBoxBy(testData.build());

        StepVerifier.create(result)
                .expectError(NullPointerException.class)
                .verify();
    }

    @Test
    void test_extractBoxBy_apiToken_and_bad_http_request_returns_jsonParseException() {
        ApiToken testData = getOpenTripPlannerApiToken();
        when(callService.get(anyString(), any(HttpHeaders.class)))
                .thenReturn(Mono.just(new ResponseEntity<>("error", HttpStatus.BAD_REQUEST)));

        Mono<Box> result = classUnderTest.extractBoxBy(testData);

        StepVerifier.create(result)
                .expectError(JsonParseException.class)
                .verify();
    }

    @Test
    void test_extractBoxBy_apiToken_returns_error_when_coordinate_is_null() throws JsonProcessingException {
        ApiToken testData = getOpenTripPlannerApiToken();
        String polygonJson = getResourceFileAsString("json/openTripPlannerPolygonJson.json");
        OpenTripPlannerPolygonResponse polygonResponse = retrieveJsonToPojo(polygonJson, OpenTripPlannerPolygonResponse.class);
        polygonResponse.setLowerLeftLatitude(null);
        polygonJson = new ObjectMapper().writeValueAsString(polygonResponse);
        when(callService.get(anyString(), any(HttpHeaders.class)))
                .thenReturn(Mono.just(new ResponseEntity<>(polygonJson, HttpStatus.OK)));

        Mono<Box> result = classUnderTest.extractBoxBy(testData);

        StepVerifier.create(result)
                .expectError(NullPointerException.class)
                .verify();
    }

    @Test
    void test_extractBoxBy_apiToken_as_null_returns_error() {

        Mono<Box> result = classUnderTest.extractBoxBy(null);

        StepVerifier.create(result)
                .expectError(NullPointerException.class)
                .verify();
    }

    @Test
    void test_extractBoxBy_apiToken_with_thrown_exception_of_callBuilder_returns_error() {
        ApiToken testData = getOpenTripPlannerApiToken();
        when(openTripPlannerHttpCallBuilderService.buildOpenTripPlannerPolygonPathWith(any(ApiToken.class)))
                .thenThrow(NullPointerException.class);

        Mono<Box> result = classUnderTest.extractBoxBy(testData);

        StepVerifier.create(result)
                .expectError(NullPointerException.class)
                .verify();
    }

    @Test
    void test_extractBoxBy_apiToken_and_with_error_by_callService_returns_error() {
        ApiToken testData = getOpenTripPlannerApiToken();
        when(callService.get(anyString(), any(HttpHeaders.class)))
                .thenReturn(Mono.error(new RuntimeException()));

        Mono<Box> result = classUnderTest.extractBoxBy(testData);

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

    }

}
