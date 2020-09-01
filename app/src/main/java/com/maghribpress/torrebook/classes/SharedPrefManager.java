package com.maghribpress.torrebook.classes;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.v7.preference.PreferenceManager;

public class SharedPrefManager {
    public static final String SP_TOKEN = "spToken";
    public static final String SP_LOGIN_STATE = "spLoginState";
    public static final String SP_DISCLAIMER_STATE = "spDisclaimerState";
    public static final String SP_NAME = "spName";
    public static final String SP_EMAIL = "spEmail";
    public static final String save_directory_preference = "save_directory_preference";
    public static final String SP_AVATAR = "spAvatar";
    SharedPreferences sp;
    SharedPreferences.Editor spEditor;
    Context mcontext;
    public SharedPrefManager(Context context){
        this.mcontext = context;
        sp = PreferenceManager.getDefaultSharedPreferences(mcontext);
        spEditor = sp.edit();
    }
    public void saveToken(String token) {
        spEditor.putString(SP_TOKEN, token);
        spEditor.commit();
    }
    public String getToken() {
        return sp.getString(SP_TOKEN, "");
    }
    public void saveLoginState(boolean state){
        spEditor.putBoolean(SP_LOGIN_STATE, state);
        spEditor.commit();
    }
    public boolean getLoginState() {
        return sp.getBoolean(SP_LOGIN_STATE, false);
    }
    public void saveName(String name) {
        spEditor.putString(SP_NAME, name);
        spEditor.commit();
    }
    public String getName() {
        return sp.getString(SP_NAME, "");
    }
    public void saveEmail(String email) {
        spEditor.putString(SP_EMAIL, email);
        spEditor.commit();
    }
    public String getEmail() {
        return sp.getString(SP_EMAIL, "");
    }
    public String getSaveDirectory() {
        return sp.getString(save_directory_preference, String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()));
    }
    public boolean getDisclaimerstate() {
        return sp.getBoolean(SP_DISCLAIMER_STATE, false);
    }
    public void setDisclaimerState(boolean seen) {
        spEditor.putBoolean(SP_DISCLAIMER_STATE, seen);
        spEditor.commit();
    }
    public void setAvatar(String avatar) {
        spEditor.putString(SP_AVATAR, avatar);
        spEditor.commit();
    }
    public String getAvatar() {
        return sp.getString(SP_AVATAR, "");
    }
}
