package de.blackforestsolutions.dravelopspolygonservice.service.callbuilderservice;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;

public interface PeliasHttpCallBuilderService {
    String buildPeliasAutocompletePathWith(ApiToken apiToken);

    String buildPeliasReversePathWith(ApiToken apiToken);
}
