package de.blackforestsolutions.dravelopspolygonservice.service.communicationservice;

import de.blackforestsolutions.dravelopsdatamodel.util.ApiToken;
import de.blackforestsolutions.dravelopspolygonservice.objectmothers.ApiTokenObjectMother;
import de.blackforestsolutions.dravelopspolygonservice.objectmothers.PolygonObjectMother;
import de.blackforestsolutions.dravelopspolygonservice.service.communicationservice.restcalls.CallService;
import de.blackforestsolutions.dravelopspolygonservice.service.supportservice.OpenTripPlannerHttpCallBuilderService;
import de.blackforestsolutions.dravelopspolygonservice.testutils.TestUtils;
import org.assertj.core.api.Assertions;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.geo.Point;
import org.springframework.data.geo.Polygon;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static de.blackforestsolutions.dravelopspolygonservice.objectmothers.ApiTokenObjectMother.getOpenTripPlannerApiToken;
import static de.blackforestsolutions.dravelopspolygonservice.testutils.TestUtils.getResourceFileAsString;
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
    void test_() {
        ApiToken testData = getOpenTripPlannerApiToken();
        Polygon expectedPolygon = PolygonObjectMother.getPolygon();

        Mono<Polygon> result = classUnderTest.extractPolygon(testData);

        StepVerifier.create(result)
                .assertNext(polygon -> {
                    Assertions.assertThat(polygon.getPoints().size()).isEqualTo(12);
                    Assertions.assertThat(polygon.getPoints()).containsExactly(
                            new Point(6.651751d, 49.75588d),
                            new Point(5.9561251d, 50.80702410000001d),
                            new Point(5.886104100000001d, 50.979022900000004d),
                            new Point(5.866844700000001d, 51.029371100000006d),
                            new Point(5.863574300000001d, 51.045629100000006d),
                            new Point(5.8629671000000005d, 51.0558945d),
                            new Point(6.254944d, 51.833795d),
                            new Point(7.43572d, 52.276498d),
                            new Point(8.934161d, 52.290127d),
                            new Point(8.661455d, 50.579053d),
                            new Point(8.258708d, 50.001111d),
                            new Point(8.016066d, 49.961436d),
                            new Point(6.651751d, 49.75588d)
                    );
                })
                .verifyComplete();
    }
}
