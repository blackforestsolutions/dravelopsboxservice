package de.blackforestsolutions.dravelopsboxservice;

import de.blackforestsolutions.dravelopsboxservice.configuration.TravelPointApiServiceTestConfiguration;
import de.blackforestsolutions.dravelopsboxservice.service.communicationservice.TravelPointApiService;
import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.Point;
import de.blackforestsolutions.dravelopsdatamodel.TravelPoint;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static de.blackforestsolutions.dravelopsboxservice.service.testutil.TestAssertions.getTravelPointApiNearestAddressesAsserts;

@Import(TravelPointApiServiceTestConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TravelPointApiServiceIT {

    @Autowired
    private TravelPointApiService classUnderTest;

    @Autowired
    private ApiToken.ApiTokenBuilder travelPointApiToken;


    @Test
    void test_retrieveAutocompleteAddressesFromApiService_with_incorrect_apiToken_returns_zero_travelPoints() {
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(travelPointApiToken.build());
        testData.setDeparture("Noooooooooooooo expected Result");

        Flux<TravelPoint> result = classUnderTest.retrieveAutocompleteAddressesFromApiService(testData.build());

        StepVerifier.create(result)
                .expectNextCount(0L)
                .verifyComplete();
    }

    @Test
    void test_retrieveNearestAddressesFromApiService_with_correct_apiToken_returns_travelPoints() {
        ApiToken testData = travelPointApiToken.build();

        Flux<TravelPoint> result = classUnderTest.retrieveNearestAddressesFromApiService(testData);

        StepVerifier.create(result)
                .assertNext(getTravelPointApiNearestAddressesAsserts())
                .thenConsumeWhile(travelPoint -> true, getTravelPointApiNearestAddressesAsserts())
                .verifyComplete();
    }

    @Test
    void test_retrieveNearestAddressesFromApiService_with_incorrect_apiToken_returns_zero_travelPoints() {
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(travelPointApiToken.build());
        testData.setArrivalCoordinate(new Point.PointBuilder(0.0d, 0.0d).build());
        testData.setRadiusInKilometers(1);

        Flux<TravelPoint> result = classUnderTest.retrieveNearestAddressesFromApiService(testData.build());

        StepVerifier.create(result)
                .expectNextCount(0L)
                .verifyComplete();
    }
}
