package mad.mad_app;

import android.content.Context;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by Tim on 15/10/2016.
 */

public class SpeedTestListItem {
    public SpeedTest data;

    // UI elements
    public TextView tvDateTime;
    public TextView tvSpeed;
    public ImageButton btnEdit;
    public CheckBox cbSelector;

    public SpeedTestListItem(Context context, SpeedTest data) {
        this.data = data;

        this.cbSelector = new CheckBox(context);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof SpeedTestListItem &&
                ((SpeedTestListItem)o).data.equals(data);
    }
}
