package at.wien.technikum.if15b057.stationfinder.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.ArrayList;

import at.wien.technikum.if15b057.stationfinder.R;
import at.wien.technikum.if15b057.stationfinder.StationDetailsActivity;
import at.wien.technikum.if15b057.stationfinder.adapter.RvStationListAdapter;
import at.wien.technikum.if15b057.stationfinder.data.Station;

public class StationListFragment extends Fragment implements View.OnClickListener {

    private static final String LOG_TAG = StationListFragment.class.getName();

    private RecyclerView rvStationList;
    private RvStationListAdapter stationListAdapter;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<Station> stations;


    // constructor

    public StationListFragment() {
        // Required empty public constructor
    }


    // setter

    public void setStations(ArrayList<Station> stations) {
        this.stations = stations;
        if(stationListAdapter != null)
            stationListAdapter.setContent(stations);
    }


    // methods

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_station_list, container, false);

        rvStationList = (RecyclerView) root.findViewById(R.id.fragment_station_list_rv_station_list);

        stationListAdapter = new RvStationListAdapter(getActivity());
        stationListAdapter.setClickListener(this);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        rvStationList.setAdapter(stationListAdapter);
        rvStationList.setLayoutManager(linearLayoutManager);

        return root;
    }

    @Override
    public void onClick(View v) {
        Log.v(LOG_TAG, String.valueOf(v.getId()));
        int index = rvStationList.getChildAdapterPosition(v);
        Intent intent = new Intent(getActivity(), StationDetailsActivity.class);
        Bundle bundle = new Bundle();

        bundle.putParcelable("station", stations.get(index));
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
