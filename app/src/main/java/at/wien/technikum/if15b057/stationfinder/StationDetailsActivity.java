package at.wien.technikum.if15b057.stationfinder;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import at.wien.technikum.if15b057.stationfinder.data.Station;

public class StationDetailsActivity extends AppCompatActivity implements LocationListener {

    private final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 0;
    private final String LOG_TAG = StationDetailsActivity.class.getName();

    private Station station;
    private TextView tvName;
    private TextView tvLocation;
    private TextView tvLines;
    private TextView tvMyLocation;

    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // get extras
        Bundle bundle = getIntent().getExtras();

        if (bundle != null)
            station = bundle.getParcelable("station");
        else {
            finish();
            return;
        }

        // get views
        tvName = (TextView) findViewById(R.id.activity_station_details_tv_name);
        tvLocation = (TextView) findViewById(R.id.activity_station_details_tv_location);
        tvLines = (TextView) findViewById(R.id.activity_station_details_tv_lines);
        tvMyLocation = (TextView) findViewById(R.id.activity_station_details_tv_myLocation);

        // set content
        tvName.setText(station.getName());
        tvLocation.setText(station.getLocation().toString());
        tvLines.setText(station.getLinesAsString());
        tvMyLocation.setText(String.valueOf(station.getDistance()));

        //getLocation();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getLocation() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Location location = null;

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_FINE_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    1000,
                    0, this);

            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if(location != null)
                tvMyLocation.setText(location.toString());
            else
                tvMyLocation.setText("No Location available");
        }
    }


    // activity lifecycle


    @Override
    protected void onResume() {
        super.onResume();

        //getLocation();
    }

    @Override
    protected void onPause() {
        super.onPause();

        //locationManager.removeUpdates(this);
    }


    // permission request

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    // location listener

    @Override
    public void onLocationChanged(Location location) {
        if(location != null) {
            Log.v(LOG_TAG, "Location changed: " + location.toString());
            tvMyLocation.setText(location.toString());
        }
        else {
            Log.v(LOG_TAG, "Location changed: NULL");
            tvMyLocation.setText("No Location available");
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.v(LOG_TAG, "Provider enabled: " + provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.v(LOG_TAG, "Provider disabled: " + provider);
    }
}
