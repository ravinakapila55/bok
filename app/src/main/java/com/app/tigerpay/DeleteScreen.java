package com.app.tigerpay;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.app.tigerpay.Util.Constant;
import com.app.tigerpay.Util.PreferenceFile;


public class DeleteScreen extends AppCompatActivity {

    TextView tvhyper,tvNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_screen);
        tvhyper= (TextView) findViewById(R.id.tvhyper);
        tvNumber= (TextView) findViewById(R.id.tvNumber);

//        tvhyper.setMovementMethod(LinkMovementMethod.getInstance());
//        String text = "<a href='http://18.216.88.154/metapay'> metapay.com </a>";
//        tvhyper.setText(Html.fromHtml(text));

        tvNumber.setText(PreferenceFile.getInstance().getPreferenceData(this, Constant.phone));
    }
}
