package de.blackforestsolutions.dravelopsboxservice.service.callbuilderservice;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.Box;
import de.blackforestsolutions.dravelopsdatamodel.Point;
import org.junit.jupiter.api.Test;

import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.ApiTokenObjectMother.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PeliasHttpCallBuilderServiceTest {

    private final PeliasHttpCallBuilderService classUnderTest = new PeliasHttpCallBuilderServiceImpl();

    @Test
    void test_buildPeliasAutocompletePathWith_apiToken_returns_valid_path() {
        ApiToken testData = getPeliasAutocompleteApiToken();

        String result = classUnderTest.buildPeliasAutocompletePathWith(testData);

        assertThat(result).isEqualTo("/v1/autocomplete?text=Am Großhausberg 8&size=10&lang=de&boundary.rect.min_lon=7.593844&boundary.rect.max_lon=9.798538&boundary.rect.min_lat=49.717617&boundary.rect.max_lat=47.590746&layers=venue,address,street,country,macroregion,region,macrocounty,county,locality,localadmin,borough,neighbourhood,coarse,postalcode");
    }

    @Test
    void test_buildPeliasAutocompletePathWith_apiToken_and_apiVersion_as_null_throws_exception() {
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(getPeliasAutocompleteApiToken());
        testData.setApiVersion(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildPeliasAutocompletePathWith(testData.build()));
    }

    @Test
    void test_buildPeliasAutocompletePathWith_apiToken_and_departure_as_null_throws_exception() {
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(getPeliasAutocompleteApiToken());
        testData.setDeparture(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildPeliasAutocompletePathWith(testData.build()));
    }

    @Test
    void test_buildPeliasAutocompletePathWith_apiToken_and_maxResults_as_null_throws_exception() {
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(getPeliasAutocompleteApiToken());
        testData.setMaxResults(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildPeliasAutocompletePathWith(testData.build()));
    }

    @Test
    void test_buildPeliasAutocompletePathWith_apiToken_and_language_as_null_throws_exception() {
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(getPeliasAutocompleteApiToken());
        testData.setLanguage(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildPeliasAutocompletePathWith(testData.build()));
    }

    @Test
    void test_buildPeliasAutocompletePathWith_apiToken_and_box_as_null_throws_exception() {
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(getPeliasAutocompleteApiToken());
        testData.setBox(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildPeliasAutocompletePathWith(testData.build()));
    }

    @Test
    void test_buildPeliasAutocompletePathWith_apiToken_and_layers_as_null_throws_exception() {
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(getPeliasAutocompleteApiToken());
        testData.setLayers(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildPeliasAutocompletePathWith(testData.build()));
    }

    @Test
    void test_buildPeliasAutocompletePathWith_apiToken_and_topLeft_box_point_as_null_throws_exception() {
        Box.BoxBuilder testBox = new Box.BoxBuilder(null, getPeliasAutocompleteApiToken().getBox().getBottomRight());
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(getPeliasAutocompleteApiToken());
        testData.setBox(testBox.build());

        assertThrows(NullPointerException.class, () -> classUnderTest.buildPeliasAutocompletePathWith(testData.build()));
    }

    @Test
    void test_buildPeliasAutocompletePathWith_apiToken_and_rightBottom_box_point_as_null_throws_exception() {
        Box.BoxBuilder testBox = new Box.BoxBuilder(getPeliasAutocompleteApiToken().getBox().getTopLeft(), null);
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(getPeliasAutocompleteApiToken());
        testData.setBox(testBox.build());

        assertThrows(NullPointerException.class, () -> classUnderTest.buildPeliasAutocompletePathWith(testData.build()));
    }

    @Test
    void test_buildPeliasReversePathWith_apiToken_returns_valid_path() {
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(getPeliasNearestAddressesApiToken());
        testData.setArrivalCoordinate(new Point.PointBuilder(7.891595d, 48.087517d).build());

        String result = classUnderTest.buildPeliasReversePathWith(testData.build());

        assertThat(result).isEqualTo("/v1/reverse?point.lat=48.087517&point.lon=7.891595&size=10&lang=de&layers=venue,address,street,country,macroregion,region,macrocounty,county,locality,localadmin,borough,neighbourhood,coarse,postalcode&boundary.circle.radius=1.0");
    }

    @Test
    void test_buildPeliasReversePathWith_apiToken_and_arrivalCoordinate_as_null_throws_exception() {
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(getPeliasNearestAddressesApiToken());
        testData.setArrivalCoordinate(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildPeliasReversePathWith(testData.build()));
    }

    @Test
    void test_buildPeliasReversePathWith_apiToken_and_apiVersion_as_null_throws_exception() {
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(getPeliasNearestAddressesApiToken());
        testData.setArrivalCoordinate(new Point.PointBuilder(7.891595d, 48.087517d).build());
        testData.setApiVersion(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildPeliasReversePathWith(testData.build()));
    }

    @Test
    void test_buildPeliasReversePathWith_apiToken_and_maxResults_as_null_throws_exception() {
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(getPeliasNearestAddressesApiToken());
        testData.setArrivalCoordinate(new Point.PointBuilder(7.891595d, 48.087517d).build());
        testData.setMaxResults(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildPeliasReversePathWith(testData.build()));
    }

    @Test
    void test_buildPeliasReversePathWith_apiToken_and_language_as_null_throws_exception() {
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(getPeliasNearestAddressesApiToken());
        testData.setArrivalCoordinate(new Point.PointBuilder(7.891595d, 48.087517d).build());
        testData.setLanguage(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildPeliasReversePathWith(testData.build()));
    }

    @Test
    void test_buildPeliasReversePathWith_apiToken_and_layers_as_null_throws_exception() {
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(getPeliasNearestAddressesApiToken());
        testData.setArrivalCoordinate(new Point.PointBuilder(7.891595d, 48.087517d).build());
        testData.setLayers(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildPeliasReversePathWith(testData.build()));
    }

    @Test
    void test_buildPeliasReversePathWith_apiToken_and_radiusInKilometers_as_null_throws_exception() {
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(getPeliasNearestAddressesApiToken());
        testData.setArrivalCoordinate(new Point.PointBuilder(7.891595d, 48.087517d).build());
        testData.setRadiusInKilometers(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildPeliasReversePathWith(testData.build()));
    }
}