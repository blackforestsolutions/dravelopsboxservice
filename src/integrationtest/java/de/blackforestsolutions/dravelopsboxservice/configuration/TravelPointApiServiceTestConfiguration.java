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

    @Value("${graphql.playground.tabs[2].variables.text}")
    private String text;
    @Value("${graphql.playground.tabs[2].variables.language}")
    private Locale language;
    @Value("${graphql.playground.tabs[3].variables.longitude}")
    private Double longitude;
    @Value("${graphql.playground.tabs[3].variables.latitude}")
    private Double latitude;
    @Value("${graphql.playground.tabs[3].variables.radiusInKilometers}")
    private Double radiusInKilometers;


    @Bean
    public ApiToken travelPointApiToken() {
        return new ApiToken.ApiTokenBuilder()
                .setDeparture(text)
                .setLanguage(language)
                .setArrivalCoordinate(new Point.PointBuilder(longitude, latitude).build())
                .setRadiusInKilometers(new Distance(radiusInKilometers, Metrics.KILOMETERS))
                .build();
    }
}