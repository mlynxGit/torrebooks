package com.maghribpress.torrebook.classes;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.View;

import com.maghribpress.torrebook.db.entity.Book;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.util.Base64.DEFAULT;

public class Functions {
    public static boolean isEmailValid(String email)
    {
        String regExpn =
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                        +"((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        +"([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                        +"([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(regExpn,Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        if(matcher.matches())
            return true;
        else
            return false;
    }
    public static Activity getActivity(View view) {
        Context context = view.getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity)context;
            }
            context = ((ContextWrapper)context).getBaseContext();
        }
        return null;
    }
    public static String toJSon(Book book) {
        try {
            // Here we convert Java Object to JSON
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("title", book.getTitle()); // Set the first name/pair
            jsonObj.put("created_at", book.getCreated_at());
            jsonObj.put("base64cover",bitmapToBase64(book.getCover()));
            return jsonObj.toString();
        }
        catch(JSONException ex) {
            ex.printStackTrace();
        }
        return null;
    }
    public static List<Book> fromJson(String json) {
        try {

            List<Book> _books = new ArrayList<>();
            org.json.JSONArray jArray = new org.json.JSONArray(json);
            for(int i=0;i<jArray.length();i++) {
                Book _book = new Book();
                org.json.JSONObject jObj = jArray.getJSONObject(i);
                if(jObj.has("title")) {
                    _book.setTitle(jObj.getString("title"));
                }
                if(jObj.has("created_at")) {
                    _book.setCreated_at(jObj.getLong("created_at"));
                }
                if(jObj.has("base64cover")) {
                    _book.setCover(base64ToBitmap(jObj.getString("base64cover")));
                }
                _books.add(_book);
            }
            return _books;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos); //bm is the bitmap object
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, DEFAULT);
    }
    public static Bitmap base64ToBitmap(String base64) {
        byte[] imageAsBytes = Base64.decode(base64.getBytes(),DEFAULT);
       return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
    }
    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }
}
