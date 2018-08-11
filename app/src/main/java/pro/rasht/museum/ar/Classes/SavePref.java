package pro.rasht.museum.ar.Classes;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Maziar on 3/23/2018.
 */

public class SavePref {
    private final String pref_name = "ir.loper.neginsabz.Network";
    private SharedPreferences sharedPref;

    public 	SavePref(Context context){
        sharedPref = context.getSharedPreferences(pref_name, Context.MODE_PRIVATE);
    }
    public  void save(String key, int value) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public void save(String key, boolean value) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public void save(String key, float value) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putFloat(key, value);
        editor.commit();
    }

    public void save(String key, String value) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.commit();
    }


    public String load(String key, String DefaultValue) {
        return sharedPref.getString(key, DefaultValue).toString();
    }

    public boolean load(String key, boolean DefaultValue) {
        return sharedPref.getBoolean(key, DefaultValue);
    }

    public float load(String key, float DefaultValue) {
        return sharedPref.getFloat(key, DefaultValue);
    }

    public int load(String key, int DefaultValue) {
        return sharedPref.getInt(key, DefaultValue);
    }

    public long load(String key, long DefaultValue) {
        return sharedPref.getLong(key, DefaultValue);
    }

}
