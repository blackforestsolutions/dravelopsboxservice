package de.blackforestsolutions.dravelopsboxservice.service.mapperservice;

import de.blackforestsolutions.dravelopsdatamodel.CallStatus;
import de.blackforestsolutions.dravelopsdatamodel.Status;
import de.blackforestsolutions.dravelopsdatamodel.TravelPoint;
import de.blackforestsolutions.dravelopsgeneratedcontent.pelias.PeliasTravelPointResponse;
import org.junit.jupiter.api.Test;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static de.blackforestsolutions.dravelopsboxservice.service.testutil.TestAssertions.getPeliasSuccessCallStatusAsserts;
import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.TravelPointObjectMother.*;
import static de.blackforestsolutions.dravelopsdatamodel.testutil.TestUtils.retrieveJsonToPojo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PeliasMapperServiceTest {

    private final PeliasMapperService classUnderTest = new PeliasMapperServiceImpl();

    @Test
    void test_extractTravelPointsFrom_autocomplete_http_body_maps_correctly_to_travelPoints() {
        PeliasTravelPointResponse testData = retrieveJsonToPojo("json/peliasAutocompleteResult.json", PeliasTravelPointResponse.class);

        Flux<CallStatus<TravelPoint>> result = classUnderTest.extractTravelPointsFrom(testData);

        StepVerifier.create(result)
                .assertNext(getPeliasSuccessCallStatusAsserts(getStuttgarterStreetOneTravelPoint(null)))
                .assertNext(getPeliasSuccessCallStatusAsserts(getGermanyTravelPoint(null)))
                .assertNext(getPeliasSuccessCallStatusAsserts(getRendsburgCountyTravelPoint(null)))
                .assertNext(getPeliasSuccessCallStatusAsserts(getFurtwangenLocalityTravelPoint(null)))
                .assertNext(getPeliasSuccessCallStatusAsserts(getAmGrosshaubergTravelPoint(null)))
                .assertNext(getPeliasSuccessCallStatusAsserts(getGermanWatchMuseumTravelPoint(null)))
                .verifyComplete();
    }

    @Test
    void test_extractTravelPointsFrom_reverse_http_body_maps_correctly_to_travelPoints() {
        PeliasTravelPointResponse testData = retrieveJsonToPojo("json/peliasReverseResult.json", PeliasTravelPointResponse.class);

        Flux<CallStatus<TravelPoint>> result = classUnderTest.extractTravelPointsFrom(testData);

        StepVerifier.create(result)
                .assertNext(getPeliasSuccessCallStatusAsserts(getStuttgarterStreetOneTravelPoint(new Distance(0.0d, Metrics.KILOMETERS))))
                .assertNext(getPeliasSuccessCallStatusAsserts(getGermanyTravelPoint(new Distance(0.04d, Metrics.KILOMETERS))))
                .assertNext(getPeliasSuccessCallStatusAsserts(getRendsburgCountyTravelPoint(new Distance(0.04d, Metrics.KILOMETERS))))
                .assertNext(getPeliasSuccessCallStatusAsserts(getFurtwangenLocalityTravelPoint(new Distance(0.046d, Metrics.KILOMETERS))))
                .assertNext(getPeliasSuccessCallStatusAsserts(getAmGrosshaubergTravelPoint(new Distance(0.056d, Metrics.KILOMETERS))))
                .assertNext(getPeliasSuccessCallStatusAsserts(getGermanWatchMuseumTravelPoint(new Distance(0.069d, Metrics.KILOMETERS))))
                .verifyComplete();
    }

    @Test
    void test_extractTravelPointsFrom_autocomplete_body_returns_no_travelPoints_when_features_are_empty() {
        PeliasTravelPointResponse testData = retrieveJsonToPojo("json/peliasAutocompleteNoResult.json", PeliasTravelPointResponse.class);

        Flux<CallStatus<TravelPoint>> result = classUnderTest.extractTravelPointsFrom(testData);

        StepVerifier.create(result)
                .expectNextCount(0L)
                .verifyComplete();
    }

    @Test
    void test_extractTravelPointsFrom_reverse_body_returns_no_travelPoints_when_features_are_empty() {
        PeliasTravelPointResponse testData = retrieveJsonToPojo("json/peliasReverseNoResult.json", PeliasTravelPointResponse.class);

        Flux<CallStatus<TravelPoint>> result = classUnderTest.extractTravelPointsFrom(testData);

        StepVerifier.create(result)
                .expectNextCount(0L)
                .verifyComplete();
    }

    @Test
    void test_extractTravelPointsFrom_autocomplete_body_with_error_in_first_element_returns_first_failed_call_status_and_rest_is_success() {
        PeliasTravelPointResponse testData = retrieveJsonToPojo("json/peliasAutocompleteResult.json", PeliasTravelPointResponse.class);
        testData.getFeatures().get(0).setProperties(null);

        Flux<CallStatus<TravelPoint>> result = classUnderTest.extractTravelPointsFrom(testData);

        StepVerifier.create(result)
                .assertNext(error -> {
                    assertThat(error.getStatus()).isEqualTo(Status.FAILED);
                    assertThat(error.getCalledObject()).isNull();
                    assertThat(error.getThrowable()).isInstanceOf(NullPointerException.class);
                })
                .expectNextCount(1L)
                .thenConsumeWhile(travelPoint -> {
                    assertThat(travelPoint.getStatus()).isEqualTo(Status.SUCCESS);
                    assertThat(travelPoint.getThrowable()).isNull();
                    assertThat(travelPoint.getCalledObject()).isInstanceOf(TravelPoint.class);
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void test_extractTravelPointsFrom_reverse_body_with_error_in_first_element_returns_first_failed_call_status_and_rest_is_success() {
        PeliasTravelPointResponse testData = retrieveJsonToPojo("json/peliasReverseResult.json", PeliasTravelPointResponse.class);
        testData.getFeatures().get(0).setProperties(null);

        Flux<CallStatus<TravelPoint>> result = classUnderTest.extractTravelPointsFrom(testData);

        StepVerifier.create(result)
                .assertNext(error -> {
                    assertThat(error.getStatus()).isEqualTo(Status.FAILED);
                    assertThat(error.getCalledObject()).isNull();
                    assertThat(error.getThrowable()).isInstanceOf(NullPointerException.class);
                })
                .expectNextCount(1L)
                .thenConsumeWhile(travelPoint -> {
                    assertThat(travelPoint.getStatus()).isEqualTo(Status.SUCCESS);
                    assertThat(travelPoint.getThrowable()).isNull();
                    assertThat(travelPoint.getCalledObject()).isInstanceOf(TravelPoint.class);
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void test_extractTravelPointsFrom_autocomplete_body_and_features_as_null_returns_a_failed_callStatus() {
        PeliasTravelPointResponse testData = retrieveJsonToPojo("json/peliasAutocompleteResult.json", PeliasTravelPointResponse.class);
        testData.setFeatures(null);

        Flux<CallStatus<TravelPoint>> result = classUnderTest.extractTravelPointsFrom(testData);

        StepVerifier.create(result)
                .assertNext(travelPointCallStatus -> {
                    assertThat(travelPointCallStatus.getStatus()).isEqualTo(Status.FAILED);
                    assertThat(travelPointCallStatus.getCalledObject()).isNull();
                    assertThat(travelPointCallStatus.getThrowable()).isInstanceOf(NullPointerException.class);
                })
                .verifyComplete();
    }

    @Test
    void test_extractTravelPointsFrom_reverse_body_and_features_as_null_returns_a_failed_callStatus() {
        PeliasTravelPointResponse testData = retrieveJsonToPojo("json/peliasReverseResult.json", PeliasTravelPointResponse.class);
        testData.setFeatures(null);

        Flux<CallStatus<TravelPoint>> result = classUnderTest.extractTravelPointsFrom(testData);

        StepVerifier.create(result)
                .assertNext(travelPointCallStatus -> {
                    assertThat(travelPointCallStatus.getStatus()).isEqualTo(Status.FAILED);
                    assertThat(travelPointCallStatus.getCalledObject()).isNull();
                    assertThat(travelPointCallStatus.getThrowable()).isInstanceOf(NullPointerException.class);
                })
                .verifyComplete();
    }

    @Test
    void test_extractTravelPointsFrom_body_as_null_throws_exception() {
        PeliasTravelPointResponse testData = null;

        assertThrows(NullPointerException.class, () -> classUnderTest.extractTravelPointsFrom(testData));
    }


}
