package de.blackforestsolutions.dravelopsboxservice.service.listener;

import de.blackforestsolutions.dravelopsboxservice.service.communicationservice.BackendApiService;
import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.Box;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.endpoint.RefreshEndpoint;
import org.springframework.test.annotation.DirtiesContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class RefreshScopeListenerMockIT {

    @MockBean
    private BackendApiService backendApiService;

    @Autowired
    private RefreshEndpoint refreshEndpoint;


    @Test
    void test_refresh_mechanism_updates_box() {

        refreshEndpoint.refresh();

        // called first on startup and secondly on actuator refresh
        verify(backendApiService, times(2)).getOneBy(any(ApiToken.class), eq(Box.class));
    }
}
