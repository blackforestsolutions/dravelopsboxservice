package de.blackforestsolutions.dravelopsboxservice.configuration;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.Box;
import de.blackforestsolutions.dravelopsdatamodel.Point;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static de.blackforestsolutions.dravelopsboxservice.configuration.GeocodingConfiguration.*;

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

    @Bean
    public ApiToken stationPersistenceBoxApiToken() {
        return new ApiToken.ApiTokenBuilder()
                .setProtocol(stationPersistenceProtocol)
                .setHost(stationPersistenceHost)
                .setPort(stationPersistencePort)
                .setPath(stationPersistenceBoxPath)
                .setRetryTimeInSeconds(retryTimeInSeconds)
                .build();
    }

    @Bean
    public Box stationPersistenceBox() {
        return new Box.BoxBuilder(
                new Point.PointBuilder(MIN_WGS_84_LONGITUDE, MAX_WGS_84_LATITUDE).build(),
                new Point.PointBuilder(MAX_WGS_84_LONGITUDE, MIN_WGS_84_LATITUDE).build()
        ).build();
    }
}
