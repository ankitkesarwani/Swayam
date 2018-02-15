package com.example.kesar.swayam;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by kesar on 2/15/2018.
 */

public class IntroManager {

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    Context context;

    public IntroManager(Context context) {

        this.context = context;
        pref = context.getSharedPreferences("first", 0);
        editor = pref.edit();

    }

    public void setFirst(boolean isFirst) {

        editor.putBoolean("check", isFirst);
        editor.commit();

    }

    public boolean Check() {

        return pref.getBoolean("check", true);

    }

}
