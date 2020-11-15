package de.blackforestsolutions.dravelopspolygonservice;

import de.blackforestsolutions.dravelopsdatamodel.TravelPoint;
import de.blackforestsolutions.dravelopsdatamodel.util.ApiToken;
import de.blackforestsolutions.dravelopspolygonservice.configuration.TravelPointApiServiceTestConfiguration;
import de.blackforestsolutions.dravelopspolygonservice.service.communicationservice.TravelPointApiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TravelPointApiServiceTestConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TravelPointApiServiceIT {

    @Autowired
    private TravelPointApiService classUnderTest;

    @Autowired
    private ApiToken.ApiTokenBuilder polygonApiToken;

    @Test
    void test_retrieveTravelPointsFromApiService_returns_result() {
        ApiToken testData = polygonApiToken.build();

        Flux<TravelPoint> result = classUnderTest.retrieveTravelPointsFromApiService(testData);

        StepVerifier.create(result)
                .expectNextCount(1L)
                .thenConsumeWhile(travelPoint -> {
                    assertThat(travelPoint.getName()).isNotEmpty();
                    assertThat(travelPoint.getPoint()).isNotNull();
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void test_retrieveTravelPointsFromApiService_with_incorrect_apiToken_return_zero_travelPoints() {
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(polygonApiToken.build());
        testData.setDeparture("Noooooooooooooo expected Result");

        Flux<TravelPoint> result = classUnderTest.retrieveTravelPointsFromApiService(testData.build());

        StepVerifier.create(result)
                .expectNextCount(0L)
                .verifyComplete();
    }
}
