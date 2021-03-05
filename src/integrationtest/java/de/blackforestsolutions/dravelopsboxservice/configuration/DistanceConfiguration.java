package de.blackforestsolutions.dravelopsboxservice.configuration;

import de.blackforestsolutions.dravelopsboxservice.configuration.converter.DistanceConverter;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class DistanceConfiguration {

    @Bean
    @ConfigurationPropertiesBinding
    public DistanceConverter distanceConverter() {
        return new DistanceConverter();
    }
}
