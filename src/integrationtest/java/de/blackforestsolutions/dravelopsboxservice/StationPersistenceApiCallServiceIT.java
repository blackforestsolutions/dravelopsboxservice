package de.blackforestsolutions.dravelopsboxservice;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.Box;
import de.blackforestsolutions.dravelopsboxservice.configuration.StationPersistenceApiTestConfiguration;
import de.blackforestsolutions.dravelopsboxservice.service.communicationservice.restcalls.CallService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.annotation.DirtiesContext;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static de.blackforestsolutions.dravelopsdatamodel.util.DravelOpsHttpCallBuilder.buildUrlWith;
import static org.assertj.core.api.Assertions.assertThat;

@Import(StationPersistenceApiTestConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class StationPersistenceApiCallServiceIT {

    private static final double MIN_WGS_84_LONGITUDE = -180.0d;
    private static final double MAX_WGS_84_LONGITUDE = 180.0d;
    private static final double MIN_WGS_84_LATITUDE = -90.0d;
    private static final double MAX_WGS_84_LATITUDE = 90.0d;

    @Autowired
    private CallService classUnderTest;

    @Autowired
    private ApiToken.ApiTokenBuilder stationPersistenceBoxApiTokenIT;

    @Test
    void test_getOneReactive_box_returns_a_correct_box() {
        ApiToken testData = stationPersistenceBoxApiTokenIT.build();

        Mono<Box> result = classUnderTest.getOneReactive(buildUrlWith(testData).toString(), HttpHeaders.EMPTY, Box.class);

        StepVerifier.create(result)
                .assertNext(box -> {
                    assertThat(box.getTopLeft()).isNotNull();
                    assertThat(box.getBottomRight()).isNotNull();
                    assertThat(box.getTopLeft().getX()).isGreaterThanOrEqualTo(MIN_WGS_84_LONGITUDE);
                    assertThat(box.getTopLeft().getY()).isLessThanOrEqualTo(MAX_WGS_84_LATITUDE);
                    assertThat(box.getBottomRight().getX()).isLessThanOrEqualTo(MAX_WGS_84_LONGITUDE);
                    assertThat(box.getBottomRight().getY()).isGreaterThanOrEqualTo(MIN_WGS_84_LATITUDE);
                })
                .verifyComplete();
    }
}
