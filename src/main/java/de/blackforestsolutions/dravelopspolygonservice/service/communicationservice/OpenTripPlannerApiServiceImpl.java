package de.blackforestsolutions.dravelopspolygonservice.service.communicationservice;

import de.blackforestsolutions.dravelopsdatamodel.util.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.util.DravelOpsJsonMapper;
import de.blackforestsolutions.dravelopsgeneratedcontent.opentripplanner.polygon.OpenTripPlannerPolygonResponse;
import de.blackforestsolutions.dravelopspolygonservice.service.communicationservice.restcalls.CallService;
import de.blackforestsolutions.dravelopspolygonservice.service.callbuilderservice.OpenTripPlannerHttpCallBuilderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Box;
import org.springframework.data.geo.Point;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.net.URL;

import static de.blackforestsolutions.dravelopsdatamodel.util.DravelOpsHttpCallBuilder.buildUrlWith;

@Slf4j
@Service
public class OpenTripPlannerApiServiceImpl implements OpenTripPlannerApiService {

    private final CallService callService;
    private final OpenTripPlannerHttpCallBuilderService openTripPlannerHttpCallBuilderService;

    @Autowired
    public OpenTripPlannerApiServiceImpl(CallService callService, OpenTripPlannerHttpCallBuilderService openTripPlannerHttpCallBuilderService) {
        this.callService = callService;
        this.openTripPlannerHttpCallBuilderService = openTripPlannerHttpCallBuilderService;
    }

    @Override
    public Mono<Box> extractBoxBy(ApiToken apiToken) {
        try {
            String url = getPolygonRequestUrlWith(apiToken);
            return callService.get(url, HttpHeaders.EMPTY)
                    .flatMap(response -> convertToPojo(response.getBody()))
                    .map(responseBody -> extractBoxFrom(responseBody.getLowerLeftLatitude(), responseBody.getLowerLeftLongitude(), responseBody.getUpperRightLatitude(), responseBody.getUpperRightLongitude()));
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    private Box extractBoxFrom(double lowerLeftLatitude, double lowerLeftLongitude, double upperRightLatitude, double upperRightLongitude) {
        Point lowerLeft = new Point(lowerLeftLongitude, lowerLeftLatitude);
        Point upperRight = new Point(upperRightLongitude, upperRightLatitude);
        return new Box(lowerLeft, upperRight);
    }

    private String getPolygonRequestUrlWith(ApiToken apiToken) {
        ApiToken.ApiTokenBuilder builder = new ApiToken.ApiTokenBuilder(apiToken);
        builder.setPath(openTripPlannerHttpCallBuilderService.buildOpenTripPlannerPolygonPathWith(builder.build()));
        URL request = buildUrlWith(builder.build());
        return request.toString();
    }

    private Mono<OpenTripPlannerPolygonResponse> convertToPojo(String json) {
        DravelOpsJsonMapper mapper = new DravelOpsJsonMapper();
        return mapper.mapJsonToPojo(json, OpenTripPlannerPolygonResponse.class);
    }

}
