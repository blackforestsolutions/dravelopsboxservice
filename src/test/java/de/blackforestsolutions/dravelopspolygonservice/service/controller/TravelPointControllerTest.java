package de.blackforestsolutions.dravelopspolygonservice.service.controller;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.TravelPoint;
import de.blackforestsolutions.dravelopspolygonservice.controller.TravelPointController;
import de.blackforestsolutions.dravelopspolygonservice.service.communicationservice.TravelPointApiService;
import de.blackforestsolutions.dravelopspolygonservice.service.communicationservice.TravelPointApiServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.ApiTokenObjectMother.getPolygonApiToken;
import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.TravelPointObjectMother.getGermanyTravelPoint;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class TravelPointControllerTest {

    private final TravelPointApiService travelPointApiService = mock(TravelPointApiServiceImpl.class);

    private final TravelPointController classUnderTest = new TravelPointController(travelPointApiService);

    @Test
    void test_retrievePeliasTravelPoints_is_exectued_correctly_and_return_travelPoints() {
        ArgumentCaptor<ApiToken> requestArg = ArgumentCaptor.forClass(ApiToken.class);
        ApiToken testData = getPolygonApiToken();
        when(travelPointApiService.retrieveTravelPointsFromApiService(any(ApiToken.class)))
                .thenReturn(Flux.just(getGermanyTravelPoint()));

        Flux<TravelPoint> result = classUnderTest.retrievePeliasTravelPoints(testData);

        verify(travelPointApiService, times(1)).retrieveTravelPointsFromApiService(requestArg.capture());
        assertThat(requestArg.getValue()).isEqualToComparingFieldByField(getPolygonApiToken());
        StepVerifier.create(result)
                .assertNext(travelPoint -> assertThat(travelPoint).isEqualToComparingFieldByFieldRecursively(getGermanyTravelPoint()))
                .verifyComplete();
    }

    @Test
    void test_retrievePeliasTravelPoints_is_executed_correctly_when_no_results_are_available() {
        ApiToken testData = getPolygonApiToken();
        when(travelPointApiService.retrieveTravelPointsFromApiService(any(ApiToken.class)))
                .thenReturn(Flux.empty());

        Flux<TravelPoint> result = classUnderTest.retrievePeliasTravelPoints(testData);

        StepVerifier.create(result)
                .expectNextCount(0L)
                .verifyComplete();
    }

}
