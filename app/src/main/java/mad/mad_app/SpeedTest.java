package mad.mad_app;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Tim on 11/09/2016.
 */
public class SpeedTest {
    // For when DB is implemented
    //private Long id;
    //private Long parentId;

    private Double lat, lon;
    private Date dateTime;
    private Double speedMbps;
    private String connType;
    private String connSubType;


    public SpeedTest(Date dateTime, Double speedMbps, String connType, Double lat, Double lon) {
        this.lat = lat;
        this.lon = lon;
        this.dateTime = dateTime;
        this.speedMbps = speedMbps;
        this.connType = connType;
    }

    public SpeedTest(Long dateTime, Double speedMbps, String connType, Double lat, Double lon) {
        this(new Date(dateTime), speedMbps, connType, lat, lon);
    }

    public SpeedTest() {
        this(System.currentTimeMillis(), 0.0, "UNKNOWN", 0.0, 0.0);
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public void setDateTime(Long dateTime) {
        setDateTime(new Date(dateTime));
    }

    public Double getSpeedMbps() {
        return speedMbps;
    }

    public void setSpeedMbps(Double speedMbps) {
        this.speedMbps = speedMbps;
    }

    public String getConnType() {
        return connType;
    }

    public void setConnType(String connType) {
        this.connType = connType;
    }

    public Double getLat() { return lat; }
    public void setLat(Double lat) { this.lat = lat; }

    public Double getLon() { return lon; }
    public void setLon(Double lon) { this.lon = lon; }

    public String getConnSubType() { return connSubType; }
    public void setConnSubType(String connSubType) { this.connSubType = connSubType; }


    @Override
    public String toString() {
        return String.format("Location:%s:%s\t%s\tSpeed: %sMB/s\tConncetion: %s %s",
                lat.toString(), lon.toString(),
                new SimpleDateFormat("EEE, MMM d, ''yy").format(dateTime),
                speedMbps.toString(), connType, connSubType);
    }
}
