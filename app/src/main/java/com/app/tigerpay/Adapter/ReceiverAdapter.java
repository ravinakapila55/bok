package com.app.tigerpay.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.tigerpay.R;


/**
 * Created by pro22 on 27/10/17.
 */

public class ReceiverAdapter extends RecyclerView.Adapter<ReceiverAdapter.MyBookingHolder> {
    Context context;

    public ReceiverAdapter(Context context)
    {
        this.context=context;
    }

    @Override
    public ReceiverAdapter.MyBookingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.send_layot,parent,false);
        return new ReceiverAdapter.MyBookingHolder(view);
    }

    @Override
    public void onBindViewHolder(ReceiverAdapter.MyBookingHolder holder, final int position) {


    }
    @Override
    public int getItemCount() {
        return 5;
    }

    public class MyBookingHolder extends RecyclerView.ViewHolder {

        ImageView imgAppointmentCompletion;
        TextView textName,textServiceType,textPayment,textDate,textAddress,txViewDetails;
        public MyBookingHolder(View itemView) {
            super(itemView);

        }
    }
}