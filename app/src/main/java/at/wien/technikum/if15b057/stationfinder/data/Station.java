package at.wien.technikum.if15b057.stationfinder.data;

import android.graphics.PointF;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by matthias on 26.09.17.
 */

public class Station implements Parcelable {

    private String name;
    private PointF location;
    private ArrayList<String> lines;


    // constructor

    public Station() {
        name = "";
        location = new PointF(0, 0);
        lines = new ArrayList<>();
    }

    protected Station(Parcel in) {
        name = in.readString();
        location = in.readParcelable(PointF.class.getClassLoader());
        lines = in.createStringArrayList();
    }

    // getter

    public String getName() {
        return name;
    }

    public PointF getLocation() {
        return location;
    }

    public ArrayList<String> getLines() {
        return lines;
    }

    public String getLinesAsString() {
        StringBuilder stringBuilder = new StringBuilder();

        for (String s : lines
             ) {
            stringBuilder.append(s);
            stringBuilder.append(", ");
        }

        // delete last ", "
        stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length());

        return stringBuilder.toString();
    }


    // setter

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(PointF location) {
        this.location = location;
    }

    public void setLines(ArrayList<String> lines) {
        this.lines = lines;
    }


    // parcelable methods

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeParcelable(location, flags);
        dest.writeStringList(lines);
    }

    public static final Creator<Station> CREATOR = new Creator<Station>() {
        @Override
        public Station createFromParcel(Parcel in) {
            return new Station(in);
        }

        @Override
        public Station[] newArray(int size) {
            return new Station[size];
        }
    };
}
