package lu.dainesch.luxadrservice;

import java.awt.geom.Rectangle2D;
import java.util.Comparator;
import lu.dainesch.luxadrservice.adr.entity.Building;
import lu.dainesch.luxadrservice.adr.entity.Coordinates;

public class GeoUtil {

    public static final float EARTH_RADIUS = 6_371; //km

    private GeoUtil() {
    }

    public static Rectangle2D.Float getBoundingBox(float lat, float lon, float distance) {

        float latDiff = (float) Math.toDegrees(distance / EARTH_RADIUS);
        float minLat = lat - latDiff;

        float lonDiff = (float) Math.toDegrees(Math.asin(distance / EARTH_RADIUS) / Math.cos(Math.toRadians(lat)));
        float minLon = lon - lonDiff;

        return new Rectangle2D.Float(minLat, minLon, 2 * latDiff, 2 * lonDiff);

    }

    public static double getDistance(double lat1, double lon1, double lat2, double lon2) {

        double dLat = Math.toRadians((lat2 - lat1));
        double dLong = Math.toRadians((lon2 - lon1));

        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = haversin(dLat) + Math.cos(lat1) * Math.cos(lat2) * haversin(dLong);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // result in km
        return EARTH_RADIUS * c;
    }

    public static double haversin(double val) {
        return Math.pow(Math.sin(val / 2), 2);
    }

    public static Comparator<Building> getNearestBuildingComp(float lat, float lon) {
        return (Building o1, Building o2) -> {
            double d1 = getDistance(lat, lon, o1.getCoordinates().getLatitude(), o1.getCoordinates().getLongitude());
            double d2 = getDistance(lat, lon, o2.getCoordinates().getLatitude(), o2.getCoordinates().getLongitude());
            if (d1 < d2) {
                return -1;
            } else {
                return 1;
            }
        };
    }

    public static Comparator<Coordinates> getNearestCoordComp(float lat, float lon) {
        return (Coordinates o1, Coordinates o2) -> {
            double d1 = getDistance(lat, lon, o1.getLatitude(), o1.getLongitude());
            double d2 = getDistance(lat, lon, o2.getLatitude(), o2.getLongitude());
            if (d1 < d2) {
                return -1;
            } else {
                return 1;
            }
        };
    }

}
