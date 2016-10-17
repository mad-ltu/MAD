package mad.mad_app;

import java.io.Serializable;

/**
 * Created by Tim on 25/09/2016.
 */
public class LocationGroup implements Serializable{

    private Long id;

    private String name;
    private Double lat, lon;

    public LocationGroup() {}

    public LocationGroup(String name, Double lat, Double lon) {
        this.name = name;
        this.lat = lat;
        this.lon = lon;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Double getLat() { return lat; }
    public void setLat(Double lat) { this.lat = lat; }

    public Double getLon() { return lon; }
    public void setLon(Double lon) { this.lon = lon; }

    @Override
    public String toString() {
        return String.format("%s - %.2f, %.2f", name, lat, lon);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof LocationGroup &&
                ((LocationGroup)o).getId().equals(id);
    }
}
