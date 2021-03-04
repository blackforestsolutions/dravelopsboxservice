package de.blackforestsolutions.dravelopsboxservice;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.Point;
import de.blackforestsolutions.dravelopsgeneratedcontent.pelias.PeliasTravelPointResponse;
import de.blackforestsolutions.dravelopsboxservice.configuration.PeliasTestConfiguration;
import de.blackforestsolutions.dravelopsboxservice.service.callbuilderservice.PeliasHttpCallBuilderService;
import de.blackforestsolutions.dravelopsboxservice.service.communicationservice.restcalls.CallService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClientResponseException;
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
    private ApiToken.ApiTokenBuilder peliasTestApiToken;

    @Test
    void test_peliasAutocompleteCall_returns_more_than_one_results_and_correct_query_params() {
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(peliasTestApiToken.build());
        testData.setPath(peliasHttpCallBuilderService.buildPeliasAutocompletePathWith(testData.build()));

        Mono<PeliasTravelPointResponse> result = callService.getOne(buildUrlWith(testData.build()).toString(), HttpHeaders.EMPTY, PeliasTravelPointResponse.class);

        StepVerifier.create(result)
                .assertNext(peliasTravelPointResponse -> {
                    assertThat(peliasTravelPointResponse.getGeocoding().getQuery().getSize()).isEqualTo((Long.valueOf(testData.getMaxResults())));
                    assertThat(peliasTravelPointResponse.getGeocoding().getQuery().getText()).isEqualTo(testData.getDeparture());
                    assertThat(peliasTravelPointResponse.getGeocoding().getQuery().getLang().getIso6391()).isEqualTo(testData.getLanguage().getLanguage());
                    assertThat(peliasTravelPointResponse.getFeatures().size()).isGreaterThan(0);
                    assertThat(peliasTravelPointResponse.getFeatures().size()).isLessThanOrEqualTo(testData.getMaxResults());
                })
                .verifyComplete();
    }

    @Test
    void test_peliasAutocompleteCall_returns_no_result_with_unknown_search_text() {
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(peliasTestApiToken);
        testData.setDeparture("Noooooooooooooo expected Result");
        testData.setPath(peliasHttpCallBuilderService.buildPeliasAutocompletePathWith(testData.build()));

        Mono<PeliasTravelPointResponse> result = callService.getOne(buildUrlWith(testData.build()).toString(), HttpHeaders.EMPTY, PeliasTravelPointResponse.class);

        StepVerifier.create(result)
                .assertNext(peliasTravelPointResponse -> {
                    assertThat(peliasTravelPointResponse.getGeocoding().getQuery().getSize()).isEqualTo((Long.valueOf(testData.getMaxResults())));
                    assertThat(peliasTravelPointResponse.getGeocoding().getQuery().getText()).isEqualTo(testData.getDeparture());
                    assertThat(peliasTravelPointResponse.getGeocoding().getQuery().getLang().getIso6391()).isEqualTo(testData.getLanguage().getLanguage());
                    assertThat(peliasTravelPointResponse.getFeatures().size()).isEqualTo(0);
                    assertThat(peliasTravelPointResponse.getFeatures().size()).isLessThanOrEqualTo(testData.getMaxResults());
                })
                .verifyComplete();
    }

    @Test
    void test_peliasReverseCall_returns_more_than_one_results_and_correct_query_params() {
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(peliasTestApiToken);
        testData.setPath(peliasHttpCallBuilderService.buildPeliasReversePathWith(peliasTestApiToken.build()));

        Mono<PeliasTravelPointResponse> result = callService.getOne(buildUrlWith(testData.build()).toString(), HttpHeaders.EMPTY, PeliasTravelPointResponse.class);

        StepVerifier.create(result)
                .assertNext(peliasTravelPointResponse -> {
                    assertThat(peliasTravelPointResponse.getGeocoding().getQuery().getSize()).isEqualTo((Long.valueOf(testData.getMaxResults())));
                    assertThat(peliasTravelPointResponse.getGeocoding().getQuery().getPointLon()).isEqualTo(testData.getArrivalCoordinate().getX());
                    assertThat(peliasTravelPointResponse.getGeocoding().getQuery().getPointLat()).isEqualTo(testData.getArrivalCoordinate().getY());
                    assertThat(peliasTravelPointResponse.getGeocoding().getQuery().getLang().getIso6391()).isEqualTo(testData.getLanguage().getLanguage());
                    assertThat(peliasTravelPointResponse.getFeatures().size()).isGreaterThan(0);
                    assertThat(peliasTravelPointResponse.getFeatures().size()).isLessThanOrEqualTo(testData.getMaxResults());
                })
                .verifyComplete();
    }

    @Test
    void test_peliasReverseCall_returns_no_result_with_unknown_coordinates() {
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(peliasTestApiToken);
        testData.setArrivalCoordinate(new Point.PointBuilder(0.0d, 0.0d).build());
        testData.setRadiusInKilometers(1);
        testData.setPath(peliasHttpCallBuilderService.buildPeliasReversePathWith(testData.build()));

        Mono<PeliasTravelPointResponse> result = callService.getOne(buildUrlWith(testData.build()).toString(), HttpHeaders.EMPTY, PeliasTravelPointResponse.class);

        StepVerifier.create(result)
                .assertNext(peliasTravelPointResponse -> {
                    assertThat(peliasTravelPointResponse.getGeocoding().getQuery().getSize()).isEqualTo((Long.valueOf(testData.getMaxResults())));
                    assertThat(peliasTravelPointResponse.getGeocoding().getQuery().getPointLon()).isEqualTo(testData.getArrivalCoordinate().getX());
                    assertThat(peliasTravelPointResponse.getGeocoding().getQuery().getPointLat()).isEqualTo(testData.getArrivalCoordinate().getY());
                    assertThat(peliasTravelPointResponse.getGeocoding().getQuery().getLang().getIso6391()).isEqualTo(testData.getLanguage().getLanguage());
                    assertThat(peliasTravelPointResponse.getFeatures().size()).isEqualTo(0);
                    assertThat(peliasTravelPointResponse.getFeatures().size()).isLessThanOrEqualTo(testData.getMaxResults());
                })
                .verifyComplete();
    }

    @Test
    void test_peliasReverseCall_with_wrong_path() {
        String testUrl = "wrongPath";
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(peliasTestApiToken);
        testData.setPath(testUrl);

        Mono<PeliasTravelPointResponse> result = callService.getOne(buildUrlWith(testData.build()).toString(), HttpHeaders.EMPTY, PeliasTravelPointResponse.class);

        StepVerifier.create(result)
                .expectError(WebClientResponseException.class)
                .verify();
    }


}
