package lu.dainesch.luxadrdto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class GeoRequest implements Serializable {

    @Schema(description = "Latitude",example = "49.496484")
    private float latitude;
    @Schema(description = "Longitude",example = "5.981526")
    private float longitude;
    @Schema(description = "Distance in kilometers", example = "0.1")
    private float distance;
    @Schema(description = "Use the lucene to search and not the database. Lucene must be enabled and the index created")
    private Boolean lucene;

    public GeoRequest() {
    }

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

    public Boolean getLucene() {
        return lucene;
    }

    public void setLucene(Boolean lucene) {
        this.lucene = lucene;
    }

}
