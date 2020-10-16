package de.blackforestsolutions.dravelopspolygonservice.service.communicationservice;

import de.blackforestsolutions.dravelopsdatamodel.util.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.util.DravelOpsJsonMapper;
import de.blackforestsolutions.dravelopsgeneratedcontent.opentripplanner.polygon.OpenTripPlannerPolygonResponse;
import de.blackforestsolutions.dravelopspolygonservice.service.communicationservice.restcalls.CallService;
import de.blackforestsolutions.dravelopspolygonservice.service.supportservice.OpenTripPlannerHttpCallBuilderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.data.geo.Polygon;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URL;

import static de.blackforestsolutions.dravelopsdatamodel.util.DravelOpsHttpCallBuilder.buildUrlWith;

@Slf4j
@Service
public class OpenTripPlannerApiServiceImpl implements OpenTripPlannerApiService {

    private static final int FIRST_INDEX = 0;
    private static final int SECOND_INDEX = 1;

    private final CallService callService;
    private final OpenTripPlannerHttpCallBuilderService openTripPlannerHttpCallBuilderService;

    @Autowired
    public OpenTripPlannerApiServiceImpl(CallService callService, OpenTripPlannerHttpCallBuilderService openTripPlannerHttpCallBuilderService) {
        this.callService = callService;
        this.openTripPlannerHttpCallBuilderService = openTripPlannerHttpCallBuilderService;
    }

    @Override
    public Mono<Polygon> extractPolygonBy(ApiToken apiToken) {
        try {
            String url = getPolygonRequestUrlWith(apiToken);
            return callService.get(url, HttpHeaders.EMPTY)
                    .flatMap(response -> convertToPojo(response.getBody()))
                    .map(OpenTripPlannerPolygonResponse::getPolygon)
                    .flatMapMany(polygon -> Flux.fromIterable(polygon.getCoordinates()))
                    .flatMap(Flux::fromIterable)
                    .map(coordinate -> new Point(coordinate.get(FIRST_INDEX), coordinate.get(SECOND_INDEX)))
                    .collectList()
                    .map(Polygon::new);
        } catch (Exception e) {
            return Mono.error(e);
        }
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
