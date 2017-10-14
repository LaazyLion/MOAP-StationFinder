package at.wien.technikum.if15b057.stationfinder.loader;

import android.content.Context;
import android.graphics.PointF;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import at.wien.technikum.if15b057.stationfinder.data.Station;

/**
 * Created by matthias on 26.09.17.
 */

public class StationDistanceLoader extends AsyncTaskLoader<List<Station>> {

    private static final String LOG_TAG = StationDistanceLoader.class.getName();
    private Location location;
    private List<Station> stations;


    // constructor

    public StationDistanceLoader(Context context, Bundle args) {
        super(context);
        location = args.getParcelable("location");
    }


    // setter

    public void setStations(List<Station> stations) {
        this.stations = stations;
    }


    // loader methods

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public List<Station> loadInBackground() {

        Log.v(LOG_TAG, "Started sorting...");

        if(stations == null) return null;

        for (Station s : stations
             ) {
            Location temp = new Location(LocationManager.GPS_PROVIDER);
            temp.setLatitude(s.getLocation().y);
            temp.setLongitude(s.getLocation().x);
            s.setDistance((int) location.distanceTo(temp));
        }

        // api 24 required
        // stations.sort(Comparator.comparing(Station::getDistance));

        Collections.sort(stations, (s1, s2) -> (s1.getDistance() - s2.getDistance()));

        Log.v(LOG_TAG, "Sorting done!");

        return stations;
    }
}
