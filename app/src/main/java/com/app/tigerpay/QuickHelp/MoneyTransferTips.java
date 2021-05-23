package com.app.tigerpay.QuickHelp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.app.tigerpay.R;

import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip;

public class MoneyTransferTips extends AppCompatActivity {
    ImageView iv_withdraw;
    TextView tv_info, tv_buy,tv_skip,tv_next,tv_selling;;
    SimpleTooltip simpleTooltip;
    LinearLayout lnTransfer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money_transfer_tips);
        // iv_withdraw=(ImageView)findViewById(R.id.iv_withdraw);
        tv_info=(TextView)findViewById(R.id.tv_info);
        tv_skip=(TextView)findViewById(R.id.tv_skip);
        tv_next=(TextView)findViewById(R.id.tv_next);
        lnTransfer=(LinearLayout)findViewById(R.id.lnTransfer) ;
        tv_info.setText("Here,you can transfer the wallet money to other wallet Account.");
        new SimpleTooltip.Builder(this)
                .anchorView(lnTransfer)
                .padding((float) 4.0)
                .arrowHeight(1)
                .arrowWidth(1)
                .textColor(Color.WHITE)
                .showArrow(true)
                .dismissOnOutsideTouch(false)
                .animated(false)
                .transparentOverlay(false)
                .overlayMatchParent(false)
                .build()
                .show();

        tv_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MoneyTransferTips.this,DashBoardHelp.class);
                startActivity(intent);

            }
        });
        tv_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MoneyTransferTips.this,ReceiveTips.class);
                startActivity(intent);

            }
        });
    }


    public void alertDialog(Context context) {

        final Dialog dialog  = new Dialog(context);
        dialog.setCancelable(false);

        LayoutInflater li = LayoutInflater.from(context);
        View promptsView2 = li.inflate(R.layout.exit, null);
        dialog.setContentView(promptsView2);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        TextView btnok=(TextView)promptsView2.findViewById(R.id.btnok);
        TextView btncancel=(TextView)promptsView2.findViewById(R.id.btncansel);


        btnok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveTaskToBack(true);
                // finish();
            }
        });

        btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onBackPressed() {

        alertDialog(this);

    }
}
