package mad.mad_app;

/**
 * Created by Tim on 19/10/2016.
 */

public class StatsWrapper {
    public LocationGroup group;
    public SpeedTest test;

    public StatsWrapper(LocationGroup group, SpeedTest test) {
        this.group = group;
        this.test = test;
    }
}
