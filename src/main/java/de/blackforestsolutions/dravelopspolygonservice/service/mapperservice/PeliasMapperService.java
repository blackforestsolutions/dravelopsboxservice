package de.blackforestsolutions.dravelopspolygonservice.service.mapperservice;

import de.blackforestsolutions.dravelopsdatamodel.CallStatus;
import de.blackforestsolutions.dravelopsdatamodel.TravelPoint;
import de.blackforestsolutions.dravelopsgeneratedcontent.pelias.PeliasTravelPointResponse;
import reactor.core.publisher.Flux;

public interface PeliasMapperService {
    Flux<CallStatus<TravelPoint>> extractTravelPointsFrom(PeliasTravelPointResponse response);
}
