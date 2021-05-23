package com.app.tigerpay.SendingReceiving;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.tigerpay.Adapter.ReceiverAdapter;
import com.app.tigerpay.R;


public class ReceivingBitcoin  extends Fragment
{
    RecyclerView recyclerView;
    View view;
    ReceiverAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view= inflater.inflate(R.layout.fragment_receiving_bitcoin, container, false);

        recyclerView= (RecyclerView) view.findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        adapter=new ReceiverAdapter(getActivity());
        recyclerView.setAdapter(adapter);
        return view;
    }
}