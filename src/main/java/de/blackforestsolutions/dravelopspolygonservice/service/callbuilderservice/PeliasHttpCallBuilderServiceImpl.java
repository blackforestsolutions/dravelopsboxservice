package de.blackforestsolutions.dravelopspolygonservice.service.callbuilderservice;

import de.blackforestsolutions.dravelopsdatamodel.util.ApiToken;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class PeliasHttpCallBuilderServiceImpl implements PeliasHttpCallBuilderService {

    private static final String AUTOCOMPLETE_PATH = "autocomplete";

    private static final String TEXT_PARAM = "text";
    private static final String SIZE_PARAM = "size";
    private static final String LANGUAGE_PARAM = "lang";
    private static final String BOUNDARY_BOX_MIN_LONGITUDE_PARAM = "boundary.rect.min_lon";
    private static final String BOUNDARY_BOX_MAX_LONGITUDE_PARAM = "boundary.rect.max_lon";
    private static final String BOUNDARY_BOX_MIN_LATITUDE_PARAM = "boundary.rect.min_lat";
    private static final String BOUNDARY_BOX_MAX_LATITUDE_PARAM = "boundary.rect.max_lat";
    private static final String LAYERS_PARAM = "layers";

    @Override
    public String buildPeliasAutocompletePathWith(ApiToken apiToken) {
        Objects.requireNonNull(apiToken.getApiVersion(), "apiVersion is not allowed to be null");
        Objects.requireNonNull(apiToken.getDeparture(), "departure (text) is not allowed to be null");
        Objects.requireNonNull(apiToken.getMaxResults(), "maxResults is not allowed to be null");
        Objects.requireNonNull(apiToken.getLanguage(), "language is not allowed to be null");
        Objects.requireNonNull(apiToken.getBox(), "box is not allowed to be null");
        Objects.requireNonNull(apiToken.getLayers(), "layers is not allowed to be null");
        Objects.requireNonNull(apiToken.getLayers().get(0), "first layer element is not allowed to be null");
        return "/"
                .concat(apiToken.getApiVersion())
                .concat("/")
                .concat(AUTOCOMPLETE_PATH)
                .concat("?")
                .concat(TEXT_PARAM)
                .concat("=")
                .concat(apiToken.getDeparture())
                .concat("&")
                .concat(SIZE_PARAM)
                .concat("=")
                .concat(String.valueOf(apiToken.getMaxResults()))
                .concat("&")
                .concat(LANGUAGE_PARAM)
                .concat("=")
                .concat(apiToken.getLanguage().toLanguageTag())
                .concat("&")
                .concat(BOUNDARY_BOX_MIN_LONGITUDE_PARAM)
                .concat("=")
                .concat(String.valueOf(apiToken.getBox().getFirst().getX()))
                .concat("&")
                .concat(BOUNDARY_BOX_MAX_LONGITUDE_PARAM)
                .concat("=")
                .concat(String.valueOf(apiToken.getBox().getSecond().getX()))
                .concat("&")
                .concat(BOUNDARY_BOX_MIN_LATITUDE_PARAM)
                .concat("=")
                .concat(String.valueOf(apiToken.getBox().getFirst().getY()))
                .concat("&")
                .concat(BOUNDARY_BOX_MAX_LATITUDE_PARAM)
                .concat("=")
                .concat(String.valueOf(apiToken.getBox().getSecond().getY()))
                .concat("&")
                .concat(LAYERS_PARAM)
                .concat("=")
                .concat(String.join(",", apiToken.getLayers()));
    }

}
