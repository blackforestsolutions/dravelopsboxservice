package de.blackforestsolutions.dravelopspolygonservice.configuration;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.Box;
import de.blackforestsolutions.dravelopsdatamodel.Point;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StationPersistenceApiConfiguration {

    private static final double BOX_START_VALUE = 0.0d;

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
                new Point.PointBuilder(BOX_START_VALUE, BOX_START_VALUE).build(),
                new Point.PointBuilder(BOX_START_VALUE, BOX_START_VALUE).build()
        ).build();
    }
}
