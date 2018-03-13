package utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by kesar on 3/13/2018.
 */

public class SharedPrefManager {

    Context context;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    public static final String FILE_NAME="swayamSharedPref";
    public static final String KEY_LANG="language";

    public SharedPrefManager(Context context) {
        this.context = context;
        sharedPreferences=context.getSharedPreferences(FILE_NAME,Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();
    }

    public void setLanguage(String lang){
        editor.putString(KEY_LANG,lang);
        editor.commit();
    }

    public String getLanguage(){
        return sharedPreferences.getString(KEY_LANG,"en");
    }

}
