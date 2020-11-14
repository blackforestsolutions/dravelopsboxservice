package de.blackforestsolutions.dravelopspolygonservice.configuration;

import de.blackforestsolutions.dravelopsdatamodel.util.ApiToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.geo.Box;
import org.springframework.data.geo.Point;

import java.util.Locale;

@TestConfiguration
public class PeliasTestConfiguration {

    @Value("${test.apitokens[0].departure}")
    private String departure;
    @Value("${test.apitokens[0].language}")
    private Locale language;
    @Value("${test.apitokens[0].box[0].x}")
    private Double firstDepartureLongitude;
    @Value("${test.apitokens[0].box[0].y}")
    private Double firstDepartureLatitude;
    @Value("${test.apitokens[0].box[1].x}")
    private Double secondDepartureLongitude;
    @Value("${test.apitokens[0].box[1].y}")
    private Double secondDepartureLatitude;

    @Bean
    @ConfigurationProperties(prefix = "pelias")
    public ApiToken.ApiTokenBuilder peliasAutocompleteApiToken() {
        return new ApiToken.ApiTokenBuilder()
                .setDeparture(departure)
                .setLanguage(language)
                .setBox(new Box(
                        new Point(firstDepartureLongitude, firstDepartureLatitude),
                        new Point(secondDepartureLongitude, secondDepartureLatitude)
                ));
    }
}
