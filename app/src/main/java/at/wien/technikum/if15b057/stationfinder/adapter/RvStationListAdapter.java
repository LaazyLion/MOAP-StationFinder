package at.wien.technikum.if15b057.stationfinder.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import at.wien.technikum.if15b057.stationfinder.R;
import at.wien.technikum.if15b057.stationfinder.data.Station;

/**
 * Created by matthias on 26.09.17.
 */

public class RvStationListAdapter extends RecyclerView.Adapter {

    private ArrayList<Station> content;
    private View.OnClickListener clickListener;


    // setter

    public void setContent(ArrayList<Station> content) {
        this.content = content;
        notifyDataSetChanged();
    }

    public void setClickListener(View.OnClickListener clickListener) {
        this.clickListener = clickListener;
    }


    // adapter methods

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.content_station_list, parent, false);

        v.setOnClickListener(clickListener);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder)holder).tvName.setText(content.get(position).getName());
    }

    @Override
    public int getItemCount() {
        if(content != null)
            return content.size();
        else
            return 0;
    }


    // ViewHolder

    private class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvName;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.content_station_list_tv_name);
        }
    }
}
