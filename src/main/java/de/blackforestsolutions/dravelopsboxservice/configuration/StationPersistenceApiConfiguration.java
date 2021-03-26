package de.blackforestsolutions.dravelopsboxservice.configuration;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.Box;
import de.blackforestsolutions.dravelopsdatamodel.Point;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static de.blackforestsolutions.dravelopsboxservice.configuration.GeocodingConfiguration.*;

@RefreshScope
@Configuration
public class StationPersistenceApiConfiguration {

    @Value("${stationpersistence.protocol}")
    private String stationPersistenceProtocol;
    @Value("${stationpersistence.host}")
    private String stationPersistenceHost;
    @Value("${stationpersistence.port}")
    private int stationPersistencePort;
    @Value("${stationpersistence.get.box.path}")
    private String stationPersistenceBoxPath;
    @Value("${stationpersistence.retry.time.seconds}")
    private long retryTimeInSeconds;

    @RefreshScope
    @Bean
    public ApiToken stationPersistenceBoxApiToken() {
        ApiToken apiToken = new ApiToken();

        apiToken.setProtocol(stationPersistenceProtocol);
        apiToken.setHost(stationPersistenceHost);
        apiToken.setPort(stationPersistencePort);
        apiToken.setPath(stationPersistenceBoxPath);
        apiToken.setRetryTimeInSeconds(retryTimeInSeconds);

        return apiToken;
    }

    @Bean
    public Box stationPersistenceBox() {
        return new Box.BoxBuilder(
                new Point.PointBuilder(MIN_WGS_84_LONGITUDE, MAX_WGS_84_LATITUDE).build(),
                new Point.PointBuilder(MAX_WGS_84_LONGITUDE, MIN_WGS_84_LATITUDE).build()
        ).build();
    }
}
