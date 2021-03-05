package de.blackforestsolutions.dravelopsboxservice.service.supportservice;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.Box;
import de.blackforestsolutions.dravelopsboxservice.service.communicationservice.BackendApiService;
import de.blackforestsolutions.dravelopsboxservice.service.communicationservice.BackendApiServiceImpl;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.ApiTokenObjectMother.*;
import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.BoxObjectMother.getBoxServiceStartBox;
import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.BoxObjectMother.getStationPersistenceBox;
import static de.blackforestsolutions.dravelopsdatamodel.testutil.TestUtils.getPropertyFromFileAsString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class RequestTokenHandlerServiceTest {

    private static final long ASYNC_DELAY = 1L;

    private final Box stationPersistenceBox = getBoxServiceStartBox();
    private final ApiToken configuredBoxPersistenceApiToken = getConfiguredBoxPersistenceApiToken();
    private final BackendApiService backendApiService = mock(BackendApiServiceImpl.class);

    private final RequestTokenHandlerService classUnderTest = new RequestTokenHandlerServiceImpl(stationPersistenceBox, configuredBoxPersistenceApiToken, backendApiService);


    @Test
    @DisabledOnOs(OS.LINUX)
    void test_cron_from_properties_is_executed_next_time_correctly_relative_to_last_time() throws ParseException {
        // Locale.US as github workflow is apparently not executed in germany
        SimpleDateFormat formatter = new SimpleDateFormat("EE MMM dd HH:mm:ss zzzz yyyy", Locale.US);
        Date lastExecutionTestDate = formatter.parse("Mon Aug 30 00:00:00 CEST 2020");
        String cron = getPropertyFromFileAsString("application-dev.properties", "stationpersistence.box.updatetime");

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
    void test_updateStationPersistenceBox_updates_box_within_service() {
        when(backendApiService.getOneBy(any(ApiToken.class), eq(Box.class)))
                .thenReturn(Mono.just(getStationPersistenceBox()));

        classUnderTest.updateStationPersistenceBox();

        Awaitility.await()
                .untilAsserted(() -> {
                    Box stationPersistenceBox = (Box) ReflectionTestUtils.getField(classUnderTest, "stationPersistenceBox");
                    assertThat(stationPersistenceBox).isEqualToComparingFieldByFieldRecursively(getStationPersistenceBox());
                });
    }

    @Test
    void test_updateStationPersistenceBox_updates_not_box_when_error_is_thrown() {
        when(backendApiService.getOneBy(any(ApiToken.class), eq(Box.class)))
                .thenReturn(Mono.error(new Exception()))
                .thenReturn(Mono.just(getStationPersistenceBox()));

        classUnderTest.updateStationPersistenceBox();

        Awaitility.await()
                .atMost(configuredBoxPersistenceApiToken.getRetryTimeInSeconds() + ASYNC_DELAY, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    Box stationPersistenceBox = (Box) ReflectionTestUtils.getField(classUnderTest, "stationPersistenceBox");
                    assertThat(stationPersistenceBox).isEqualToComparingFieldByFieldRecursively(getStationPersistenceBox());
                    verify(backendApiService, times(2)).getOneBy(any(ApiToken.class), eq(Box.class));
                });
    }

    @Test
    void test_updateStationPersistenceBox_updates_not_box_when_empty_result_is_given_back() {
        when(backendApiService.getOneBy(any(ApiToken.class), eq(Box.class)))
                .thenReturn(Mono.empty())
                .thenReturn(Mono.just(getStationPersistenceBox()));

        classUnderTest.updateStationPersistenceBox();

        Awaitility.await()
                .atMost(configuredBoxPersistenceApiToken.getRetryTimeInSeconds() + ASYNC_DELAY, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    Box stationPersistenceBox = (Box) ReflectionTestUtils.getField(classUnderTest, "stationPersistenceBox");
                    assertThat(stationPersistenceBox).isEqualToComparingFieldByFieldRecursively(getStationPersistenceBox());
                    verify(backendApiService, times(2)).getOneBy(any(ApiToken.class), eq(Box.class));
                });
    }

    @Test
    void test_updateStationPersistenceBox_updates_box_after_three_and_one_right_backend_result() {
        when(backendApiService.getOneBy(any(ApiToken.class), eq(Box.class)))
                .thenReturn(Mono.empty())
                .thenReturn(Mono.empty())
                .thenReturn(Mono.error(new Exception()))
                .thenReturn(Mono.just(getStationPersistenceBox()));

        classUnderTest.updateStationPersistenceBox();

        Awaitility.await()
                .atMost(configuredBoxPersistenceApiToken.getRetryTimeInSeconds() * 3 + ASYNC_DELAY, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    Box stationPersistenceBox = (Box) ReflectionTestUtils.getField(classUnderTest, "stationPersistenceBox");
                    assertThat(stationPersistenceBox).isEqualToComparingFieldByFieldRecursively(getStationPersistenceBox());
                    verify(backendApiService, times(4)).getOneBy(any(ApiToken.class), eq(Box.class));
                });
    }

    @Test
    void test_getAutocompleteApiTokenWith_autocompleteBoxToken_and_configuredPeliasApiToken_returns_merged_token_for_autocompleteCall() {
        ApiToken autocompleteTokenTestData = getAutocompleteBoxServiceApiToken();
        ApiToken configuredPeliasTestData = getConfiguredPeliasApiToken();

        ApiToken result = classUnderTest.getAutocompleteApiTokenWith(autocompleteTokenTestData, configuredPeliasTestData);

        assertThat(result).isEqualToIgnoringGivenFields(getPeliasAutocompleteApiToken(), "box");
        assertThat(result.getBox()).isEqualToComparingFieldByFieldRecursively(getBoxServiceStartBox());
    }

    @Test
    void test_getNearestAddressesApiTokenWith_nearestAddressesToken_and_configuredPeliasApiToken_returns_merged_token_for_reverseCall() {
        ApiToken nearestAddressTokenTestData = getNearestAddressesBoxServiceApiToken();
        ApiToken configuredPeliasTestData = getConfiguredPeliasApiToken();

        ApiToken result = classUnderTest.getNearestAddressesApiTokenWith(nearestAddressTokenTestData, configuredPeliasTestData);

        assertThat(result).isEqualToComparingFieldByFieldRecursively(getPeliasNearestAddressesApiToken());
    }
}
