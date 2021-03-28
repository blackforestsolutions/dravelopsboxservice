package de.blackforestsolutions.dravelopsboxservice.listener;

import de.blackforestsolutions.dravelopsboxservice.service.supportservice.RequestTokenHandlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.scope.refresh.RefreshScopeRefreshedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class RefreshScopeListener implements ApplicationListener<RefreshScopeRefreshedEvent> {

    private final RequestTokenHandlerService requestTokenHandlerService;

    @Autowired
    public RefreshScopeListener(RequestTokenHandlerService requestTokenHandlerService) {
        this.requestTokenHandlerService = requestTokenHandlerService;
    }

    @Override
    public void onApplicationEvent(RefreshScopeRefreshedEvent event) {
        requestTokenHandlerService.materializeAfterRefresh();
    }
}
