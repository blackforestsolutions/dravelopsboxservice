package de.blackforestsolutions.dravelopsboxservice.configuration;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;

import java.util.List;

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
    @Value("${graphql.playground.tabs[2].maxResults}")
    private int autocompleteMaxResults;
    @Value("${graphql.playground.tabs[2].layers}")
    private List<String> autocompleteLayers;
    @Value("${graphql.playground.tabs[3].maxResults}")
    private int nearestAddressesMaxResults;
    @Value("${graphql.playground.tabs[3].layers}")
    private List<String> nearestAddressesLayers;


    @RefreshScope
    @Bean
    public ApiToken peliasAutocompleteApiToken() {
        return buildApiTokenBy(autocompleteMaxResults, autocompleteLayers);
    }

    @RefreshScope
    @Bean
    public ApiToken peliasNearestAddressesApiToken() {
        return buildApiTokenBy(nearestAddressesMaxResults, nearestAddressesLayers);
    }

    private ApiToken buildApiTokenBy(int maxResults, List<String> layers) {
        ApiToken apiToken = new ApiToken();

        apiToken.setProtocol(protocol);
        apiToken.setHost(host);
        apiToken.setPort(port);
        apiToken.setApiVersion(apiVersion);
        apiToken.setMaxResults(maxResults);
        apiToken.setLayers(layers);

        return apiToken;
    }
}
