package com.app.tigerpay;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.tigerpay.Adapter.CustomMySubscriptionsAdapter;
import com.app.tigerpay.Adapter.ServicesAdapter;
import com.app.tigerpay.Model.MySubscriptionModel;
import com.app.tigerpay.Model.NotificationModel;
import com.app.tigerpay.Model.ServiceModel;
import com.app.tigerpay.TransationSaction.TransationBitcoinType;
import com.app.tigerpay.TransationSaction.TransationINRtype;
import com.app.tigerpay.Util.Constant;
import com.app.tigerpay.Util.PreferenceFile;
import com.app.tigerpay.Util.Retrofit2;
import com.app.tigerpay.Util.RetrofitResponse;
import com.app.tigerpay.Util.UtilClass;
import com.app.tigerpay.activties.DashboardActivities;
import com.google.gson.stream.JsonWriter;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class Dashboard extends Drawer implements View.OnClickListener, RetrofitResponse
{
    ImageView ivarrow;
    ImageView ivPremium;
    Bitmap bitmap;
    boolean doubleBackToExitPressedOnce = false;
    String vibrate = "", sound = "";
    Typeface tfArchitectsDaughter;
    static public int flag = 0;
    NumberFormat formatter;
    TextView txName, txSelling,tvCount, txBuy, tvBuyrate, txSellRate, tvBitcoin, tvINR, tvwallet, currentsymbol;
    LinearLayout lnBidding, lnrefresh, lnINR, lnAsk, lntransaction, lnDeposit, lnFlight, lnPan,lnRateChart,lnSettings,
            lnMovies, lnMobileRecharge, lnTransfer, lnReceiver, lnBank, lnRece, lnSend;
    Handler handler;
    Timer timer;
    RelativeLayout rlCountt;
    String key="0";
    LinearLayout ll_bottom;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        ivarrow = (ImageView) findViewById(R.id.ivarrow);
        ivPremium = (ImageView) findViewById(R.id.ivPremium);
        txName = (TextView) findViewById(R.id.txName);
        ll_bottom = (LinearLayout) findViewById(R.id.ll_bottom);
        rlCountt = (RelativeLayout) findViewById(R.id.rlCountt);
        txBuy = (TextView) findViewById(R.id.txBuy);
        tvCount = (TextView) findViewById(R.id.tvCount);
        lnrefresh = (LinearLayout) findViewById(R.id.lnrefresh);
        tvBuyrate = (TextView) findViewById(R.id.tvBuyrate);
        txSellRate = (TextView) findViewById(R.id.txSellRate);
        tvwallet = (TextView) findViewById(R.id.tvwallet);
        tvINR = (TextView) findViewById(R.id.tvINR);
        tvBitcoin = (TextView) findViewById(R.id.tvBitcoin);
        txSelling = (TextView) findViewById(R.id.txSelling);
        lnSend = (LinearLayout) findViewById(R.id.lnSend);

        lnBank = (LinearLayout) findViewById(R.id.lnBank);
        lnDeposit = (LinearLayout) findViewById(R.id.lnDeposit);
        lntransaction = (LinearLayout) findViewById(R.id.lntransaction);
        lnTransfer = (LinearLayout) findViewById(R.id.lnTransfer);
        lnINR = (LinearLayout) findViewById(R.id.lnINR);
        lnReceiver = (LinearLayout) findViewById(R.id.lnReceiver);
        lnRece = (LinearLayout) findViewById(R.id.lnRece);


        lnRateChart = (LinearLayout) findViewById(R.id.lnRateChart);
        lnSettings = (LinearLayout) findViewById(R.id.lnSettings);

        lnSettings.setOnClickListener(this);
        lnRateChart.setOnClickListener(this);
        ivPremium.setOnClickListener(this);
        ll_bottom.setOnClickListener(this);

        ivarrow.setVisibility(View.GONE);
        flag = 0;

        rlMain.setVisibility(View.GONE);

        handler=new Handler();
        timer = new Timer();

        if ((2.9671177849035E11)> (602000.0))
        {
            Log.e("resuttt ","gretaer");
        }
        else if ((2.9671177849035E11)<( 602000.0))
        {
            Log.e("resuttt ","less");
        }
        if ((2.9671177849035E11)==( 602000.0))
        {
            Log.e("resuttt ","equal");
        }

        startService(new Intent(Dashboard.this, BackGroundService.class));

        callService();

        if(PreferenceFile.getInstance().getPreferenceData(Dashboard.this, Constant.NORMAL_DEP_AMT)!=null)
        {
            Log.e("InsideNORMAL_DEP_AMT ","inside");

            final TimerTask doAsynchronousTask1 = new TimerTask()
            {
                @Override
                public void run()
                {
                    handler.post(new Runnable()
                    {
                        public void run()
                        {
                            checkDepositStatus();
                        }
                    });
                }
            };
            timer.schedule(doAsynchronousTask1, 0, 10000);
        }
        else
        {
//            Log.e("TAGGG", "onCreate:null " );
        }

        lntransaction.setOnClickListener(this);

        lnBank.setOnClickListener(this);
        lnINR.setOnClickListener(this);

        lnSend.setOnClickListener(this);
        lnDeposit.setOnClickListener(this);

        lnTransfer.setOnClickListener(this);
        txSelling.setOnClickListener(this);
        txBuy.setOnClickListener(this);
        lnReceiver.setOnClickListener(this);
        lnRece.setOnClickListener(this);
        lnrefresh.setOnClickListener(this);

//       formatter = new DecimalFormat("#,##,###");


        formatter = NumberFormat.getCurrencyInstance(new Locale("en", PreferenceFile.getInstance().
                getPreferenceData(Dashboard.this,Constant.selectedCountryNameCode).toString()));
        DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) formatter).getDecimalFormatSymbols();
        decimalFormatSymbols.setCurrencySymbol("");
        ((DecimalFormat) formatter).setDecimalFormatSymbols(decimalFormatSymbols);

        tfArchitectsDaughter = Typeface.createFromAsset(getAssets(), "Fonts/DroidSans-Bold.ttf");
        tvBitcoin.setTypeface(tfArchitectsDaughter);

        Double bit = Double.parseDouble(PreferenceFile.getInstance().getPreferenceData(this, Constant.BTC_amount));
        Double finacal = Double.parseDouble(BigDecimal.valueOf(bit).toPlainString());

        String symbol = UtilClass.getCurrencySymbol(PreferenceFile.getInstance().getPreferenceData(this, Constant.Currency_Code));

        PreferenceFile.getInstance().saveData(this, Constant.Currency_Symbol, symbol);
        Log.e("symbol-->", symbol);


        Double val = 1.0;

        if (bit > 0) {
            tvBitcoin.setText(String.format("%.8f", finacal));
        } else {
            tvBitcoin.setText("0.00000000");
        }

        Double inr = Double.parseDouble(PreferenceFile.getInstance().getPreferenceData(this, Constant.Inr_Amount));

        BigDecimal d = new BigDecimal(inr);
        Log.e("newcal-->", "d -->" + d);

        if (inr > 0)
        {
            tvwallet.setText(" "+String.format("%.0f", d)+" "+PreferenceFile.getInstance().getPreferenceData(this, Constant.Currency_Symbol));
        } else {
            tvwallet.setText(" 0 "+PreferenceFile.getInstance().getPreferenceData(this, Constant.Currency_Symbol));
        }

        txName.setText("Dashboard");

        LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, new IntentFilter("bit_rate"));
        LocalBroadcastManager.getInstance(this).registerReceiver(onNotice1, new IntentFilter("refresh_wallet_balance"));

    }


    private BroadcastReceiver onNotice = new BroadcastReceiver()
    {
    @Override
    public void onReceive(Context context, Intent intent)
    {
    String buy = intent.getStringExtra("buy");
    String sell = intent.getStringExtra("sell");

   Log.e("dashboard>>>", buy + "sender id-->" + sell);

   double calcul = Double.parseDouble(PreferenceFile.getInstance().getPreferenceData(Dashboard.this, Constant.BTC_amount));

   BigDecimal d = new BigDecimal(calcul);

   tvBuyrate.setText( formatter.format(Double.valueOf(buy)) +" "+PreferenceFile.getInstance().getPreferenceData(Dashboard.this, Constant.Currency_Symbol)+" ");
   txSellRate.setText(" "+formatter.format(Double.valueOf(sell))+" "+PreferenceFile.getInstance().getPreferenceData(Dashboard.this, Constant.Currency_Symbol));

   PreferenceFile.getInstance().saveData(Dashboard.this, Constant.BUYRATE, tvBuyrate.getText().toString());
   PreferenceFile.getInstance().saveData(Dashboard.this, Constant.SELLRATE, txSellRate.getText().toString());
   Double buy_rate = Double.parseDouble(String.valueOf(d)) * Double.parseDouble(buy);

   if (buy_rate > 0)
   {
   tvINR.setText(" "+formatter.format(buy_rate)+" "+PreferenceFile.getInstance().getPreferenceData(Dashboard.this,
   Constant.Currency_Symbol));
   PreferenceFile.getInstance().saveData(Dashboard.this, Constant.INR_PRICE_BITCOIN, tvINR.getText().toString());
  }
   else
  {
  tvINR.setText("0.00 "+" "+PreferenceFile.getInstance().getPreferenceData(Dashboard.this, Constant.Currency_Symbol));
  PreferenceFile.getInstance().saveData(Dashboard.this, Constant.INR_PRICE_BITCOIN, tvINR.getText().toString());
  }

  }
  };

    private BroadcastReceiver onNotice1 = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            callStateService();
        }
    };

    private void callStateService() {
        if (Constant.isConnectingToInternet(this)) {
            new Retrofit2(this, Dashboard.this, Constant.REQ_Dashboard_Refresh, Constant.Dashboard_Refresh +
                    PreferenceFile.getInstance().getPreferenceData(this, Constant.ID)).callService(false);
        } else {
            Constant.alertDialog(this, getResources().getString(R.string.check_connection));
        }
    }

    private void checkDepositStatus()
    {

        if (Constant.isConnectingToInternet(this))
        {
            try {
                new Retrofit2(this, Dashboard.this, Constant.REQ_CHECK_DEPOSIT_STATUS,
                        Constant.CHECK_DEPOSIT_STATUS+"/"+PreferenceFile.getInstance().getPreferenceData(getApplication(), Constant.ID))
                        .callService(false);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            Constant.alertDialog(this, getResources().getString(R.string.check_connection));
        }
    }

    private void callService()
    {
        if (Constant.isConnectingToInternet(this))
        {
            new Retrofit2(this, Dashboard.this, Constant.REQ_BTC_RATE, Constant.BTC_RATE).callService(true);
        }
        else
        {
            Constant.alertDialog(this, getResources().getString(R.string.check_connection));
        }
    }

    public void callCHeckPremiumUser()
    {
        if (Constant.isConnectingToInternet(this))
        {
            try {
                new Retrofit2(this, Dashboard.this, Constant.REQ_CHECK_PREMIUM_USERS,
                        Constant.CHECK_PREMIUM_USERS+"/"+PreferenceFile.getInstance().getPreferenceData(getApplication(), Constant.ID))
                        .callService(false);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            Constant.alertDialog(this, getResources().getString(R.string.check_connection));
        }
    }

    public void getNotiifcationCount()
    {
        if (Constant.isConnectingToInternet(this)) {

            //  new Retrofit2(this, BitcoinAddressAddedList.this, Constant.REQ_AddedAddresslist,Constant.AddedAddresslist+"56").callService(true);
            new Retrofit2(this, Dashboard.this, Constant.REQ_USER_NOTIFICATION,
                    Constant.USER_NOTIFICATION+PreferenceFile.getInstance().getPreferenceData(this,Constant.ID)).callService(true);
        }
        else {
            Constant.alertDialog(this, getResources().getString(R.string.check_connection));
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        Intent intent;

        switch (v.getId()) {

            case R.id.lnRateChart:

//                intent = new Intent(Dashboard.this, RateChart.class);
                intent = new Intent(Dashboard.this, DashboardActivities.class);
                startActivity(intent);

                break;

                case R.id.ivPremium:
                    Log.e("ListSizeClick ",list.size()+"");
                    showMySubscriptions();

                break;

                case R.id.ll_bottom:
                    Log.e("serviceList ",serviceList.size()+"");
                    showExtraServices();

                break;

                case R.id.lnSettings:

                    intent = new Intent(Dashboard.this, NotificationPage.class);
                    startActivity(intent);
                break;

                case R.id.lnrefresh:

                callService();

                break;

            case R.id.lnReceiver:

                if (PreferenceFile.getInstance().getPreferenceData(this, Constant.BITCOIN_ADDRESS) == null)
                {
                    Constant.alertDialog(this, "wait for the verification");
                }
                else {
                    intent = new Intent(Dashboard.this, BitcoinAddress.class);
                    intent.putExtra("key", "receive");
                    startActivity(intent);
                }
                break;

            case R.id.lnRece:
            {
                intent = new Intent(Dashboard.this, ReceiveAmount.class);
                startActivity(intent);
            }
            break;

            case R.id.lntransaction:

                if (PreferenceFile.getInstance().getPreferenceData(this, Constant.BITCOIN_ADDRESS) == null)
                {

                    Constant.alertDialog(this, "wait for the verification");
                }
                else {
                    intent = new Intent(Dashboard.this, TransationBitcoinType.class);
                    intent.putExtra("key", "btc");
                    startActivity(intent);
                }
                break;

            case R.id.lnINR:
                intent = new Intent(Dashboard.this, TransationINRtype.class);
                intent.putExtra("key", "inr");
                startActivity(intent);
                break;

            case R.id.lnDeposit:
                intent = new Intent(Dashboard.this, DepositTransaction.class);
                startActivity(intent);
                break;

            case R.id.lnTransfer:

                if (isRunning.equalsIgnoreCase("1"))
                {
                    showPopUp("restriction");
                }
                else
                {
                    intent = new Intent(Dashboard.this, MoneyTranfer.class);
                    startActivity(intent);
                }

                break;

            case R.id.lnBank:

                if (PreferenceFile.getInstance().getPreferenceData
                        (this, Constant.VERIFY_BANK).equals("Pending") ||
                        PreferenceFile.getInstance().getPreferenceData(this, Constant.VERIFY_PAN).equals("Pending")
                        || PreferenceFile.getInstance().getPreferenceData(this, Constant.VERIFY_Adhaar).equals("Pending")) {

                    Constant.alertDialog(this, "Please verify your account.");
                } else {


                    intent = new Intent(Dashboard.this, Withdrow.class);
                    startActivity(intent);
                }

                break;

            case R.id.lnSend:

                if (PreferenceFile.getInstance().getPreferenceData(this, Constant.BITCOIN_ADDRESS) == null) {

                    Constant.alertDialog(this, "wait for the verification");
                } else {

                    if (isRunning.equalsIgnoreCase("1"))
                    {
                        showPopUp("restriction");
                    }
                    else {
                        intent = new Intent(Dashboard.this, SendBitcoin.class);
                        startActivity(intent);
                    }


                }

                break;

            case R.id.txSelling:

                if (PreferenceFile.getInstance().getPreferenceData(this, Constant.BITCOIN_ADDRESS) == null) {

                    Constant.alertDialog(this, "wait for the verification");
                } else {

                    txSelling.setBackground(getResources().getDrawable(R.drawable.sell_dashboard_activebg));
                    txSelling.setTextColor(getResources().getColor(R.color.white));
                    txBuy.setBackground(getResources().getDrawable(R.drawable.buy_dashboard_inactivebg));
                    txBuy.setTextColor(getResources().getColor(R.color.text_grey_color));


                    intent = new Intent(Dashboard.this, SellBitcoin.class);
                    startActivity(intent);
                }

                break;

            case R.id.txBuy:

                if (PreferenceFile.getInstance().getPreferenceData(this, Constant.BITCOIN_ADDRESS) == null) {

                    Constant.alertDialog(this, "wait for the verification");
                } else {

                    txBuy.setBackground(getResources().getDrawable(R.drawable.buy_dashboard_activebg));
                    txBuy.setTextColor(getResources().getColor(R.color.white));
                    txSelling.setBackground(getResources().getDrawable(R.drawable.sell_dashboard_inactivebg));
                    txSelling.setTextColor(getResources().getColor(R.color.text_grey_color));


                    intent = new Intent(Dashboard.this, BuyBitcoin.class);
                    startActivity(intent);
                }

                break;
        }
    }

    @Override
    public void onBackPressed()
    {
        /*super.onBackPressed();
        finishAffinity();*/
        if (doubleBackToExitPressedOnce)
        {
            super.onBackPressed();
            finishAffinity();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }


    @Override
    public void onServiceResponse(int requestCode, Response<ResponseBody> response)
    {
        super.onServiceResponse(requestCode, response);
        switch (requestCode)
        {
            case Constant.REQ_CHECK_DEPOSIT_STATUS:

                if (response.isSuccessful())
                {
                    try
                    {
                        JSONObject result1 = new JSONObject(response.body().string());
                        Log.e("result", result1.toString());
                        String data = result1.getString("data");
                        String message = result1.getString("message");
                        String amount = result1.getString("amount");
                        String response1 = result1.getString("response");

                        if (response1.equals("true"))
                        {
                            if (data.equalsIgnoreCase("no"))
                            {//pending
                                PreferenceFile.getInstance().saveData(Dashboard.this, Constant.NORMAL_DEP_AMT, amount);
                            }
                            else if (data.equalsIgnoreCase("Yes"))
                            {//Approved
                                PreferenceFile.getInstance().saveData(Dashboard.this, Constant.NORMAL_DEP_AMT, null);
                                timer.cancel();
                            } else if (data.equalsIgnoreCase("Rejected"))
                            {//Rejected
                                PreferenceFile.getInstance().saveData(Dashboard.this, Constant.NORMAL_DEP_AMT, null);
                                timer.cancel();
                            }
                        }
                        else
                        {

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;


            case Constant.REQ_Dashboard_Refresh:

                if (response.isSuccessful()) {

                    callCHeckPremiumUser();

                    try {
                        JSONObject result1 = new JSONObject(response.body().string());

                        String status = result1.getString("response");
                        String message = result1.getString("message");

                        if (status.equals("true")) {

                            // PreferenceFile.getInstance().saveData(this,Constant.COUNT_SECURITY,"0");
                            JSONObject result = result1.getJSONObject("data");

                            PreferenceFile.getInstance().saveData(this, Constant.ID, result.getString("id"));
                            PreferenceFile.getInstance().saveData(this, Constant.phone, result.getString("phone"));
                            PreferenceFile.getInstance().saveData(this, Constant.secure_pin, result.getString("secure_pin"));
                            PreferenceFile.getInstance().saveData(this, Constant.Inr_Amount, result.getString("inr_amount"));
                            PreferenceFile.getInstance().saveData(this, Constant.BTC_amount, result.getString("btc_amount"));
                            PreferenceFile.getInstance().saveData(this, Constant.Courtry_id, result.getString("country"));
                            PreferenceFile.getInstance().saveData(this, Constant.REFERCODE, result.getString("refer_code"));

                            Double bit = Double.parseDouble(PreferenceFile.getInstance().getPreferenceData(this, Constant.BTC_amount));
                            Double finacal = Double.parseDouble(BigDecimal.valueOf(bit).toPlainString());
                            Double inr = Double.parseDouble(PreferenceFile.getInstance().getPreferenceData(this, Constant.Inr_Amount));

                            BigDecimal ff = new BigDecimal(inr);

                            tvwallet.setText(formatter.format(ff) + " " +
                                    PreferenceFile.getInstance().getPreferenceData(Dashboard.this, Constant.Currency_Symbol));
                            // tvwallet.setText(String.format(formatter, ff)+" "+PreferenceFile.getInstance().getPreferenceData(this, Constant.Currency_Symbol));
                            // tvwallet.setText(formatter.format(ff) + " " + PreferenceFile.getInstance().getPreferenceData(this, Constant.Currency_Symbol));

                            if (bit > 0)
                            {
                                tvBitcoin.setText(String.format("%.8f", finacal));
                            } else {
                                tvBitcoin.setText("0.00000000");
                            }

                            if (!result.getString("first_name").equals("null"))
                            {

                                PreferenceFile.getInstance().saveData(this, Constant.Username, result.getString("first_name") + " " + result.getString("last_name"));
                                PreferenceFile.getInstance().saveData(this, Constant.Email, result.getString("email"));
                                PreferenceFile.getInstance().saveData(this, Constant.Dob, result.getString("dob"));
                                PreferenceFile.getInstance().saveData(this, Constant.email_verification, result.getString("verification"));
                                PreferenceFile.getInstance().saveData(this, Constant.Gender, result.getString("gender"));
                                PreferenceFile.getInstance().saveData(this, Constant.Image, result.getString("image"));

                                PreferenceFile.getInstance().saveData(this, Constant.City_name, result.getString("city"));
                                PreferenceFile.getInstance().saveData(this, Constant.VERIFY_PAN, result.getString("verify_pan"));
                                PreferenceFile.getInstance().saveData(this, Constant.VERIFY_BANK, result.getString("verify_bank"));
                                PreferenceFile.getInstance().saveData(this, Constant.VERIFY_Adhaar, result.getString("verify_add"));

                                if (!result.isNull("block_address")) {

                                    PreferenceFile.getInstance().saveData(this, Constant.BITCOIN_ADDRESS, result.getString("block_address"));
                                }

                                JSONObject StateName = result.getJSONObject("StateName");
                                PreferenceFile.getInstance().saveData(this, Constant.State_name, StateName.getString("name"));
                                PreferenceFile.getInstance().saveData(this, Constant.State_id, StateName.getString("id"));

                                JSONObject CountryName = result.getJSONObject("CountryName");
                                PreferenceFile.getInstance().saveData(this, Constant.Country_name, CountryName.getString("name"));
                                PreferenceFile.getInstance().saveData(this, Constant.Country_id, CountryName.getString("id"));

                                if (!result.isNull("BankName")) {

                                    JSONObject BankName = result.getJSONObject("BankName");
                                    PreferenceFile.getInstance().saveData(this, Constant.BANK_NAME, BankName.getString("bank_name"));
                                    PreferenceFile.getInstance().saveData(this, Constant.ACCOUNT_TYPE, BankName.getString("account_type"));
                                    PreferenceFile.getInstance().saveData(this, Constant.BRANCH, BankName.getString("branch"));
                                    PreferenceFile.getInstance().saveData(this, Constant.PASSBOOK_IMAGE, BankName.getString("passbook_image"));
                                    PreferenceFile.getInstance().saveData(this, Constant.IFSC, BankName.getString("ifsc"));
                                    PreferenceFile.getInstance().saveData(this, Constant.ACCOUNT_HOLDER, BankName.getString("account_holder"));
                                    PreferenceFile.getInstance().saveData(this, Constant.ACCOUNT_NUMBER, BankName.getString("account_number"));
                                }

                                if (!result.isNull("AddName")) {

                                    JSONObject AddName = result.getJSONObject("AddName");
                                    PreferenceFile.getInstance().saveData(this, Constant.Adhaar_image, AddName.getString("aadhar"));
                                    PreferenceFile.getInstance().saveData(this, Constant.Adhaar_image_back, AddName.getString("aadhar_back"));
                                    PreferenceFile.getInstance().saveData(this, Constant.Adhaar_number, AddName.getString("aadhar_number"));
                                    PreferenceFile.getInstance().saveData(this, Constant.Adhaar_line1, AddName.getString("line1"));
                                    PreferenceFile.getInstance().saveData(this, Constant.Adhaar_line2, AddName.getString("line2"));
                                    PreferenceFile.getInstance().saveData(this, Constant.LANDMARK, AddName.getString("landmark"));
                                    PreferenceFile.getInstance().saveData(this, Constant.Aadhar_city, AddName.getString("city"));
                                    PreferenceFile.getInstance().saveData(this, Constant.Aadhar_state, AddName.getString("state"));

                                    JSONObject state = AddName.getJSONObject("StateName");
                                    PreferenceFile.getInstance().saveData(this, Constant.Aadhar_state, state.getString("name"));

                                }
                                if (!result.isNull("PanName")) {

                                    JSONObject PanName = result.getJSONObject("PanName");
                                    PreferenceFile.getInstance().saveData(this, Constant.Pan_name, PanName.getString("name"));
                                    PreferenceFile.getInstance().saveData(this, Constant.Pan_last, PanName.getString("last_name"));
                                    PreferenceFile.getInstance().saveData(this, Constant.Pan_image, PanName.getString("image"));
                                    PreferenceFile.getInstance().saveData(this, Constant.Pan_number, PanName.getString("pan_number"));
                                    PreferenceFile.getInstance().saveData(this, Constant.Pan_dob, PanName.getString("dob"));
                                    PreferenceFile.getInstance().saveData(this, Constant.Pan_gender, PanName.getString("gender"));
                                }
                            }

                            // PreferenceFile.getInstance().saveData(this,Constant.COUNT_SECURITY,"1");

                        } else {
                            Constant.alertWithIntent(this, "Account Blocked", BlockScreen.class);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {

                    }
                }
                break;

            case Constant.REQ_CHECK_PREMIUM_USERS:

                try {
                    if (response.isSuccessful()) {
                        JSONObject result = new JSONObject(response.body().string());

                        Log.e("REQ_CHECK_PREMIUM_USERS", result.toString());

                        String status = result.getString("response");
                        getNotiifcationCount();

                        if (status.equals("true"))
                        {
                            list.clear();

                            JSONArray data=result.getJSONArray("data");
                            if (data.length()>0)
                            {
                                for (int i = 0; i <data.length() ; i++)
                                {
                                    JSONObject jsonObject=data.getJSONObject(i);
                                    MySubscriptionModel mySubscriptionModel=new MySubscriptionModel();

                                    mySubscriptionModel.setSubscription_id(jsonObject.getString("id"));
                                    mySubscriptionModel.setCompetition_id(jsonObject.getString("competition_id"));
                                    mySubscriptionModel.setPlan_id(jsonObject.getString("plan_id"));

                                    JSONObject Plans=jsonObject.getJSONObject("Plans");

                                    mySubscriptionModel.setPlan_name(Plans.getString("name"));
                                    mySubscriptionModel.setFees(Plans.getString("entry_fee"));
                                    mySubscriptionModel.setPlanBenefits(Plans.getString("benefits"));
                                    mySubscriptionModel.setPlanDesc(Plans.getString("description"));

                                    JSONObject CompetitionLists=jsonObject.getJSONObject("CompetitionLists");

                                    mySubscriptionModel.setCompetition_name(CompetitionLists.getString("name"));
                                    mySubscriptionModel.setStart_date(CompetitionLists.getString("start_date"));
                                    mySubscriptionModel.setStart_time(CompetitionLists.getString("start_time"));
                                    mySubscriptionModel.setEnd_date(CompetitionLists.getString("end_date"));
                                    mySubscriptionModel.setEnd_time(CompetitionLists.getString("end_time"));


                                    String input_date="",input_time="";
                                    input_date=CompetitionLists.getString("start_date");
                                    input_time=CompetitionLists.getString("start_time");
                                    String finalDay="",dateee="";
                                    String currentDate="";
                                    SimpleDateFormat format1=new SimpleDateFormat("yyyy-MM-dd");
                                    try {
                                        Date dt1=format1.parse(input_date);
                                        DateFormat format2=new SimpleDateFormat("EEEE");
                                        DateFormat formatSd=new SimpleDateFormat("dd-MM-yyyy");
                                        //todo for day
                                        finalDay=format2.format(dt1);

                                        //todo for date
                                        dateee=formatSd.format(dt1);


                                        currentDate= UtilClass.getCurrentDate();
                                        Log.e("finalStartDate ",currentDate);
                                    }catch (Exception ex)
                                    {
                                        ex.printStackTrace();
                                    }
                                    String timeeeGet="",currentTime="",currentTimeformat="";

                                    currentTime=UtilClass.getCurrentTime();
                                    Log.e("currentTime ",currentTime+"");

                                    String current12="";
                                    try
                                    {
                                        Date cccdd=null;

                                        SimpleDateFormat input=new SimpleDateFormat("hh:mm");
                                        SimpleDateFormat output=new SimpleDateFormat("hh:mm aa");

                                        Date dt3bfffh11=input.parse(currentTime);
                                        current12=output.format(dt3bfffh11);
                                        Log.e("current12 ",current12+"");
                                        Log.e("StartTime ",input_time+"");

                                    }catch (Exception ex)
                                    {
                                        ex.printStackTrace();
                                    }




                                    //todo for time
                                    SimpleDateFormat format21=new SimpleDateFormat("hh:mm aa");
                                    try {
                                        Date dt311=format21.parse(input_time);
                                        Date dt311Curr=format21.parse(current12);
                                        DateFormat formatSd=new SimpleDateFormat("HH:mm aa");
                                        timeeeGet=formatSd.format(dt311);
                                        currentTimeformat=formatSd.format(dt311Curr);
                                        Log.e("timeeeGet ",timeeeGet);
                                        Log.e("currentTimeformat ",currentTimeformat);
                                    }catch (Exception ex)
                                    {
                                        ex.printStackTrace();
                                    }


                                    String compareDate="";
                                    compareDate=UtilClass.compareDates(dateee,currentDate);

                                    String compareTime="";
                                    compareTime=UtilClass.compareTiminng24(timeeeGet,currentTimeformat);


                                    Log.e("CompareDate ",compareDate);
                                    Log.e("compareTime ",compareTime);


                                    if (compareDate.equalsIgnoreCase("equal") || compareDate.equalsIgnoreCase("before"))
                                    {
                                        if ( compareDate.equalsIgnoreCase("before"))
                                        {
                                            if (compareTime.equalsIgnoreCase("after"))
                                            {
                                               isRunning="1";
                                            }
                                        }
                                        else
                                        {
                                            if (compareTime.equalsIgnoreCase("equal") || compareTime.equalsIgnoreCase("before"))
                                            {
                                                isRunning="1";
                                            }
                                            else
                                            {
                                                isRunning="0";
                                            }
                                        }
                                    }
                                    else
                                    {
                                        isRunning="0";
                                    }







                                    list.add(mySubscriptionModel);
                                }

                                Log.e("ListSize ",list.size()+"");
                            }
                            ivPremium.setVisibility(View.VISIBLE);


                            //todo coming from Buy Subscription then only show this
                            if (key.equalsIgnoreCase("buy"))
                            {
                                showSuccessPopup("You have subscribed Tiger  Pay Competiton entry pass.\nYou will receive Tips or news  that " +
                                        "will help you learn how to \ntrade in crypto more efficiently.\nOnce or twice day by day,\nyou will get notifications alert." +
                                        "\nWhich will help you to \nbecome " +
                                        "a winner and get \nmany rewards.\nBest of luck \n \nStay home stay safe \nTrade with Tiger Pay \nBecause there is" +
                                        " no space for losses.");
                            }


                        } else
                            {
                            ivPremium.setVisibility(View.GONE);
                        }

                    }
                } catch (Exception ex)
                {
                    ex.printStackTrace();
                }

                break;

            case Constant.REQ_USER_NOTIFICATION:

                try {
                    JSONObject result = new JSONObject(response.body().string());

                    Log.e("NotificationResulr ", result.toString());

                    String status = result.getString("response");
                    String message = result.getString("message");

                    notiList.clear();

                    if (status.equals("true")) {

                        JSONArray data = result.getJSONArray("data");
                        if (data.length() > 0) {
                            for (int i = 0; i < data.length(); i++) {
                                NotificationModel notificationModel = new NotificationModel();

                                JSONObject jsonObject = data.getJSONObject(i);

                                if (jsonObject.getString("view_status").equalsIgnoreCase("Open"))
                                {
                                    notificationModel.setNotification_id(jsonObject.getString("id"));
                                    notiList.add(notificationModel);
                                }

                              /*
                              if (jsonObject.getString("notification_type").equalsIgnoreCase("Notification"))
                                {
                                    if (jsonObject.getString("view_status").equalsIgnoreCase("Open"))
                                     {
                                        notificationModel.setNotification_id(jsonObject.getString("id"));
                                        notiList.add(notificationModel);
                                     }
                                }*/
                            }

                            if (notiList.size() > 0)
                            {
                                Log.e("NotiListSize ", notiList.size() + "");
                                rlCountt.setVisibility(View.VISIBLE);
                                tvCount.setText(String.valueOf(notiList.size()));

                            } else {
                                rlCountt.setVisibility(View.GONE);
                            }
                        }
                    } else
                        {
//                        Constant.alertWithIntent(this, message, Dashboard.class);
                    }

                }
                catch (JSONException e)
                {
                      e.printStackTrace();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                break;

            case Constant.REQ_BTC_RATE:

                try {

                    if (response.isSuccessful()) {

                        JSONObject result = new JSONObject(response.body().string());

                        Log.e("dashbardres", result.toString());
                        callStateService();

                        String status = result.getString("response");
                        String message = result.getString("message");

                        if (status.equals("true")) {

                            JSONObject data = result.getJSONObject("data");

                            double calcul = Double.parseDouble(PreferenceFile.getInstance().getPreferenceData(this, Constant.BTC_amount));

                            BigDecimal d = new BigDecimal(calcul);

                            tvBuyrate.setText(formatter.format(Double.valueOf(data.getString("buy"))) + " " + PreferenceFile.getInstance().getPreferenceData(this, Constant.Currency_Symbol) + " ");
                            txSellRate.setText(formatter.format(Double.valueOf(data.getString("sell"))) + " " + PreferenceFile.getInstance().getPreferenceData(this, Constant.Currency_Symbol));

                            PreferenceFile.getInstance().saveData(this, Constant.BUY, data.getString("buy"));
                            PreferenceFile.getInstance().saveData(this, Constant.SELL, data.getString("sell"));
                            PreferenceFile.getInstance().saveData(this, Constant.BUYRATE, tvBuyrate.getText().toString());
                            PreferenceFile.getInstance().saveData(this, Constant.SELLRATE, txSellRate.getText().toString());

                            Double buy_rate = Double.parseDouble(String.valueOf(d)) * Double.parseDouble(data.getString("buy"));

                            // tvINR.setText(String.valueOf(buy_rate));

                            if (buy_rate > 0) {

                                tvINR.setText(formatter.format(buy_rate) + " " + PreferenceFile.getInstance().getPreferenceData(this, Constant.Currency_Symbol));

                                PreferenceFile.getInstance().saveData(this, Constant.INR_PRICE_BITCOIN, tvINR.getText().toString());
                            } else {
                                tvINR.setText("0.00" + " " + PreferenceFile.getInstance().getPreferenceData(this, Constant.Currency_Symbol));
                                PreferenceFile.getInstance().saveData(this, Constant.INR_PRICE_BITCOIN, tvINR.getText().toString());
                            }


                        } else {
                            if (PreferenceFile.getInstance().getPreferenceData(this, Constant.BUY) != null) {
                                double calcul = Double.parseDouble(PreferenceFile.getInstance().getPreferenceData(this, Constant.BTC_amount));

                                BigDecimal d = new BigDecimal(calcul);

                                tvBuyrate.setText(formatter.format(Double.valueOf(PreferenceFile.getInstance().getPreferenceData(this, Constant.BUY))) + " " + PreferenceFile.getInstance().getPreferenceData(this, Constant.Currency_Symbol) + " ");
                                txSellRate.setText(" " + formatter.format(Double.valueOf(PreferenceFile.getInstance().getPreferenceData(this, Constant.SELL))) + " " + PreferenceFile.getInstance().getPreferenceData(this, Constant.Currency_Symbol));

                                PreferenceFile.getInstance().saveData(this, Constant.BUYRATE, tvBuyrate.getText().toString());
                                PreferenceFile.getInstance().saveData(this, Constant.SELLRATE, txSellRate.getText().toString());
                                Double buy_rate = Double.parseDouble(String.valueOf(d)) * Double.parseDouble(PreferenceFile.getInstance().getPreferenceData(this, Constant.SELL));

                                if (buy_rate > 0) {

                                    tvINR.setText(formatter.format(buy_rate) + " " + PreferenceFile.getInstance().getPreferenceData(this, Constant.Currency_Symbol));

                                    PreferenceFile.getInstance().saveData(this, Constant.INR_PRICE_BITCOIN, tvINR.getText().toString());
                                } else {
                                    tvINR.setText("0.00" + " " + PreferenceFile.getInstance().getPreferenceData(this, Constant.Currency_Symbol));
                                    PreferenceFile.getInstance().saveData(this, Constant.INR_PRICE_BITCOIN, tvINR.getText().toString());
                                }

                            }

                        }

                    } else {
                        Constant.alertDialog(this, getResources().getString(R.string.try_again));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    public void showMySubscriptions()
    {

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View promptsView2 = inflater.inflate(R.layout.show_my_subscription, null);

        dialogBuilder.setView(promptsView2);
        dialogBuilder.setCancelable(false);
        final AlertDialog alertDialog = dialogBuilder.create();
        // alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        int width = WindowManager.LayoutParams.WRAP_CONTENT;
        int height = WindowManager.LayoutParams.WRAP_CONTENT;

        TextView tvLabel=(TextView) promptsView2.findViewById(R.id.tvLabel);
        TextView tvNoData=(TextView) promptsView2.findViewById(R.id.tvNoData);
        RecyclerView recyclerSub=(RecyclerView) promptsView2.findViewById(R.id.recyclerSub);
        ImageView ivCross=(ImageView) promptsView2.findViewById(R.id.ivCross);
        /*final Dialog dialog=new Dialog(Dashboard.this, R.style.StatisticsDialog);
        dialog.setCancelable(false);

        LayoutInflater li = LayoutInflater.from(Dashboard.this);
        View promptsView2 = li.inflate(R.layout.show_my_subscription, null);
        dialog.setContentView(promptsView2);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        */


        if (list.size()>0)
        {
            tvNoData.setVisibility(View.GONE);
            recyclerSub.setVisibility(View.VISIBLE);
            recyclerSub.setLayoutManager(new LinearLayoutManager(Dashboard.this));
            CustomMySubscriptionsAdapter customMySubscriptionsAdapter=new CustomMySubscriptionsAdapter(Dashboard.this,list);
            recyclerSub.setAdapter(customMySubscriptionsAdapter);
        }
        else
        {
            tvNoData.setVisibility(View.VISIBLE);
            recyclerSub.setVisibility(View.GONE);
        }

        ivCross.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
               alertDialog .dismiss();
            }
        });

        alertDialog.show();
    }

    public void showExtraServices()
    {

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View promptsView2 = inflater.inflate(R.layout.show_extra_servuices, null);

        dialogBuilder.setView(promptsView2);
        dialogBuilder.setCancelable(false);
        final AlertDialog alertDialog = dialogBuilder.create();
        // alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


        int width = WindowManager.LayoutParams.MATCH_PARENT;
        int height = WindowManager.LayoutParams.MATCH_PARENT;

        alertDialog.getWindow().setLayout(width, height);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams params = alertDialog.getWindow().getAttributes();
        params.gravity = Gravity.BOTTOM;

        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        TextView tvLabel=(TextView) promptsView2.findViewById(R.id.tvLabel);
        TextView tvNoData=(TextView) promptsView2.findViewById(R.id.tvNoData);

        RecyclerView recyclerSub=(RecyclerView) promptsView2.findViewById(R.id.recyclerSub);
        ImageView ivCross=(ImageView) promptsView2.findViewById(R.id.ivCross);

        serviceList.clear();

        ServiceModel serviceModel=new ServiceModel("1","Mobile Recharge");
        serviceList.add(serviceModel);

        serviceModel=new ServiceModel("2","DTH");
        serviceList.add(serviceModel);

        serviceModel=new ServiceModel("3","Electricity");
        serviceList.add(serviceModel);

        serviceModel=new ServiceModel("4","Credit Card Bill");
        serviceList.add(serviceModel);

        serviceModel=new ServiceModel("5","Postpaid");
        serviceList.add(serviceModel);

        serviceModel=new ServiceModel("6","Donate");
        serviceList.add(serviceModel);

        serviceModel=new ServiceModel("7","Book A Cylinder");
        serviceList.add(serviceModel);

        serviceModel=new ServiceModel("8","Broadband");
        serviceList.add(serviceModel);

        serviceModel=new ServiceModel("9","Landline");
        serviceList.add(serviceModel);

        serviceModel=new ServiceModel("10","Waterbill");
        serviceList.add(serviceModel);

        if (serviceList.size()>0)
        {
            tvNoData.setVisibility(View.GONE);
            recyclerSub.setVisibility(View.VISIBLE);
            recyclerSub.setLayoutManager(new GridLayoutManager(this, 3));
            ServicesAdapter servicesAdapter=new ServicesAdapter(Dashboard.this,serviceList);
            recyclerSub.setAdapter(servicesAdapter);

            servicesAdapter.onItemSelectedListener(new ServicesAdapter.onCLickListner()
            {
                @Override
                public void onItemClick(int layoutPosition, View view)
                {
                    showPopUp("coming");
                }
            });
        }
        else
        {
            tvNoData.setVisibility(View.VISIBLE);
            recyclerSub.setVisibility(View.GONE);
        }

        ivCross.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                alertDialog .dismiss();
            }
        });

        alertDialog.show();
    }


    ArrayList<MySubscriptionModel> list=new ArrayList<>();
    ArrayList<ServiceModel> serviceList=new ArrayList<>();
    public void showSuccessPopup(String message)
    {

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View promptsView2 = inflater.inflate(R.layout.congratulations_pop_up, null);

        dialogBuilder.setView(promptsView2);
        dialogBuilder.setCancelable(false);
        final AlertDialog alertDialog = dialogBuilder.create();
        // alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        int width = WindowManager.LayoutParams.WRAP_CONTENT;
        int height = WindowManager.LayoutParams.WRAP_CONTENT;

        ImageView ivCross=(ImageView) promptsView2.findViewById(R.id.ivCross);
        TextView tvReason=(TextView) promptsView2.findViewById(R.id.tvReason);
        tvReason.setText(message);

        ivCross.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                alertDialog .dismiss();
            }
        });
        alertDialog.show();
    }

    String isRunning="0";

    public void showPopUp(String key)
    {
        final Dialog dialog=new Dialog(Dashboard.this, R.style.StatisticsDialog);
        dialog.setTitle("TigerPay");
        dialog.setCancelable(true);

        LayoutInflater li = LayoutInflater.from(Dashboard.this);
        View promptsView2 = li.inflate(R.layout.deposit_transaction_admin_popup, null);
        dialog.setContentView(promptsView2);
        dialog.setCanceledOnTouchOutside(false);
        Button btnokk=(Button)promptsView2.findViewById(R.id.btnokk);
        TextView tvReason=(TextView) promptsView2.findViewById(R.id.tvReason);

        if (key.equalsIgnoreCase("coming"))
        {
            tvReason.setText("Coming Soon");
        }
        else {
            tvReason.setText("You are restricted to do this transaction");
        }



        btnokk.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    ArrayList<NotificationModel> notiList=new ArrayList<>();
    @Override
    protected void onResume()
    {
        super.onResume();

        if (getIntent().hasExtra("key"))
        {
            key=getIntent().getExtras().getString("key");
            Log.e("Key:--",key);
        }

    }
}
