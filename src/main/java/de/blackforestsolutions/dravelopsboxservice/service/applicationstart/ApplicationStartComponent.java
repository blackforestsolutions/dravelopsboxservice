package de.blackforestsolutions.dravelopsboxservice.service.applicationstart;

import de.blackforestsolutions.dravelopsboxservice.service.supportservice.RequestTokenHandlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartComponent implements ApplicationListener<ApplicationReadyEvent> {

    private final RequestTokenHandlerService requestTokenHandlerService;

    @Autowired
    public ApplicationStartComponent(RequestTokenHandlerService requestTokenHandlerService) {
        this.requestTokenHandlerService = requestTokenHandlerService;
    }

    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {
        requestTokenHandlerService.updateStationPersistenceBox();
    }
}