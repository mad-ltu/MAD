package mad.mad_app;

import android.content.Context;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by Tim on 15/10/2016.
 */

public class LocationGroupListItem {
    public LocationGroup data;

    // UI elements
    public TextView tvLocationName;
    public ImageButton btnEdit;
    public CheckBox cbSelector;

    public LocationGroupListItem(Context context, LocationGroup data) {
        this.data = data;

        this.cbSelector = new CheckBox(context);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof LocationGroupListItem &&
                ((LocationGroupListItem)o).data.equals(this.data);
    }
}
