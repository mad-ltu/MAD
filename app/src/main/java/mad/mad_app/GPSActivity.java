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
    private TextView txtCoords;
    private Button btnStartGPS;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);
        initButtons();
    }

    public void initButtons(){
        txtGPS = (TextView) findViewById(R.id.txtGPS);
        txtCoords = (TextView) findViewById(R.id.txtCoords);

        btnStartGPS = (Button) findViewById(R.id.btnStartGPS);
        btnStartGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Co-ords code here.
                txtCoords.append("\n New Co-ords");
            }
        });

        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GPSActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
