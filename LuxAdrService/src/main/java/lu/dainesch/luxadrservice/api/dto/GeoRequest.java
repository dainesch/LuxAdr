package lu.dainesch.luxadrservice.api.dto;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class GeoRequest {

    private float latitude;
    private float longitude;
    private float distance;

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

}
