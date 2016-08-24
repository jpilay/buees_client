package com.jpilay.bueesclient.adapter;

/**
 * Created by jpilay on 22/08/16.
 */

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.jpilay.bueesclient.R;
import com.jpilay.bueesclient.application.AppController;
import com.jpilay.bueesclient.models.DriverPublication;

import java.util.List;

public class DriverPublicationAdapter extends RecyclerView.Adapter<DriverPublicationAdapter.MyViewHolder> {

    private List<DriverPublication> driverPublicationList;
    private Context mContext;
    private ImageLoader imageLoader = AppController.getInstance().getImageLoader();;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public NetworkImageView image;
        public TextView description, route, datetime;
        public ImageView setting_delivery;

        public MyViewHolder(View view) {
            super(view);
            image = (NetworkImageView) view.findViewById(R.id.image);
            description = (TextView) view.findViewById(R.id.description);
            route = (TextView) view.findViewById(R.id.route);
            datetime = (TextView) view.findViewById(R.id.datetime);
            setting_delivery = (ImageView) view.findViewById(R.id.setting_delivery);
        }
    }


    public DriverPublicationAdapter(List<DriverPublication> publicationsList) {
        this.driverPublicationList = publicationsList;

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
    }

    public void setDriverPublicationList(List<DriverPublication> publicationsList){
        this.driverPublicationList = publicationsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.driver_publication_row, parent, false);
        mContext = parent.getContext();

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        DriverPublication driverPublication = driverPublicationList.get(position);
        holder.image.setImageUrl(driverPublication.getImage(),imageLoader);
        holder.description.setText(driverPublication.getDescription());
        holder.route.setText(driverPublication.getBusRoute().getName());
        holder.datetime.setText(driverPublication.getHour() + " - " + driverPublication.getDate());

        ((Activity) mContext).registerForContextMenu(holder.setting_delivery);
        holder.setting_delivery.showContextMenu();
    }

    @Override
    public int getItemCount() {
        return driverPublicationList.size();
    }

}
