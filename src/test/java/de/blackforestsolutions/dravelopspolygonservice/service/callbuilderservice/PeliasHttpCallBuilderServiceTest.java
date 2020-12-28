package de.blackforestsolutions.dravelopspolygonservice.service.callbuilderservice;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.ApiTokenObjectMother.getConfiguredPeliasAutocompleteApiToken;
import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.ApiTokenObjectMother.getPeliasAutocompleteApiToken;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PeliasHttpCallBuilderServiceTest {

    private final PeliasHttpCallBuilderService classUnderTest = new PeliasHttpCallBuilderServiceImpl();

    @Test
    void test_buildPeliasAutocompletePathWith_apiToken_returns_valid_path() {
        ApiToken testData = getPeliasAutocompleteApiToken();

        String result = classUnderTest.buildPeliasAutocompletePathWith(testData);

        assertThat(result).isEqualTo("/v1/autocomplete?text=Sick AG&size=10&lang=de-DE&boundary.rect.min_lon=7.593844&boundary.rect.max_lon=9.798538&boundary.rect.min_lat=47.590746&boundary.rect.max_lat=49.717617&layers=venue,address,street,country,macroregion,region,macrocounty,county,locality,localadmin,borough,neighbourhood,coarse,postalcode");
    }

    @Test
    void test_buildPeliasAutocompletePathWith_apiToken_and_apiVersion_as_null_throws_exception() {
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(getConfiguredPeliasAutocompleteApiToken());
        testData.setApiVersion(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildPeliasAutocompletePathWith(testData.build()));
    }

    @Test
    void test_buildPeliasAutocompletePathWith_apiToken_and_departure_as_null_throws_exception() {
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(getConfiguredPeliasAutocompleteApiToken());
        testData.setDeparture(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildPeliasAutocompletePathWith(testData.build()));
    }

    @Test
    void test_buildPeliasAutocompletePathWith_apiToken_and_maxResults_as_null_throws_exception() {
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(getConfiguredPeliasAutocompleteApiToken());
        testData.setMaxResults(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildPeliasAutocompletePathWith(testData.build()));
    }

    @Test
    void test_buildPeliasAutocompletePathWith_apiToken_and_language_as_null_throws_exception() {
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(getConfiguredPeliasAutocompleteApiToken());
        testData.setLanguage(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildPeliasAutocompletePathWith(testData.build()));
    }

    @Test
    void test_buildPeliasAutocompletePathWith_apiToken_and_box_as_null_throws_exception() {
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(getConfiguredPeliasAutocompleteApiToken());
        testData.setBox(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildPeliasAutocompletePathWith(testData.build()));
    }

    @Test
    void test_buildPeliasAutocompletePathWith_apiToken_and_layers_as_null_throws_exception() {
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(getConfiguredPeliasAutocompleteApiToken());
        testData.setLayers(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildPeliasAutocompletePathWith(testData.build()));
    }

    @Test
    void test_buildPeliasAutocompletePathWith_apiToken_and_zero_layer_elements_throws_exception() {
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(getConfiguredPeliasAutocompleteApiToken());
        testData.setLayers(Collections.emptyList());

        assertThrows(NullPointerException.class, () -> classUnderTest.buildPeliasAutocompletePathWith(testData.build()));
    }


}
