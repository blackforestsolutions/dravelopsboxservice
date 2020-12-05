package de.blackforestsolutions.dravelopspolygonservice.controller;

import de.blackforestsolutions.dravelopsdatamodel.TravelPoint;
import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopspolygonservice.service.communicationservice.TravelPointApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@Slf4j
@RestController
@RequestMapping("pelias/travelpoints")
public class TravelPointController {

    private final TravelPointApiService travelPointApiService;

    @Autowired
    public TravelPointController(TravelPointApiService travelPointApiService) {
        this.travelPointApiService = travelPointApiService;
    }

    @RequestMapping(value = "/get", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<TravelPoint> retrievePeliasTravelPoints(@RequestBody ApiToken request) {
        return travelPointApiService.retrieveTravelPointsFromApiService(request);
    }
}
