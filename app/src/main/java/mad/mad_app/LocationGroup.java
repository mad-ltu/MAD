package mad.mad_app;

/**
 * Created by Tim on 25/09/2016.
 */
public class LocationGroup {

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
}
