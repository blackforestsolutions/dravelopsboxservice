package de.blackforestsolutions.dravelopsboxservice.configuration;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;

import java.util.List;

@RefreshScope
@SpringBootConfiguration
public class PeliasConfiguration {

    @Value("${pelias.protocol}")
    private String protocol;
    @Value("${pelias.host}")
    private String host;
    @Value("${pelias.port}")
    private int port;
    @Value("${pelias.apiVersion}")
    private String apiVersion;
    @Value("${pelias.maxResults}")
    private int maxResults;
    @Value("${pelias.layers}")
    private List<String> layers;


    @RefreshScope
    @Bean
    public ApiToken peliasApiToken() {
        ApiToken apiToken = new ApiToken();

        apiToken.setProtocol(protocol);
        apiToken.setHost(host);
        apiToken.setPort(port);
        apiToken.setApiVersion(apiVersion);
        apiToken.setMaxResults(maxResults);
        apiToken.setLayers(layers);

        return apiToken;
    }
}
