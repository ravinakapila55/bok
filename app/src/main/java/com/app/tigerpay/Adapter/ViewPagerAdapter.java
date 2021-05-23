package com.app.tigerpay.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.app.tigerpay.ReceiveBitcoin;
import com.app.tigerpay.SendBitcoin;
import com.app.tigerpay.SendingReceiving.ReceivingBitcoin;
import com.app.tigerpay.SendingReceiving.SendedBitcoin;


/**
 * Created by pro22 on 27/10/17.
 */

public class ViewPagerAdapter  extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public ViewPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                SendedBitcoin tab1 = new SendedBitcoin();
                return tab1;
            case 1:
                ReceivingBitcoin tab2 = new ReceivingBitcoin();
                return tab2;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}