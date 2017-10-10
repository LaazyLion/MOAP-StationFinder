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

import at.wien.technikum.if15b057.stationfinder.R;
import at.wien.technikum.if15b057.stationfinder.data.Station;

/**
 * Created by matthias on 26.09.17.
 */

public class RvStationListAdapter extends RecyclerView.Adapter {

    private ArrayList<Station> content;
    private View.OnClickListener clickListener;
    private Context context;


    // constructor

    public RvStationListAdapter(Context context) {
        this.context = context;
    }


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
        int distance = content.get(position).getDistance();
        String unit = distance > 1000 ? "km" : "m";
        String text = String.valueOf(distance > 1000 ? distance / 1000 : distance) + " " + unit;
        ((ViewHolder)holder).tvDistance.setText(text);

        boolean hassuburban = false;
        boolean hasunderground = false;

        for (String s : content.get(position).getLines()
             ) {
            if(s.startsWith("U"))
                hasunderground = true;

            if(s.startsWith("S"))
                hassuburban = true;
        }

        if(hassuburban && hasunderground)
            ((ViewHolder)holder).ivIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.suburbanunderground));
        else if(hassuburban)
            ((ViewHolder)holder).ivIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.suburban));
        else if(hasunderground)
            ((ViewHolder)holder).ivIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.underground));

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
            tvName = (TextView) itemView.findViewById(R.id.content_station_list_tv_name);
            tvDistance = (TextView) itemView.findViewById(R.id.content_station_list_tv_distance);
            ivIcon = (ImageView) itemView.findViewById(R.id.content_station_list_iv_icon);
        }
    }
}
