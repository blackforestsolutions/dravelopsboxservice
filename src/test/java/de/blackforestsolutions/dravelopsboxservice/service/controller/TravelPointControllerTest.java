package de.blackforestsolutions.dravelopsboxservice.service.controller;

import de.blackforestsolutions.dravelopsboxservice.controller.TravelPointController;
import de.blackforestsolutions.dravelopsboxservice.service.communicationservice.TravelPointApiService;
import de.blackforestsolutions.dravelopsboxservice.service.communicationservice.TravelPointApiServiceImpl;
import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.TravelPoint;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.ApiTokenObjectMother.getAutocompleteBoxServiceApiToken;
import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.ApiTokenObjectMother.getNearestAddressesBoxServiceApiToken;
import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.TravelPointObjectMother.getGermanyTravelPoint;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class TravelPointControllerTest {

    private final TravelPointApiService travelPointApiService = mock(TravelPointApiServiceImpl.class);

    private final TravelPointController classUnderTest = new TravelPointController(travelPointApiService);

    @Test
    void test_getAutocompleteAddresses_is_executed_correctly_and_return_travelPoints() {
        ArgumentCaptor<ApiToken> requestArg = ArgumentCaptor.forClass(ApiToken.class);
        ApiToken testData = getAutocompleteBoxServiceApiToken();
        when(travelPointApiService.retrieveAutocompleteAddressesFromApiService(any(ApiToken.class)))
                .thenReturn(Flux.just(getGermanyTravelPoint(null)));

        Flux<TravelPoint> result = classUnderTest.getAutocompleteAddresses(testData);

        verify(travelPointApiService, times(1)).retrieveAutocompleteAddressesFromApiService(requestArg.capture());
        assertThat(requestArg.getValue()).isEqualToComparingFieldByField(getAutocompleteBoxServiceApiToken());
        StepVerifier.create(result)
                .assertNext(travelPoint -> assertThat(travelPoint).isEqualToComparingFieldByFieldRecursively(getGermanyTravelPoint(null)))
                .verifyComplete();
    }

    @Test
    void test_getAutocompleteAddresses_is_executed_correctly_when_no_results_are_available() {
        ApiToken testData = getAutocompleteBoxServiceApiToken();
        when(travelPointApiService.retrieveAutocompleteAddressesFromApiService(any(ApiToken.class)))
                .thenReturn(Flux.empty());

        Flux<TravelPoint> result = classUnderTest.getAutocompleteAddresses(testData);

        StepVerifier.create(result)
                .expectNextCount(0L)
                .verifyComplete();
    }

    @Test
    void test_getNearestAddresses_is_executed_correctly_and_return_travelPoints() {
        ArgumentCaptor<ApiToken> requestArg = ArgumentCaptor.forClass(ApiToken.class);
        ApiToken testData = getNearestAddressesBoxServiceApiToken();
        when(travelPointApiService.retrieveNearestAddressesFromApiService(any(ApiToken.class)))
                .thenReturn(Flux.just(getGermanyTravelPoint(new Distance(0.0d, Metrics.KILOMETERS))));

        Flux<TravelPoint> result = classUnderTest.getNearestAddresses(testData);

        verify(travelPointApiService, times(1)).retrieveNearestAddressesFromApiService(requestArg.capture());
        assertThat(requestArg.getValue()).isEqualToComparingFieldByField(getNearestAddressesBoxServiceApiToken());
        StepVerifier.create(result)
                .assertNext(travelPoint -> assertThat(travelPoint).isEqualToComparingFieldByFieldRecursively(getGermanyTravelPoint(new Distance(0.0d, Metrics.KILOMETERS))))
                .verifyComplete();
    }

    @Test
    void test_getNearestAddresses_is_executed_correctly_when_no_results_are_available() {
        ApiToken testData = getNearestAddressesBoxServiceApiToken();
        when(travelPointApiService.retrieveNearestAddressesFromApiService(any(ApiToken.class)))
                .thenReturn(Flux.empty());

        Flux<TravelPoint> result = classUnderTest.getNearestAddresses(testData);

        StepVerifier.create(result)
                .expectNextCount(0L)
                .verifyComplete();
    }

}
