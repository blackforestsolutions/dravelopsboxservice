package de.blackforestsolutions.dravelopsboxservice.service.callbuilderservice;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class PeliasHttpCallBuilderServiceImpl implements PeliasHttpCallBuilderService {

    private static final String AUTOCOMPLETE_PATH = "autocomplete";
    private static final String REVERSE_PATH = "reverse";

    private static final String TEXT_PARAM = "text";
    private static final String SIZE_PARAM = "size";
    private static final String LANGUAGE_PARAM = "lang";
    private static final String BOUNDARY_BOX_MIN_LONGITUDE_PARAM = "boundary.rect.min_lon";
    private static final String BOUNDARY_BOX_MAX_LONGITUDE_PARAM = "boundary.rect.max_lon";
    private static final String BOUNDARY_BOX_MIN_LATITUDE_PARAM = "boundary.rect.min_lat";
    private static final String BOUNDARY_BOX_MAX_LATITUDE_PARAM = "boundary.rect.max_lat";
    private static final String LAYERS_PARAM = "layers";
    private static final String LATITUDE_PARAM = "point.lat";
    private static final String LONGITUDE_PARAM = "point.lon";
    private static final String RADIUS_PARAM = "boundary.circle.radius";

    @Override
    public String buildPeliasAutocompletePathWith(ApiToken apiToken) {
        Objects.requireNonNull(apiToken.getApiVersion(), "apiVersion is not allowed to be null");
        Objects.requireNonNull(apiToken.getDeparture(), "departure (text) is not allowed to be null");
        Objects.requireNonNull(apiToken.getMaxResults(), "maxResults is not allowed to be null");
        Objects.requireNonNull(apiToken.getLanguage(), "language is not allowed to be null");
        Objects.requireNonNull(apiToken.getBox(), "box is not allowed to be null");
        Objects.requireNonNull(apiToken.getBox().getTopLeft(), "topLeft from box is not allowed to be null");
        Objects.requireNonNull(apiToken.getBox().getBottomRight(), "bottomRight from box is not allowed to be null");
        Objects.requireNonNull(apiToken.getLayers(), "layers is not allowed to be null");
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
                .concat(apiToken.getLanguage().getLanguage())
                .concat("&")
                .concat(BOUNDARY_BOX_MIN_LONGITUDE_PARAM)
                .concat("=")
                .concat(String.valueOf(apiToken.getBox().getTopLeft().getX()))
                .concat("&")
                .concat(BOUNDARY_BOX_MAX_LONGITUDE_PARAM)
                .concat("=")
                .concat(String.valueOf(apiToken.getBox().getBottomRight().getX()))
                .concat("&")
                .concat(BOUNDARY_BOX_MIN_LATITUDE_PARAM)
                .concat("=")
                .concat(String.valueOf(apiToken.getBox().getBottomRight().getY()))
                .concat("&")
                .concat(BOUNDARY_BOX_MAX_LATITUDE_PARAM)
                .concat("=")
                .concat(String.valueOf(apiToken.getBox().getTopLeft().getY()))
                .concat("&")
                .concat(LAYERS_PARAM)
                .concat("=")
                .concat(String.join(",", apiToken.getLayers()));
    }

    @Override
    public String buildPeliasReversePathWith(ApiToken apiToken) {
        Objects.requireNonNull(apiToken.getArrivalCoordinate(), "arrivalCoordinate is not allowed be null");
        Objects.requireNonNull(apiToken.getLanguage(), "language is not allowed to be null");
        Objects.requireNonNull(apiToken.getMaxResults(), "maxResults is not allowed to be null");
        Objects.requireNonNull(apiToken.getApiVersion(), "apiVersion is not allowed to be null");
        Objects.requireNonNull(apiToken.getLayers(), "layers is not allowed to be null");
        Objects.requireNonNull(apiToken.getRadiusInKilometers(), "radius in kilometers is not allowed to be null");
        return "/"
                .concat(apiToken.getApiVersion())
                .concat("/")
                .concat(REVERSE_PATH)
                .concat("?")
                .concat(LATITUDE_PARAM)
                .concat("=")
                .concat(String.valueOf(apiToken.getArrivalCoordinate().getY()))
                .concat("&")
                .concat(LONGITUDE_PARAM)
                .concat("=")
                .concat(String.valueOf(apiToken.getArrivalCoordinate().getX()))
                .concat("&")
                .concat(SIZE_PARAM)
                .concat("=")
                .concat(String.valueOf(apiToken.getMaxResults()))
                .concat("&")
                .concat(LANGUAGE_PARAM)
                .concat("=")
                .concat(apiToken.getLanguage().toLanguageTag())
                .concat("&")
                .concat(LAYERS_PARAM)
                .concat("=")
                .concat(String.join(",", apiToken.getLayers()))
                .concat("&")
                .concat(RADIUS_PARAM)
                .concat("=")
                .concat(String.valueOf(apiToken.getRadiusInKilometers()));
    }

}
