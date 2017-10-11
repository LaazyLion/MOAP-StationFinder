package at.wien.technikum.if15b057.stationfinder;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;

import at.wien.technikum.if15b057.stationfinder.adapter.RvStationListAdapter;
import at.wien.technikum.if15b057.stationfinder.adapter.TabsCollectionPagerAdapter;
import at.wien.technikum.if15b057.stationfinder.data.Station;
import at.wien.technikum.if15b057.stationfinder.fragments.MapFragment;
import at.wien.technikum.if15b057.stationfinder.fragments.StationListFragment;
import at.wien.technikum.if15b057.stationfinder.loader.StationDistanceLoader;
import at.wien.technikum.if15b057.stationfinder.loader.StationListWebLoader;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<Station>>, LocationListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String LOG_TAG = MainActivity.class.getName();
    private static final int STATION_LOADER_ID = 1;
    private static final int STATION_DISTANCE_LOADER_ID = 2;
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 0;
    private static final String LOCATION_PROVIDER = LocationManager.GPS_PROVIDER;

    private LocationManager locationManager;
    private Location lastLocation;
    private SharedPreferences sharedPref;
    private boolean settingShowSTrain;
    private boolean settingShowUTrain;
    private String url;
    private ArrayList<Station> stationList;
    private ActionBar actionBar;
    private TabsCollectionPagerAdapter tabsCollectionPagerAdapter;
    private ViewPager vpTabs;
    private StationListFragment stationListFragment;
    private MapFragment mapFragment;
    private ProgressBar pbLoading;


    // lifecycle methods

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // read sharedPreferences
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        settingShowSTrain = sharedPref.getBoolean(getString(R.string.settings_s_train_visible_key), true);
        settingShowUTrain = sharedPref.getBoolean(getString(R.string.settings_u_train_visible_key), true);
        sharedPref.registerOnSharedPreferenceChangeListener(this);

        // get url
        url = getString(R.string.oeff_station_json_url);

        // setup views
        stationListFragment = new StationListFragment();
        mapFragment = new MapFragment();
        tabsCollectionPagerAdapter = new TabsCollectionPagerAdapter(getSupportFragmentManager());
        tabsCollectionPagerAdapter.addContent(stationListFragment);
        tabsCollectionPagerAdapter.addContent(mapFragment);
        vpTabs = (ViewPager) findViewById(R.id.activity_main_vp_tabs);
        vpTabs.setAdapter(tabsCollectionPagerAdapter);
        actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                vpTabs.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

            }

            @Override
            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

            }
        };
        actionBar.addTab(actionBar.newTab().setText("Liste").setTabListener(tabListener));
        actionBar.addTab(actionBar.newTab().setText("Map").setTabListener(tabListener));
        vpTabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                getSupportActionBar().setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        pbLoading = (ProgressBar) findViewById(R.id.activity_main_pb_loading);
        pbLoading.setVisibility(View.INVISIBLE);

        // bind loader
        startLoader(STATION_LOADER_ID, false);

        requestLocationUpdates();
    }

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sharedPref.unregisterOnSharedPreferenceChangeListener(this);
    }

    // methods

    private void startLoader(int loaderID, boolean restart) {
        Bundle args = new Bundle();


        switch (loaderID) {
            case STATION_LOADER_ID:
                args.putString("url", url);
                args.putBoolean("showstrain", settingShowSTrain);
                args.putBoolean("showutrain", settingShowUTrain);
                pbLoading.setVisibility(View.VISIBLE);
                break;

            case STATION_DISTANCE_LOADER_ID:
                args.putParcelable("location", lastLocation);
                break;
        }

        if (getSupportLoaderManager().getLoader(loaderID) == null && !restart)
            getSupportLoaderManager().initLoader(loaderID, args, this);
        else
            getSupportLoaderManager().restartLoader(loaderID, args, this);

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

        switch (itemId) {
            case R.id.menu_main_action_reload:
                startLoader(STATION_LOADER_ID, true);
                return true;

            case R.id.menu_main_action_open_settings:
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);

        }

        return super.onOptionsItemSelected(item);
    }


    // location

    private void requestLocationUpdates() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_FINE_LOCATION);
        } else {
            locationManager.requestLocationUpdates(
                    LOCATION_PROVIDER,
                    1000,
                    0, this);

            if(locationManager.getLastKnownLocation(LOCATION_PROVIDER) != null)
                onLocationChanged(locationManager.getLastKnownLocation(LOCATION_PROVIDER));
        }
    }


    // location listener

    @Override
    public void onLocationChanged(Location location) {
        Log.v(LOG_TAG, "Location changed: " + location.toString());
        lastLocation = location;
        startLoader(STATION_DISTANCE_LOADER_ID, false);
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
                stationListFragment.setStations(stationList);
                mapFragment.setStations(stationList);
                pbLoading.setVisibility(View.INVISIBLE);
                break;

            case STATION_DISTANCE_LOADER_ID:
                if(data != null) stationList = data;
                stationListFragment.setStations(stationList);
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


    // sharedPreference changed listener

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if(s.equals(getString(R.string.settings_s_train_visible_key)) || s.equals(getString(R.string.settings_u_train_visible_key))) {
            settingShowSTrain = sharedPref.getBoolean(getString(R.string.settings_s_train_visible_key), true);
            settingShowUTrain = sharedPref.getBoolean(getString(R.string.settings_u_train_visible_key), true);

            startLoader(STATION_LOADER_ID, true);
        }
    }
}
