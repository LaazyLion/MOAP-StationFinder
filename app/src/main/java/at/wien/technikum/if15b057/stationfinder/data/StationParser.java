package at.wien.technikum.if15b057.stationfinder.data;

import android.graphics.PointF;
import android.util.JsonReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by matthias on 26.09.17.
 */

public class StationParser {

    private static final String LOG_TAG = StationParser.class.getName();

    public static ArrayList<Station> fromStream(InputStream inputStream) throws IOException, JSONException {
        JsonReader jsonReader = new JsonReader(new InputStreamReader(inputStream));
        ArrayList<Station> stations = fromJson(readJsonObject(jsonReader));
        jsonReader.close();

        return stations;
    }

    public static ArrayList<Station> fromJson(JSONObject jsonRoot) throws JSONException {

        int totalFeatures = jsonRoot.getInt("totalFeatures");
        ArrayList<Station> stationList = new ArrayList<>(totalFeatures);
        JSONArray jsonFeatures = jsonRoot.getJSONArray("features");

        for (int i = 0; i < totalFeatures; i++) {
            // station
            JSONObject jsonFeature = jsonFeatures.getJSONObject(i);
            Station station = new Station();

            // position
            JSONObject jsonGeometry = jsonFeature.getJSONObject("geometry");
            JSONArray jsonCoordinates = jsonGeometry.getJSONArray("coordinates");

            // properties
            JSONObject jsonProperties = jsonFeature.getJSONObject("properties");
            String linesString = jsonProperties.getString("HLINIEN");

            // split lines into array
            ArrayList<String> lineList = new ArrayList<>();

            linesString = linesString.trim();

            for (String s : linesString.split(",")
                    ) {
                lineList.add(s);
            }

            station.setName(jsonProperties.getString("HTXT"));
            station.setLocation(new PointF((float)jsonCoordinates.getDouble(0), (float)jsonCoordinates.getDouble(1)));
            station.setLines(lineList);

            stationList.add(station);
        }

        return stationList;
    }

    public static JSONObject readJsonObject(JsonReader jsonReader) throws IOException, JSONException {
        JSONObject jsonObject = new JSONObject();
        String name = "";

        jsonReader.beginObject();

        while (jsonReader.hasNext()) {
            switch (jsonReader.peek()) {
                case BEGIN_ARRAY:
                    jsonObject.put(name, readJsonArray(jsonReader));
                    break;
                case BEGIN_OBJECT:
                    jsonObject.put(name, readJsonObject(jsonReader));
                    break;
                case BOOLEAN:
                    jsonObject.put(name, jsonReader.nextBoolean());
                    break;
                case END_DOCUMENT:
                    break;
                case NAME:
                    name = jsonReader.nextName();
                    break;
                case NULL:
                    jsonReader.nextNull();
                    jsonObject.put(name, null);
                    break;
                case NUMBER:
                    jsonObject.put(name, jsonReader.nextDouble());
                    break;
                case STRING:
                    jsonObject.put(name, jsonReader.nextString());
                    break;
            }
        }

        jsonReader.endObject();

        return jsonObject;
    }

    public static JSONArray readJsonArray(JsonReader jsonReader) throws IOException, JSONException {
        JSONArray jsonArray = new JSONArray();
        String name = "";

        jsonReader.beginArray();

        while (jsonReader.hasNext()) {

            switch (jsonReader.peek()) {
                case BEGIN_ARRAY:
                    jsonArray.put(readJsonArray(jsonReader));
                    break;
                case BEGIN_OBJECT:
                    jsonArray.put(readJsonObject(jsonReader));
                    break;
                case BOOLEAN:
                    jsonArray.put(jsonReader.nextBoolean());
                    break;
                case END_DOCUMENT:
                    break;
                case NULL:
                    jsonReader.nextNull();
                    jsonArray.put(null);
                    break;
                case NUMBER:
                    jsonArray.put(jsonReader.nextDouble());
                    break;
                case STRING:
                    jsonArray.put(jsonReader.nextString());
                    break;
            }
        }

        jsonReader.endArray();

        return jsonArray;
    }
}
