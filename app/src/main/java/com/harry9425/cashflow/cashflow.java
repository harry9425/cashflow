package com.harry9425.cashflow;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class cashflow extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
