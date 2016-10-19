package mad.mad_app;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tim on 19/10/2016.
 */

public class HotSpot {
    public Double maxSpeed = Double.MIN_VALUE;
    public Double minSpeed = Double.MAX_VALUE;
    public Double avgSpeed = 0.0;
    public List<Double> speeds = new ArrayList<>();
    public String name = "";

    public void calcAvgSpeed() {
        double total = 0.0;
        for(Double speed: speeds) {
            total += speed;
        }
        if(speeds.size() != 0) {
            avgSpeed = total/speeds.size();
        }
    }
}
