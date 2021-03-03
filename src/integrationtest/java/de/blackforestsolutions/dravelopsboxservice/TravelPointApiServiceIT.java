package de.blackforestsolutions.dravelopsboxservice;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.TravelPoint;
import de.blackforestsolutions.dravelopsboxservice.configuration.TravelPointApiServiceTestConfiguration;
import de.blackforestsolutions.dravelopsboxservice.service.communicationservice.TravelPointApiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@Import(TravelPointApiServiceTestConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TravelPointApiServiceIT {

    @Autowired
    private TravelPointApiService classUnderTest;

    @Autowired
    private ApiToken.ApiTokenBuilder boxApiToken;

    @Test
    void test_retrieveTravelPointsFromApiService_with_incorrect_apiToken_return_zero_travelPoints() {
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(boxApiToken.build());
        testData.setDeparture("Noooooooooooooo expected Result");

        Flux<TravelPoint> result = classUnderTest.retrieveTravelPointsFromApiService(testData.build());

        StepVerifier.create(result)
                .expectNextCount(0L)
                .verifyComplete();
    }
}
