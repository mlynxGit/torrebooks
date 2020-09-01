package com.maghribpress.torrebook.classes;

public class ApiTokenObject {
    private static ApiTokenObject INSTANCE = null;
    private static String mToken=null;
    private static String mEmail=null;
    private static String mUsername=null;
    private static String mAvatar=null;
    private ApiTokenObject() {};

    public static ApiTokenObject getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ApiTokenObject();
        }
        return(INSTANCE);
    }

    public  String getmEmail() {
        return mEmail;
    }

    public  void setmEmail(String mEmail) {
        ApiTokenObject.mEmail = mEmail;
    }

    public  String getmUsername() {
        return mUsername;
    }

    public  void setmUsername(String mUsername) {
        ApiTokenObject.mUsername = mUsername;
    }

    public void setToken(String token) {
        mToken =token;

    }
    public String getToken() {
            return mToken;
    }
    public void setAvatar(String avatar) {
        mAvatar = avatar;
    }
    public String getAvatar() {
        return mAvatar;
    }
}
