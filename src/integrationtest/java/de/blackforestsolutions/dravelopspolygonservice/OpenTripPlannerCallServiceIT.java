package de.blackforestsolutions.dravelopspolygonservice;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsgeneratedcontent.opentripplanner.polygon.OpenTripPlannerPolygonResponse;
import de.blackforestsolutions.dravelopspolygonservice.service.callbuilderservice.OpenTripPlannerHttpCallBuilderService;
import de.blackforestsolutions.dravelopspolygonservice.service.communicationservice.restcalls.CallService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static de.blackforestsolutions.dravelopsdatamodel.util.DravelOpsHttpCallBuilder.buildUrlWith;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OpenTripPlannerCallServiceIT {

    @Autowired
    private OpenTripPlannerHttpCallBuilderService httpCallBuilderService;

    @Autowired
    private CallService callService;

    @Autowired
    private ApiToken openTripPlannerApiToken;

    @Test
    void test_box() {
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(openTripPlannerApiToken);
        testData.setPath(httpCallBuilderService.buildOpenTripPlannerPolygonPathWith(testData.build()));

        Mono<OpenTripPlannerPolygonResponse> result = callService.getOne(buildUrlWith(testData.build()).toString(), HttpHeaders.EMPTY, OpenTripPlannerPolygonResponse.class);

        StepVerifier.create(result)
                .assertNext(openTripPlannerPolygonResponse -> assertThat(openTripPlannerPolygonResponse.getPolygon().getCoordinates().get(0).size()).isGreaterThan(4))
                .verifyComplete();
    }
}
