package mad.mad_app;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Tim on 11/09/2016.
 */
public class SpeedTest {
    private Long id;
    private Long parentId;

    private Date dateTime;
    private Double speedKbps;
    private String connType;
    private String connSubType;


    public SpeedTest(Date dateTime, Double speedMbps, String connType) {
        this.dateTime = dateTime;
        this.speedKbps = speedMbps;
        this.connType = connType;
    }

    public SpeedTest(Long dateTime, Double speedMbps, String connType, Double lat, Double lon) {
        this(new Date(dateTime), speedMbps, connType);
    }

    public SpeedTest() {
        this(System.currentTimeMillis(), 0.0, "UNKNOWN", 0.0, 0.0);
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

    public Double getSpeedKbps() {
        return speedKbps;
    }
    public void setSpeedKbps(Double speedKbps) {
        this.speedKbps = speedKbps;
    }

    public String getConnType() {
        return connType;
    }
    public void setConnType(String connType) {
        this.connType = connType;
    }

    public String getConnSubType() { return connSubType; }
    public void setConnSubType(String connSubType) { this.connSubType = connSubType; }

    @Override
    public String toString() {
        return String.format("%s\tSpeed: %sMB/s\tConnection: %s %s",
                new SimpleDateFormat("EEE, MMM d, ''yy @ HH:mm").format(dateTime),
                speedKbps.toString(), connType, connSubType);
    }
}
