package de.blackforestsolutions.dravelopsboxservice.configuration;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.List;

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


    @Bean
    public ApiToken peliasAutocompleteApiToken() {
        return new ApiToken.ApiTokenBuilder()
                .setProtocol(protocol)
                .setHost(host)
                .setPort(port)
                .setApiVersion(apiVersion)
                .setMaxResults(autocompleteMaxResults)
                .setLayers(autocompleteLayers)
                .build();
    }

    @Bean
    public ApiToken peliasNearestAddressesApiToken() {
        return new ApiToken.ApiTokenBuilder()
                .setProtocol(protocol)
                .setHost(host)
                .setPort(port)
                .setApiVersion(apiVersion)
                .setMaxResults(nearestAddressesMaxResults)
                .setLayers(nearestAddressesLayers)
                .build();
    }
}
