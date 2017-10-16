package at.wien.technikum.if15b057.stationfinder.loader;

import android.content.Context;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import org.json.JSONException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

import at.wien.technikum.if15b057.stationfinder.data.Station;
import at.wien.technikum.if15b057.stationfinder.data.StationParser;

/**
 * Created by matthias on 26.09.17.
 */

public class StationListWebLoader extends AsyncTaskLoader<List<Station>> {

    private static final String LOG_TAG = StationListWebLoader.class.getName();
    private URL url;
    private boolean showSTrain;
    private boolean showUTrain;
    private HttpURLConnection connection;


    // constructor

    public StationListWebLoader(Context context, Bundle args) {
        super(context);

        try {
            url = new URL(args.getString("url"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        showSTrain = args.getBoolean("showstrain");
        showUTrain = args.getBoolean("showutrain");
    }


    // loader methods

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public List<Station> loadInBackground() {

        Log.v(LOG_TAG, "Started loading...");

        List<Station> stationList = new ArrayList<>();

        try {
            connection = (HttpURLConnection) url.openConnection();
            stationList = StationParser.fromStream(connection.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            stationList = new ArrayList<>();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if(connection != null)
                connection.disconnect();
        }

        try {
            stationList.removeIf(station -> (!station.isContainingUnderground() && !station.isContainingSTrain()));
        } catch (NoClassDefFoundError ex) {
            ex.printStackTrace();
        }

        Log.v(LOG_TAG, "Loading done!");

        return stationList;
    }
}
