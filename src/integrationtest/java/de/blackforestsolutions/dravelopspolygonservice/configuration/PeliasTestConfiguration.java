package de.blackforestsolutions.dravelopspolygonservice.configuration;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.Box;
import de.blackforestsolutions.dravelopsdatamodel.Point;
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
    @Value("${test.apitokens[0].box.leftTop.x}")
    private Double leftTopLongitude;
    @Value("${test.apitokens[0].box.leftTop.y}")
    private Double leftTopLatitude;
    @Value("${test.apitokens[0].box.bottomRight.x}")
    private Double bottomRightLongitude;
    @Value("${test.apitokens[0].box.bottomRight.y}")
    private Double bottomRightLatitude;

    @Bean
    @ConfigurationProperties(prefix = "pelias")
    public ApiToken.ApiTokenBuilder peliasAutocompleteApiToken() {
        return new ApiToken.ApiTokenBuilder()
                .setDeparture(departure)
                .setLanguage(language)
                .setBox(new Box.BoxBuilder(
                        new Point.PointBuilder(leftTopLongitude, leftTopLatitude).build(),
                        new Point.PointBuilder(bottomRightLongitude, bottomRightLatitude).build()
                ).build());
    }
}
