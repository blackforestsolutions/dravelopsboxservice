package de.blackforestsolutions.dravelopsboxservice.configuration;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.List;

import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.BoxObjectMother.getBoxServiceStartBox;

@TestConfiguration
@Import(TravelPointApiServiceTestConfiguration.class)
public class PeliasTestConfiguration {

    @Value("${graphql.playground.tabs.ADDRESS_AUTOCOMPLETION.maxResults}")
    private int autocompleteMaxResults;
    @Value("${graphql.playground.tabs.ADDRESS_AUTOCOMPLETION.layers}")
    private List<String> autocompleteLayers;
    @Value("${graphql.playground.tabs.NEAREST_ADDRESSES.maxResults}")
    private int nearestAddressesMaxResults;
    @Value("${graphql.playground.tabs.NEAREST_ADDRESSES.layers}")
    private List<String> nearestAddressesLayers;

    @Bean
    @ConfigurationProperties(prefix = "pelias")
    public ApiToken peliasTestAutocompleteApiToken(@Autowired ApiToken travelPointApiToken) {
        ApiToken apiToken = new ApiToken(travelPointApiToken);

        apiToken.setMaxResults(autocompleteMaxResults);
        apiToken.setLayers(autocompleteLayers);
        apiToken.setBox(getBoxServiceStartBox());

        return apiToken;
    }

    @Bean
    @ConfigurationProperties(prefix = "pelias")
    public ApiToken peliasTestNearestAddressesApiToken(@Autowired ApiToken travelPointApiToken) {
        ApiToken apiToken = new ApiToken(travelPointApiToken);

        apiToken.setMaxResults(nearestAddressesMaxResults);
        apiToken.setLayers(nearestAddressesLayers);
        apiToken.setBox(getBoxServiceStartBox());

        return apiToken;
    }
}
