package at.wien.technikum.if15b057.stationfinder;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;

import at.wien.technikum.if15b057.stationfinder.adapter.RvStationListAdapter;
import at.wien.technikum.if15b057.stationfinder.data.Station;
import at.wien.technikum.if15b057.stationfinder.loader.StationDistanceLoader;
import at.wien.technikum.if15b057.stationfinder.loader.StationListWebLoader;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, LoaderManager.LoaderCallbacks<ArrayList<Station>>, LocationListener {

    private static final String LOG_TAG = MainActivity.class.getName();
    private static final int STATION_LOADER_ID = 1;
    private static final int STATION_DISTANCE_LOADER_ID = 2;
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 0;


    private String url;

    private RecyclerView rvStationList;
    private RvStationListAdapter stationListAdapter;
    private LinearLayoutManager linearLayoutManager;
    private ProgressBar pbLoading;

    private LocationManager locationManager;

    // data
    private ArrayList<Station> stationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.v(LOG_TAG, "Creating Activity...");

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // get url
        url = getString(R.string.oeff_station_json_url);

        // setup recycler view
        rvStationList = (RecyclerView) findViewById(R.id.activity_main_rv_station_list);
        stationListAdapter = new RvStationListAdapter();
        linearLayoutManager = new LinearLayoutManager(this);

        stationListAdapter.setClickListener(this);

        rvStationList.setAdapter(stationListAdapter);
        rvStationList.setLayoutManager(linearLayoutManager);

        // setup other views
        pbLoading = (ProgressBar) findViewById(R.id.activity_main_pb_loading);

        // bind loader
        Bundle args = new Bundle();
        args.putString("url", url);
        getSupportLoaderManager().initLoader(STATION_LOADER_ID, args, this);

        requestLocationUpdates();
    }

    @Override
    public void onClick(View v) {
        int index = rvStationList.getChildAdapterPosition(v);
        Intent intent = new Intent(this, StationDetailsActivity.class);
        Bundle bundle = new Bundle();

        bundle.putParcelable("station", stationList.get(index));
        intent.putExtras(bundle);
        startActivity(intent);
    }


    // menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.menu_main_action_reload) {
            stationListAdapter.setContent(null);

            LoaderManager loaderManager = getSupportLoaderManager();
            Loader<String> loader = loaderManager.getLoader(STATION_LOADER_ID);
            Bundle args = new Bundle();

            args.putString("url", url);

            pbLoading.setVisibility(View.VISIBLE);

            if (loader == null)
                loaderManager.initLoader(STATION_LOADER_ID, args, this);
            else
                loaderManager.restartLoader(STATION_LOADER_ID, args, this);

            return true;
        }

        return super.onOptionsItemSelected(item);
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
                    LocationManager.NETWORK_PROVIDER,
                    1000,
                    0, this);

            if(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) != null)
                onLocationChanged(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
        }
    }


    // location listener

    @Override
    public void onLocationChanged(Location location) {
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> loader;
        Bundle args = new Bundle();

        Log.v(LOG_TAG, "Location changed: " + location.toString());

        if (stationList != null) {

            loader = loaderManager.getLoader(STATION_DISTANCE_LOADER_ID);
            args.putParcelable("location", location);

            pbLoading.setVisibility(View.VISIBLE);

            if (loader == null)
                loaderManager.initLoader(STATION_DISTANCE_LOADER_ID, args, this);
            else
                loaderManager.restartLoader(STATION_DISTANCE_LOADER_ID, args, this);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    // loader

    @Override
    public Loader<ArrayList<Station>> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "Loader created: " + id);

        if(id == STATION_LOADER_ID)
            return new StationListWebLoader(this, args);
        else if(id == STATION_DISTANCE_LOADER_ID) {
            StationDistanceLoader stationDistanceLoader = new StationDistanceLoader(this, args);
            stationDistanceLoader.setStations(stationList);
            return stationDistanceLoader;
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Station>> loader, ArrayList<Station> data) {

        switch (loader.getId()) {

            case STATION_LOADER_ID:
                if(data != null) stationList = data;
                stationListAdapter.setContent(data);
                pbLoading.setVisibility(View.GONE);
                break;

            case STATION_DISTANCE_LOADER_ID:
                if(data != null) stationList = data;
                stationListAdapter.setContent(stationList);
                pbLoading.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Station>> loader) {
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


    // activity lifecycle

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestLocationUpdates();
    }
}
