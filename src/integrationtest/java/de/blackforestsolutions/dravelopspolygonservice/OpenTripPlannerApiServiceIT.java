package de.blackforestsolutions.dravelopspolygonservice;

import de.blackforestsolutions.dravelopsdatamodel.util.ApiToken;
import de.blackforestsolutions.dravelopspolygonservice.service.communicationservice.OpenTripPlannerApiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.geo.Polygon;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OpenTripPlannerApiServiceIT {

    @Autowired
    private OpenTripPlannerApiService classUnderTest;

    @Autowired
    private ApiToken openTripPlannerApiToken;

    @Test
    void test_extractPolygonBy_returns_polygon() {

        Mono<Polygon> result = classUnderTest.extractPolygonBy(openTripPlannerApiToken);

        StepVerifier.create(result)
                .assertNext(polygon -> assertThat(polygon.getPoints().size()).isGreaterThan(1))
                .verifyComplete();
    }

}
