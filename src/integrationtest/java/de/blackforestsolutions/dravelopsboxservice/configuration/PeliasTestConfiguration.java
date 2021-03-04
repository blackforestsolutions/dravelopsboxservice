package de.blackforestsolutions.dravelopsboxservice.configuration;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.Box;
import de.blackforestsolutions.dravelopsdatamodel.Point;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;

import java.util.Locale;

@TestConfiguration
public class PeliasTestConfiguration {

    @Value("${test.apitokens[0].departure}")
    private String departure;
    @Value("${test.apitokens[0].language}")
    private Locale language;
    @Value("${test.apitokens[0].arrivalCoordinateLongitude}")
    private double coordinateLongitude;
    @Value("${test.apitokens[0].arrivalCoordinateLatitude}")
    private double coordinateLatitude;
    @Value("${test.apitokens[0].box.leftTop.x}")
    private Double leftTopLongitude;
    @Value("${test.apitokens[0].box.leftTop.y}")
    private Double leftTopLatitude;
    @Value("${test.apitokens[0].box.bottomRight.x}")
    private Double bottomRightLongitude;
    @Value("${test.apitokens[0].box.bottomRight.y}")
    private Double bottomRightLatitude;
    @Value("${test.apitokens[0].radiusInKilometers}")
    private Double radiusInKilometers;

    @Bean
    @ConfigurationProperties(prefix = "pelias")
    public ApiToken.ApiTokenBuilder peliasTestApiToken() {
        return new ApiToken.ApiTokenBuilder()
                .setDeparture(departure)
                .setLanguage(language)
                .setArrivalCoordinate(new de.blackforestsolutions.dravelopsdatamodel.Point.PointBuilder(coordinateLongitude, coordinateLatitude).build())
                .setRadiusInKilometers(new Distance(radiusInKilometers, Metrics.KILOMETERS))
                .setBox(new Box.BoxBuilder(
                        new Point.PointBuilder(leftTopLongitude, leftTopLatitude).build(),
                        new Point.PointBuilder(bottomRightLongitude, bottomRightLatitude).build()
                ).build());
    }
}
