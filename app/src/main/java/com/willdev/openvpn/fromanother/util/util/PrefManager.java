package com.willdev.openvpn.fromanother.util.util;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;


    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "video";
    private static final String FIRST_OPEN = "first_open";

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setFirstOpen(boolean isFirstTime) {
        editor.putBoolean(FIRST_OPEN, isFirstTime);
        editor.commit();
    }

    public boolean isFirstOpen() {
        return pref.getBoolean(FIRST_OPEN, true);
    }

}
