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

import at.wien.technikum.if15b057.stationfinder.data.Station;
import at.wien.technikum.if15b057.stationfinder.data.StationParser;

/**
 * Created by matthias on 26.09.17.
 */

public class StationListWebLoader extends AsyncTaskLoader<ArrayList<Station>> {

    private static final String TAG = StationListWebLoader.class.getName();
    private URL url;
    private HttpURLConnection connection;


    // constructor

    public StationListWebLoader(Context context, Bundle args) {
        super(context);

        try {
            url = new URL(args.getString("url"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }


    // loader methods

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public ArrayList<Station> loadInBackground() {

        Log.v(TAG, "Started loading...");

        ArrayList<Station> stationList = new ArrayList<>();

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

        Log.v(TAG, "Loading done!");

        return stationList;
    }


    // methods

    private ArrayList<Station> createDummyStations(int count) {
        ArrayList<Station> stationList = new ArrayList<>();

        for(int i = 0; i < count; i ++) {
            Station station = new Station();
            station.setName("Station " + i);
            station.setLocation(new PointF(i, i));

            ArrayList<String> lineList = new ArrayList<>();

            for(int j = 0; j < 6; j++) {
                lineList.add("Line " + j);
            }

            station.setLines(lineList);

            stationList.add(station);
        }

        return stationList;
    }
}
