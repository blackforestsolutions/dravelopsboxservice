package de.blackforestsolutions.dravelopsboxservice.service.supportservice;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import org.springframework.scheduling.annotation.Scheduled;

public interface RequestTokenHandlerService {
    ApiToken getAutocompleteApiTokenWith(ApiToken requestApiToken, ApiToken configuredPeliasApiToken);

    ApiToken getNearestAddressesApiTokenWith(ApiToken requestApiToken, ApiToken configuredPeliasApiToken);

    @Scheduled(cron = "${stationpersistence.box.updatetime}")
    void updateStationPersistenceBox();
}
