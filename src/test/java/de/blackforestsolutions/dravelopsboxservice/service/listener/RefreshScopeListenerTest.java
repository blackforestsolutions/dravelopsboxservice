package de.blackforestsolutions.dravelopsboxservice.service.listener;

import de.blackforestsolutions.dravelopsboxservice.listener.RefreshScopeListener;
import de.blackforestsolutions.dravelopsboxservice.service.supportservice.RequestTokenHandlerService;
import de.blackforestsolutions.dravelopsboxservice.service.supportservice.RequestTokenHandlerServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.context.scope.refresh.RefreshScopeRefreshedEvent;

import static org.mockito.Mockito.*;

class RefreshScopeListenerTest {

    private final RequestTokenHandlerService requestTokenHandlerService = mock(RequestTokenHandlerServiceImpl.class);

    private final RefreshScopeListener classUnderTest = new RefreshScopeListener(requestTokenHandlerService);

    @Test
    void test_onApplicationEvent_with_refreshScopeEvent_calls_requestTokenHandlerService_once() {
        RefreshScopeRefreshedEvent testData = new RefreshScopeRefreshedEvent();

        classUnderTest.onApplicationEvent(testData);

        verify(requestTokenHandlerService, times(1)).materializeAfterRefresh();
    }
}
