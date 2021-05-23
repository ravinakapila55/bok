package com.app.tigerpay.Notifications;

import android.content.SharedPreferences;
import android.util.Log;

import com.app.tigerpay.Util.App;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;





public class FirebaseID extends FirebaseInstanceIdService
{
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String refToken;

    @Override
    public void onTokenRefresh() {
        Log.e("class call","yes");
        refToken= FirebaseInstanceId.getInstance().getToken();
        Log.e("priyarefToken",refToken);
        sharedPreferences= App.getIdPref();
        editor=sharedPreferences.edit();
        editor.putString("TOKEN",refToken);
        editor.commit();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
