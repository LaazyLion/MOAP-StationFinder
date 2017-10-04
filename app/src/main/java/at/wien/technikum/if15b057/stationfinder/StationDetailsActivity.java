package at.wien.technikum.if15b057.stationfinder;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import at.wien.technikum.if15b057.stationfinder.data.Station;

public class StationDetailsActivity extends AppCompatActivity {

    private Station station;
    private TextView tvName;
    private TextView tvLocation;
    private TextView tvLines;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_details);

        // get extras
        Bundle bundle = getIntent().getExtras();

        if(bundle != null)
            station = bundle.getParcelable("station");
        else {
            finish();
            return;
        }

        // get views
        tvName = (TextView) findViewById(R.id.activity_station_details_tv_name);
        tvLocation = (TextView) findViewById(R.id.activity_station_details_tv_location);
        tvLines = (TextView) findViewById(R.id.activity_station_details_tv_lines);

        // set content
        tvName.setText(station.getName());
        tvLocation.setText(station.getLocation().toString());
        tvLines.setText(station.getLinesAsString());
    }
}
