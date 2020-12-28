package de.blackforestsolutions.dravelopspolygonservice.configuration;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.Locale;

@TestConfiguration
public class TravelPointApiServiceTestConfiguration {

    @Value("${test.apitokens[0].language}")
    private Locale language;

    @Bean
    @ConfigurationProperties(prefix = "test.apitokens[0]")
    public ApiToken.ApiTokenBuilder polygonApiToken() {
        return new ApiToken.ApiTokenBuilder()
                .setLanguage(language);
    }
}