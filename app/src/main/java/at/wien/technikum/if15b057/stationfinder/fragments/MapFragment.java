package at.wien.technikum.if15b057.stationfinder.fragments;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import at.wien.technikum.if15b057.stationfinder.R;
import at.wien.technikum.if15b057.stationfinder.data.Station;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {

    private List<Station> stations;
    private SupportMapFragment mapFragment;
    private GoogleMap map;


    // constructor

    public MapFragment() {
        // Required empty public constructor
    }


    // setter

    public void setStations(List<Station> stations) {
        this.stations = stations;
        drawMarkers();
    }


    // methods

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_map, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragment_map_mapfragment);
        mapFragment.getMapAsync(this);

        return root;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
            drawMarkers();
        }
    }

    private void drawMarkers() {
        if(map != null) {
            map.clear();

            if(stations != null) {
                for (Station s : stations
                        ) {
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(new LatLng(s.getLocation().y, s.getLocation().x));
                    markerOptions.title(s.getName());
                    map.addMarker(markerOptions);
                }
            }
        }
    }
}
