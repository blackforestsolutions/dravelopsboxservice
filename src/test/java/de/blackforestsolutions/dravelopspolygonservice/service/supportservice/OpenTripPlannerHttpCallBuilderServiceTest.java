package de.blackforestsolutions.dravelopspolygonservice.service.supportservice;

import de.blackforestsolutions.dravelopsdatamodel.util.ApiToken;
import org.junit.jupiter.api.Test;

import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.ApiTokenObjectMother.getOpenTripPlannerApiToken;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OpenTripPlannerHttpCallBuilderServiceTest {

    private final OpenTripPlannerHttpCallBuilderService classUnderTest = new OpenTripPlannerHttpCallBuilderServiceImpl();

    @Test
    void test_buildOpenTripPlannerPolygonPathWith_valid_apiToken_returns_valid_path_string() {
        ApiToken testData = getOpenTripPlannerApiToken();

        String result = classUnderTest.buildOpenTripPlannerPolygonPathWith(testData);

        assertThat(result).isEqualTo("/otp/routers/bw");
    }

    @Test
    void test_buildOpenTripPlannerPolygonPathWith_apiToken_and_router_as_null_throws_NullPointerException() {
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(getOpenTripPlannerApiToken());
        testData.setRouter(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildOpenTripPlannerPolygonPathWith(testData.build()));
    }
}
