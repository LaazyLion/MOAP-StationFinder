package at.wien.technikum.if15b057.stationfinder.data;

import android.graphics.PointF;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashSet;

public class Station implements Parcelable {

    private String name;
    private PointF location;
    private int distance;
    private boolean containingUnderground;
    private boolean containingSTrain;
    private HashSet<String> lines;


    // constructor

    public Station() {
        name = "";
        location = new PointF(0, 0);
        distance = 0;
        lines = new HashSet<>();
        containingUnderground = false;
        containingSTrain = false;
    }

    protected Station(Parcel in) {
        name = in.readString();
        location = in.readParcelable(PointF.class.getClassLoader());
        distance = in.readInt();
        lines = new HashSet<>(in.createStringArrayList());
        boolean[] booleanArray = in.createBooleanArray();
        containingUnderground = booleanArray[0];
        containingSTrain = booleanArray[1];
    }

    // getter

    public String getName() {
        return name;
    }

    public PointF getLocation() {
        return location;
    }

    public int getDistance() {
        return distance;
    }

    public HashSet<String> getLines() {
        return lines;
    }

    public String getLinesAsString() {
        StringBuilder stringBuilder = new StringBuilder();

        for (String s : lines
             ) {
            stringBuilder.append(s);
            stringBuilder.append("\n");
        }

        return stringBuilder.toString();
    }

    public boolean isContainingUnderground() {
        return containingUnderground;
    }

    public boolean isContainingSTrain() {
        return containingSTrain;
    }


    // setter

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(PointF location) {
        this.location = location;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public void setLines(HashSet<String> lines) {
        this.lines = lines;
    }

    public void setContainingUnderground(boolean containingUnderground) {
        this.containingUnderground = containingUnderground;
    }

    public void setContainingSTrain(boolean containingSTrain) {
        this.containingSTrain = containingSTrain;
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
        dest.writeInt(distance);
        ArrayList<String> buffer = new ArrayList<>();
        buffer.addAll(lines);
        dest.writeStringList(buffer);
        boolean[] booleanArray = new boolean[2];
        booleanArray[0] = containingUnderground;
        booleanArray[1] = containingSTrain;
        dest.writeBooleanArray(booleanArray);
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
