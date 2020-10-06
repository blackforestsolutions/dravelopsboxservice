package de.blackforestsolutions.dravelopspolygonservice.service.polygonservice;

import de.blackforestsolutions.dravelopsdatamodel.util.ApiToken;
import de.blackforestsolutions.dravelopspolygonservice.service.communicationservice.OpenTripPlannerApiService;
import org.awaitility.Awaitility;
import org.awaitility.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.data.geo.Polygon;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import static de.blackforestsolutions.dravelopspolygonservice.objectmothers.ApiTokenObjectMother.getOpenTripPlannerApiToken;
import static de.blackforestsolutions.dravelopspolygonservice.objectmothers.PolygonObjectMother.getPolygon;
import static de.blackforestsolutions.dravelopspolygonservice.testutils.TestUtils.getPropertyFromFileAsString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PolygonServiceTest {

    private final Polygon openTripPlannerPolygon = new Polygon(Collections.emptyList());
    private final ApiToken openTripPlannerApiToken = getOpenTripPlannerApiToken();
    private final OpenTripPlannerApiService openTripPlannerApiService = mock(OpenTripPlannerApiService.class);

    private final PolygonService classUnderTest = new PolygonServiceImpl(openTripPlannerPolygon, openTripPlannerApiToken, openTripPlannerApiService);


    @Test
    void test_cron_from_properties_is_executed_next_time_correctly_relative_to_last_time() throws ParseException {
        // Locale.US as github workflow is apparently not executed in germany
        SimpleDateFormat formatter = new SimpleDateFormat("EE MMM dd HH:mm:ss zzzz yyyy", Locale.US);
        Date lastExecutionTestDate = formatter.parse("Mon Aug 30 00:00:00 CEST 2020");

        String cron = getPropertyFromFileAsString("application-bw-dev.properties", "otp.polygonupdatetime");
        CronTrigger cronUnderTest = new CronTrigger(cron);
        Date result = cronUnderTest.nextExecutionTime(new TriggerContext() {
            @Override
            public Date lastScheduledExecutionTime() {
                return lastExecutionTestDate;
            }

            @Override
            public Date lastActualExecutionTime() {
                return lastExecutionTestDate;
            }

            @Override
            public Date lastCompletionTime() {
                return lastExecutionTestDate;
            }
        });

        assertThat(result.toString()).isEqualTo("Mon Aug 31 00:00:00 CEST 2020");
    }

    @Test
    void test_updateOpenTripPlannerPolygon_updates_polygon_within_service() {
        when(openTripPlannerApiService.extractPolygonBy(any(ApiToken.class)))
                .thenReturn(Mono.just(getPolygon()));

        classUnderTest.updateOpenTripPlannerPolygon();

        Awaitility.await()
                .atMost(Duration.ONE_SECOND)
                .untilAsserted(() -> {
                    Polygon openTripPlannerPolygon = (Polygon) ReflectionTestUtils.getField(classUnderTest, "openTripPlannerPolygon");
                    assertThat(openTripPlannerPolygon.getPoints().size()).isEqualTo(5);
                    assertThat(openTripPlannerPolygon.getPoints()).containsExactly(
                            getPolygon().getPoints().get(0),
                            getPolygon().getPoints().get(1),
                            getPolygon().getPoints().get(2),
                            getPolygon().getPoints().get(3),
                            getPolygon().getPoints().get(4)
                    );
                });
    }

    @Test
    void test_updateOpenTripPlannerPolygon_updates_not_polygon_when_error_is_thrown() {
        when(openTripPlannerApiService.extractPolygonBy(any(ApiToken.class)))
                .thenReturn(Mono.error(new NullPointerException()));

        classUnderTest.updateOpenTripPlannerPolygon();

        Awaitility.await()
                .atMost(Duration.ONE_SECOND)
                .untilAsserted(() -> {
                    Polygon openTripPlannerPolygon = (Polygon) ReflectionTestUtils.getField(classUnderTest, "openTripPlannerPolygon");
                    assertThat(openTripPlannerPolygon.getPoints().size()).isEqualTo(0);
                });
    }


}
