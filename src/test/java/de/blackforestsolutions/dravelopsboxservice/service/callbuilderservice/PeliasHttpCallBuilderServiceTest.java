package de.blackforestsolutions.dravelopsboxservice.service.callbuilderservice;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.Box;
import de.blackforestsolutions.dravelopsdatamodel.Layer;
import de.blackforestsolutions.dravelopsdatamodel.Point;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;

import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.ApiTokenObjectMother.getPeliasAutocompleteApiToken;
import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.ApiTokenObjectMother.getPeliasNearestAddressesApiToken;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PeliasHttpCallBuilderServiceTest {

    private final PeliasHttpCallBuilderService classUnderTest = new PeliasHttpCallBuilderServiceImpl();

    @Test
    void test_buildPeliasAutocompletePathWith_apiToken_returns_valid_path() {
        ApiToken testData = getPeliasAutocompleteApiToken();

        String result = classUnderTest.buildPeliasAutocompletePathWith(testData);

        assertThat(result).isEqualTo("/v1/autocomplete?text=Am Großhausberg 8&size=10&lang=de&boundary.rect.min_lon=7.593844&boundary.rect.max_lon=9.798538&boundary.rect.min_lat=49.717617&boundary.rect.max_lat=47.590746&layers=venue,address,locality,street");
    }

    @Test
    void test_buildPeliasAutocompletePathWith_apiToken_and_layers_hasVanue_as_false_returns_path_without_venue() {
        ApiToken testData = new ApiToken(getPeliasAutocompleteApiToken());
        LinkedHashMap<Layer, Boolean> layers = testData.getLayers();
        layers.put(Layer.HAS_VENUE, false);
        testData.setLayers(layers);

        String result = classUnderTest.buildPeliasAutocompletePathWith(testData);

        assertThat(result).doesNotContain("venue");
    }

    @Test
    void test_buildPeliasAutocompletePathWith_apiToken_and_apiVersion_as_null_throws_exception() {
        ApiToken testData = new ApiToken(getPeliasAutocompleteApiToken());
        testData.setApiVersion(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildPeliasAutocompletePathWith(testData));
    }

    @Test
    void test_buildPeliasAutocompletePathWith_apiToken_and_departure_as_null_throws_exception() {
        ApiToken testData = new ApiToken(getPeliasAutocompleteApiToken());
        testData.setDeparture(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildPeliasAutocompletePathWith(testData));
    }

    @Test
    void test_buildPeliasAutocompletePathWith_apiToken_and_maxResults_as_null_throws_exception() {
        ApiToken testData = new ApiToken(getPeliasAutocompleteApiToken());
        testData.setMaxResults(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildPeliasAutocompletePathWith(testData));
    }

    @Test
    void test_buildPeliasAutocompletePathWith_apiToken_and_language_as_null_throws_exception() {
        ApiToken testData = new ApiToken(getPeliasAutocompleteApiToken());
        testData.setLanguage(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildPeliasAutocompletePathWith(testData));
    }

    @Test
    void test_buildPeliasAutocompletePathWith_apiToken_and_box_as_null_throws_exception() {
        ApiToken testData = new ApiToken(getPeliasAutocompleteApiToken());
        testData.setBox(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildPeliasAutocompletePathWith(testData));
    }

    @Test
    void test_buildPeliasAutocompletePathWith_apiToken_and_layers_as_null_throws_exception() {
        ApiToken testData = new ApiToken(getPeliasAutocompleteApiToken());
        testData.setLayers(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildPeliasAutocompletePathWith(testData));
    }

    @Test
    void test_buildPeliasAutocompletePathWith_apiToken_and_topLeft_box_point_as_null_throws_exception() {
        Box testBox = new Box.BoxBuilder(null, getPeliasAutocompleteApiToken().getBox().getBottomRight()).build();
        ApiToken testData = new ApiToken(getPeliasAutocompleteApiToken());
        testData.setBox(testBox);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildPeliasAutocompletePathWith(testData));
    }

    @Test
    void test_buildPeliasAutocompletePathWith_apiToken_and_rightBottom_box_point_as_null_throws_exception() {
        Box testBox = new Box.BoxBuilder(getPeliasAutocompleteApiToken().getBox().getTopLeft(), null).build();
        ApiToken testData = new ApiToken(getPeliasAutocompleteApiToken());
        testData.setBox(testBox);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildPeliasAutocompletePathWith(testData));
    }

    @Test
    void test_buildPeliasAutocompletePathWith_apiToken_and_layers_hasVenue_as_null_throws_exception() {
        ApiToken testData = new ApiToken(getPeliasAutocompleteApiToken());
        LinkedHashMap<Layer, Boolean> layers = testData.getLayers();
        layers.remove(Layer.HAS_VENUE);
        testData.setLayers(layers);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildPeliasAutocompletePathWith(testData));
    }

    @Test
    void test_buildPeliasAutocompletePathWith_apiToken_and_layers_hasAddress_as_null_throws_exception() {
        ApiToken testData = new ApiToken(getPeliasAutocompleteApiToken());
        LinkedHashMap<Layer, Boolean> layers = testData.getLayers();
        layers.remove(Layer.HAS_ADDRESS);
        testData.setLayers(layers);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildPeliasAutocompletePathWith(testData));
    }

    @Test
    void test_buildPeliasAutocompletePathWith_apiToken_and_layers_hasLocality_as_null_throws_exception() {
        ApiToken testData = new ApiToken(getPeliasAutocompleteApiToken());
        LinkedHashMap<Layer, Boolean> layers = testData.getLayers();
        layers.remove(Layer.HAS_LOCALITY);
        testData.setLayers(layers);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildPeliasAutocompletePathWith(testData));
    }

    @Test
    void test_buildPeliasAutocompletePathWith_apiToken_and_layers_hasStreet_as_null_throws_exception() {
        ApiToken testData = new ApiToken(getPeliasAutocompleteApiToken());
        LinkedHashMap<Layer, Boolean> layers = testData.getLayers();
        layers.remove(Layer.HAS_STREET);
        testData.setLayers(layers);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildPeliasAutocompletePathWith(testData));
    }

    @Test
    void test_buildPeliasReversePathWith_apiToken_returns_valid_path() {
        ApiToken testData = new ApiToken(getPeliasNearestAddressesApiToken());
        testData.setArrivalCoordinate(new Point.PointBuilder(7.891595d, 48.087517d).build());

        String result = classUnderTest.buildPeliasReversePathWith(testData);

        assertThat(result).isEqualTo("/v1/reverse?point.lat=48.087517&point.lon=7.891595&size=10&lang=de&layers=venue,address,locality,street&boundary.circle.radius=1.0");
    }

    @Test
    void test_buildPeliasReversePathWith_apiToken_and_layers_hasVanue_as_false_returns_path_without_venue() {
        ApiToken testData = new ApiToken(getPeliasNearestAddressesApiToken());
        LinkedHashMap<Layer, Boolean> layers = testData.getLayers();
        layers.put(Layer.HAS_VENUE, false);
        testData.setLayers(layers);

        String result = classUnderTest.buildPeliasReversePathWith(testData);

        assertThat(result).doesNotContain("venue");
    }

    @Test
    void test_buildPeliasReversePathWith_apiToken_and_arrivalCoordinate_as_null_throws_exception() {
        ApiToken testData = new ApiToken(getPeliasNearestAddressesApiToken());
        testData.setArrivalCoordinate(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildPeliasReversePathWith(testData));
    }

    @Test
    void test_buildPeliasReversePathWith_apiToken_and_apiVersion_as_null_throws_exception() {
        ApiToken testData = new ApiToken(getPeliasNearestAddressesApiToken());
        testData.setArrivalCoordinate(new Point.PointBuilder(7.891595d, 48.087517d).build());
        testData.setApiVersion(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildPeliasReversePathWith(testData));
    }

    @Test
    void test_buildPeliasReversePathWith_apiToken_and_maxResults_as_null_throws_exception() {
        ApiToken testData = new ApiToken(getPeliasNearestAddressesApiToken());
        testData.setArrivalCoordinate(new Point.PointBuilder(7.891595d, 48.087517d).build());
        testData.setMaxResults(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildPeliasReversePathWith(testData));
    }

    @Test
    void test_buildPeliasReversePathWith_apiToken_and_language_as_null_throws_exception() {
        ApiToken testData = new ApiToken(getPeliasNearestAddressesApiToken());
        testData.setArrivalCoordinate(new Point.PointBuilder(7.891595d, 48.087517d).build());
        testData.setLanguage(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildPeliasReversePathWith(testData));
    }

    @Test
    void test_buildPeliasReversePathWith_apiToken_and_layers_as_null_throws_exception() {
        ApiToken testData = new ApiToken(getPeliasNearestAddressesApiToken());
        testData.setArrivalCoordinate(new Point.PointBuilder(7.891595d, 48.087517d).build());
        testData.setLayers(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildPeliasReversePathWith(testData));
    }

    @Test
    void test_buildPeliasReversePathWith_apiToken_and_radiusInKilometers_as_null_throws_exception() {
        ApiToken testData = new ApiToken(getPeliasNearestAddressesApiToken());
        testData.setArrivalCoordinate(new Point.PointBuilder(7.891595d, 48.087517d).build());
        testData.setRadiusInKilometers(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildPeliasReversePathWith(testData));
    }

    @Test
    void test_buildPeliasReversePathWith_apiToken_and_layers_hasVenue_as_null_throws_exception() {
        ApiToken testData = new ApiToken(getPeliasNearestAddressesApiToken());
        LinkedHashMap<Layer, Boolean> layers = testData.getLayers();
        layers.remove(Layer.HAS_VENUE);
        testData.setLayers(layers);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildPeliasReversePathWith(testData));
    }

    @Test
    void test_buildPeliasReversePathWith_apiToken_and_layers_hasAddress_as_null_throws_exception() {
        ApiToken testData = new ApiToken(getPeliasNearestAddressesApiToken());
        LinkedHashMap<Layer, Boolean> layers = testData.getLayers();
        layers.remove(Layer.HAS_ADDRESS);
        testData.setLayers(layers);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildPeliasReversePathWith(testData));
    }

    @Test
    void test_buildPeliasReversePathWith_apiToken_and_layers_hasLocality_as_null_throws_exception() {
        ApiToken testData = new ApiToken(getPeliasNearestAddressesApiToken());
        LinkedHashMap<Layer, Boolean> layers = testData.getLayers();
        layers.remove(Layer.HAS_LOCALITY);
        testData.setLayers(layers);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildPeliasReversePathWith(testData));
    }

    @Test
    void test_buildPeliasReversePathWith_apiToken_and_layers_hasStreet_as_null_throws_exception() {
        ApiToken testData = new ApiToken(getPeliasNearestAddressesApiToken());
        LinkedHashMap<Layer, Boolean> layers = testData.getLayers();
        layers.remove(Layer.HAS_STREET);
        testData.setLayers(layers);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildPeliasReversePathWith(testData));
    }
}
