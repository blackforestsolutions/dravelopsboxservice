package de.blackforestsolutions.dravelopspolygonservice.service.supportservice;

import de.blackforestsolutions.dravelopsdatamodel.util.ApiToken;
import de.blackforestsolutions.dravelopspolygonservice.service.communicationservice.OpenTripPlannerApiService;
import org.awaitility.Awaitility;
import org.awaitility.Duration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.springframework.data.geo.Box;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.ApiTokenObjectMother.*;
import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.BoxObjectMother.getOpenTripPlannerBox;
import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.BoxObjectMother.getOpenTripPlannerStartBox;
import static de.blackforestsolutions.dravelopsdatamodel.testutil.TestUtils.getPropertyFromFileAsString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RequestTokenHandlerServiceTest {

    private final Box openTripPlannerBox = getOpenTripPlannerStartBox();
    private final ApiToken openTripPlannerApiToken = getOpenTripPlannerApiToken();
    private final OpenTripPlannerApiService openTripPlannerApiService = mock(OpenTripPlannerApiService.class);

    private final RequestTokenHandlerService classUnderTest = new RequestTokenHandlerServiceImpl(openTripPlannerBox, openTripPlannerApiToken, openTripPlannerApiService);


    @Test
    @DisabledOnOs(OS.WINDOWS)
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
    void test_updateOpenTripPlannerBox_updates_polygon_within_service() {
        when(openTripPlannerApiService.extractBoxBy(any(ApiToken.class)))
                .thenReturn(Mono.just(getOpenTripPlannerBox()));

        classUnderTest.updateOpenTripPlannerBox();

        Awaitility.await()
                .atMost(Duration.ONE_SECOND)
                .untilAsserted(() -> {
                    Box openTripPlannerBox = (Box) ReflectionTestUtils.getField(classUnderTest, "openTripPlannerBox");
                    assertThat(openTripPlannerBox).isEqualTo(getOpenTripPlannerBox());
                });
    }


    @Test
    void test_updateOpenTripPlannerBox_updates_not_polygon_when_error_is_thrown() {
        when(openTripPlannerApiService.extractBoxBy(any(ApiToken.class)))
                .thenReturn(Mono.error(new NullPointerException()));

        classUnderTest.updateOpenTripPlannerBox();

        Awaitility.await()
                .atMost(Duration.ONE_SECOND)
                .untilAsserted(() -> {
                    Box openTripPlannerBox = (Box) ReflectionTestUtils.getField(classUnderTest, "openTripPlannerBox");
                    assertThat(openTripPlannerBox).isEqualTo(getOpenTripPlannerStartBox());
                });
    }

    @Test
    void test_getRequestApiTokenWith_polygonToken_and_configuredPeliasApiToken_returns_merged_token_for_autocompleteCall() {
        ApiToken polygonTokenTestData = getPolygonApiToken();
        ApiToken configuredPeliasTestData = getConfiguredPeliasAutocompleteApiToken();

        ApiToken result = classUnderTest.getRequestApiTokenWith(polygonTokenTestData, configuredPeliasTestData);

        assertThat(result).isEqualToIgnoringGivenFields(getPeliasAutocompleteApiToken(), "box");
        assertThat(result.getBox()).isEqualTo(getOpenTripPlannerStartBox());
    }
}
