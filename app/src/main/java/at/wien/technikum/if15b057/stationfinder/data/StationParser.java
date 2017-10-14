package at.wien.technikum.if15b057.stationfinder.data;

import android.graphics.PointF;
import android.util.JsonReader;
import android.util.Log;
import android.util.SparseArray;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.comparator.ExtensionFileComparator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Created by matthias on 26.09.17.
 */

public class StationParser {

    private static final String LOG_TAG = StationParser.class.getName();

    public static List<Station> fromStream(InputStream inputStream) throws IOException, JSONException {

        Log.v(LOG_TAG, "Loading from Stream...");
        JSONObject jsonObject = new JSONObject(IOUtils.toString(inputStream));
        Log.v(LOG_TAG, "Loading done");
        return fromJson(jsonObject);
    }

    public static List<Station> fromJson(JSONObject jsonRoot) throws JSONException {

        int totalFeatures = jsonRoot.getInt("totalFeatures");
        Integer divaID;
        Station station;
        ArrayList<Station> stationList;
        SparseArray<Station> buffer = new SparseArray<>();
        JSONArray jsonFeatures = jsonRoot.getJSONArray("features");

        for (int i = 0; i < totalFeatures; i++) {
            // station
            JSONObject jsonFeature = jsonFeatures.getJSONObject(i);

            // position
            JSONObject jsonGeometry = jsonFeature.getJSONObject("geometry");
            JSONArray jsonCoordinates = jsonGeometry.getJSONArray("coordinates");

            // properties
            JSONObject jsonProperties = jsonFeature.getJSONObject("properties");
            String linesString = jsonProperties.getString("HLINIEN");
            try {
                divaID = jsonProperties.getInt("DIVA_ID");
            } catch (Exception e) {
                divaID = null;
            }

            // split lines into array
            HashSet<String> lineSet = new HashSet<>();

            linesString = linesString.trim();

            boolean containsUnderground = false;
            boolean containsSTrain = false;
            for (String s : linesString.split(",")
                    ) {
                s = s.trim();
                lineSet.add(s);
                if(s.startsWith("U")) {
                    containsUnderground = true;
                } else if(s.startsWith("S")) {
                    containsSTrain = true;
                }
            }

            if(!containsUnderground && !containsSTrain) continue;

            // add to list
            if(divaID != null)
                station = buffer.get(divaID, null);
            else
                station = null;

            if(station == null) {
                station = new Station();
                station.setName(jsonProperties.getString("HTXT"));
                station.setLocation(new PointF((float) jsonCoordinates.getDouble(0), (float) jsonCoordinates.getDouble(1)));
                station.setLines(lineSet);
                station.setContainingUnderground(containsUnderground);
                station.setContainingSTrain(containsSTrain);
                if(divaID != null)
                    buffer.put(divaID, station);
            } else {
                station.getLines().addAll(lineSet);
            }
        }

        stationList = new ArrayList<>(buffer.size());

        for (int i = 0; i < buffer.size(); i++) {
            stationList.add(buffer.valueAt(i));
        }

        return stationList;
    }
}
