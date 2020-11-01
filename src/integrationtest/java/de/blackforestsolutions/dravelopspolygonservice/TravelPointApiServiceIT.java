package de.blackforestsolutions.dravelopspolygonservice;

import de.blackforestsolutions.dravelopsdatamodel.TravelPoint;
import de.blackforestsolutions.dravelopsdatamodel.util.ApiToken;
import de.blackforestsolutions.dravelopspolygonservice.service.communicationservice.TravelPointApiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.ApiTokenObjectMother.getPolygonApiToken;
import static de.blackforestsolutions.dravelopsdatamodel.testutil.TestUtils.retrieveJsonToPojo;
import static de.blackforestsolutions.dravelopsdatamodel.testutil.TestUtils.toJson;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TravelPointApiServiceIT {

    @Autowired
    private TravelPointApiService classUnderTest;

    @Test
    void test_retrieveTravelPointsFromApiService_returns_result() {
        ApiToken testData = getPolygonApiToken();
        String jsonTestData = toJson(testData);

        Flux<String> result = classUnderTest.retrieveTravelPointsFromApiService(jsonTestData);

        StepVerifier.create(result)
                .expectNextCount(1L)
                .thenConsumeWhile(travelPoint -> {
                    TravelPoint travelPointResult = retrieveJsonToPojo(travelPoint, TravelPoint.class);
                    assertThat(travelPointResult.getName()).isNotEmpty();
                    assertThat(travelPointResult.getPoint()).isNotNull();
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void test_retrieveTravelPointsFromApiService_with_incorrect_apiToken_return_zero_results() {
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(getPolygonApiToken());
        testData.setDeparture("Noooooooooooooo expected Result");
        String jsonTestData = toJson(testData.build());

        Flux<String> result = classUnderTest.retrieveTravelPointsFromApiService(jsonTestData);

        StepVerifier.create(result)
                .expectNextCount(0L)
                .verifyComplete();
    }
}
