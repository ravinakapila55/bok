package com.app.tigerpay.Zendesk;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;


import com.app.tigerpay.R;
import com.app.tigerpay.Util.Constant;
import com.app.tigerpay.Util.PreferenceFile;
import com.app.tigerpay.Util.Retrofit2;
import com.app.tigerpay.Util.RetrofitResponse;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class MyTickets extends AppCompatActivity implements RetrofitResponse {

    ImageView ivarrow;
    RecyclerView recyclerView;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tickets);

        ivarrow = (ImageView) findViewById(R.id.ivarrow);
        recyclerView= (RecyclerView) findViewById(R.id.recyclerview);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        ivarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        callservice();
    }

    private void callservice() {

        if (Constant.isConnectingToInternet(this)) {
            new Retrofit2(this, MyTickets.this, Constant.REQ_State, Constant.State+ PreferenceFile.getInstance().getPreferenceData(this,Constant.Courtry_id)).callService(true);
        }
        else {
            Constant.alertDialog(this, getResources().getString(R.string.check_connection));
        }
    }

    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            finishAffinity();
//            super.onBackPressed();
            Log.e("Biding","once");
//            return;
        }

        this.doubleBackToExitPressedOnce = true;
        //Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();


        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
                finish();
            }
        }, 1000);
    }

    @Override
    public void onServiceResponse(int requestCode, Response<ResponseBody> response) {

    }
}
