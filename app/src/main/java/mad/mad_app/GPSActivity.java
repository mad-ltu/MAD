package mad.mad_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GPSActivity extends AppCompatActivity {

    private TextView txtGPS;
    private Button btnStartGPS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);
        initButtons();
    }

    public void initButtons(){
        btnStartGPS = (Button) findViewById(R.id.btnStartGPS);
        btnStartGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GPSActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
