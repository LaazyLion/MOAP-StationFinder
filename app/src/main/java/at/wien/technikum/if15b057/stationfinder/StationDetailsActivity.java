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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import at.wien.technikum.if15b057.stationfinder.data.Station;

public class StationDetailsActivity extends AppCompatActivity implements LocationListener, OnMapReadyCallback {

    private final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 0;
    private final String LOG_TAG = StationDetailsActivity.class.getName();
    private static final String LOCATION_PROVIDER = LocationManager.GPS_PROVIDER;

    private Station station;
    private TextView tvLocation;
    private TextView tvLines;
    private TextView tvDistance;
    private SupportMapFragment mapFragment;
    private GoogleMap map;

    private LocationManager locationManager;


    // activity lifecycle

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
        tvLocation = (TextView) findViewById(R.id.activity_station_details_tv_location);
        tvLines = (TextView) findViewById(R.id.activity_station_details_tv_lines);
        tvDistance = (TextView) findViewById(R.id.activity_station_details_tv_myLocation);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.activity_station_details_mapfragment);
        mapFragment.getMapAsync(this);

        // set content
        setTitle(station.getName());
        tvLocation.setText(station.getLocation().toString());
        tvLines.setText(station.getLinesAsString());
        tvDistance.setText(String.valueOf(station.getDistance()));

        requestLocationUpdates();
    }

    @Override
    public void onResume() {
        super.onResume();
        requestLocationUpdates();
    }

    @Override
    public void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }


    // google maps

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;

        map.clear();
        LatLng position = new LatLng(station.getLocation().y, station.getLocation().x);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(position);
        map.addMarker(markerOptions);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 17));
        map.getUiSettings().setAllGesturesEnabled(false);
        map.getUiSettings().setZoomGesturesEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.setMinZoomPreference(14);
    }


    // menu

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

            if (location != null)
                tvDistance.setText(location.toString());
            else
                tvDistance.setText("No Location available");
        }
    }


    // location

    private void requestLocationUpdates() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

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
            }
        } else {
            locationManager.requestLocationUpdates(
                    LOCATION_PROVIDER,
                    1000,
                    0, this);

            if (locationManager.getLastKnownLocation(LOCATION_PROVIDER) != null)
                onLocationChanged(locationManager.getLastKnownLocation(LOCATION_PROVIDER));
        }
    }

    // location listener

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            Log.v(LOG_TAG, "Location changed: " + location.toString());
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Location stationLocation = locationManager.getLastKnownLocation(LOCATION_PROVIDER);
            stationLocation.setLatitude(station.getLocation().y);
            stationLocation.setLongitude(station.getLocation().x);
            float distance = stationLocation.distanceTo(location);
            String unit = distance > 1000 ? "km" : "m";
            distance = distance > 1000 ? distance / 1000 : distance;
            tvDistance.setText(String.valueOf(distance) + " " + unit);
        }
        else {
            Log.v(LOG_TAG, "Location changed: NULL");
            tvDistance.setText("No Location available");
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


    // permission request

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestLocationUpdates();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }
}
