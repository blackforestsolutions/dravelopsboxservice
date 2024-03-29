package de.blackforestsolutions.dravelopsboxservice.configuration;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.Point;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;

import java.util.Locale;

@TestConfiguration
public class TravelPointApiServiceTestConfiguration {

    @Value("${graphql.playground.tabs.ADDRESS_AUTOCOMPLETION.variables.text}")
    private String text;
    @Value("${graphql.playground.tabs.ADDRESS_AUTOCOMPLETION.variables.language}")
    private Locale language;
    @Value("${graphql.playground.tabs.NEAREST_ADDRESSES.variables.longitude}")
    private Double longitude;
    @Value("${graphql.playground.tabs.NEAREST_ADDRESSES.variables.latitude}")
    private Double latitude;
    @Value("${graphql.playground.tabs.NEAREST_ADDRESSES.variables.radiusInKilometers}")
    private Double radiusInKilometers;


    @Bean
    public ApiToken travelPointApiToken() {
        ApiToken apiToken = new ApiToken();

        apiToken.setDeparture(text);
        apiToken.setLanguage(language);
        apiToken.setArrivalCoordinate(new Point.PointBuilder(longitude, latitude).build());
        apiToken.setRadiusInKilometers(new Distance(radiusInKilometers, Metrics.KILOMETERS));

        return apiToken;
    }
}