package de.blackforestsolutions.dravelopspolygonservice.objectmothers;

import org.springframework.data.geo.Point;
import org.springframework.data.geo.Polygon;

public class PolygonObjectMother {

    public static Polygon getPolygon() {
        return new Polygon(
                new Point(6.651751d, 49.75588d),
                new Point(5.9561251d, 50.80702410000001d),
                new Point(5.886104100000001d, 50.979022900000004d),
                new Point(5.866844700000001d, 51.029371100000006d),
                new Point(5.863574300000001d, 51.045629100000006d),
                new Point(5.8629671000000005d, 51.0558945d),
                new Point(6.254944d, 51.833795d),
                new Point(7.43572d, 52.276498d),
                new Point(8.934161d, 52.290127d),
                new Point(8.661455d, 50.579053d),
                new Point(8.258708d, 50.001111d),
                new Point(8.016066d, 49.961436d),
                new Point(6.651751d, 49.75588d)
        );
    }
}
