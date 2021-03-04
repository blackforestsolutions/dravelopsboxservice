package de.blackforestsolutions.dravelopsboxservice.service.testutil;

import de.blackforestsolutions.dravelopsdatamodel.CallStatus;
import de.blackforestsolutions.dravelopsdatamodel.Status;
import de.blackforestsolutions.dravelopsdatamodel.TravelPoint;
import org.springframework.data.geo.Metrics;

import java.util.function.Consumer;

import static de.blackforestsolutions.dravelopsboxservice.configuration.GeocodingConfiguration.*;
import static org.assertj.core.api.Assertions.assertThat;

public class TestAssertions {

    public static Consumer<CallStatus<TravelPoint>> getPeliasSuccessCallStatusAsserts(TravelPoint expectedTravelPoint) {
        return travelPointResult -> {
            assertThat(travelPointResult.getStatus()).isEqualTo(Status.SUCCESS);
            assertThat(travelPointResult.getThrowable()).isNull();
            assertThat(travelPointResult.getCalledObject()).isEqualToComparingFieldByFieldRecursively(expectedTravelPoint);
        };
    }

    public static Consumer<TravelPoint> getTravelPointApiNearestAddressesAsserts() {
        return travelPoint -> {
            assertThat(travelPoint.getName()).isNotEmpty();
            assertThat(travelPoint.getPoint()).isNotNull();
            assertThat(travelPoint.getPoint().getX()).isGreaterThanOrEqualTo(MIN_WGS_84_LONGITUDE);
            assertThat(travelPoint.getPoint().getX()).isLessThanOrEqualTo(MAX_WGS_84_LONGITUDE);
            assertThat(travelPoint.getPoint().getY()).isGreaterThanOrEqualTo(MIN_WGS_84_LATITUDE);
            assertThat(travelPoint.getPoint().getY()).isLessThanOrEqualTo(MAX_WGS_84_LATITUDE);
            assertThat(travelPoint.getDistanceInKilometers()).isNotNull();
            assertThat(travelPoint.getDistanceInKilometers().getValue()).isGreaterThanOrEqualTo(MIN_DISTANCE_IN_KILOMETERS_TO_POINT);
            assertThat(travelPoint.getDistanceInKilometers().getMetric()).isEqualTo(Metrics.KILOMETERS);
            assertThat(travelPoint.getDepartureTime()).isNull();
            assertThat(travelPoint.getArrivalTime()).isNull();
            assertThat(travelPoint.getPlatform()).isEmpty();
        };
    }
}
