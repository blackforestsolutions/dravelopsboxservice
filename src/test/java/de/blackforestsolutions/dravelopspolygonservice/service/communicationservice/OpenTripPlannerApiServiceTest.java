package de.blackforestsolutions.dravelopspolygonservice.service.communicationservice;

import com.fasterxml.jackson.core.JsonParseException;
import de.blackforestsolutions.dravelopsdatamodel.util.ApiToken;
import de.blackforestsolutions.dravelopspolygonservice.service.communicationservice.restcalls.CallService;
import de.blackforestsolutions.dravelopspolygonservice.service.supportservice.OpenTripPlannerHttpCallBuilderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.geo.Polygon;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static de.blackforestsolutions.dravelopspolygonservice.objectmothers.ApiTokenObjectMother.getOpenTripPlannerApiToken;
import static de.blackforestsolutions.dravelopspolygonservice.objectmothers.PolygonObjectMother.getPolygon;
import static de.blackforestsolutions.dravelopspolygonservice.testutils.TestUtils.getResourceFileAsString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
    void test_extractPolygonBy_apiToken_returns_polygon_correctly() {
        ApiToken testData = getOpenTripPlannerApiToken();
        Polygon expectedPolygon = getPolygon();

        Mono<Polygon> result = classUnderTest.extractPolygonBy(testData);

        StepVerifier.create(result)
                .assertNext(polygon -> {
                    assertThat(polygon.getPoints().size()).isEqualTo(5);
                    assertThat(polygon.getPoints()).containsExactly(
                            expectedPolygon.getPoints().get(0),
                            expectedPolygon.getPoints().get(1),
                            expectedPolygon.getPoints().get(2),
                            expectedPolygon.getPoints().get(3),
                            expectedPolygon.getPoints().get(4)
                    );
                })
                .verifyComplete();
    }

    @Test
    void test_extractPolygonBy_apiToken_with_error_returns_nullPointerException() {
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(getOpenTripPlannerApiToken());
        testData.setHost(null);

        Mono<Polygon> result = classUnderTest.extractPolygonBy(testData.build());

        StepVerifier.create(result)
                .expectError(NullPointerException.class)
                .verify();
    }

    @Test
    void test_extractPolygonBy_apiToken_and_bad_http_request_returns_jsonParseException() {
        ApiToken testData =  getOpenTripPlannerApiToken();
        when(callService.get(anyString(), any(HttpHeaders.class)))
                .thenReturn(Mono.just(new ResponseEntity<>("error", HttpStatus.BAD_REQUEST)));

        Mono<Polygon> result = classUnderTest.extractPolygonBy(testData);

        StepVerifier.create(result)
                .expectError(JsonParseException.class)
                .verify();
    }

}
