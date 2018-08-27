
package pro.rasht.museum.ar.network.poi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Southwest {

    @SerializedName("lat")
    @Expose
    private Float lat;
    @SerializedName("lng")
    @Expose
    private Float lng;

    public Southwest(Float lat, Float lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public Float getLat() {
        return lat;
    }

    public void setLat(Float lat) {
        this.lat = lat;
    }

    public Float getLng() {
        return lng;
    }

    public void setLng(Float lng) {
        this.lng = lng;
    }

}
