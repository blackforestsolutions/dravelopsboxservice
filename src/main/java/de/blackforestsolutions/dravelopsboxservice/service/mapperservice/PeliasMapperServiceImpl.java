package de.blackforestsolutions.dravelopsboxservice.service.mapperservice;

import de.blackforestsolutions.dravelopsdatamodel.CallStatus;
import de.blackforestsolutions.dravelopsdatamodel.Point;
import de.blackforestsolutions.dravelopsdatamodel.Status;
import de.blackforestsolutions.dravelopsdatamodel.TravelPoint;
import de.blackforestsolutions.dravelopsgeneratedcontent.pelias.Feature;
import de.blackforestsolutions.dravelopsgeneratedcontent.pelias.Geometry;
import de.blackforestsolutions.dravelopsgeneratedcontent.pelias.PeliasTravelPointResponse;
import de.blackforestsolutions.dravelopsgeneratedcontent.pelias.Properties;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
public class PeliasMapperServiceImpl implements PeliasMapperService {

    private static final int FIRST_INDEX = 0;
    private static final int SECOND_INDEX = 1;

    @Override
    public Flux<CallStatus<TravelPoint>> extractTravelPointsFrom(PeliasTravelPointResponse response) {
        return Mono.just(response)
                .map(PeliasTravelPointResponse::getFeatures)
                .flatMapMany(Flux::fromIterable)
                .map(this::extractTravelPointCallStatusFrom)
                .onErrorResume(error -> Mono.just(new CallStatus<>(null, Status.FAILED, error)));
    }

    private CallStatus<TravelPoint> extractTravelPointCallStatusFrom(Feature feature) {
        try {
            TravelPoint travelPoint = extractTravelPointFrom(feature);
            return new CallStatus<>(travelPoint, Status.SUCCESS, null);
        } catch (Exception e) {
            return new CallStatus<>(null, Status.FAILED, e);
        }
    }

    private TravelPoint extractTravelPointFrom(Feature feature) {
        return new TravelPoint.TravelPointBuilder()
                .setName(feature.getProperties().getLabel())
                .setPoint(extractPointFrom(feature.getGeometry()))
                .setDistanceInKilometers(extractDistanceFrom(feature.getProperties()))
                .build();
    }

    private Point extractPointFrom(Geometry geometry) {
        return new Point.PointBuilder(
                geometry.getCoordinates().get(FIRST_INDEX),
                geometry.getCoordinates().get(SECOND_INDEX)
        ).build();
    }

    private Distance extractDistanceFrom(Properties properties) {
        return Optional.ofNullable(properties.getDistance())
                .map(distance -> new Distance(distance, Metrics.KILOMETERS))
                .orElse(null);
    }
}
