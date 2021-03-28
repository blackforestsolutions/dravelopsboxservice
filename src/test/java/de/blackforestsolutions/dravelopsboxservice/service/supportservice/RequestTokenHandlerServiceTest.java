package de.blackforestsolutions.dravelopsboxservice.service.supportservice;

import de.blackforestsolutions.dravelopsboxservice.service.communicationservice.BackendApiService;
import de.blackforestsolutions.dravelopsboxservice.service.communicationservice.BackendApiServiceImpl;
import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.Box;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;

import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.ApiTokenObjectMother.*;
import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.BoxObjectMother.getBoxServiceStartBox;
import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.BoxObjectMother.getStationPersistenceBox;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class RequestTokenHandlerServiceTest {

    private final Box stationPersistenceBox = getBoxServiceStartBox();
    private final ApiToken configuredBoxPersistenceApiToken = getConfiguredBoxPersistenceApiToken();
    private final BackendApiService backendApiService = mock(BackendApiServiceImpl.class);

    private final RequestTokenHandlerService classUnderTest = new RequestTokenHandlerServiceImpl(stationPersistenceBox, configuredBoxPersistenceApiToken, backendApiService);

    @Test
    void test_updateStationPersistenceBox_updates_box_within_service() {
        when(backendApiService.getOneBy(any(ApiToken.class), eq(Box.class)))
                .thenReturn(Mono.just(getStationPersistenceBox()));

        classUnderTest.updateStationPersistenceBox();

        Awaitility.await()
                .untilAsserted(() -> {
                    Box stationPersistenceBox = (Box) ReflectionTestUtils.getField(classUnderTest, "stationPersistenceBox");
                    assertThat(stationPersistenceBox).isEqualToComparingFieldByFieldRecursively(getStationPersistenceBox());
                    verify(backendApiService, times(1)).getOneBy(any(ApiToken.class), eq(Box.class));
                });
    }

    @Test
    void test_updateStationPersistenceBox_does_not_update_box_when_empty_result_is_given_back() {
        when(backendApiService.getOneBy(any(ApiToken.class), eq(Box.class)))
                .thenReturn(Mono.empty());

        classUnderTest.updateStationPersistenceBox();

        Awaitility.await()
                .untilAsserted(() -> {
                    Box stationPersistenceBox = (Box) ReflectionTestUtils.getField(classUnderTest, "stationPersistenceBox");
                    assertThat(stationPersistenceBox).isEqualToComparingFieldByFieldRecursively(getBoxServiceStartBox());
                    verify(backendApiService, times(1)).getOneBy(any(ApiToken.class), eq(Box.class));
                });
    }

    @Test
    void test_updateStationPersistenceBox_does_not_update_box_when_error_result_is_given_back() {
        when(backendApiService.getOneBy(any(ApiToken.class), eq(Box.class)))
                .thenReturn(Mono.error(new Exception()));

        classUnderTest.updateStationPersistenceBox();

        Awaitility.await()
                .untilAsserted(() -> {
                    Box stationPersistenceBox = (Box) ReflectionTestUtils.getField(classUnderTest, "stationPersistenceBox");
                    assertThat(stationPersistenceBox).isEqualToComparingFieldByFieldRecursively(getBoxServiceStartBox());
                    verify(backendApiService, times(1)).getOneBy(any(ApiToken.class), eq(Box.class));
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
