package de.blackforestsolutions.dravelopspolygonservice;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopspolygonservice.service.communicationservice.OpenTripPlannerApiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.geo.Box;
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
    void test_extractBoxBy_returns_polygon() {

        Mono<Box> result = classUnderTest.extractBoxBy(openTripPlannerApiToken);

        StepVerifier.create(result)
                .assertNext(box -> {
                    assertThat(box.getFirst()).isNotNull();
                    assertThat(box.getSecond()).isNotNull();
                })
                .verifyComplete();
    }

}
