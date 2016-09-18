package mad.mad_app;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Tim on 11/09/2016.
 */
public class SpeedTest {

    public enum ConnectionType {
        WIFI,
        _3G,
        _4G,
        LTE
    }

    // For when DB is implemented
    //private Long id;
    //private Long parentId;

    private Date dateTime;
    private Double speedMbps;
    private ConnectionType type;


    public SpeedTest(Date dateTime, Double speedMbps, ConnectionType type) {
        this.dateTime = dateTime;
        this.speedMbps = speedMbps;
        this.type = type;
    }

    public SpeedTest(Long dateTime, Double speedMbps, ConnectionType type) {
        this(new Date(dateTime), speedMbps, type);
    }

    public SpeedTest() {
        this(System.currentTimeMillis(), new Double(0), ConnectionType.WIFI);
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

    public ConnectionType getType() {
        return type;
    }

    public void setType(ConnectionType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return String.format("%s\tSpeed: %sMB/s\tConncetion: %s",
                new SimpleDateFormat("EEE, MMM d, ''yy").format(dateTime),
                speedMbps.toString(), type.toString());
    }
}
