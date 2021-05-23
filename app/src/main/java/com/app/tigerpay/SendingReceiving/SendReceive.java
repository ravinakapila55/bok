package com.app.tigerpay.SendingReceiving;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.tigerpay.Adapter.ViewPagerAdapter;
import com.app.tigerpay.Dashboard;
import com.app.tigerpay.R;


public class SendReceive extends AppCompatActivity implements View.OnClickListener {
    ImageView ivarrow;
    TextView txName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sending_receiving);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ivarrow= (ImageView) findViewById(R.id.ivarrow);
        txName= (TextView) findViewById(R.id.txName);

        ivarrow.setOnClickListener(this);
        txName.setVisibility(View.VISIBLE);
        txName.setText("Transations");

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Send"));
        tabLayout.addTab(tabLayout.newTab().setText("Receive"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent=new Intent(SendReceive.this, Dashboard.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        Intent intent;

        switch (v.getId()){

            case R.id.ivarrow:

                intent=new Intent(SendReceive.this,Dashboard.class);
                startActivity(intent);

                break;
        }
    }

}