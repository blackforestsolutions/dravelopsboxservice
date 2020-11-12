package de.blackforestsolutions.dravelopspolygonservice.configuration;

import de.blackforestsolutions.dravelopsdatamodel.util.ApiToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.Locale;

@TestConfiguration
public class PeliasTestConfiguration {

    @Value("${test.apitokens[0].departure}")
    private String departure;
    @Value("${test.apitokens[0].language}")
    private Locale language;

    @Bean
    @ConfigurationProperties(prefix = "pelias")
    public ApiToken.ApiTokenBuilder peliasAutocompleteApiToken() {
        return new ApiToken.ApiTokenBuilder()
                .setDeparture(departure)
                .setLanguage(language);
    }
}
