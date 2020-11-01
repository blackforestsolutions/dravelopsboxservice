package de.blackforestsolutions.dravelopspolygonservice.service.mapperservice;

import de.blackforestsolutions.dravelopsdatamodel.CallStatus;
import de.blackforestsolutions.dravelopsdatamodel.Status;
import de.blackforestsolutions.dravelopsdatamodel.TravelPoint;
import de.blackforestsolutions.dravelopsgeneratedcontent.pelias.PeliasTravelPointResponse;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.TravelPointObjectMother.*;
import static de.blackforestsolutions.dravelopsdatamodel.testutil.TestUtils.getResourceFileAsString;
import static de.blackforestsolutions.dravelopsdatamodel.testutil.TestUtils.retrieveJsonToPojo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PeliasMapperServiceTest {

    private final PeliasMapperService classUnderTest = new PeliasMapperServiceImpl();

    @Test
    void test_extractTravelPointsFrom_http_body_maps_correctly_to_travelPoints() {
        String testDataJson = getResourceFileAsString("json/peliasResult.json");
        PeliasTravelPointResponse testData = retrieveJsonToPojo(testDataJson, PeliasTravelPointResponse.class);

        Flux<CallStatus<TravelPoint>> result = classUnderTest.extractTravelPointsFrom(testData);

        StepVerifier.create(result)
                .assertNext(travelPoint -> {
                    assertThat(travelPoint.getStatus()).isEqualTo(Status.SUCCESS);
                    assertThat(travelPoint.getThrowable()).isNull();
                    assertThat(travelPoint.getCalledObject()).isEqualToComparingFieldByField(getStuttgarterStreetOneTravelPoint());
                })
                .assertNext(travelPoint -> {
                    assertThat(travelPoint.getStatus()).isEqualTo(Status.SUCCESS);
                    assertThat(travelPoint.getThrowable()).isNull();
                    assertThat(travelPoint.getCalledObject()).isEqualToComparingFieldByField(getGermanyTravelPoint());
                })
                .assertNext(travelPoint -> {
                    assertThat(travelPoint.getStatus()).isEqualTo(Status.SUCCESS);
                    assertThat(travelPoint.getThrowable()).isNull();
                    assertThat(travelPoint.getCalledObject()).isEqualToComparingFieldByField(getRendsburgCountyTravelPoint());
                })
                .assertNext(travelPoint -> {
                    assertThat(travelPoint.getStatus()).isEqualTo(Status.SUCCESS);
                    assertThat(travelPoint.getThrowable()).isNull();
                    assertThat(travelPoint.getCalledObject()).isEqualToComparingFieldByField(getFurtwangenLocalityTravelPoint());
                })
                .assertNext(travelPoint -> {
                    assertThat(travelPoint.getStatus()).isEqualTo(Status.SUCCESS);
                    assertThat(travelPoint.getThrowable()).isNull();
                    assertThat(travelPoint.getCalledObject()).isEqualToComparingFieldByField(getAmGrosshaubergTravelPoint());
                })
                .assertNext(travelPoint -> {
                    assertThat(travelPoint.getStatus()).isEqualTo(Status.SUCCESS);
                    assertThat(travelPoint.getThrowable()).isNull();
                    assertThat(travelPoint.getCalledObject()).isEqualToComparingFieldByField(getGermanWatchMuseumTravelPoint());
                })
                .verifyComplete();
    }

    @Test
    void test_extractTravelPointsFrom_body_returns_no_travelPoints_when_features_are_empty() {
        String testDataJson = getResourceFileAsString("json/peliasNoResult.json");
        PeliasTravelPointResponse testData = retrieveJsonToPojo(testDataJson, PeliasTravelPointResponse.class);

        Flux<CallStatus<TravelPoint>> result = classUnderTest.extractTravelPointsFrom(testData);

        StepVerifier.create(result)
                .expectNextCount(0L)
                .verifyComplete();
    }

    @Test
    void test_extractTravelPointsFrom_body_with_error_in_first_element_returns_first_failed_call_status_and_rest_is_success() {
        String testDataJson = getResourceFileAsString("json/peliasResult.json");
        PeliasTravelPointResponse testData = retrieveJsonToPojo(testDataJson, PeliasTravelPointResponse.class);
        testData.getFeatures().get(0).setProperties(null);

        Flux<CallStatus<TravelPoint>> result = classUnderTest.extractTravelPointsFrom(testData);

        StepVerifier.create(result)
                .assertNext(error ->{
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
    void test_extractTravelPointsFrom_body_and_features_as_null_returns_a_failed_callStatus() {
        String testDataJson = getResourceFileAsString("json/peliasResult.json");
        PeliasTravelPointResponse testData = retrieveJsonToPojo(testDataJson, PeliasTravelPointResponse.class);
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
