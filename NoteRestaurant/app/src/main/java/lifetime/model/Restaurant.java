package lifetime.model;

import java.io.Serializable;

/**
 * Created by MyPC on 02/10/2017.
 */

public class Restaurant implements Serializable {
    private String name;
    public int image;
    private double latitude;
    private double longitude;

    public Restaurant() {
    }
    //error when lat<->long it will auto fix to 90
    public Restaurant(String name, int image, double latitude, double longitude) {
        this.name = name;
        this.image = image;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }



    @Override
    public String toString() {
        return this.name;
    }
}
