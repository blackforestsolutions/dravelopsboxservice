package de.blackforestsolutions.dravelopsboxservice.configuration;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.Point;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TravelPointApiServiceTestConfiguration {

    @Value("${test.apitokens[0].arrivalCoordinateLongitude}")
    private Double arrivalCoordinateLongitude;
    @Value("${test.apitokens[0].arrivalCoordinateLatitude}")
    private Double arrivalCoordinateLatitude;

    @Bean
    @ConfigurationProperties(prefix = "test.apitokens[0]")
    public ApiToken.ApiTokenBuilder travelPointApiToken() {
        return new ApiToken.ApiTokenBuilder()
                .setArrivalCoordinate(new Point.PointBuilder(arrivalCoordinateLongitude, arrivalCoordinateLatitude).build());
    }
}