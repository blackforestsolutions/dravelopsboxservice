package de.blackforestsolutions.dravelopsboxservice.configuration;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class StationPersistenceApiTestConfiguration {

    @Value("${stationpersistence.protocol}")
    private String protocol;
    @Value("${stationpersistence.host}")
    private String host;
    @Value("${stationpersistence.port}")
    private int port;
    @Value("${stationpersistence.get.box.path}")
    private String stationPersistenceBoxPath;


    @Bean
    public ApiToken.ApiTokenBuilder stationPersistenceBoxApiTokenIT() {
        return new ApiToken.ApiTokenBuilder()
                .setProtocol(protocol)
                .setHost(host)
                .setPort(port)
                .setPath(stationPersistenceBoxPath);
    }
}
