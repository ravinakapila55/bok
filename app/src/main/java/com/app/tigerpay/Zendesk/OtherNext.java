package com.app.tigerpay.Zendesk;

import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;

import com.app.tigerpay.R;
import com.app.tigerpay.ToolabarActivity;


public class OtherNext extends ToolabarActivity implements View.OnClickListener {
    CardView acc_verification, rs_deposite;
    TextView txName;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_next);
        acc_verification = (CardView) findViewById(R.id.tv_acc_verification);
        rs_deposite = (CardView) findViewById(R.id.tv_rs_deposite);

        txName = (TextView) findViewById(R.id.txName);
        txName.setText("Support");

        acc_verification.setOnClickListener(this);
        rs_deposite.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.tv_acc_verification:
                intent=new Intent(OtherNext.this,SubmitRequest.class);
                intent.putExtra("first","Bitcoin Cash Address");
                startActivity(intent);

                break;

            case R.id.tv_rs_deposite:
                intent=new Intent(OtherNext.this,SubmitRequest.class);
                intent.putExtra("first","Any other issue in the app");
                startActivity(intent);
                break;


        }
    }

    @Override
    public void onBackPressed() {


        if (doubleBackToExitPressedOnce) {
            finishAffinity();
        }

        this.doubleBackToExitPressedOnce = true;


        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
                finish();
            }
        }, 1000);
    }
}
