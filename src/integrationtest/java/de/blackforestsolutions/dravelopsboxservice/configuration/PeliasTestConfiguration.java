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

    @Value("${graphql.playground.tabs[2].maxResults}")
    private int autocompleteMaxResults;
    @Value("${graphql.playground.tabs[2].layers}")
    private List<String> autocompleteLayers;
    @Value("${graphql.playground.tabs[3].maxResults}")
    private int nearestAddressesMaxResults;
    @Value("${graphql.playground.tabs[3].layers}")
    private List<String> nearestAddressesLayers;

    @Bean
    @ConfigurationProperties(prefix = "pelias")
    public ApiToken.ApiTokenBuilder peliasTestAutocompleteApiToken(@Autowired ApiToken travelPointApiToken) {
        return new ApiToken.ApiTokenBuilder(travelPointApiToken)
                .setMaxResults(autocompleteMaxResults)
                .setLayers(autocompleteLayers)
                .setBox(getBoxServiceStartBox());
    }

    @Bean
    @ConfigurationProperties(prefix = "pelias")
    public ApiToken.ApiTokenBuilder peliasTestNearestAddressesApiToken(@Autowired ApiToken travelPointApiToken) {
        return new ApiToken.ApiTokenBuilder(travelPointApiToken)
                .setMaxResults(nearestAddressesMaxResults)
                .setLayers(nearestAddressesLayers)
                .setBox(getBoxServiceStartBox());
    }
}
