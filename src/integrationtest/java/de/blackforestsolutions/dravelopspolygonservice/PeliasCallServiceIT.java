package de.blackforestsolutions.dravelopspolygonservice;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsgeneratedcontent.pelias.PeliasTravelPointResponse;
import de.blackforestsolutions.dravelopspolygonservice.configuration.PeliasTestConfiguration;
import de.blackforestsolutions.dravelopspolygonservice.service.callbuilderservice.PeliasHttpCallBuilderService;
import de.blackforestsolutions.dravelopspolygonservice.service.communicationservice.restcalls.CallService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static de.blackforestsolutions.dravelopsdatamodel.util.DravelOpsHttpCallBuilder.buildUrlWith;
import static org.assertj.core.api.Assertions.assertThat;

@Import(PeliasTestConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PeliasCallServiceIT {

    @Autowired
    private PeliasHttpCallBuilderService peliasHttpCallBuilderService;

    @Autowired
    private CallService callService;

    @Autowired
    private ApiToken.ApiTokenBuilder peliasAutocompleteApiToken;

    @Test
    void test_travelPoints() {
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(peliasAutocompleteApiToken.build());
        testData.setPath(peliasHttpCallBuilderService.buildPeliasAutocompletePathWith(testData.build()));

        Mono<PeliasTravelPointResponse> result = callService.getOne(buildUrlWith(testData.build()).toString(), HttpHeaders.EMPTY, PeliasTravelPointResponse.class);

        StepVerifier.create(result)
                .assertNext(peliasTravelPointResponse -> {
                    assertThat(peliasTravelPointResponse.getGeocoding().getQuery().getLang().getIso6391()).isEqualTo(testData.getLanguage().getLanguage());
                    assertThat(peliasTravelPointResponse.getFeatures().size()).isGreaterThan(0);
                    assertThat(peliasTravelPointResponse.getFeatures().size()).isLessThanOrEqualTo(testData.getMaxResults());
                })
                .verifyComplete();
    }

    @Test
    void test_travelPoints_returns_no_result_with_unknown_search_text() {
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(peliasAutocompleteApiToken.build());
        testData.setDeparture("Noooooooooooooo expected Result");
        testData.setPath(peliasHttpCallBuilderService.buildPeliasAutocompletePathWith(testData.build()));

        Mono<PeliasTravelPointResponse> result = callService.getOne(buildUrlWith(testData.build()).toString(), HttpHeaders.EMPTY, PeliasTravelPointResponse.class);

        StepVerifier.create(result)
                .assertNext(peliasTravelPointResponse -> {
                    assertThat(peliasTravelPointResponse.getGeocoding().getQuery().getLang().getIso6391()).isEqualTo(testData.getLanguage().getLanguage());
                    assertThat(peliasTravelPointResponse.getFeatures().size()).isEqualTo(0);
                    assertThat(peliasTravelPointResponse.getFeatures().size()).isLessThanOrEqualTo(testData.getMaxResults());
                })
                .verifyComplete();
    }


}
