package de.blackforestsolutions.dravelopsboxservice.configuration;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.Layer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.LinkedHashMap;

import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.BoxObjectMother.getBoxServiceStartBox;

@TestConfiguration
@Import(TravelPointApiServiceTestConfiguration.class)
public class PeliasTestConfiguration {

    @Value("${graphql.playground.tabs.ADDRESS_AUTOCOMPLETION.maxResults}")
    private int autocompleteMaxResults;
    @Value("${graphql.playground.tabs.ADDRESS_AUTOCOMPLETION.layers.hasVenue}")
    private Boolean autocompleteLayerHasVenue;
    @Value("${graphql.playground.tabs.ADDRESS_AUTOCOMPLETION.layers.hasAddress}")
    private Boolean autocompleteLayerHasAddress;
    @Value("${graphql.playground.tabs.ADDRESS_AUTOCOMPLETION.layers.hasStreet}")
    private Boolean autocompleteLayerHasStreet;
    @Value("${graphql.playground.tabs.ADDRESS_AUTOCOMPLETION.layers.hasLocality}")
    private Boolean autocompleteLayerHasLocality;
    @Value("${graphql.playground.tabs.NEAREST_ADDRESSES.maxResults}")
    private int nearestAddressesMaxResults;
    @Value("${graphql.playground.tabs.NEAREST_ADDRESSES.layers.hasVenue}")
    private Boolean nearestAddressesHasVenue;
    @Value("${graphql.playground.tabs.NEAREST_ADDRESSES.layers.hasAddress}")
    private Boolean nearestAddressesLayerHasAddress;
    @Value("${graphql.playground.tabs.NEAREST_ADDRESSES.layers.hasStreet}")
    private Boolean nearestAddressesLayerHasStreet;
    @Value("${graphql.playground.tabs.NEAREST_ADDRESSES.layers.hasLocality}")
    private Boolean nearestAddressesLayerHasLocality;

    @Bean
    @ConfigurationProperties(prefix = "pelias")
    public ApiToken peliasTestAutocompleteApiToken(@Autowired ApiToken travelPointApiToken) {
        ApiToken apiToken = new ApiToken(travelPointApiToken);

        apiToken.setMaxResults(autocompleteMaxResults);
        apiToken.setLayers(buildLayersMapWith(
                autocompleteLayerHasVenue,
                autocompleteLayerHasAddress,
                autocompleteLayerHasStreet,
                autocompleteLayerHasLocality
        ));
        apiToken.setBox(getBoxServiceStartBox());

        return apiToken;
    }

    @Bean
    @ConfigurationProperties(prefix = "pelias")
    public ApiToken peliasTestNearestAddressesApiToken(@Autowired ApiToken travelPointApiToken) {
        ApiToken apiToken = new ApiToken(travelPointApiToken);

        apiToken.setMaxResults(nearestAddressesMaxResults);
        apiToken.setLayers(buildLayersMapWith(
                nearestAddressesHasVenue,
                nearestAddressesLayerHasAddress,
                nearestAddressesLayerHasStreet,
                nearestAddressesLayerHasLocality
        ));
        apiToken.setBox(getBoxServiceStartBox());

        return apiToken;
    }

    private LinkedHashMap<Layer, Boolean> buildLayersMapWith(boolean hasVenue, boolean hasAddress, boolean hasStreet, boolean hasLocality) {
        LinkedHashMap<Layer, Boolean> layers = new LinkedHashMap<>();

        layers.put(Layer.HAS_VENUE, hasVenue);
        layers.put(Layer.HAS_ADDRESS, hasAddress);
        layers.put(Layer.HAS_STREET, hasStreet);
        layers.put(Layer.HAS_LOCALITY, hasLocality);

        return layers;
    }
}
