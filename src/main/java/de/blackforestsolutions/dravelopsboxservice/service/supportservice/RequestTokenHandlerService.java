package de.blackforestsolutions.dravelopsboxservice.service.supportservice;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;

public interface RequestTokenHandlerService {
    ApiToken getAutocompleteApiTokenWith(ApiToken requestApiToken, ApiToken configuredPeliasApiToken);

    ApiToken getNearestAddressesApiTokenWith(ApiToken requestApiToken, ApiToken configuredPeliasApiToken);

    void updateStationPersistenceBox();

    /**
     * Used after refresh context for scheduler bean initialization
     */
    default void materializeAfterRefresh() {
    }
}
