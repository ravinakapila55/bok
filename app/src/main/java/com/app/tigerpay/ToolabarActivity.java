package com.app.tigerpay;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class ToolabarActivity extends AppCompatActivity {

    public View view;
    public FrameLayout frame;
    public ImageView iv_arrow;
    public TextView txName;
    public Toolbar toolbar;
    public RelativeLayout rllayer,rlmain;

    @Override
    public void setContentView(int layoutResID) {
        view=getLayoutInflater().inflate(R.layout.toolbar_arrow,null);
        frame=(FrameLayout)view.findViewById(R.id.frame);

        getLayoutInflater().inflate(layoutResID,frame,true);
        super.setContentView(view);
        setTitle("");
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        rllayer=(RelativeLayout) findViewById(R.id.rllayer);
        rlmain=(RelativeLayout) findViewById(R.id.rlmain);
        txName=(TextView) findViewById(R.id.txName);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }

    @Override
    public void onBackPressed() {
        finish();
    }


}
