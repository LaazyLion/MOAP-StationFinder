package at.wien.technikum.if15b057.stationfinder.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import at.wien.technikum.if15b057.stationfinder.R;
import at.wien.technikum.if15b057.stationfinder.data.Station;

/**
 * Created by matthias on 26.09.17.
 */

public class RvStationListAdapter extends RecyclerView.Adapter {

    private List<Station> content;
    private View.OnClickListener clickListener;
    private Context context;


    // constructor

    public RvStationListAdapter(Context context) {
        this.context = context;
    }


    // setter

    public void setContent(List<Station> content) {
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
        ViewHolder viewHolder = (ViewHolder) holder;
        Station station = content.get(position);
        viewHolder.tvName.setText(station.getName());

        int distance = station.getDistance();

        if(distance != 0) {
            String unit = distance > 1000 ? "km" : "m";
            String text = String.valueOf(distance > 1000 ? distance / 1000 : distance) + " " + unit;
            viewHolder.tvDistance.setVisibility(View.VISIBLE);
            viewHolder.tvDistance.setText(text);
        } else {
            viewHolder.tvDistance.setVisibility(View.INVISIBLE);
        }

        if(station.isContainingUnderground() && station.isContainingSTrain())
            viewHolder.ivIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.suburbanunderground));
        else if(station.isContainingSTrain())
            viewHolder.ivIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.suburban));
        else if(station.isContainingUnderground())
            viewHolder.ivIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.underground));

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

        TextView tvName;
        TextView tvDistance;
        ImageView ivIcon;

        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.content_station_list_tv_name);
            tvDistance = itemView.findViewById(R.id.content_station_list_tv_distance);
            ivIcon = itemView.findViewById(R.id.content_station_list_iv_icon);
        }
    }
}
