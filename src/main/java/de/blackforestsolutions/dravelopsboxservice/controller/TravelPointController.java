package de.blackforestsolutions.dravelopsboxservice.controller;

import de.blackforestsolutions.dravelopsboxservice.service.communicationservice.TravelPointApiService;
import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.TravelPoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@Slf4j
@RestController
@RequestMapping("/travelpoints")
public class TravelPointController {

    private final TravelPointApiService travelPointApiService;

    @Autowired
    public TravelPointController(TravelPointApiService travelPointApiService) {
        this.travelPointApiService = travelPointApiService;
    }

    @RequestMapping(value = "/autocomplete", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<TravelPoint> getAutocompleteAddresses(@RequestBody ApiToken request) {
        return travelPointApiService.retrieveAutocompleteAddressesFromApiService(request);
    }

    @RequestMapping(value = "/nearest", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<TravelPoint> getNearestAddresses(@RequestBody ApiToken request) {
        return travelPointApiService.retrieveNearestAddressesFromApiService(request);
    }
}
