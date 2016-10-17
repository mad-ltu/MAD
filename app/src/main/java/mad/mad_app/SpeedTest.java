package mad.mad_app;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Tim on 11/09/2016.
 */
public class SpeedTest implements Serializable {
    private Long id;
    private Long parentId;

    private Double lat, lon;
    private Date dateTime;
    private Double speedKBps;
    private String connType;
    private String connSubType;


    public SpeedTest(Date dateTime, Double speedKBps, String connType, String connSubType) {
        this.dateTime = dateTime;
        this.speedKBps = speedKBps;
        this.connType = connType;
        this.connSubType = connSubType;
    }

    public SpeedTest(Long dateTime, Double speedKBps, String connType, String connSubType) {
        this(new Date(dateTime), speedKBps, connType, connSubType);
    }

    public SpeedTest() {
        this(System.currentTimeMillis(), 0.0, "UNKNOWN", "UNKNOWN");
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }

    public Date getDateTime() {
        return dateTime;
    }
    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }
    public void setDateTime(Long dateTime) {
        setDateTime(new Date(dateTime));
    }

    public Double getSpeedKBps() {
        return speedKBps;
    }
    public void setSpeedKBps(Double speedKBps) {
        this.speedKBps = speedKBps;
    }

    public String getConnType() {
        return connType;
    }
    public void setConnType(String connType) {
        this.connType = connType;
    }

    public String getConnSubType() { return connSubType; }
    public void setConnSubType(String connSubType) { this.connSubType = connSubType; }

    public Double getLon() { return lon; }
    public void setLon(Double lon) { this.lon = lon; }

    public Double getLat() { return lat; }
    public void setLat(Double lat) { this.lat = lat; }

    @Override
    public String toString() {
        return String.format("%s\tSpeed: %.2fKB/s\nConnection: %s %s",
                new SimpleDateFormat("EEE, MMM d, ''yy @ hh:mm.ssa").format(dateTime),
                speedKBps, connType, connSubType);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof SpeedTest &&
                ((SpeedTest)o).getId().equals(id);
    }
}
