package de.blackforestsolutions.dravelopspolygonservice.configuration;

import de.blackforestsolutions.dravelopsdatamodel.util.ApiToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.geo.Box;
import org.springframework.data.geo.Point;
import org.springframework.data.geo.Polygon;

import java.util.Collections;

@SpringBootConfiguration
public class OpenTripPlannerConfiguration {

    @Value("${otp.protocol}")
    private String protocol;
    @Value("${otp.host}")
    private String host;
    @Value("${otp.port}")
    private int port;
    @Value("${otp.router}")
    private String router;

    @Bean(name = "openTripPlannerApiToken")
    public ApiToken apiToken() {
        return new ApiToken.ApiTokenBuilder()
                .setProtocol(protocol)
                .setHost(host)
                .setPort(port)
                .setRouter(router)
                .build();
    }

    @Bean(name = "openTripPlannerBox")
    public Box polygon() {
        return new Box(new Point(0d, 0d), new Point(0d, 0d));
    }

}
