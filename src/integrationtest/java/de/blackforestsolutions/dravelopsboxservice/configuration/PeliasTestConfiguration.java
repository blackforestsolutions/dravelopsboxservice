package de.blackforestsolutions.dravelopsboxservice.configuration;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.BoxObjectMother.getBoxServiceStartBox;

@TestConfiguration
@Import(TravelPointApiServiceTestConfiguration.class)
public class PeliasTestConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "pelias")
    public ApiToken.ApiTokenBuilder peliasTestApiToken(@Autowired ApiToken travelPointApiToken) {
        return new ApiToken.ApiTokenBuilder(travelPointApiToken)
                .setBox(getBoxServiceStartBox());
    }
}
