package de.blackforestsolutions.dravelopsboxservice.configuration;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.Layer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;

import java.util.LinkedHashMap;

@RefreshScope
@SpringBootConfiguration
public class PeliasConfiguration {

    @Value("${pelias.protocol}")
    private String protocol;
    @Value("${pelias.host}")
    private String host;
    @Value("${pelias.port}")
    private int port;
    @Value("${pelias.apiVersion}")
    private String apiVersion;
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
    private Boolean nearestAddressesLayerHasVenue;
    @Value("${graphql.playground.tabs.NEAREST_ADDRESSES.layers.hasAddress}")
    private Boolean nearestAddressesLayerHasAddress;
    @Value("${graphql.playground.tabs.NEAREST_ADDRESSES.layers.hasStreet}")
    private Boolean nearestAddressesLayerHasStreet;
    @Value("${graphql.playground.tabs.NEAREST_ADDRESSES.layers.hasLocality}")
    private Boolean nearestAddressesLayerHasLocality;


    @RefreshScope
    @Bean
    public ApiToken peliasAutocompleteApiToken() {
        LinkedHashMap<Layer, Boolean> layers = buildLayersMapWith(
                autocompleteLayerHasVenue,
                autocompleteLayerHasAddress,
                autocompleteLayerHasStreet,
                autocompleteLayerHasLocality
        );

        return buildApiTokenBy(autocompleteMaxResults, layers);
    }

    @RefreshScope
    @Bean
    public ApiToken peliasNearestAddressesApiToken() {
        LinkedHashMap<Layer, Boolean> layers = buildLayersMapWith(
                nearestAddressesLayerHasVenue,
                nearestAddressesLayerHasAddress,
                nearestAddressesLayerHasStreet,
                nearestAddressesLayerHasLocality
        );

        return buildApiTokenBy(nearestAddressesMaxResults, layers);
    }

    private ApiToken buildApiTokenBy(int maxResults, LinkedHashMap<Layer, Boolean> layers) {
        ApiToken apiToken = new ApiToken();

        apiToken.setProtocol(protocol);
        apiToken.setHost(host);
        apiToken.setPort(port);
        apiToken.setApiVersion(apiVersion);
        apiToken.setMaxResults(maxResults);
        apiToken.setLayers(layers);

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
