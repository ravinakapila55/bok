package com.app.tigerpay;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.fingerprint.FingerprintManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.app.tigerpay.Broadcast.SmsListener;
import com.app.tigerpay.Broadcast.SmsReceiver;
import com.app.tigerpay.FingerPrint.FingerPrintLister;
import com.app.tigerpay.FingerPrint.FingerprintAuthenticationHandler;
import com.app.tigerpay.SmsGateway.Sender;
import com.app.tigerpay.Util.Constant;
import com.app.tigerpay.Util.GpsTracker;
import com.app.tigerpay.Util.PreferenceFile;
import com.app.tigerpay.Util.Retrofit2;
import com.app.tigerpay.Util.RetrofitResponse;
import com.app.tigerpay.Widget.PinEntryEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class CheckSecurePin extends AppCompatActivity implements View.OnClickListener, RetrofitResponse {

    TextView txnext, tvattempts, txback, txTxt, txUnathorized,
            AmtVal, netVal, gstVal, TotAmt, AmtFee, netfee,gstfee,
            txSubmit, txEnter, tvGenerate;
    Double final_Charge=0.0;
    ImageView ivarrow, imFis, imS, imth, imfo;
    EditText edfirst, edSecond, edthird, edFouth;
    String enterotp = "", otp = "";
    TextView chronometer, txResend, txNext;
    String confirm_secure_pin, operator = "", macAddress = "";
    File file;
    Dialog dialog;
    KeyGenerator keyGenerator;
    ImageView image;
    Sender sender;
    CountDownTimer countDownTimer;
    TextView tvforgot;
    MultipartBody.Part body;
    Double latitude, longitude;
    private HashMap<String, RequestBody> map;
    GpsTracker gpsTracker;
    String order_id;
    String IPaddress;
    Double amt1;    //  EditText edfirst,edSecond,edthird,edForth;

    LinearLayout lnLayerforgot;
    String text = "";
    int x;
    String otpReceived;

    private LinearLayout eight;
    private LinearLayout five;
    private LinearLayout four;
    private LinearLayout ok;
    private LinearLayout nine;
    private StringBuffer num;
    private LinearLayout one;
    private LinearLayout seven;
    private LinearLayout six;
    private LinearLayout back;
    private LinearLayout three;
    private LinearLayout two;
    private LinearLayout zero;
    private PinEntryEditText txtPinEntry;
    private ImageView pin1, pin2, pin3, pin4;

    public KeyguardManager keyguardManager;
    public FingerprintManager fingerprintManager;
    private KeyStore keyStore;
    private Cipher cipher;
    private static final String KEY_NAME = "studytutorial";
    String androidSDK, androidVersion, androidBrand, androidManufacturer, androidModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_check_secure_pin);
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        boolean isWifiConn = networkInfo.isConnected();
        networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        boolean isMobileConn = networkInfo.isConnected();

        lnLayerforgot = (LinearLayout) findViewById(R.id.lnLayerforgot);
        tvattempts = (TextView) findViewById(R.id.tvattempts);
        tvforgot = (TextView) findViewById(R.id.tvforgot);
        ivarrow = (ImageView) findViewById(R.id.ivarrow);
        tvforgot.setOnClickListener(this);
        ivarrow.setOnClickListener(this);

        androidSDK = String.valueOf(android.os.Build.VERSION.SDK_INT);
        androidVersion = android.os.Build.VERSION.RELEASE;
        androidBrand = android.os.Build.BRAND;
        androidManufacturer = android.os.Build.MANUFACTURER;
        androidModel = android.os.Build.MODEL;



        if (PreferenceFile.getInstance().getPreferenceData(this, Constant.COUNT_SECURITY) == null) {
            lnLayerforgot.setVisibility(View.GONE);
        } else {
            int count = Integer.parseInt(PreferenceFile.getInstance().getPreferenceData(this, Constant.COUNT_SECURITY));
            if (count > 0) {

                count = 4 - count;

                if (count == 1) {
                    Constant.alertDialog(this, "Warning! This is your Last attempt otherwise your account has been blocked.");
                    tvattempts.setText(count + " attempts remaining. ");
                } else {
                    tvattempts.setText(count + " attempts remaining. ");
                }
            }
        }

        getLocation();
        NetwordDetect();
        initializeViews();


        if (Build.VERSION.SDK_INT >= 23) {

            keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
            fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
        }


        if (PreferenceFile.getInstance().getPreferenceData(CheckSecurePin.this, Constant.Finger_Lock) != null) {
            if (PreferenceFile.getInstance().getPreferenceData(CheckSecurePin.this, Constant.Finger_Lock).equals("1")) {

                if (Build.VERSION.SDK_INT >= 23) {
                    checkFingerPrintScanner();
                }
            }
        }

        if (Build.VERSION.SDK_INT >= 23) {

            FingerprintAuthenticationHandler.bindListener(new FingerPrintLister() {
                @Override
                public void fingerPrintcall(String e, Boolean success) {


                    if (success) {

                        dialog.dismiss();
                        txUnathorized.setVisibility(View.GONE);

                        if (getIntent().getStringExtra("key").equals("finger_print")) {
                            alertDialog(CheckSecurePin.this, "Are you sure to apply the fingerprint? ");
                        }
                        if (getIntent().getStringExtra("key").equals("lock_transaction")) {
                            alertDialog(CheckSecurePin.this, "Are you sure to change lock outgoing transaction status? ");
                        }
                        if (getIntent().getStringExtra("key").equals("withdraw")) {

                            alertDialogDesc(CheckSecurePin.this, "Are you sure to withdraw money? ",
                                    getIntent().getStringExtra("original_amount"), getIntent().getStringExtra("charges"), getIntent().getStringExtra("gst"), getIntent().getStringExtra("inr_amount"));

                        }
                        if (getIntent().getStringExtra("key").equals("sell")) {
                            alertDialogBitCoinDesc(CheckSecurePin.this, "Are you sure to sell BitCoins? ", getIntent().getStringExtra("inr_amount"),
                                    getIntent().getStringExtra("charge"), getIntent().getStringExtra("gst"),
                                    getIntent().getStringExtra("btc_amount"),"sell");

                        }
                        if (getIntent().getStringExtra("key").equals("ask")) {
                            alertDialogBitCoinDesc(CheckSecurePin.this, "Are you sure to Ask BitCoins? ", getIntent().getStringExtra("sell_amount"),
                                    getIntent().getStringExtra("charge"),getIntent().getStringExtra("gst"),getIntent().getStringExtra("sell_btc"),"ask" );
                        }
                        if (getIntent().getStringExtra("key").equals("buy")) {
                            alertDialogBitCoinDesc(CheckSecurePin.this, "Are you sure to Buy BitCoins? ", getIntent().getStringExtra("inr_amount"),
                                    getIntent().getStringExtra("charge"), getIntent().getStringExtra("gst"),
                                    getIntent().getStringExtra("btc_amount"),"buy");
                        }
                        if (getIntent().getStringExtra("key").equals("addbitAddress")) {
                            alertDialog(CheckSecurePin.this, "Are you sure to Add Bitcoin Address? ");
                        }
                        if (getIntent().getStringExtra("key").equals("moneytransfer")) {
                            alertDialog(CheckSecurePin.this, "Are you sure to Transfer Money? ");
                        }
                        if (getIntent().getStringExtra("key").equals("bid")) {
                            alertDialogBitCoinDesc(CheckSecurePin.this, "Are you sure to Bid Bitcoins? ",
                                    getIntent().getStringExtra("amount"),
                                    getIntent().getStringExtra("charge"),getIntent().getStringExtra("gst"),
                                    getIntent().getStringExtra("total_btc"),"bid" );

                        }
                        if (getIntent().getStringExtra("key").equals("deposit")) {
                            alertDialog(CheckSecurePin.this, "Are you sure to Deposit? ");
                        }
                        if (getIntent().getStringExtra("key").equals("send")) {
                            checkThirdPartyApi();
//                            alertDialogBitCoinDesc(CheckSecurePin.this, "Are you sure to Send BitCoins? "
//                                    , getIntent().getStringExtra("amount_inr"),
//                                    getIntent().getStringExtra("charges"),
//                                    getIntent().getStringExtra("gst"),
//                                    getIntent().getStringExtra("amount"),"send");
                        }
                        if (getIntent().getStringExtra("key").equals("removeaddress")) {
                            alertDialog(CheckSecurePin.this, "Are you sure to delete Bitcoin Address? ");
                        }
                    } else {

                        // keyStore=null;

                        cipher = null;
                        keyGenerator = null;

                        try {
                            keyStore.load(null);
                            keyStore.deleteEntry(KEY_NAME);
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        } catch (NoSuchAlgorithmException e1) {
                            e1.printStackTrace();
                        } catch (CertificateException e1) {
                            e1.printStackTrace();
                        } catch (KeyStoreException e1) {
                            e1.printStackTrace();
                        }

                        txUnathorized.setVisibility(View.VISIBLE);

                        // dialog.dismiss();

                        // Toast.makeText(CheckSecurePin.this, "Incorrect fingerprint", Toast.LENGTH_SHORT).show();

                    }

                }
            });
        }

    }

    protected void checkFingerPrintScanner() {
        // Check whether the device has a Fingerprint sensor.
        if (!fingerprintManager.isHardwareDetected()) {
            /**
             * An error message will be dispGlayed if the device does not contain the fingerprint hardware.
             * However if you plan to implement a default authentication method,
             */
            //textView.setText("Your Device does not have a Fingerprint Sensor");
        } else {
            // Checks whether fingerprint permission is set on manifest
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                //  textView.setText("Fingerprint authentication permission not enabled");
            } else {
                // Check whether at least one fingerprint is registered
                if (!fingerprintManager.hasEnrolledFingerprints()) {
                } else {
                    // Checks whether lock screen security is enabled or not
                    if (!keyguardManager.isKeyguardSecure()) {
                        // textView.setText("Lock screen security not enabled in Settings");
                    } else {
                        ImageView ivarrow;
                        dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                        // dialog  = new Dialog(this);
                        LayoutInflater li = LayoutInflater.from(this);
                        View promptsView2 = li.inflate(R.layout.fingerprint_login, null);


                        image = (ImageView) promptsView2.findViewById(R.id.ivkeypad);
                        txTxt = (TextView) promptsView2.findViewById(R.id.txTxt);
                        txUnathorized = (TextView) promptsView2.findViewById(R.id.txUnathorized);
                        ImageView imgProfile = (ImageView) promptsView2.findViewById(R.id.imgProfile);

                        ivarrow = (ImageView) promptsView2.findViewById(R.id.ivarrow);

                        txTxt.setText(PreferenceFile.getInstance().getPreferenceData(this, Constant.Username));

                        if (PreferenceFile.getInstance().getPreferenceData(this, Constant.Image) != null) {


                            Picasso.with(this)
                                    .load(Constant.ImagePath + PreferenceFile.getInstance().getPreferenceData(this, Constant.Image)).resize(400, 400).placeholder(getResources().getDrawable(R.drawable.placeholder))
                                    .into(imgProfile);
                        }

                        ivarrow.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                finish();
                            }
                        });

                        image.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                dialog.dismiss();
                            }
                        });

                        dialog.setContentView(promptsView2);
                        dialog.setCanceledOnTouchOutside(true);
                        dialog.show();

                        generateKey();

                        if (cipherInit()) {
                            FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);
                            FingerprintAuthenticationHandler helper = new FingerprintAuthenticationHandler(this);
                            helper.startAuth(fingerprintManager, cryptoObject);
                        }
                    }
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean cipherInit() {

        try {
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" +
                    KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e)
        {
            throw new RuntimeException("Failed to get Cipher", e);
        }

        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME, null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    protected void generateKey() {

        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException("Failed to get KeyGenerator instance", e);
        }

        try {
            keyStore.load(null);
            keyGenerator.init(new KeyGenParameterSpec.Builder(KEY_NAME, KeyProperties.PURPOSE_ENCRYPT |
                    KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(
                            KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException |
                InvalidAlgorithmParameterException
                | CertificateException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void calltimer() {

        //txNext.setVisibility(View.VISIBLE);

        countDownTimer = new CountDownTimer(40000, 1000) {
            public void onTick(long millisUntilFinished) {
                chronometer.setText("" + millisUntilFinished / 1000 + " Sec");
            }

            public void onFinish() {

                txResend.setVisibility(View.VISIBLE);
                enterotp = "";
                edfirst.setText("");
                edSecond.setText("");
                edthird.setText("");
                edFouth.setText("");

                chronometer.setText("done!");
                otp = "";
                txNext.setVisibility(View.GONE);
            }
        }.start();
    }

    private void initializeViews() {

        txtPinEntry = (PinEntryEditText) findViewById(R.id.txt_pin_entry);
        pin1 = (ImageView) findViewById(R.id.pin1);
        pin2 = (ImageView) findViewById(R.id.pin2);
        pin3 = (ImageView) findViewById(R.id.pin3);
        pin4 = (ImageView) findViewById(R.id.pin4);
        if (txtPinEntry != null) {
            txtPinEntry.setOnPinEnteredListener(new PinEntryEditText.OnPinEnteredListener() {
                @Override
                public void onPinEntered(CharSequence str) {
                    if (str.length() == 4) {
                        text = str.toString();
                        callfunction();
                    }
                }

                @Override
                public void onTextChange(CharSequence str) {
                    switch (str.length()) {
                        case 0:
                            pin1.setImageResource(R.color.transparent);
                            pin2.setImageResource(R.color.transparent);
                            pin3.setImageResource(R.color.transparent);
                            pin4.setImageResource(R.color.transparent);
                            break;
                        case 1:
                            pin1.setImageResource(R.drawable.password);
                            break;
                        case 2:
                            pin2.setImageResource(R.drawable.password);
                            break;
                        case 3:
                            pin3.setImageResource(R.drawable.password);
                            break;
                        case 4:
                            pin4.setImageResource(R.drawable.password);
                            break;
                    }
                }
            });
        }
        this.num = new StringBuffer();
        this.num.append("");

        this.one = (LinearLayout) findViewById(R.id.one);
        this.two = (LinearLayout) findViewById(R.id.two);
        this.three = (LinearLayout) findViewById(R.id.three);
        this.four = (LinearLayout) findViewById(R.id.four);
        this.five = (LinearLayout) findViewById(R.id.five);
        this.six = (LinearLayout) findViewById(R.id.six);
        this.seven = (LinearLayout) findViewById(R.id.seven);
        this.eight = (LinearLayout) findViewById(R.id.eight);
        this.nine = (LinearLayout) findViewById(R.id.nine);
        this.zero = (LinearLayout) findViewById(R.id.zero);
        this.back = (LinearLayout) findViewById(R.id.back);
        this.ok = (LinearLayout) findViewById(R.id.ok);

        this.one.setOnClickListener(this);
        this.two.setOnClickListener(this);
        this.three.setOnClickListener(this);
        this.four.setOnClickListener(this);
        this.five.setOnClickListener(this);
        this.six.setOnClickListener(this);
        this.seven.setOnClickListener(this);
        this.eight.setOnClickListener(this);
        this.nine.setOnClickListener(this);
        this.zero.setOnClickListener(this);
        this.back.setOnClickListener(this);
        this.ok.setOnClickListener(this);
    }


    private void NetwordDetect() {

        boolean WIFI = false;

        boolean MOBILE = false;

        ConnectivityManager CM = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo[] networkInfo = CM.getAllNetworkInfo();

        for (NetworkInfo netInfo : networkInfo) {

            if (netInfo.getTypeName().equalsIgnoreCase("WIFI"))

                if (netInfo.isConnected())

                    WIFI = true;

            if (netInfo.getTypeName().equalsIgnoreCase("MOBILE"))

                if (netInfo.isConnected())

                    MOBILE = true;
        }

        if(WIFI == true)
        {
            IPaddress = GetDeviceipWiFiData();

            TelephonyManager tMgr =  (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
            operator = tMgr.getNetworkOperatorName();

        }

        if(MOBILE == true)
        {
            IPaddress = GetDeviceipMobileData();
            macAddress=getMacAddr();
            TelephonyManager tManager = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
            operator=tManager.getNetworkOperatorName();
        }
    }
    public String GetDeviceipWiFiData() {

        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);

        macAddress=  wm.getConnectionInfo().getMacAddress();
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

        return ip;

    }
    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    //res1.append(Integer.toHexString(b & 0xFF) + ":");
                    res1.append(String.format("%02X:",b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
        }
        return "02:00:00:00:00:00";
    }


    public String GetDeviceipMobileData() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
                 en.hasMoreElements(); ) {
                NetworkInterface networkinterface = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = networkinterface.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        String ip=Formatter.formatIpAddress(inetAddress.hashCode());
                        return  ip;
//                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (Exception ex) {
        }
        return null;
    }


    public void alertDialog(Context context, String str) {

        final Dialog dialog = new Dialog(CheckSecurePin.this);

//        dialog.setTitle("FIN-CEX");
        dialog.setCancelable(false);


        dialog.setContentView(R.layout.dialog);

        int width = WindowManager.LayoutParams.MATCH_PARENT;
        int height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setLayout(width, height);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.gravity = Gravity.CENTER;


        dialog.setCanceledOnTouchOutside(true);
        dialog.show();


        TextView tvText = (TextView) dialog.findViewById(R.id.tvText);
        TextView btnok = (TextView) dialog.findViewById(R.id.btnok);
        TextView btncancel = (TextView) dialog.findViewById(R.id.btncansel);
        tvText.setText(str);

        btnok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // moveTaskToBack(true);
                dialog.dismiss();
                callservices();
            }
        });

        btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
            }
        });
    }

    public void alertDialogDesc(Context context, String str,String Total,String network,String gst,String amt) {

        final Dialog dialog = new Dialog(CheckSecurePin.this);

//        dialog.setTitle("FIN-CEX");
        dialog.setCancelable(false);

        dialog.setContentView(R.layout.alert_dialog_new);


        int width = WindowManager.LayoutParams.MATCH_PARENT;
        int height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setLayout(width, height);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.gravity = Gravity.CENTER;


        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        netVal = (TextView) dialog.findViewById(R.id.netVal);
        gstVal = (TextView) dialog.findViewById(R.id.gstVal);
        TotAmt = (TextView) dialog.findViewById(R.id.TotAmt);
        AmtVal = (TextView) dialog.findViewById(R.id.AmtVal);
        netfee = (TextView) dialog.findViewById(R.id.netfee);
        gstfee = (TextView) dialog.findViewById(R.id.gstfee);

        if (gst.equalsIgnoreCase("") || gst.equalsIgnoreCase("0")) {
            gstVal.setVisibility(View.GONE);
            netfee.setText("Withdrawal Fees");
        } else {
            gstVal.setVisibility(View.VISIBLE);
            netfee.setText("Network Fees");
        }

        if (getIntent().getStringExtra("key").equals("withdraw")) {
            gstfee.setVisibility(View.GONE);
            gstVal.setVisibility(View.GONE);
            netfee.setText("Withdrawal Fees");
        }

        TotAmt.setText(Total+" "+PreferenceFile.getInstance().getPreferenceData(CheckSecurePin.this,Constant.Currency_Symbol));
        netVal.setText(network+" "+PreferenceFile.getInstance().getPreferenceData(CheckSecurePin.this,Constant.Currency_Symbol));
        gstVal.setText(gst+" "+PreferenceFile.getInstance().getPreferenceData(CheckSecurePin.this,Constant.Currency_Symbol));
        AmtVal.setText(amt+" "+PreferenceFile.getInstance().getPreferenceData(CheckSecurePin.this,Constant.Currency_Symbol));

        TextView tvText = (TextView) dialog.findViewById(R.id.tvText);
        TextView btnok = (TextView) dialog.findViewById(R.id.btnok);
        TextView btncancel = (TextView) dialog.findViewById(R.id.btncansel);
        tvText.setText(str);

        btnok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // moveTaskToBack(true);
                dialog.dismiss();
                callservices();
            }
        });

        btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
            }
        });
    }

    public void  alertDialogSimple(Context context, String str) {

        final Dialog dialog = new Dialog(CheckSecurePin.this);

//        dialog.setTitle("FIN-CEX");
        dialog.setCancelable(false);

        dialog.setContentView(R.layout.simple_alert);


        int width = WindowManager.LayoutParams.MATCH_PARENT;
        int height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setLayout(width, height);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.gravity = Gravity.CENTER;



        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        TextView  tvText = (TextView) dialog.findViewById(R.id.tvText);
        TextView btnok = (TextView) dialog.findViewById(R.id.btnok);

        tvText.setText(str);

        btnok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }

    public void  alertDialogBitCoinDesc(Context context, String str, String Total,
                                        final String network, String gst, String btc, final String type) {

        final Dialog dialog = new Dialog(CheckSecurePin.this);

//        dialog.setTitle("FIN-CEX");
        dialog.setCancelable(false);


        dialog.setContentView(R.layout.alert_dialog_new);

        int width = WindowManager.LayoutParams.MATCH_PARENT;
        int height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setLayout(width, height);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.gravity = Gravity.CENTER;




        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        netVal = (TextView) dialog.findViewById(R.id.netVal);
        gstVal = (TextView) dialog.findViewById(R.id.gstVal);
        AmtFee = (TextView) dialog.findViewById(R.id.AmtFee);
        TotAmt = (TextView) dialog.findViewById(R.id.TotAmt);
        AmtVal = (TextView) dialog.findViewById(R.id.AmtVal);

        if (type.equalsIgnoreCase("buy")) {

            Log.e("DIALOG_DETAILS", PreferenceFile.getInstance().getPreferenceData(this, Constant.Inr_Amount) + " \n" +
                    str + " " + Total + " " + network + " " + gst + " " + btc + " " + type);


            if (Double.parseDouble(Total.trim()) == Double.parseDouble(PreferenceFile.getInstance().getPreferenceData(this, Constant.Inr_Amount))) {
                //need to change
                Double networkBTC = Double.parseDouble(network.trim()) / Double.parseDouble(getIntent().getStringExtra("buy_rate"));
                Double gstBTC = Double.parseDouble(gst.trim()) / Double.parseDouble(getIntent().getStringExtra("buy_rate"));

                netVal.setText("" + String.format("%.2f", Double.valueOf(network)) + " " + PreferenceFile.getInstance().getPreferenceData(CheckSecurePin.this, Constant.Currency_Symbol) + "/" + String.format("%.8f", networkBTC) + " " + "Ƀ");
                gstVal.setText("" + String.format("%.2f", Double.valueOf(gst)) + " " + PreferenceFile.getInstance().getPreferenceData(CheckSecurePin.this, Constant.Currency_Symbol) + "/" + String.format("%.8f", gstBTC) + " " + "Ƀ");

                amt1 = Double.parseDouble(Total.trim()) - (Double.parseDouble(network.trim()) + Double.parseDouble(gst.trim()));


                AmtVal.setText(String.format("%.2f", amt1) + " " + PreferenceFile.getInstance().getPreferenceData(CheckSecurePin.this, Constant.Currency_Symbol) + "/" + String.format("%.8f", amt1 / Double.parseDouble(getIntent().getStringExtra("buy_rate"))) + " " + "Ƀ");
                TotAmt.setText(String.format("%.2f", Double.valueOf(Total)) + " " + PreferenceFile.getInstance().getPreferenceData(CheckSecurePin.this, Constant.Currency_Symbol) + "/" + btc + " " + "Ƀ");


            } else {

                amt1 = Double.parseDouble(Total.trim()) + (Double.parseDouble(network.trim()) + Double.parseDouble(gst.trim()));

                AmtVal.setText(String.format("%.2f", Double.valueOf(Total)) + " " + PreferenceFile.getInstance().getPreferenceData(CheckSecurePin.this, Constant.Currency_Symbol) + "/" + btc + " " + "Ƀ");

                Double networkBTC = Double.parseDouble(network.trim()) / Double.parseDouble(getIntent().getStringExtra("buy_rate"));
                Double gstBTC = Double.parseDouble(gst.trim()) / Double.parseDouble(getIntent().getStringExtra("buy_rate"));

                netVal.setText("" + String.format("%.2f", Double.valueOf(network)) + " " + PreferenceFile.getInstance().getPreferenceData(CheckSecurePin.this, Constant.Currency_Symbol) + "/" + String.format("%.8f", networkBTC) + " " + "Ƀ");
                gstVal.setText("" + String.format("%.2f", Double.valueOf(gst)) + " " + PreferenceFile.getInstance().getPreferenceData(CheckSecurePin.this, Constant.Currency_Symbol) + "/" + String.format("%.8f", gstBTC) + " " + "Ƀ");

                Double totBTC = Double.parseDouble(btc) + networkBTC + gstBTC;
                TotAmt.setText(String.format("%.2f", amt1) + " " + PreferenceFile.getInstance().getPreferenceData(CheckSecurePin.this, Constant.Currency_Symbol) + "/" + String.format("%.8f", totBTC) + " " + "Ƀ");

            }

        } else if(type.equalsIgnoreCase("send")) {


            Double actualBTC = Double.parseDouble(getIntent().getStringExtra("actual_btc"));

            final_Charge = (Double.parseDouble(network.trim()) + Double.parseDouble(gst.trim()));

//            if(getIntent().getStringExtra("data_key").equalsIgnoreCase("1")) {
//
//                amt1 = Double.parseDouble(Total.trim()) - (Double.parseDouble(network.trim()) + Double.parseDouble(gst.trim()));
//                Log.e("recvdBTC", "" + amt1 / Double.parseDouble(getIntent().getStringExtra("rate")));
//
//                AmtVal.setText(String.format("%.2f", amt1) + " " + PreferenceFile.getInstance().getPreferenceData(CheckSecurePin.this, Constant.Currency_Symbol) + "/" + String.format("%.8f", amt1 / Double.parseDouble(getIntent().getStringExtra("rate"))) + " " + "Ƀ");
//                TotAmt.setText(String.format("%.2f", Double.valueOf(Total)) + " " + PreferenceFile.getInstance().getPreferenceData(CheckSecurePin.this, Constant.Currency_Symbol) + "/" + btc + " " + "Ƀ");
////                    amtval bcum tot amt and vice versa
//
//                Double networkBTC = Double.parseDouble(network.trim()) / Double.parseDouble(getIntent().getStringExtra("rate"));
//                Double gstBTC = Double.parseDouble(gst.trim()) / Double.parseDouble(getIntent().getStringExtra("rate"));
//
//                netVal.setText("" + String.format("%.2f", Double.valueOf(network)) + " " + PreferenceFile.getInstance().getPreferenceData(CheckSecurePin.this, Constant.Currency_Symbol) + "/" + String.format("%.8f", networkBTC) + " " + "Ƀ");
//                gstVal.setText("" + String.format("%.2f", Double.valueOf(gst)) + " " + PreferenceFile.getInstance().getPreferenceData(CheckSecurePin.this, Constant.Currency_Symbol) + "/" + String.format("%.8f", gstBTC) + " " + "Ƀ");
//
//
//            }else{
            if (actualBTC > Double.parseDouble(btc)) {

                amt1 = Double.parseDouble(Total.trim()) + (Double.parseDouble(network.trim()) + Double.parseDouble(gst.trim()));
                AmtVal.setText(String.format("%.2f", Double.valueOf(Total)) + " " + PreferenceFile.getInstance().getPreferenceData(CheckSecurePin.this, Constant.Currency_Symbol) + "/" + btc + " " + "Ƀ");

                Double networkBTC = Double.parseDouble(network.trim()) / Double.parseDouble(getIntent().getStringExtra("rate"));
                Double gstBTC = Double.parseDouble(gst.trim()) / Double.parseDouble(getIntent().getStringExtra("rate"));

                netVal.setText("" + String.format("%.2f", Double.valueOf(network)) + " " + PreferenceFile.getInstance().getPreferenceData(CheckSecurePin.this, Constant.Currency_Symbol) + "/" + String.format("%.8f", networkBTC) + " " + "Ƀ");
                gstVal.setText("" + String.format("%.2f", Double.valueOf(gst)) + " " + PreferenceFile.getInstance().getPreferenceData(CheckSecurePin.this, Constant.Currency_Symbol) + "/" + String.format("%.8f", gstBTC) + " " + "Ƀ");

                Double totBTC = Double.parseDouble(btc) + networkBTC + gstBTC;

                TotAmt.setText(String.format("%.2f", amt1) + " " + PreferenceFile.getInstance().getPreferenceData(CheckSecurePin.this, Constant.Currency_Symbol) + "/" + String.format("%.8f", totBTC) + " " + "Ƀ");


            } else if (actualBTC == Double.parseDouble(btc)) {
                {

                    amt1 = Double.parseDouble(Total.trim()) - (Double.parseDouble(network.trim()) + Double.parseDouble(gst.trim()));
                    Log.e("recvdBTC", "" + amt1 / Double.parseDouble(getIntent().getStringExtra("rate")));

                    AmtVal.setText(String.format("%.2f", amt1) + " " + PreferenceFile.getInstance().getPreferenceData(CheckSecurePin.this, Constant.Currency_Symbol) + "/" + String.format("%.8f", amt1 / Double.parseDouble(getIntent().getStringExtra("rate"))) + " " + "Ƀ");
                    TotAmt.setText(String.format("%.2f", Double.valueOf(Total)) + " " + PreferenceFile.getInstance().getPreferenceData(CheckSecurePin.this, Constant.Currency_Symbol) + "/" + btc + " " + "Ƀ");
//                    amtval bcum tot amt and vice versa

                    Double networkBTC = Double.parseDouble(network.trim()) / Double.parseDouble(getIntent().getStringExtra("rate"));
                    Double gstBTC = Double.parseDouble(gst.trim()) / Double.parseDouble(getIntent().getStringExtra("rate"));

                    netVal.setText("" + String.format("%.2f", Double.valueOf(network)) + " " + PreferenceFile.getInstance().getPreferenceData(CheckSecurePin.this, Constant.Currency_Symbol) + "/" + String.format("%.8f", networkBTC) + " " + "Ƀ");
                    gstVal.setText("" + String.format("%.2f", Double.valueOf(gst)) + " " + PreferenceFile.getInstance().getPreferenceData(CheckSecurePin.this, Constant.Currency_Symbol) + "/" + String.format("%.8f", gstBTC) + " " + "Ƀ");

                }

            }
//            }
        }
        else if(type.equalsIgnoreCase("sell")) {

            Double actualBTC = Double.parseDouble(getIntent().getStringExtra("actual_btc"));
            Log.e("DATA_RECEIVED_BTC", btc + "   actualBTC  " + actualBTC + " " + getIntent().getStringExtra("data_key"));

            final_Charge = (Double.parseDouble(network.trim()) + Double.parseDouble(gst.trim()));

            Log.e("DATA_RECEIVED", Total + "   " + network + "    " + gst + "    " + " final_Charge " + final_Charge);
            Log.e("DATA_RECEIVED_RATE", getIntent().getStringExtra("sell_rate"));


            if (getIntent().getStringExtra("data_key").equalsIgnoreCase("1")) {


                amt1 = Double.parseDouble(Total.trim()) - (Double.parseDouble(network.trim()) + Double.parseDouble(gst.trim()));
                Log.e("recvdBTC", "" + amt1 / Double.parseDouble(getIntent().getStringExtra("sell_rate")));

                AmtVal.setText(String.format("%.2f", amt1) + " " + PreferenceFile.getInstance().getPreferenceData(CheckSecurePin.this, Constant.Currency_Symbol) + "/" + String.format("%.8f", amt1 / Double.parseDouble(getIntent().getStringExtra("sell_rate"))) + " " + "Ƀ");
                TotAmt.setText(String.format("%.2f", Double.valueOf(Total)) + " " + PreferenceFile.getInstance().getPreferenceData(CheckSecurePin.this, Constant.Currency_Symbol) + "/" + String.format("%.8f", Double.parseDouble(btc)) + " " + "Ƀ");
//                    amtval bcum tot amt and vice versa

                Double networkBTC = Double.parseDouble(network.trim()) / Double.parseDouble(getIntent().getStringExtra("sell_rate"));
                Double gstBTC = Double.parseDouble(gst.trim()) / Double.parseDouble(getIntent().getStringExtra("sell_rate"));

//                     Double totBTC=Double.parseDouble(btc)+networkBTC+gstBTC;

                netVal.setText(String.format("%.2f", Double.valueOf(network)) + " " + PreferenceFile.getInstance().getPreferenceData(CheckSecurePin.this, Constant.Currency_Symbol) + "/" + String.format("%.8f", networkBTC) + " " + "Ƀ");
                gstVal.setText(String.format("%.2f", Double.valueOf(gst)) + " " + PreferenceFile.getInstance().getPreferenceData(CheckSecurePin.this, Constant.Currency_Symbol) + "/" + String.format("%.8f", gstBTC) + " " + "Ƀ");

            } else {

                if (actualBTC > Double.parseDouble(String.format("%.8f", Double.parseDouble(btc)))) { //sell particular

                    amt1 = Double.parseDouble(Total.trim()) + (Double.parseDouble(network.trim()) + Double.parseDouble(gst.trim()));
                    AmtVal.setText(String.format("%.2f", Double.valueOf(Total)) + " " + PreferenceFile.getInstance().getPreferenceData(CheckSecurePin.this, Constant.Currency_Symbol) + "/" + String.format("%.8f", Double.parseDouble(btc)) + " " + "Ƀ");

                    Double networkBTC = Double.parseDouble(network.trim()) / Double.parseDouble(getIntent().getStringExtra("sell_rate"));
                    Double gstBTC = Double.parseDouble(gst.trim()) / Double.parseDouble(getIntent().getStringExtra("sell_rate"));

                    netVal.setText(String.format("%.2f", Double.valueOf(network)) + " " + PreferenceFile.getInstance().getPreferenceData(CheckSecurePin.this, Constant.Currency_Symbol)
                            + "/" + String.format("%.8f", networkBTC) + " " + "Ƀ");

                    gstVal.setText(String.format("%.2f", Double.valueOf(gst)) + " " + PreferenceFile.getInstance().getPreferenceData(CheckSecurePin.this, Constant.Currency_Symbol)
                            + "/" + String.format("%.8f", gstBTC) + " " + "Ƀ");

                    Double totBTC = Double.parseDouble(btc) + networkBTC + gstBTC;

                    TotAmt.setText(String.format("%.2f", amt1) + " " + PreferenceFile.getInstance().getPreferenceData(CheckSecurePin.this, Constant.Currency_Symbol)
                            + "/" + String.format("%.8f", totBTC) + " " + "Ƀ");

                } else if (actualBTC == Double.parseDouble(String.format("%.8f", Double.parseDouble(btc)))
                        || Double.parseDouble(Total) == Double.parseDouble(getIntent().getStringExtra("data"))
                        ) { //sell all
                    {
                        amt1 = Double.parseDouble(Total.trim()) - (Double.parseDouble(network.trim()) + Double.parseDouble(gst.trim()));
                        Log.e("recvdBTC", "" + amt1 / Double.parseDouble(getIntent().getStringExtra("sell_rate")));

                        AmtVal.setText(String.format("%.2f", amt1) + " " + PreferenceFile.getInstance().getPreferenceData(CheckSecurePin.this, Constant.Currency_Symbol) + "/" + String.format("%.8f", amt1 / Double.parseDouble(getIntent().getStringExtra("sell_rate"))) + " " + "Ƀ");
                        TotAmt.setText(String.format("%.2f", Double.valueOf(Total)) + " " + PreferenceFile.getInstance().getPreferenceData(CheckSecurePin.this, Constant.Currency_Symbol) + "/" + String.format("%.8f", Double.parseDouble(btc)) + " " + "Ƀ");
//                    amtval bcum tot amt and vice versa

                        Double networkBTC = Double.parseDouble(network.trim()) / Double.parseDouble(getIntent().getStringExtra("sell_rate"));
                        Double gstBTC = Double.parseDouble(gst.trim()) / Double.parseDouble(getIntent().getStringExtra("sell_rate"));

//                     Double totBTC=Double.parseDouble(btc)+networkBTC+gstBTC;

                        netVal.setText(String.format("%.2f", Double.valueOf(network)) + " " + PreferenceFile.getInstance().getPreferenceData(CheckSecurePin.this, Constant.Currency_Symbol) + "/" + String.format("%.8f", networkBTC) + " " + "Ƀ");
                        gstVal.setText(String.format("%.2f", Double.valueOf(gst)) + " " + PreferenceFile.getInstance().getPreferenceData(CheckSecurePin.this, Constant.Currency_Symbol) + "/" + String.format("%.8f", gstBTC) + " " + "Ƀ");

                    }

                }
            }


        }
        else if(type.equalsIgnoreCase("bid")) {

            Double networkBTC=Double.parseDouble(network.trim())/Double.parseDouble(getIntent().getStringExtra("rate"));
            Double gstBTC=Double.parseDouble(gst.trim())/Double.parseDouble(getIntent().getStringExtra("rate"));

            Double totBTC=Double.parseDouble(btc)+networkBTC+gstBTC;

            amt1=Double.parseDouble(Total.trim())+(Double.parseDouble(network.trim())+Double.parseDouble(gst.trim()));

            AmtVal.setText(String.format("%.2f",Double.valueOf( Total)) +" "+
                    PreferenceFile.getInstance().getPreferenceData(CheckSecurePin.this,Constant.Currency_Symbol)+
                    "/"+ String.format("%.8f",Double.parseDouble(btc))+" "+"Ƀ");

            TotAmt.setText(String.format("%.2f",amt1)+" "+
                    PreferenceFile.getInstance().getPreferenceData(CheckSecurePin.this,Constant.Currency_Symbol)
                    +"/"+ String.format("%.8f",totBTC)+" "+"Ƀ");


            netVal.setText(String.format("%.2f",Double.valueOf( network))+" "+
                    PreferenceFile.getInstance().getPreferenceData(CheckSecurePin.this,Constant.Currency_Symbol)+"/"+
                    String.format("%.8f",networkBTC)+" "+"Ƀ");

            gstVal.setText(String.format("%.2f",Double.valueOf( gst))+" "+
                    PreferenceFile.getInstance().getPreferenceData(CheckSecurePin.this,Constant.Currency_Symbol)+"/"+
                    String.format("%.8f",gstBTC)+" "+"Ƀ");

        }  else if(type.equalsIgnoreCase("ask")) {


            Double networkBTC=Double.parseDouble(network.trim())/Double.parseDouble(getIntent().getStringExtra("sell_rate"));
            Double gstBTC=Double.parseDouble(gst.trim())/Double.parseDouble(getIntent().getStringExtra("sell_rate"));

            Double totBTC=Double.parseDouble(btc)+networkBTC+gstBTC;

            amt1=Double.parseDouble(Total.trim())-(Double.parseDouble(network.trim())+Double.parseDouble(gst.trim()));

            AmtVal.setText(String.format("%.2f",amt1)+" "
                    +PreferenceFile.getInstance().getPreferenceData(CheckSecurePin.this,Constant.Currency_Symbol)+
                    "/"+ String.format("%.8f",Double.parseDouble(btc))+" "+"Ƀ");

            TotAmt.setText(String.format("%.2f",Double.valueOf( Total)) +" "+
                    PreferenceFile.getInstance().getPreferenceData(CheckSecurePin.this,Constant.Currency_Symbol)
                    +"/"+ String.format("%.8f",totBTC)+" "+"Ƀ");


            netVal.setText(String.format("%.2f",Double.valueOf( network))+" "+
                    PreferenceFile.getInstance().getPreferenceData(CheckSecurePin.this,Constant.Currency_Symbol)+"/"+
                    String.format("%.8f",networkBTC)+" "+"Ƀ");

            gstVal.setText(String.format("%.2f",Double.valueOf( gst))+" "+
                    PreferenceFile.getInstance().getPreferenceData(CheckSecurePin.this,Constant.Currency_Symbol)+"/"+
                    String.format("%.8f",gstBTC)+" "+"Ƀ");

        } else  {


            Double networkBTC=Double.parseDouble(network.trim())/Double.parseDouble(getIntent().getStringExtra("rate"));
            Double gstBTC=Double.parseDouble(gst.trim())/Double.parseDouble(getIntent().getStringExtra("rate"));

            Double totBTC=Double.parseDouble(btc)+networkBTC+gstBTC;

            amt1=Double.parseDouble(Total.trim())-(Double.parseDouble(network.trim())+Double.parseDouble(gst.trim()));

            AmtVal.setText(String.format("%.2f",amt1)+" "
                    +PreferenceFile.getInstance().getPreferenceData(CheckSecurePin.this,Constant.Currency_Symbol)+
                    "/"+ String.format("%.8f",Double.parseDouble(btc))+" "+"Ƀ");

            TotAmt.setText(String.format("%.2f",Double.valueOf( Total)) +" "+
                    PreferenceFile.getInstance().getPreferenceData(CheckSecurePin.this,Constant.Currency_Symbol));

            netVal.setText(String.format("%.2f",Double.valueOf( network))+" "+
                    PreferenceFile.getInstance().getPreferenceData(CheckSecurePin.this,Constant.Currency_Symbol));

            gstVal.setText(String.format("%.2f",Double.valueOf( gst))+" "+
                    PreferenceFile.getInstance().getPreferenceData(CheckSecurePin.this,Constant.Currency_Symbol));

        }


        AmtFee.setText("Amount/BTC");
        Log.e("BTCCCC",btc+"");

        TextView tvText = (TextView) dialog.findViewById(R.id.tvText);
        TextView btnok = (TextView) dialog.findViewById(R.id.btnok);
        final TextView btncancel = (TextView) dialog.findViewById(R.id.btncansel);
        tvText.setText(str);

        btnok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // moveTaskToBack(true);
                dialog.dismiss();
//                callservices();
                if(type.equalsIgnoreCase("send")) {
                    sendTransactions(String.valueOf(final_Charge), network);
                }else{
                    callservices();
                }
//                sendTransactions(false, Double.parseDouble(getIntent().getStringExtra("charges")));
            }
        });

        btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
            }
        });
    }


    public void getLocation() {

        gpsTracker = new GpsTracker(CheckSecurePin.this);

        if (gpsTracker.canGetLocation()) {
            longitude = gpsTracker.getLongitude();
            latitude = gpsTracker.getLatitude();

        } else {
            AlertDialog.Builder alertDialog;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                alertDialog = new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_Alert);
            } else {
                alertDialog = new AlertDialog.Builder(this);
            }

            alertDialog.setCancelable(false);
            // Setting Dialog Title
            alertDialog.setTitle(this.getResources().getString(R.string.gps_setting));//gps_setting
            alertDialog.setMessage(this.getResources().getString(R.string.gps_not_enable));
            alertDialog.setPositiveButton(this.getResources().getString(R.string.action_settings), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 1);

                }
            });

            alertDialog.show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {

            new CountDownTimer(50000, 2000) {
                public void onTick(long millisUntilFinished) {
                    latitude = GpsTracker.latitude;
                    longitude = GpsTracker.longitude;
                    Log.e("lati-->", latitude + " longi--->" + longitude);
                }

                public void onFinish() {

                }

            }.start();
        }
    }

    public void onClick(View view) {
        if (this.txtPinEntry.getText().toString().length() > 4) {
            txtPinEntry.setText(null);
            num.delete(0, num.length());
        }

        switch (view.getId()) {

            case R.id.ivarrow:
                finish();
                return;
            case R.id.tvforgot:
                Intent intent1=new Intent(CheckSecurePin.this,ForgotPin.class);
                startActivity(intent1);
                return;
            case R.id.one:
                this.num.append("1");
                this.txtPinEntry.setText(this.num);
                return;
            case R.id.two:
                this.num.append("2");
                this.txtPinEntry.setText(this.num);
                return;
            case R.id.three:
                this.num.append("3");
                this.txtPinEntry.setText(this.num);
                return;
            case R.id.four:
                this.num.append("4");
                this.txtPinEntry.setText(this.num);
                return;
            case R.id.five:
                this.num.append("5");
                this.txtPinEntry.setText(this.num);
                return;
            case R.id.six:
                this.num.append("6");
                this.txtPinEntry.setText(this.num);
                return;
            case R.id.seven:
                this.num.append("7");
                this.txtPinEntry.setText(this.num);
                return;
            case R.id.eight:
                this.num.append("8");
                this.txtPinEntry.setText(this.num);
                return;
            case R.id.nine:
                this.num.append("9");
                this.txtPinEntry.setText(this.num);
                return;
            case R.id.back:
                if (num.length() > 0) {
                    num.delete(num.length() - 1, num.length());
                    this.txtPinEntry.setText(null);
                    for (int i = 0; i < num.length(); i++) {
                        this.txtPinEntry.setText(num.substring(i));
                    }
                }
                return;
            case R.id.zero:
                this.num.append("0");
                this.txtPinEntry.setText(this.num);
                return;
            case R.id.ok:
                callfunction();
                return;
            default:
                return;
        }
    }

    public void callservices() {

        Log.e("callservice-->", getIntent().getStringExtra("key"));

        if (getIntent().getStringExtra("key").equals("finger_print")) {


            PreferenceFile.getInstance().saveData(this,Constant.COUNT_SECURITY,"1");
            PreferenceFile.getInstance().saveData(CheckSecurePin.this,Constant.Finger_Lock,getIntent().getStringExtra("lock"));

            if(getIntent().getStringExtra("lock").equals("0")){
                Constant.alertWithIntent(CheckSecurePin.this, "Successfully Unapplied", Dashboard.class);
            }else {
                Constant.alertWithIntent(CheckSecurePin.this, "Successfully Applied", Dashboard.class);
            }
        }

        if (getIntent().getStringExtra("key").equals("lock_transaction")) {

            try {
                if (Constant.isConnectingToInternet(CheckSecurePin.this)) {
                    Log.e("connect--->", "yes");
                    new Retrofit2(this, CheckSecurePin.this, Constant.REQ_TRANSACTION_LOG, Constant.TRANSACTION_LOG+PreferenceFile.getInstance().getPreferenceData(this,Constant.ID)).callService(true);
                } else {

                    Log.e("connect--->", "no");
                    Constant.alertDialog(CheckSecurePin.this, getResources().getString(R.string.check_connection));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (getIntent().getStringExtra("key").equals("withdraw")) {
            JSONObject postParam = new JSONObject();
            try {
                postParam.put("user_id",
                        PreferenceFile.getInstance().getPreferenceData(this, Constant.ID));
                postParam.put("amount",
                        String.format("%.2f", Double.valueOf(getIntent().getStringExtra("inr_amount"))));
                postParam.put("description", getIntent().getStringExtra("description"));
                postParam.put("fee",
                        String.format("%.2f", Double.valueOf(getIntent().getStringExtra("fee"))));
                postParam.put("tax",
                        String.format("%.2f", Double.valueOf(getIntent().getStringExtra("charges"))));
                postParam.put("gst",
                        String.format("%.2f", Double.valueOf(getIntent().getStringExtra("gst"))));
                postParam.put("original_amount",
                        String.format("%.2f", Double.valueOf(getIntent().getStringExtra("original_amount"))));

                postParam.put("ip", IPaddress);
                postParam.put("network", operator);

                postParam.put("lat", String.valueOf(latitude));
                postParam.put("lng", String.valueOf(longitude));

                postParam.put("mac_address", macAddress);
                postParam.put("device_sdk", androidSDK);
                postParam.put("device_version", androidVersion);
                postParam.put("device_brand", androidBrand);
                postParam.put("device_manufacturer", androidManufacturer);
                postParam.put("device_model", androidModel);
                postParam.put("previous_balance", PreferenceFile.getInstance().getPreferenceData(this, Constant.Inr_Amount));


                Log.e("postparam--->", postParam.toString());
                if (Constant.isConnectingToInternet(CheckSecurePin.this)) {
                    new Retrofit2(CheckSecurePin.this, CheckSecurePin.this, postParam, Constant.REQ_Withdraw, Constant.Withdraw, "3").callService(true);
                } else {

                    Constant.alertDialog(CheckSecurePin.this, getResources().getString(R.string.check_connection));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (getIntent().getStringExtra("key").equals("send")) {

            checkThirdPartyApi();
            Log.e("callServices","send");

        }

        if (getIntent().getStringExtra("key").equals("sell")) {

            Double bit = Double.parseDouble(getIntent().getStringExtra("sell_rate"));

            String rate = String.format("%.2f", bit);

            JSONObject postParam = new JSONObject();

            try {

                Double actualBTC = Double.parseDouble(getIntent().getStringExtra("actual_btc"));

                Log.e("BTCCCC", actualBTC + "");

                Log.e("BTCCCC",
                        Double.parseDouble(String.format("%.8f", Double.parseDouble(getIntent().getStringExtra("btc_amount")))) + "");

                if (actualBTC > Double.parseDouble(String.format("%.8f", Double.parseDouble(getIntent().getStringExtra("btc_amount"))))) { //sell particular

                    amt1 = Double.parseDouble(getIntent().getStringExtra("inr_amount").trim()) +
                            (Double.parseDouble(getIntent().getStringExtra("charge").trim())
                                    + Double.parseDouble(getIntent().getStringExtra("gst").trim()));

                    Double networkBTC = Double.parseDouble(getIntent().getStringExtra("charge").trim())
                            / Double.parseDouble(getIntent().getStringExtra("sell_rate"));

                    Double gstBTC = Double.parseDouble(getIntent().getStringExtra("gst").trim())
                            / Double.parseDouble(getIntent().getStringExtra("sell_rate"));

                    Double totBTC = Double.parseDouble(getIntent().getStringExtra("btc_amount")) + networkBTC + gstBTC;

                    postParam.put("inr_amount", String.format("%.2f", amt1));

                    postParam.put("btc_amount", String.format("%.8f", totBTC));

                } else if (actualBTC == Double.parseDouble(String.format("%.8f", Double.parseDouble(getIntent().getStringExtra("btc_amount"))))) {

                    amt1 = Double.parseDouble(getIntent().getStringExtra("inr_amount").trim());

                    Log.e("AMTTT", getIntent().getStringExtra("inr_amount").trim() + "");

                    Log.e("AMTTT", amt1 + "");

                    Double networkBTC = Double.parseDouble(getIntent().getStringExtra("charge").trim()) / Double.parseDouble(getIntent().getStringExtra("sell_rate"));
                    Double gstBTC = Double.parseDouble(getIntent().getStringExtra("gst").trim()) / Double.parseDouble(getIntent().getStringExtra("sell_rate"));
                    Double totBTC = Double.parseDouble(getIntent().getStringExtra("btc_amount")) + networkBTC + gstBTC;

                    postParam.put("inr_amount", String.format("%.2f", amt1));
                    postParam.put("btc_amount", String.format("%.8f", Double.parseDouble(getIntent().getStringExtra("btc_amount"))));
                }


                postParam.put("previous_btc_amount", getIntent().getStringExtra("actual_btc"));
                postParam.put("user_id", PreferenceFile.getInstance().getPreferenceData(this, Constant.ID));

                postParam.put("fee", String.format("%.2f", Double.valueOf(getIntent().getStringExtra("fee"))));
                postParam.put("gst", String.format("%.2f", Double.valueOf(getIntent().getStringExtra("gst"))));
                postParam.put("tax", String.format("%.2f", Double.valueOf(getIntent().getStringExtra("charge"))));

                postParam.put("rate", rate);
                postParam.put("ip", IPaddress);
                postParam.put("network", operator);
                postParam.put("lat", String.valueOf(latitude));
                postParam.put("lng", String.valueOf(longitude));
                postParam.put("mac_address", macAddress);
                postParam.put("device_sdk", androidSDK);
                postParam.put("device_version", androidVersion);
                postParam.put("device_brand", androidBrand);
                postParam.put("device_manufacturer", androidManufacturer);
                postParam.put("device_model", androidModel);
//                postParam.put("currency_code", PreferenceFile.getInstance().getPreferenceData(CheckSecurePin.this, Constant.Currency_Code).trim());

                Log.e("postparamSELL", postParam.toString());

                Log.e("postparamSELL", postParam.toString());

                if (Constant.isConnectingToInternet(CheckSecurePin.this)) {
                    Log.e("connect--->", "yes");
                    new Retrofit2(CheckSecurePin.this, CheckSecurePin.this, postParam, Constant.REQ_SELL_BTC, Constant.SELL_BTC, "3").callService(true);
                } else {

                    Log.e("connect--->", "no");
                    Constant.alertDialog(CheckSecurePin.this, getResources().getString(R.string.check_connection));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (getIntent().getStringExtra("key").equals("ask")) {

            JSONObject postParam = new JSONObject();
            try {
                postParam.put("user_id", PreferenceFile.getInstance().getPreferenceData(this, Constant.ID));
                postParam.put("sell_btc", getIntent().getStringExtra("sell_btc"));
                postParam.put("sell_rate", getIntent().getStringExtra("sell_rate"));
                postParam.put("sell_amount", getIntent().getStringExtra("sell_amount"));
                postParam.put("gst", getIntent().getStringExtra("gst"));
                postParam.put("tax", getIntent().getStringExtra("charge"));
                postParam.put("previous_amount", PreferenceFile.getInstance().getPreferenceData(this,Constant.Inr_Amount));

                postParam.put("fee", getIntent().getStringExtra("fee"));
                postParam.put("ip", IPaddress);
                postParam.put("network", operator);

                postParam.put("mac_address", macAddress);
                postParam.put("device_sdk", androidSDK);
                postParam.put("device_version", androidVersion);
                postParam.put("device_brand", androidBrand);
                postParam.put("device_manufacturer", androidManufacturer);
                postParam.put("device_model", androidModel);
                postParam.put("lat", String.valueOf(latitude));
                postParam.put("lng", String.valueOf(longitude));
                Log.e("postparamask--->", postParam.toString());

                if (Constant.isConnectingToInternet(CheckSecurePin.this)) {

                    Log.e("connect--->", "yes");
                    new Retrofit2(CheckSecurePin.this, CheckSecurePin.this, postParam, Constant.REQ_Ask, Constant.Ask, "3").callService(true);
                } else {

                    Log.e("connect--->", "no");
                    Constant.alertDialog(CheckSecurePin.this, getResources().getString(R.string.check_connection));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (getIntent().getStringExtra("key").equals("buy")) {

            Double bit = Double.parseDouble(getIntent().getStringExtra("buy_rate"));
            String rate=String.format("%.2f", bit);

            JSONObject postParam1 = new JSONObject();

            try {

                postParam1.put("user_id", PreferenceFile.getInstance().getPreferenceData(this, Constant.ID));

                Double totAmt=Double.parseDouble(getIntent().getStringExtra("inr_amount"))
                        +Double.parseDouble(getIntent().getStringExtra("fee"));

                if(Double.parseDouble(PreferenceFile.getInstance().getPreferenceData(this,Constant.Inr_Amount))
                        ==Double.parseDouble(getIntent().getStringExtra("inr_amount"))){

                    //TODO NEED TO CHK THIS CASE

                    postParam1.put("inr_amount", String.format("%.2f",Double.valueOf(getIntent().getStringExtra("inr_amount"))));

                    Log.e("ABCDD", ""+PreferenceFile.getInstance().getPreferenceData(this,Constant.Inr_Amount));
                    Log.e("ABCDD1", ""+String.format("%.2f",Double.valueOf(getIntent().getStringExtra("inr_amount"))));

                    Double totAmt1=Double.parseDouble(getIntent().getStringExtra("inr_amount"))
                            -Double.parseDouble(getIntent().getStringExtra("fee"));

                    Log.e("ABCDD11", ""+Double.parseDouble(getIntent().getStringExtra("inr_amount"))
                            /Double.parseDouble(getIntent().getStringExtra("buy_rate")));

                    Double btcAmt=totAmt1 /Double.parseDouble(getIntent().getStringExtra("buy_rate"));

                    postParam1.put("btc_amount", String.format("%.8f",btcAmt)+"");

                }else{

                    postParam1.put("inr_amount", String.format("%.2f",Double.parseDouble(getIntent().getStringExtra("inr_amount"))
                            +Double.parseDouble(getIntent().getStringExtra("fee"))));

                    Log.e("ABCDDw", ""+PreferenceFile.getInstance().getPreferenceData(this,Constant.Inr_Amount));

                    Log.e("ABCDDw1", ""+String.format("%.2f",Double.valueOf(getIntent().getStringExtra("inr_amount"))));

                    Log.e("ABCDDw11", ""+Double.parseDouble(getIntent().getStringExtra("inr_amount"))
                            /Double.parseDouble(getIntent().getStringExtra("buy_rate")));

                    Double btcAmt=Double.parseDouble(getIntent().getStringExtra("inr_amount"))
                            /Double.parseDouble(getIntent().getStringExtra("buy_rate"));

                    postParam1.put("btc_amount", String.format("%.8f",btcAmt)+"");


                }

                postParam1.put("fee", String.format("%.2f",Double.valueOf(getIntent().getStringExtra("fee"))));
                postParam1.put("gst", String.format("%.2f",Double.valueOf(getIntent().getStringExtra("gst"))));
                postParam1.put("tax", String.format("%.2f",Double.valueOf(getIntent().getStringExtra("charge"))));

                postParam1.put("rate", rate);
                postParam1.put("ip", IPaddress);
                postParam1.put("network", operator);

                postParam1.put("mac_address", macAddress);
                postParam1.put("device_sdk", androidSDK);
                postParam1.put("device_version", androidVersion);
                postParam1.put("device_brand", androidBrand);
                postParam1.put("device_manufacturer", androidManufacturer);
                postParam1.put("device_model", androidModel);
                postParam1.put("lat", String.valueOf(latitude));
                postParam1.put("lng", String.valueOf(longitude));
//                postParam1.put("currency_code",
//                        PreferenceFile.getInstance().getPreferenceData(CheckSecurePin.this, Constant.Currency_Code).trim());

                Log.e("PARAMS", postParam1.toString());

                if (Constant.isConnectingToInternet(CheckSecurePin.this)) {
                    new Retrofit2(CheckSecurePin.this, CheckSecurePin.this, postParam1, Constant.REQ_BUY_BTC, Constant.BUY_BTC, "3").callService(true);
                } else {
                    Constant.alertDialog(CheckSecurePin.this, getResources().getString(R.string.check_connection));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (getIntent().getStringExtra("key").equals("addbitAddress")) {

            JSONObject postParam = new JSONObject();
            try {
                postParam.put("user_id", PreferenceFile.getInstance().getPreferenceData(CheckSecurePin.this, Constant.ID));
                postParam.put("address", getIntent().getStringExtra("address"));
                postParam.put("phone", getIntent().getStringExtra("phone"));
                postParam.put("name",  getIntent().getStringExtra("name"));



                Log.e("postparam--->", postParam.toString());

                if (Constant.isConnectingToInternet(CheckSecurePin.this))
                {
                    Log.e("connect--->", "yes");
                    new Retrofit2(CheckSecurePin.this, CheckSecurePin.this, postParam, Constant.REQ_AddAddress,
                            Constant.AddAddress, "3").callService(true);
                } else
                {
                    Log.e("connect--->", "no");
                    Constant.alertDialog(CheckSecurePin.this, getResources().getString(R.string.check_connection));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (getIntent().getStringExtra("key").equals("removeaddress")) {

            JSONObject postParam = new JSONObject();
            try {

                String id=getIntent().getStringExtra("id");
                postParam.put("user_id", PreferenceFile.getInstance().getPreferenceData(CheckSecurePin.this, Constant.ID));
                postParam.put("id",id);
                //  Log.e("ssid>",""+getIntent().getIntExtra("id",1));
                Log.e("postparamremoveaddress>", postParam.toString());

                if (Constant.isConnectingToInternet(CheckSecurePin.this)) {
                    Log.e("connect--->", "yes");
                    new Retrofit2(CheckSecurePin.this, CheckSecurePin.this, postParam, Constant.REQ_REMOVEBITCOINADDRESS, Constant.REMOVEBITCOINADDRESS, "3").callService(true);
                }
                else {

                    Log.e("connect--->", "no");
                    Constant.alertDialog(CheckSecurePin.this, getResources().getString(R.string.check_connection));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (getIntent().getStringExtra("key").equals("moneytransfer")) {

            JSONObject postParam = new JSONObject();

            try {
                postParam.put("user_id", PreferenceFile.getInstance().getPreferenceData(this, Constant.ID));
                postParam.put("amount", String.format("%.2f", Double.valueOf(getIntent().getStringExtra("amount"))));
                postParam.put("phone", getIntent().getStringExtra("phone"));
                postParam.put("ip", IPaddress);
                postParam.put("network", operator);

                postParam.put("mac_address", macAddress);
                postParam.put("device_sdk", androidSDK);
                postParam.put("device_version", androidVersion);
                postParam.put("device_brand", androidBrand);
                postParam.put("device_manufacturer", androidManufacturer);
                postParam.put("device_model", androidModel);
                postParam.put("lat", String.valueOf(latitude));
                postParam.put("lng", String.valueOf(longitude));
                Log.e("postparam--->", postParam.toString());

                if (Constant.isConnectingToInternet(CheckSecurePin.this)) {
                    Log.e("connect--->", "yes");
                    new Retrofit2(CheckSecurePin.this, CheckSecurePin.this, postParam, Constant.REQ_MONEY_TRANSFER, Constant.MONEY_TRANSFER, "3").callService(true);
                } else {

                    Log.e("connect--->", "no");
                    Constant.alertDialog(CheckSecurePin.this, getResources().getString(R.string.check_connection));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (getIntent().getStringExtra("key").equals("bid")) {

            JSONObject postParam = new JSONObject();
            try {
                postParam.put("user_id", PreferenceFile.getInstance().getPreferenceData(this, Constant.ID));
                Double totAmt=Double.parseDouble(getIntent().getStringExtra("amount"))
                        +Double.parseDouble(getIntent().getStringExtra("gst"))
                        +Double.parseDouble(getIntent().getStringExtra("charge"));
                postParam.put("amount",""+totAmt );
                postParam.put("rate", getIntent().getStringExtra("rate"));
                postParam.put("total_btc", getIntent().getStringExtra("total_btc"));
                postParam.put("fee", getIntent().getStringExtra("fee"));
                postParam.put("gst", getIntent().getStringExtra("gst"));
                postParam.put("tax", getIntent().getStringExtra("charge"));
                postParam.put("previous_amount", PreferenceFile.getInstance().getPreferenceData(this,Constant.Inr_Amount));
                postParam.put("ip", IPaddress);
                postParam.put("network", operator);

                postParam.put("mac_address", macAddress);
                postParam.put("device_sdk", androidSDK);
                postParam.put("device_version", androidVersion);
                postParam.put("device_brand", androidBrand);
                postParam.put("device_manufacturer", androidManufacturer);
                postParam.put("device_model", androidModel);
                postParam.put("lat", String.valueOf(latitude));
                postParam.put("lng", String.valueOf(longitude));
                Log.e("postparamBid--->", postParam.toString());

                if (Constant.isConnectingToInternet(CheckSecurePin.this)) {

                    Log.e("connect--->", "yes");
                    new Retrofit2(CheckSecurePin.this, CheckSecurePin.this, postParam, Constant.REQ_BIDING, Constant.BIDING, "3").callService(true);
                } else {

                    Log.e("connect--->", "no");
                    Constant.alertDialog(CheckSecurePin.this, getResources().getString(R.string.check_connection));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (getIntent().getStringExtra("key").equals("deposit")) {

            map = new HashMap<String, RequestBody>();

            map.put("user_id", RequestBody.create(MediaType.parse("multipart/form-data"), PreferenceFile.getInstance().getPreferenceData(this, Constant.ID)));
            map.put("transaction_id", RequestBody.create(MediaType.parse("multipart/form-data"), getIntent().getStringExtra("transaction_id")));
            map.put("deposit", RequestBody.create(MediaType.parse("multipart/form-data"), getIntent().getStringExtra("deposit")));
            map.put("transaction_date", RequestBody.create(MediaType.parse("multipart/form-data"), getIntent().getStringExtra("transaction_date")));
            map.put("ip", RequestBody.create(MediaType.parse("multipart/form-data"), IPaddress));
            map.put("network", RequestBody.create(MediaType.parse("multipart/form-data"), operator));

            map.put("mac_address",RequestBody.create(MediaType.parse("multipart/form-data"), macAddress));
            map.put("device_sdk", RequestBody.create(MediaType.parse("multipart/form-data"),androidSDK));
            map.put("device_version", RequestBody.create(MediaType.parse("multipart/form-data"),androidVersion));
            map.put("device_brand",RequestBody.create(MediaType.parse("multipart/form-data"), androidBrand));
            map.put("device_manufacturer",RequestBody.create(MediaType.parse("multipart/form-data"), androidManufacturer));
            map.put("device_model",RequestBody.create(MediaType.parse("multipart/form-data"), androidModel));
            Log.e(" user_id-->", PreferenceFile.getInstance().getPreferenceData(this, Constant.ID) + " transaction_id " + getIntent().getStringExtra("transaction_id") + " deposit " + getIntent().getStringExtra("deposit") + " transaction_date " + getIntent().getStringExtra("transaction_date"));
            map.put("lat",RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(latitude)));
            map.put("lng",RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(longitude)));
            RequestBody reqFile = RequestBody.create(MediaType.parse("multipart/form-data"), Deposit.file);
            body = MultipartBody.Part.createFormData("receipt", Deposit.file.getName(), reqFile);

            if (Constant.isConnectingToInternet(CheckSecurePin.this)) {
                Log.e("depositconnect--->", "2");
                new Retrofit2(CheckSecurePin.this, CheckSecurePin.this, map, body, Constant.REQ_Wallet_deposit, Constant.Wallet_deposit, "2").callService(true);
            } else {

                Log.e("depositconnect--->", "no");
                Constant.alertDialog(CheckSecurePin.this, getResources().getString(R.string.check_connection));
            }

        }
    }


    private void checkThirdPartyApi(){

        JSONObject postParam = new JSONObject();
        try {
            postParam.put("user_id", PreferenceFile.getInstance().getPreferenceData(this, Constant.ID));
            postParam.put("amount", getIntent().getStringExtra("amount"));
            postParam.put("to_address", getIntent().getStringExtra("to_address"));

            Log.e("params", postParam.toString());

            if (Constant.isConnectingToInternet(CheckSecurePin.this)) {
                new Retrofit2(CheckSecurePin.this, CheckSecurePin.this, postParam, Constant.REQ_THIRD_PARTY_FEES, Constant.THIRD_PARTY_FEES, "3").callService(true);
            } else {

                Constant.alertDialog(CheckSecurePin.this, getResources().getString(R.string.check_connection));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void sendTransactions(String final_charge,String networkfee){

        JSONObject postParam = new JSONObject();

        Log.e("amountsend-->",getIntent().getStringExtra("amount"));
//
        try {
            postParam.put("user_id", PreferenceFile.getInstance().getPreferenceData(this, Constant.ID));
            postParam.put("amount", getIntent().getStringExtra("amount"));
            postParam.put("charge",networkfee);
            postParam.put("previous_btc_amount", getIntent().getStringExtra("actual_btc"));
            postParam.put("amount_inr", getIntent().getStringExtra("amount_inr"));
            postParam.put("charge_inr", getIntent().getStringExtra("charge_inr"));
            postParam.put("to_address", getIntent().getStringExtra("to_address"));
            postParam.put("final_charge", final_charge);
//            postParam.put("final_charge", getIntent().getStringExtra("final_charge"));
            postParam.put("gst", getIntent().getStringExtra("gst"));
            postParam.put("rate", getIntent().getStringExtra("rate"));
            postParam.put("previous_amount",  PreferenceFile.getInstance().getPreferenceData(this, Constant.BTC_amount));
            postParam.put("from_address", PreferenceFile.getInstance().getPreferenceData(this, Constant.BITCOIN_ADDRESS));
            postParam.put("ip", IPaddress);
            postParam.put("network", operator);

            postParam.put("lat", String.valueOf(latitude));
            postParam.put("lng", String.valueOf(longitude));

            postParam.put("mac_address", macAddress);
            postParam.put("device_sdk", androidSDK);
            postParam.put("device_version", androidVersion);
            postParam.put("device_brand", androidBrand);
            postParam.put("device_manufacturer", androidManufacturer);
            postParam.put("device_model", androidModel);

            Log.e("paramsend--->", postParam.toString());

            if (Constant.isConnectingToInternet(CheckSecurePin.this)) {
                Log.e("connect--->", "yes");
                new Retrofit2(CheckSecurePin.this, CheckSecurePin.this, postParam, Constant.REQ_TRANSFER_BITCOIN, Constant.TRANSFER_BITCOIN, "3").callService(true);
            } else {

                Log.e("connect--->", "no");
                Constant.alertDialog(CheckSecurePin.this, getResources().getString(R.string.check_connection));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.e("onResume-->", "yes");
    }


    public void callfunction() {

        // Constant.hideKeyboard(this, v);

        Log.e("getkey--->", getIntent().getStringExtra("key"));

        confirm_secure_pin = "";
        confirm_secure_pin = text;
        //confirm_secure_pin = edfirst.getText().toString() + edSecond.getText().toString() + edthird.getText().toString() + edFouth.getText().toString();
        Log.e("confirm_secure_pin-->", confirm_secure_pin);

        if (confirm_secure_pin.equals("")) {
            Constant.alertDialog(this, getResources().getString(R.string.please_enter_secure_pin));
        }

        else if (confirm_secure_pin.equals(PreferenceFile.getInstance().getPreferenceData(this, Constant.secure_pin))) {

            PreferenceFile.getInstance().saveData(this,Constant.COUNT_SECURITY,"1");

            if(PreferenceFile.getInstance().getPreferenceData(CheckSecurePin.this,Constant.Lock_Transaction)!=null){

                if(PreferenceFile.getInstance().getPreferenceData(CheckSecurePin.this,Constant.Lock_Transaction).equals("1")){

                    Random r = new Random();
                    int verification = (1000 + r.nextInt(9000));

                    otp=String.valueOf(verification);

                    Log.e("firstotp-->",otp);

                    sender=new Sender("sms.digimiles.in",8000,"di78-bitpay","didimile",
                            "Dear Customer, your OTP is " +otp+" valid for 4 minutes. FIN-CEX will never call you asking for OTP. Sharing your OTP with anyone means you are giving your FIN-CEX access to them.","1","0",PreferenceFile.getInstance().getPreferenceData(this,Constant.phone),"FIN-CEX");

                    TextView txno;
                    dialog=new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                    dialog.setCancelable(false);

                    dialog.setContentView(R.layout.otp_dialog);
                    int width = WindowManager.LayoutParams.MATCH_PARENT;
                    int height = WindowManager.LayoutParams.MATCH_PARENT;
                    dialog.getWindow().setLayout(width, height);
                    //   alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                    params.gravity = Gravity.CENTER;


                     /*TextView txno;
                     LayoutInflater li = LayoutInflater.from(CheckSecurePin.this);
                     View promptsView2 = li.inflate(R.layout.otp_dialog, null);
                     dialog.setContentView(promptsView2);

                   //  dialog.setContentView(R.layout.activity_clock_demo);
                     int width = WindowManager.LayoutParams.WRAP_CONTENT;
                     int height = WindowManager.LayoutParams.WRAP_CONTENT;
                     dialog.getWindow().setLayout(width, height);
                     //   alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                     WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                     params.gravity = Gravity.CENTER;*/

                    //dialog.setCanceledOnTouchOutside(true);
                    dialog.show();

                    txno=(TextView)dialog.findViewById(R.id.txno);
                    // TextView btncancel=(TextView)promptsView2.findViewById(R.id.btncansel);

                    edfirst= (EditText)dialog.findViewById(R.id.edfirst);
                    edSecond= (EditText)dialog.findViewById(R.id.edSecond);
                    edthird= (EditText)dialog.findViewById(R.id.edthird);
                    edFouth= (EditText)dialog.findViewById(R.id.edFouth);
                    chronometer= (TextView)dialog.findViewById(R.id.chronometer);
                    txResend= (TextView)dialog.findViewById(R.id.txResend);
                    txNext= (TextView)dialog.findViewById(R.id.txnext);
                    txback= (TextView) dialog.findViewById(R.id.txback);

                    txback.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            dialog.dismiss();
                            finish();
                        }
                    });

                    calltimer();

                    edfirst.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            if (edfirst.getText().toString().length() == 1)     //size as per your requirement
                            {
                                edSecond.requestFocus();
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable editable)
                        {

                        }
                    });

                    edSecond.addTextChangedListener(new TextWatcher()
                    {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            if (edSecond.getText().toString().length() == 1)     //size as per your requirement
                            {
                                edthird.requestFocus();
                            }

                        }

                        @Override
                        public void afterTextChanged(Editable editable)
                        {

                        }
                    });

                    edthird.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            if (edthird.getText().toString().length() == 1)     //size as per your requirement
                            {
                                edFouth.requestFocus();
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable editable)
                        {

                        }
                    });

                    edfirst.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            if (actionId == EditorInfo.IME_ACTION_DONE) {

                                Constant.hideKeyboard(CheckSecurePin.this,v);
                                callmethod();
                            }
                            return false;
                        }
                    });

                    edSecond.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            if (actionId == EditorInfo.IME_ACTION_DONE) {

                                Constant.hideKeyboard(CheckSecurePin.this,v);
                                callmethod();
                            }
                            return false;
                        }
                    });

                    edthird.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            if (actionId == EditorInfo.IME_ACTION_DONE) {

                                Constant.hideKeyboard(CheckSecurePin.this,v);
                                callmethod();
                            }
                            return false;
                        }
                    });

                    edFouth.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            if (actionId == EditorInfo.IME_ACTION_DONE) {

                                Constant.hideKeyboard(CheckSecurePin.this,v);

                                callmethod();
                            }
                            return false;
                        }
                    });

                    txNext.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            enterotp=edfirst.getText().toString()+edSecond.getText().toString()+edthird.getText().toString()+edFouth.getText().toString();

                            Log.e("otp-->",enterotp+" otp-->"+otp);
                            if(enterotp.length()!=4){

                                Constant.alertDialog(CheckSecurePin.this,"Please fill four digit OTP.");
                            }

                            else if(enterotp.equals(otp)){

                                dialog.dismiss();

                                countDownTimer.cancel();

                                if (getIntent().getStringExtra("key").equals("finger_print")) {
                                    alertDialog(CheckSecurePin.this, "Are you sure to apply the fingerprint?");
                                }
                                if (getIntent().getStringExtra("key").equals("lock_transaction")) {
                                    alertDialog(CheckSecurePin.this, "Are you sure to change lock outgoing transaction status? ");
                                }
                                if (getIntent().getStringExtra("key").equals("withdraw")) {
                                    alertDialogDesc(CheckSecurePin.this, "Are you sure to withdraw money? ",
                                            getIntent().getStringExtra("original_amount"),
                                            getIntent().getStringExtra("charges"),
                                            getIntent().getStringExtra("gst"),
                                            getIntent().getStringExtra("inr_amount"));
                                }
                                if (getIntent().getStringExtra("key").equals("sell")) {

                                    alertDialogBitCoinDesc(CheckSecurePin.this, "Are you sure to sell BitCoins? ",
                                            getIntent().getStringExtra("inr_amount"),
                                            getIntent().getStringExtra("charge"),getIntent().getStringExtra("gst"),getIntent().getStringExtra("btc_amount"),"sell" );


                                }
                                if (getIntent().getStringExtra("key").equals("ask")) {

                                    alertDialogBitCoinDesc(CheckSecurePin.this, "Are you sure to Ask BitCoins? ", getIntent().getStringExtra("sell_amount"),
                                            getIntent().getStringExtra("charge"),getIntent().getStringExtra("gst"),getIntent().getStringExtra("sell_btc"),"ask");
                                    //  alertDialog(CheckSecurePin.this, "Are you sure to Ask BitCoins? ");
                                }
                                if (getIntent().getStringExtra("key").equals("buy")) {
                                    alertDialogBitCoinDesc(CheckSecurePin.this, "Are you sure to Buy BitCoins? ",
                                            getIntent().getStringExtra("inr_amount"),
                                            getIntent().getStringExtra("charge"),
                                            getIntent().getStringExtra("gst"),
                                            getIntent().getStringExtra("btc_amount"),"buy" );

                                }
                                if (getIntent().getStringExtra("key").equals("addbitAddress")) {
                                    alertDialog(CheckSecurePin.this, "Are you sure to Add Bitcoin Address? ");
                                }
                                if (getIntent().getStringExtra("key").equals("moneytransfer")) {
                                    alertDialog(CheckSecurePin.this, "Are you sure to Transfer Money? ");
                                }
                                if (getIntent().getStringExtra("key").equals("bid")) {
                                    alertDialogBitCoinDesc(CheckSecurePin.this, "Are you sure to Bid Bitcoins? ",getIntent().getStringExtra("amount"),
                                            getIntent().getStringExtra("charge"),getIntent().getStringExtra("gst"),getIntent().getStringExtra("total_btc") ,"bid");
                                }

                                if (getIntent().getStringExtra("key").equals("deposit")) {
                                    alertDialog(CheckSecurePin.this, "Are you sure to Deposit? ");
                                }

                                if (getIntent().getStringExtra("key").equals("send")) {

                                    checkThirdPartyApi();

                                }

                                if (getIntent().getStringExtra("key").equals("removeaddress")) {
                                    alertDialog(CheckSecurePin.this, "Are you sure to delete Bitcoin Address? ");
                                }
                            }
                            else {

                                Constant.alertDialog(CheckSecurePin.this,getResources().getString(R.string.otp_not_match));
                            }
                        }
                    });

                    txResend.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            countDownTimer.cancel();
                            txNext.setVisibility(View.VISIBLE);
                            Random r = new Random();
                            int verification = (1000 + r.nextInt(9000));

                            otp=String.valueOf(verification);

                            Log.e("otp-->",otp);

                            sender=new Sender("sms.digimiles.in",8000,"di78-bitpay","didimile","Dear Customer, your OTP is " +otp+" valid for 4 minutes. FIN-CEX will never call you asking for OTP. Sharing your OTP with anyone means you are giving your FIN-CEX access to them.","1","0",PreferenceFile.getInstance().getPreferenceData(CheckSecurePin.this,Constant.phone),"FIN-CEX");

//                sender=new Sender("sms.digimiles.in",8000,"di78-bitpay","didimile",otp,"1","0",number,"FIN-CEX");
                            //  String[] arguments = new String[] {"123"};
                            sender.call();
                            calltimer();
                            txResend.setVisibility(View.GONE);
                        }
                    });

                    SmsReceiver.bindListener(new SmsListener() {
                        @Override
                        public void messageReceived(String messageText) {

                            try {

                                if (messageText.toString().contains("valid")) {
                                    String a[] = messageText.toString().split("valid");
                                    String b[] = a[0].toString().split(" is");
                                    otpReceived = b[1].toString().toString().trim();
                                }

                                edfirst.setText(otpReceived.charAt(0) + "");
                                edSecond.setText(otpReceived.charAt(1) + "");
                                edthird.setText(otpReceived.charAt(2) + "");
                                edFouth.setText(otpReceived.charAt(3) + "");

                                enterotp = edfirst.getText().toString() + edSecond.getText().toString() + edthird.getText().toString() + edFouth.getText().toString();

                                if (enterotp.equals(otp)) {

                                    countDownTimer.cancel();
                                    if (getIntent().getStringExtra("key").equals("finger_print")) {
                                        alertDialog(CheckSecurePin.this, "Are you sure to to apply the fingerprint? ");
                                    }
                                    if (getIntent().getStringExtra("key").equals("withdraw")) {
                                        alertDialogDesc(CheckSecurePin.this, "Are you sure to withdraw money? ",
                                                getIntent().getStringExtra("original_amount"),getIntent().getStringExtra("charges"),getIntent().getStringExtra("gst"), getIntent().getStringExtra("inr_amount"));
                                    }
                                    if (getIntent().getStringExtra("key").equals("sell")) {
                                        alertDialogBitCoinDesc(CheckSecurePin.this, "Are you sure to sell BitCoins? ",getIntent().getStringExtra("inr_amount"),
                                                getIntent().getStringExtra("charge"),getIntent().getStringExtra("gst"),getIntent().getStringExtra("btc_amount"),"sell" );

                                    }
                                    if (getIntent().getStringExtra("key").equals("ask")) {
                                        alertDialogBitCoinDesc(CheckSecurePin.this, "Are you sure to Ask BitCoins? ", getIntent().getStringExtra("sell_amount"),
                                                getIntent().getStringExtra("charge"),getIntent().getStringExtra("gst"),getIntent().getStringExtra("sell_btc"),"ask" );
                                    }
                                    if (getIntent().getStringExtra("key").equals("buy")) {
                                        alertDialogBitCoinDesc(CheckSecurePin.this, "Are you sure to Buy BitCoins? ",getIntent().getStringExtra("inr_amount"),
                                                getIntent().getStringExtra("charge"),
                                                getIntent().getStringExtra("gst"),getIntent().getStringExtra("btc_amount"),"buy" );

                                    }
                                    if (getIntent().getStringExtra("key").equals("addbitAddress")) {
                                        alertDialog(CheckSecurePin.this, "Are you sure to Add Bitcoin Address? ");
                                    }
                                    if (getIntent().getStringExtra("key").equals("moneytransfer")) {
                                        alertDialog(CheckSecurePin.this, "Are you sure to Transfer Money? ");
                                    }
                                    if (getIntent().getStringExtra("key").equals("bid")) {
                                        alertDialogBitCoinDesc(CheckSecurePin.this, "Are you sure to Bid Bitcoins? ",getIntent().getStringExtra("amount"),
                                                getIntent().getStringExtra("charge"),getIntent().getStringExtra("gst"),getIntent().getStringExtra("total_btc"),"bid" );

                                    }

                                    if (getIntent().getStringExtra("key").equals("deposit")) {
                                        alertDialog(CheckSecurePin.this, "Are you sure to Deposit? ");
                                    }

                                    if (getIntent().getStringExtra("key").equals("send")) {
                                        checkThirdPartyApi();
//                                         alertDialogBitCoinDesc(CheckSecurePin.this, "Are you sure to Send BitCoins? "
//                                                 ,getIntent().getStringExtra("amount_inr"),
//                                                 getIntent().getStringExtra("charges"),
//                                                 getIntent().getStringExtra("gst"),
//                                                 getIntent().getStringExtra("amount"),"send" );
                                    }

                                    if (getIntent().getStringExtra("key").equals("removeaddress")) {
                                        alertDialog(CheckSecurePin.this, "Are you sure to delete Bitcoin Address? ");
                                    }

                                } else {

                                    Constant.alertDialog(CheckSecurePin.this, getResources().getString(R.string.otp_not_match));
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    });


                    txno.setText("+"+PreferenceFile.getInstance().getPreferenceData(this,Constant.country_code)+PreferenceFile.getInstance().getPreferenceData(this,Constant.phone));

                }
                else {

                    if (getIntent().getStringExtra("key").equals("finger_print")) {
                        alertDialog(this, "Are you sure to apply the fingerprint? ");
                    }
                    if (getIntent().getStringExtra("key").equals("lock_transaction")) {
                        alertDialog(this, "Are you sure to change lock outgoing transaction status? ");
                    }
                    if (getIntent().getStringExtra("key").equals("withdraw")) {
                        alertDialogDesc(CheckSecurePin.this, "Are you sure to withdraw money? ",
                                getIntent().getStringExtra("original_amount"),getIntent().getStringExtra("charges"),getIntent().getStringExtra("gst"), getIntent().getStringExtra("inr_amount"));

                    }
                    if (getIntent().getStringExtra("key").equals("sell")) {
                        alertDialogBitCoinDesc(CheckSecurePin.this, "Are you sure to sell BitCoins? ",getIntent().getStringExtra("inr_amount"),
                                getIntent().getStringExtra("charge"),getIntent().getStringExtra("gst"),getIntent().getStringExtra("btc_amount"),"sell" );

                    }
                    if (getIntent().getStringExtra("key").equals("ask")) {
                        alertDialogBitCoinDesc(CheckSecurePin.this, "Are you sure to Ask BitCoins? ", getIntent().getStringExtra("sell_amount"),
                                getIntent().getStringExtra("charge"),getIntent().getStringExtra("gst"),getIntent().getStringExtra("sell_btc"),"ask" );
                    }
                    if (getIntent().getStringExtra("key").equals("buy")) {
                        alertDialogBitCoinDesc(CheckSecurePin.this, "Are you sure to Buy BitCoins? ",getIntent().getStringExtra("inr_amount"),
                                getIntent().getStringExtra("charge"),getIntent().getStringExtra("gst"),
                                getIntent().getStringExtra("btc_amount"),"buy");

                    }
                    if (getIntent().getStringExtra("key").equals("addbitAddress")) {
                        alertDialog(this, "Are you sure to Add Bitcoin Address? ");
                    }
                    if (getIntent().getStringExtra("key").equals("moneytransfer")) {
                        alertDialog(this, "Are you sure to Transfer Money? ");
                    }
                    if (getIntent().getStringExtra("key").equals("bid")) {
                        alertDialogBitCoinDesc(CheckSecurePin.this, "Are you sure to Bid Bitcoins? ",getIntent().getStringExtra("amount"),
                                getIntent().getStringExtra("charge"),getIntent().getStringExtra("gst"),getIntent().getStringExtra("total_btc"),"bid" );

                    }

                    if (getIntent().getStringExtra("key").equals("deposit")) {
                        alertDialog(this, "Are you sure to Deposit? ");
                    }

                    if (getIntent().getStringExtra("key").equals("send")) {
                        checkThirdPartyApi();
//                         alertDialogBitCoinDesc(CheckSecurePin.this, "Are you sure to Send BitCoins? "
//                                 ,getIntent().getStringExtra("amount_inr"),
//                                 getIntent().getStringExtra("charges"),
//                                 getIntent().getStringExtra("gst"),
//                                 getIntent().getStringExtra("amount"),"send" );
                    }

                    if (getIntent().getStringExtra("key").equals("removeaddress")) {
                        alertDialog(this, "Are you sure to delete Bitcoin Address? ");
                    }

                }

            }else {

                if (getIntent().getStringExtra("key").equals("finger_print")) {
                    alertDialog(this, "Are you sure to apply the fingerprint? ");
                }
                if (getIntent().getStringExtra("key").equals("lock_transaction")) {
                    alertDialog(this, "Are you sure to change lock outgoing transaction status? ");
                }
                if (getIntent().getStringExtra("key").equals("withdraw")) {
                    alertDialogDesc(CheckSecurePin.this, "Are you sure to withdraw money? ",
                            getIntent().getStringExtra("original_amount"),getIntent().getStringExtra("charges"),getIntent().getStringExtra("gst"), getIntent().getStringExtra("inr_amount"));

                }
                if (getIntent().getStringExtra("key").equals("sell")) {
                    alertDialogBitCoinDesc(CheckSecurePin.this, "Are you sure to sell BitCoins? ",getIntent().getStringExtra("inr_amount"),
                            getIntent().getStringExtra("charge"),getIntent().getStringExtra("gst"),getIntent().getStringExtra("btc_amount"),"sell" );

                }
                if (getIntent().getStringExtra("key").equals("ask")) {
                    alertDialogBitCoinDesc(CheckSecurePin.this, "Are you sure to Ask BitCoins? ", getIntent().getStringExtra("sell_amount"),
                            getIntent().getStringExtra("charge"),getIntent().getStringExtra("gst"),getIntent().getStringExtra("sell_btc"),"ask" );
                }
                if (getIntent().getStringExtra("key").equals("buy")) {
                    alertDialogBitCoinDesc(CheckSecurePin.this, "Are you sure to Buy BitCoins? ",getIntent().getStringExtra("inr_amount"),
                            getIntent().getStringExtra("charge"),getIntent().getStringExtra("gst"),
                            getIntent().getStringExtra("btc_amount"),"buy" );

                }
                if (getIntent().getStringExtra("key").equals("addbitAddress")) {
                    alertDialog(this, "Are you sure to Add Bitcoin Address? ");
                }
                if (getIntent().getStringExtra("key").equals("moneytransfer")) {
                    alertDialog(this, "Are you sure to Transfer Money? ");
                }
                if (getIntent().getStringExtra("key").equals("bid")) {
                    alertDialogBitCoinDesc(CheckSecurePin.this, "Are you sure to Bid Bitcoins? ",getIntent().getStringExtra("amount"),
                            getIntent().getStringExtra("charge"),getIntent().getStringExtra("gst"),getIntent().getStringExtra("total_btc"),"bid" );

                }

                if (getIntent().getStringExtra("key").equals("deposit")) {
                    alertDialog(this, "Are you sure to Deposit? ");
                }

                if (getIntent().getStringExtra("key").equals("send")) {
                    checkThirdPartyApi();
//                     alertDialogBitCoinDesc(CheckSecurePin.this, "Are you sure to Send BitCoins? "
//                             ,getIntent().getStringExtra("amount_inr"),
//                             getIntent().getStringExtra("charges"),
//                             getIntent().getStringExtra("gst"),
//                             getIntent().getStringExtra("amount"),"send");
                }

                if (getIntent().getStringExtra("key").equals("removeaddress")) {
                    alertDialog(this, "Are you sure to delete Bitcoin Address? ");
                }
            }
        }
        else {

            txtPinEntry.setText(null);
            num.delete(0, num.length());

            if (PreferenceFile.getInstance().getPreferenceData(this, Constant.COUNT_SECURITY) == null) {
                PreferenceFile.getInstance().saveData(this, Constant.COUNT_SECURITY, "1");

                int count= Integer.parseInt(PreferenceFile.getInstance().getPreferenceData(this,Constant.COUNT_SECURITY));

                lnLayerforgot.setVisibility(View.VISIBLE);
                count = 4 - count;

                if(count==1){
                    Log.e("count-->",count+"yes");
                    Constant.alertDialog(this,"Warning! This is your Last attempts otherwise your account has been block.");
                    tvattempts.setText(count + " attempts remaining. ");
                }
                else {

                    tvattempts.setText(count + " attempts remaining. ");
                }

                //tvattempts.setText(count + " attempts remaining. ");

            } else {
                x = Integer.parseInt(PreferenceFile.getInstance().getPreferenceData(this, Constant.COUNT_SECURITY));
                x++;
                PreferenceFile.getInstance().saveData(this, Constant.COUNT_SECURITY, String.valueOf(x));

                int count= Integer.parseInt(PreferenceFile.getInstance().getPreferenceData(this,Constant.COUNT_SECURITY));



                lnLayerforgot.setVisibility(View.VISIBLE);
                count = 4 - count;
                if(count==1){
                    Log.e("count-->",count+"yes");
                    Constant.alertDialog(this,"Warning! This is your Last attempts otherwise your account has been block.");
                    tvattempts.setText(count + " attempts remaining. ");
                }
                else {

                    tvattempts.setText(count + " attempts remaining. ");
                }

                Log.e("x-->", x + "");
            }

            if (Integer.parseInt(PreferenceFile.getInstance().getPreferenceData(this, Constant.COUNT_SECURITY)) == 4) {

                JSONObject postParam = new JSONObject();
                try {
                    postParam.put("user_id", PreferenceFile.getInstance().getPreferenceData(this, Constant.ID));
                    postParam.put("phone", PreferenceFile.getInstance().getPreferenceData(this, Constant.phone));
                    Log.e("postparam--->", postParam.toString());

                    if (Constant.isConnectingToInternet(CheckSecurePin.this)) {
                        Log.e("connect--->", "yes");
                        new Retrofit2(CheckSecurePin.this, CheckSecurePin.this, postParam, Constant.REQ_Block_USER, Constant.Block_USER, "3").callService(true);
                    } else {

                        Log.e("connect--->", "no");
                        Constant.alertDialog(CheckSecurePin.this, getResources().getString(R.string.check_connection));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.e("regenrate secure-->", x + "");
            } else {
                Constant.alertDialog(this, getResources().getString(R.string.incorrect_secure_pin));
            }
        }

    }

    void callmethod() {

        enterotp=edfirst.getText().toString()+edSecond.getText().toString()+edthird.getText().toString()+edFouth.getText().toString();

        if(enterotp.equals(otp)){

            dialog.dismiss();

            countDownTimer.cancel();
            if (getIntent().getStringExtra("key").equals("finger_print")) {
                alertDialog(CheckSecurePin.this, "Are you sure to apply the fingerprint? ");
            }
            if (getIntent().getStringExtra("key").equals("lock_transaction")) {
                alertDialog(CheckSecurePin.this, "Are you sure to change lock outgoing transaction status? ");
            }
            if (getIntent().getStringExtra("key").equals("withdraw")) {
                alertDialogDesc(CheckSecurePin.this, "Are you sure to withdraw money? ",
                        getIntent().getStringExtra("original_amount"),getIntent().getStringExtra("charges"),getIntent().getStringExtra("gst"), getIntent().getStringExtra("inr_amount"));

            }
            if (getIntent().getStringExtra("key").equals("sell")) {
                alertDialogBitCoinDesc(CheckSecurePin.this, "Are you sure to sell BitCoins? ",getIntent().getStringExtra("inr_amount"),
                        getIntent().getStringExtra("charge"),getIntent().getStringExtra("gst"),getIntent().getStringExtra("btc_amount"),"sell" );

            }
            if (getIntent().getStringExtra("key").equals("ask")) {
                alertDialogBitCoinDesc(CheckSecurePin.this, "Are you sure to Ask BitCoins? ", getIntent().getStringExtra("sell_amount"),
                        getIntent().getStringExtra("charge"),getIntent().getStringExtra("gst"),getIntent().getStringExtra("sell_btc") ,"ask");
            }
            if (getIntent().getStringExtra("key").equals("buy")) {
                alertDialogBitCoinDesc(CheckSecurePin.this, "Are you sure to Buy BitCoins? ",getIntent().getStringExtra("inr_amount"),
                        getIntent().getStringExtra("charge"),getIntent().getStringExtra("gst"),
                        getIntent().getStringExtra("btc_amount"),"buy" );

            }
            if (getIntent().getStringExtra("key").equals("addbitAddress")) {
                alertDialog(CheckSecurePin.this, "Are you sure to Add Bitcoin Address? ");
            }
            if (getIntent().getStringExtra("key").equals("moneytransfer")) {
                alertDialog(CheckSecurePin.this, "Are you sure to Transfer Money? ");
            }
            if (getIntent().getStringExtra("key").equals("bid")) {
                alertDialogBitCoinDesc(CheckSecurePin.this, "Are you sure to Bid Bitcoins? ",getIntent().getStringExtra("amount"),
                        getIntent().getStringExtra("charge"),getIntent().getStringExtra("gst"),getIntent().getStringExtra("total_btc") ,"bid");

            }
            if (getIntent().getStringExtra("key").equals("deposit")) {
                alertDialog(CheckSecurePin.this, "Are you sure to Deposit? ");
            }
            if (getIntent().getStringExtra("key").equals("send")) {
                checkThirdPartyApi();
//                alertDialogBitCoinDesc(CheckSecurePin.this, "Are you sure to Send BitCoins? "
//                        ,getIntent().getStringExtra("amount_inr"),
//                        getIntent().getStringExtra("charges"),
//                        getIntent().getStringExtra("gst"),
//                        getIntent().getStringExtra("amount"),"send" );
            }
            if (getIntent().getStringExtra("key").equals("removeaddress")) {
                alertDialog(CheckSecurePin.this, "Are you sure to delete Bitcoin Address? ");
            }
        }
    }


    @Override
    public void onServiceResponse(int requestCode, Response<ResponseBody> response) {

        switch (requestCode) {

            case Constant.REQ_THIRD_PARTY_FEES:
                try {
                    JSONObject result = new JSONObject(response.body().string());
                    Log.e("Result",result.toString());
                    String response1 = result.getString("response");
                    if(response1.equalsIgnoreCase("true")){

                        JSONObject data=result.getJSONObject("data");
                        String status=data.getString("status");

                        if(status.equalsIgnoreCase("success")) {

                            JSONObject data1 = data.getJSONObject("data");
                            double netFee = Double.parseDouble(data1.getString("estimated_network_fee"))
                                    * Double.parseDouble(getIntent().getStringExtra("rate"));
                            Log.e("netFee", netFee + "");
                            alertDialogBitCoinDesc(CheckSecurePin.this, "Are you sure to Send BitCoins? "
                                    ,getIntent().getStringExtra("amount_inr"),
                                    String.valueOf(String.format("%.0f", netFee)),
                                    getIntent().getStringExtra("gst"),
                                    getIntent().getStringExtra("amount"),"send" );
//                            sendTransactions(true, netFee);

                        }else  if(status.equalsIgnoreCase("fail")) {
                            JSONObject data1 = data.getJSONObject("data");
                            alertDialogSimple(CheckSecurePin.this,data1.getString("error_message"));
                        }

                    }else{

                        alertDialogBitCoinDesc(CheckSecurePin.this, "Are you sure to Send BitCoins? "
                                ,getIntent().getStringExtra("amount_inr"),
                                getIntent().getStringExtra("charges"),
                                getIntent().getStringExtra("gst"),
                                getIntent().getStringExtra("amount"),"send" );
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

                break;


            case Constant.REQ_TRANSACTION_LOG:

                try {

                    JSONObject result = new JSONObject(response.body().string());
                    Log.e("resultpri--->",result.toString());

                    String status = result.getString("response");
                    String message = result.getString("message");

                    if (status.equals("true")) {

                        PreferenceFile.getInstance().saveData(this,Constant.COUNT_SECURITY,"1");
                        PreferenceFile.getInstance().saveData(CheckSecurePin.this,Constant.Lock_Transaction,result.getString("data"));
                        //  Constant.alertWithIntent(this,"Successfully updates.",Setting.class);

                        Constant.alertWithIntent(CheckSecurePin.this,message,Dashboard.class);
                    }
                    else {
                        Constant.alertWithIntent(CheckSecurePin.this,message,Dashboard.class);
                    }

                } catch (JSONException e) {

                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;

            case Constant.REQ_TRANSFER_BITCOIN:

                try {

                    JSONObject result = new JSONObject(response.body().string());
                    Log.e("result--->",result.toString());

                    String status = result.getString("response");
                    String message = result.getString("message");

                    if (status.equals("true")) {

                        PreferenceFile.getInstance().saveData(this,Constant.COUNT_SECURITY,"1");

                        Constant.alertWithIntent(CheckSecurePin.this,message,Dashboard.class);
                    }
                    else {
                        Constant.alertWithIntent(CheckSecurePin.this,message,Dashboard.class);
                    }

                } catch (JSONException e) {

                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;

            case Constant.REQ_AddAddress:
                if (response.isSuccessful()) {

                    try {

                        JSONObject result = new JSONObject(response.body().string());

                        String status = result.getString("response");
                        String message = result.getString("message");

                        if (status.equals("true")) {

                            final Dialog dialog = new Dialog(CheckSecurePin.this);
                            dialog.setCancelable(false);
                            dialog.setContentView(R.layout.simple_alert);

                            int width = WindowManager.LayoutParams.MATCH_PARENT;
                            int height = WindowManager.LayoutParams.WRAP_CONTENT;
                            dialog.getWindow().setLayout(width, height);
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                            params.gravity = Gravity.CENTER;
                            dialog.setCanceledOnTouchOutside(true);
                            dialog.show();

                            TextView  tvText = (TextView) dialog.findViewById(R.id.tvText);
                            TextView btnok = (TextView) dialog.findViewById(R.id.btnok);

                            tvText.setText(message);

                            btnok.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                    Intent intent=new Intent(CheckSecurePin.this,BitcoinAddressAddedList.class);
                                    intent.putExtra("key","send");
                                    intent.putExtra("amount", getIntent().getStringExtra("amount"));
                                    intent.putExtra("amount_inr", getIntent().getStringExtra("amount_inr"));
                                    intent.putExtra("charge", getIntent().getStringExtra("charge"));
                                    intent.putExtra("charge_inr", getIntent().getStringExtra("charge_inr"));
                                    intent.putExtra("gst",getIntent().getStringExtra("gst"));
                                    intent.putExtra("final_charge", getIntent().getStringExtra("final_charge"));
                                    intent.putExtra("rate", getIntent().getStringExtra("rate"));
                                    intent.putExtra("actual_btc", getIntent().getStringExtra("actual_btc"));
                                    intent.putExtra("previous_amount", PreferenceFile.getInstance().getPreferenceData(CheckSecurePin.this,Constant.Inr_Amount));
                                    startActivity(intent);
                                }
                            });

                        }
                        else
                        {
                            Constant.alertWithIntent(CheckSecurePin.this,message,Dashboard.class);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {

                    }
                } else {
                    Constant.alertDialog(this, getResources().getString(R.string.try_again));
                }

                break;

            case Constant.REQ_Left_balance:


                if (response.isSuccessful()) {

                    try {

                        JSONObject result = new JSONObject(response.body().string());

                        String status = result.getString("response");
                        String message = result.getString("message");
                        if (status.equals("true")) {
                            JSONObject data=result.getJSONObject("data");
                            JSONObject data1=data.getJSONObject("data");

                            PreferenceFile.getInstance().saveData(this,Constant.Inr_Amount,data1.getString("available_balance"));
                            Constant.alertWithIntent(this, "BTC transfer successfully.",Dashboard.class);

                        } else
                        {
                            Constant.alertDialog(this, message);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {

                    }
                } else {
                    Constant.alertDialog(this, getResources().getString(R.string.try_again));
                }

                break;

            case Constant.REQ_Block_USER:

                if (response.isSuccessful()) {

                    JSONObject result1 = null;
                    try {
                        result1 = new JSONObject(response.body().string());

                        String status = result1.getString("response");
                        String message = result1.getString("message");

                        PreferenceFile.getInstance().saveData(this,Constant.COUNT_SECURITY,"4");
                        PreferenceFile.getInstance().saveData(this,Constant.Accunt_status,"Inactive");

                        Constant.alertWithIntent(this,"Your Account has beed Blocked.",BlockScreen.class);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            case Constant.REQ_MONEY_TRANSFER:

                PreferenceFile.getInstance().saveData(this,Constant.COUNT_SECURITY,"1");

                try {

                    JSONObject result = new JSONObject(response.body().string());
                    Log.e("result--->",result.toString());

                    String status = result.getString("response");
                    String message = result.getString("message");

                    if (status.equals("true")) {
                        JSONObject data=result.getJSONObject("data");
                        JSONObject transfer_detail=data.getJSONObject("transfer_detail");
                        JSONObject user_detail=data.getJSONObject("user_detail");

                        order_id=transfer_detail.getString("id");

                        PreferenceFile.getInstance().saveData(this,Constant.BTC_amount,user_detail.getString("btc_amount"));

                        final Dialog dialog = new Dialog(CheckSecurePin.this);
                        dialog.setCancelable(false);
                        dialog.setContentView(R.layout.simple_alert);

                        int width = WindowManager.LayoutParams.MATCH_PARENT;
                        int height = WindowManager.LayoutParams.WRAP_CONTENT;
                        dialog.getWindow().setLayout(width, height);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                        params.gravity = Gravity.CENTER;
                        dialog.setCanceledOnTouchOutside(true);
                        dialog.show();

                        TextView  tvText = (TextView) dialog.findViewById(R.id.tvText);
                        TextView btnok = (TextView) dialog.findViewById(R.id.btnok);

                        tvText.setText(message);

                        btnok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                                Intent intent=new Intent(CheckSecurePin.this,Dashboard.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.putExtra("key","moneyTransfer");
                                intent.putExtra("order_id",order_id);
                                startActivity(intent);
                            }
                        });
                    }
                    else {

                        Constant.alertWithIntent(this,message,Dashboard.class);
                    }

                } catch (JSONException e) {

                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;


            case Constant.REQ_Withdraw:
                PreferenceFile.getInstance().saveData(this,Constant.COUNT_SECURITY,"1");
                if (response.isSuccessful()) {

                    try {

                        JSONObject result = new JSONObject(response.body().string());
                        String status = result.getString("response");
                        String message = result.getString("message");
                        if (status.equals("true")) {

                            JSONObject data = result.getJSONObject("data");
                            Constant.alertWithIntent(this, message,Dashboard.class);

                        } else {
                            Constant.alertDialog(this, message);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {

                    }
                } else {
                    Constant.alertDialog(this, getResources().getString(R.string.try_again));
                }

                break;

            case Constant.REQ_Ask:

                PreferenceFile.getInstance().saveData(this,Constant.COUNT_SECURITY,"1");

                try {
                    JSONObject result = new JSONObject(response.body().string());
                    PreferenceFile.getInstance().saveData(this,Constant.COUNT_SECURITY,"1");
                    Log.e("result-->", result.toString());
                    String status = result.getString("response");
                    String message = result.getString("message");

                    if (status.equals("true")) {

                        final Dialog dialog = new Dialog(CheckSecurePin.this);
                        dialog.setCancelable(false);
                        dialog.setContentView(R.layout.simple_alert);

                        int width = WindowManager.LayoutParams.MATCH_PARENT;
                        int height = WindowManager.LayoutParams.WRAP_CONTENT;
                        dialog.getWindow().setLayout(width, height);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                        params.gravity = Gravity.CENTER;
                        dialog.setCanceledOnTouchOutside(true);
                        dialog.show();

                        TextView  tvText = (TextView) dialog.findViewById(R.id.tvText);
                        TextView btnok = (TextView) dialog.findViewById(R.id.btnok);

                        tvText.setText(message);

                        btnok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                                finish();
                                Intent intent=new Intent(CheckSecurePin.this,Dashboard.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        });

                    }
                    else {

                        Constant.alertDialog(this,message);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;

            case Constant.REQ_SELL_BTC:

                PreferenceFile.getInstance().saveData(this,Constant.COUNT_SECURITY,"1");
                if (response.isSuccessful()) {

                    try {

                        JSONObject result = new JSONObject(response.body().string());
                        PreferenceFile.getInstance().saveData(this,Constant.COUNT_SECURITY,"1");
                        Log.e("result-->", result.toString());
                        String status = result.getString("response");
                        String message = result.getString("message");
                        if (status.equals("true")) {

                            JSONObject data = result.getJSONObject("data");
                            JSONObject sell_detail = data.getJSONObject("sell_detail");
                            JSONObject user_detail = data.getJSONObject("user_detail");

                            PreferenceFile.getInstance().saveData(this,Constant.Inr_Amount,user_detail.getString("inr_amount"));

                            order_id=sell_detail.getString("id");

                            final Dialog dialog = new Dialog(CheckSecurePin.this);
                            dialog.setCancelable(false);
                            dialog.setContentView(R.layout.simple_alert);

                            int width = WindowManager.LayoutParams.MATCH_PARENT;
                            int height = WindowManager.LayoutParams.WRAP_CONTENT;
                            dialog.getWindow().setLayout(width, height);
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                            params.gravity = Gravity.CENTER;
                            dialog.setCanceledOnTouchOutside(true);
                            dialog.show();

                            TextView  tvText = (TextView) dialog.findViewById(R.id.tvText);
                            TextView btnok = (TextView) dialog.findViewById(R.id.btnok);

                            tvText.setText(message);

                            btnok.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                    Intent intent=new Intent(CheckSecurePin.this,Invoice.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent.putExtra("key","sell");
                                    intent.putExtra("order_id",order_id);
                                    startActivity(intent);
                                }
                            });
                        } else {
                            Constant.alertDialog(this, message);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {

                    }
                } else {
                    Constant.alertDialog(this, getResources().getString(R.string.try_again));
                }
                break;


            case Constant.REQ_BIDING:

                PreferenceFile.getInstance().saveData(this,Constant.COUNT_SECURITY,"1");

                try {
                    JSONObject result = new JSONObject(response.body().string());
                    PreferenceFile.getInstance().saveData(this,Constant.COUNT_SECURITY,"1");
                    Log.e("result-->", result.toString());
                    String status = result.getString("response");
                    String message = result.getString("message");

                    if (status.equals("true")) {

                        final Dialog dialog = new Dialog(CheckSecurePin.this);
                        dialog.setCancelable(false);
                        dialog.setContentView(R.layout.simple_alert);

                        int width = WindowManager.LayoutParams.MATCH_PARENT;
                        int height = WindowManager.LayoutParams.WRAP_CONTENT;
                        dialog.getWindow().setLayout(width, height);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                        params.gravity = Gravity.CENTER;
                        dialog.setCanceledOnTouchOutside(true);
                        dialog.show();

                        TextView  tvText = (TextView) dialog.findViewById(R.id.tvText);
                        TextView btnok = (TextView) dialog.findViewById(R.id.btnok);

                        tvText.setText(message);

                        btnok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                                finish();
                                Intent intent=new Intent(CheckSecurePin.this,Dashboard.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        });
                    }
                    else {

                        Constant.alertDialog(this,message);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;

            case Constant.REQ_BUY_BTC:

                PreferenceFile.getInstance().saveData(this,Constant.COUNT_SECURITY,"1");

                if (response.isSuccessful()) {
                    try {

                        JSONObject result = new JSONObject(response.body().string());
                        PreferenceFile.getInstance().saveData(this,Constant.COUNT_SECURITY,"1");

                        Log.e("result-->", result.toString());
                        String status = result.getString("response");
                        String message = result.getString("message");
                        if (status.equals("true")) {

                            JSONObject data = result.getJSONObject("data");
                            JSONObject buy_detail = data.getJSONObject("buy_detail");
                            JSONObject user_detail = data.getJSONObject("user_detail");
                            order_id=buy_detail.getString("id");
                            Log.e("data--->", data.toString());
                            Log.e("order_id--->", order_id);
                            // double inr = Double.parseDouble(PreferenceFile.getInstance().getPreferenceData(this,Constant.BTC_amount))-Double.parseDouble(edIfsc.getText().toString());

                            PreferenceFile.getInstance().saveData(this,Constant.Inr_Amount,user_detail.getString("inr_amount"));


                            final Dialog dialog = new Dialog(CheckSecurePin.this);

//        dialog.setTitle("FIN-CEX");
                            dialog.setCancelable(false);

                            dialog.setContentView(R.layout.simple_alert);


                            int width = WindowManager.LayoutParams.MATCH_PARENT;
                            int height = WindowManager.LayoutParams.WRAP_CONTENT;
                            dialog.getWindow().setLayout(width, height);
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                            params.gravity = Gravity.CENTER;



                            dialog.setCanceledOnTouchOutside(true);
                            dialog.show();

                            TextView  tvText = (TextView) dialog.findViewById(R.id.tvText);
                            TextView btnok = (TextView) dialog.findViewById(R.id.btnok);

                            tvText.setText(message);

                            btnok.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                    Intent intent=new Intent(CheckSecurePin.this,Invoice.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent.putExtra("key","buy");
                                    intent.putExtra("order_id",order_id);
                                    startActivity(intent);
                                }
                            });



                        }
                        else {
                            Constant.alertDialog(this, message);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {

                    }
                } else {
                    Constant.alertDialog(this, getResources().getString(R.string.try_again));
                }

                break;

            case Constant.REQ_REMOVEBITCOINADDRESS:
                if (response.isSuccessful()) {

                    try {

                        JSONObject result = new JSONObject(response.body().string());

                        Log.e("result-->", result.toString());
                        Log.e("delete","yes");
                        String status = result.getString("response");
                        String message = result.getString("message");

                        if (status.equals("true")) {
                            Constant.alertWithIntent(this,message,Dashboard.class);

                        }
                        else
                        {
                            Constant.alertDialog(this,message);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {

                    }
                } else {
                    Constant.alertDialog(this, getResources().getString(R.string.try_again));
                }

                break;


            case Constant.REQ_Wallet_deposit:

                PreferenceFile.getInstance().saveData(this,Constant.COUNT_SECURITY,"1");
                try {
                    JSONObject result1 = new JSONObject(response.body().string());
                    PreferenceFile.getInstance().saveData(this,Constant.COUNT_SECURITY,"1");

                    String status=result1.getString("response");
                    String message=result1.getString("message");

                    Log.e("result-->",result1.toString());

                    if(status.equals("true")){

                        final Dialog dialog = new Dialog(CheckSecurePin.this);
                        dialog.setCancelable(false);
                        dialog.setContentView(R.layout.simple_alert);

                        int width = WindowManager.LayoutParams.MATCH_PARENT;
                        int height = WindowManager.LayoutParams.WRAP_CONTENT;
                        dialog.getWindow().setLayout(width, height);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                        params.gravity = Gravity.CENTER;
                        dialog.setCanceledOnTouchOutside(true);
                        dialog.show();

                        TextView  tvText = (TextView) dialog.findViewById(R.id.tvText);
                        TextView btnok = (TextView) dialog.findViewById(R.id.btnok);

                        tvText.setText(message);

                        btnok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                                finish();
                                Intent intent=new Intent(CheckSecurePin.this,Dashboard.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        });

                    }
                    else {
                        Constant.alertDialog(this,message);
                    }

                } catch (JSONException e) {

                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;


        }

    }




}
