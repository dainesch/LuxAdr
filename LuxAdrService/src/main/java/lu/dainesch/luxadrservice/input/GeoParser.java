package lu.dainesch.luxadrservice.input;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import javax.json.Json;
import javax.json.stream.JsonParser;

public class GeoParser implements AutoCloseable {

    private final JsonParser parser;

    private CoordImp current;
    private boolean done = false;
    private int count = 0;

    public GeoParser(InputStream in) {
        this.parser = Json.createParser(new InputStreamReader(in, StandardCharsets.UTF_8));

        // skip ahead to feature array
        skip(7);
    }

    public boolean hasNext() {
        if (done || current != null) {
            return !done;
        }
        if (parser.next() != JsonParser.Event.START_OBJECT) {
            done = true;
            return false;
        }

        skip(10); // to coords
        float longitude = parser.getBigDecimal().floatValue();
        skip(1);
        float latitude = parser.getBigDecimal().floatValue();
        skip(9); // to number
        String number = parser.getString();
        skip(8); // to building
        int buildingId = Integer.parseInt(parser.getString());
        skip(4);

        current = new CoordImp(latitude, longitude, buildingId, number);

        count++;
        return !done;

    }

    public CoordImp next() {
        if (current == null) {
            hasNext();
        }
        if (done) {
            throw new NoSuchElementException("No object left");
        }
        CoordImp ret = current;
        current = null;
        return ret;
    }

    public int getCount() {
        return count;
    }

    @Override
    public void close() {
        parser.close();
    }

    private void skip(int count) {
        for (int i = 0; i < count; i++) {
            parser.next();
        }
    }
    
    public static class CoordImp {
        
        private final float latitude,longitude;
        private final int buildingId;
        private final String number;

        public CoordImp(float latitude, float longitude, int buildingId, String number) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.buildingId = buildingId;
            this.number = number;
        }

        public float getLatitude() {
            return latitude;
        }

        public float getLongitude() {
            return longitude;
        }

        public int getBuildingId() {
            return buildingId;
        }

        public String getNumber() {
            return number;
        }

        @Override
        public String toString() {
            return "CoordImp{" + "latitude=" + latitude + ", longitude=" + longitude + ", buildingId=" + buildingId + ", number=" + number + '}';
        }
        
        
    }

}
