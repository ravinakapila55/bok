package com.app.tigerpay.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.app.tigerpay.Model.FeesChargeModet;
import com.app.tigerpay.R;

import java.util.ArrayList;


public class FeesAdapter extends RecyclerView.Adapter<FeesAdapter.MyBookingHolder>{
    Context context;
    BidAskAdapter.MyClickListener mcl;
    ArrayList<FeesChargeModet> list;



    public FeesAdapter(Context context, ArrayList<FeesChargeModet> list)
    {
        this.context=context;
        this.list=list;
    }

    @Override
    public FeesAdapter.MyBookingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.fees_adapter,parent,false);
        return new FeesAdapter.MyBookingHolder(view);

    }

    @Override
    public void onBindViewHolder(FeesAdapter.MyBookingHolder holder, int position) {


        holder.txfees.setText(list.get(position).getFees());
        holder.txfrom.setText(list.get(position).getFrom());
        holder.txto.setText(list.get(position).getTo());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyBookingHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView txfees,txto,txfrom;

        public MyBookingHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            txfees= (TextView) itemView.findViewById(R.id.tv_fees);
            txto= (TextView) itemView.findViewById(R.id.tv_to);
            txfrom= (TextView) itemView.findViewById(R.id.tv_from);

        }

        @Override
        public void onClick(View v) {

            switch (v.getId()){

            }
        }
    }

    public interface MyClickListener {
        public void setOnItemClick(View v, int pos);
        public void setEditlick(View v, int pos);
    }

    public void setOnItemClickListener(BidAskAdapter.MyClickListener mcl) {
        this.mcl = mcl;
    }

}

