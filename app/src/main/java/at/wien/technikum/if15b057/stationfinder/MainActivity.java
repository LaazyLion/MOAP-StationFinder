package at.wien.technikum.if15b057.stationfinder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;

import at.wien.technikum.if15b057.stationfinder.adapter.RvStationListAdapter;
import at.wien.technikum.if15b057.stationfinder.data.Station;
import at.wien.technikum.if15b057.stationfinder.loader.StationListWebLoader;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, LoaderManager.LoaderCallbacks<ArrayList<Station>> {

    private static final String LOG_TAG = MainActivity.class.getName();
    private static final int LOADER_ID = 1;

    private String url;

    private RecyclerView rvStationList;
    private RvStationListAdapter stationListAdapter;
    private LinearLayoutManager linearLayoutManager;

    // data
    private ArrayList<Station> stationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get url
        url = getString(R.string.oeff_station_json_url);

        // setup recycler view
        rvStationList = (RecyclerView) findViewById(R.id.activity_main_rv_station_list);
        stationListAdapter = new RvStationListAdapter();
        linearLayoutManager = new LinearLayoutManager(this);

        stationListAdapter.setClickListener(this);

        rvStationList.setAdapter(stationListAdapter);
        rvStationList.setLayoutManager(linearLayoutManager);

        // bind loader
        Bundle args = new Bundle();
        args.putString("url", url);
        getSupportLoaderManager().initLoader(LOADER_ID, args, this);
    }

    @Override
    public void onClick(View v) {
        int index = rvStationList.getChildAdapterPosition(v);
        Intent intent = new Intent(this, StationDetailsActivity.class);
        Bundle bundle = new Bundle();

        bundle.putParcelable("station", stationList.get(index));
        intent.putExtras(bundle);
        startActivity(intent);
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

        if (itemId == R.id.menu_main_action_reload) {
            LoaderManager loaderManager = getSupportLoaderManager();
            Loader<String> loader = loaderManager.getLoader(LOADER_ID);
            Bundle args = new Bundle();

            args.putString("url", url);

            if (loader == null)
                loaderManager.initLoader(LOADER_ID, args, this);
            else
                loaderManager.restartLoader(LOADER_ID, args, this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    // loader

    @Override
    public Loader<ArrayList<Station>> onCreateLoader(int id, Bundle args) {
        return new StationListWebLoader(this, args);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Station>> loader, ArrayList<Station> data) {
        stationList = data;
        stationListAdapter.setContent(stationList);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Station>> loader) {

    }
}
