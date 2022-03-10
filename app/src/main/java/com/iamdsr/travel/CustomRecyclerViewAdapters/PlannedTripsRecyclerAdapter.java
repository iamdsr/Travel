package com.iamdsr.travel.CustomRecyclerViewAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.iamdsr.travel.Interfaces.RecyclerViewActionsInterface;
import com.iamdsr.travel.Models.TripModel;
import com.iamdsr.travel.R;

public class PlannedTripsRecyclerAdapter extends ListAdapter<TripModel, PlannedTripsRecyclerAdapter.ViewHolder> {

    private final RecyclerViewActionsInterface recyclerViewActionsInterface;
    private Context context;
    public PlannedTripsRecyclerAdapter(@NonNull DiffUtil.ItemCallback<TripModel> diffCallback, RecyclerViewActionsInterface recyclerViewActionsInterface) {
        super(diffCallback);
        this.recyclerViewActionsInterface = recyclerViewActionsInterface;
    }

    @NonNull
    @Override
    public PlannedTripsRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_trips_item,parent,false);
        context = parent.getContext();
        return new ViewHolder(view, recyclerViewActionsInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull PlannedTripsRecyclerAdapter.ViewHolder holder, int position) {
        TripModel tripModel = getItem(position);
        holder.bind(tripModel, context);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTitle, mDesc, mJDate, mRDate, mFrom, mTo, mTotalPax, mDuration;
        public ViewHolder(@NonNull View itemView, RecyclerViewActionsInterface recyclerViewActionsInterface) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.trip_title);
            mDesc = itemView.findViewById(R.id.trip_desc);
            mTotalPax = itemView.findViewById(R.id.total_person);
            mJDate = itemView.findViewById(R.id.journey_date);
            mFrom = itemView.findViewById(R.id.place_from);
            mRDate = itemView.findViewById(R.id.return_date);
            mTo = itemView.findViewById(R.id.place_to);
            mDuration = itemView.findViewById(R.id.total_duration);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO: 28-01-2022 handle click events
                    if (recyclerViewActionsInterface != null){
                        int position = getAdapterPosition();
                        if (position!=RecyclerView.NO_POSITION){
                            recyclerViewActionsInterface.onItemClick(position);
                        }
                    }
                }
            });
        }

        public void bind(TripModel tripModel, Context context) {
            final String title = tripModel.getTrip_title();
            final String desc = tripModel.getTrip_desc();
            final String from = tripModel.getPlace_from();
            final String to = tripModel.getPlace_to();
            final String jDate = tripModel.getJourney_date();
            final String rDate = tripModel.getReturn_date();
            final long pax = tripModel.getTotal_heads();
            final long duration = tripModel.getDuration_in_days();
            mTitle.setText(title);
            mDesc.setText(desc);
            mJDate.setText(jDate);
            mRDate.setText(rDate);
            mFrom.setText(from);
            mTo.setText(to);
            mTotalPax.setText(String.valueOf(pax));
            mDuration.setText(String.valueOf(duration));
        }
    }
}
