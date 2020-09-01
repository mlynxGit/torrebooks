package com.maghribpress.torrebook.db.converter;

import android.arch.persistence.room.TypeConverter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

public class BitmapConverter {
    @TypeConverter
    public static Bitmap toBitmap(byte[] bytes) {
        if(bytes==null) {
            return null;
        }else {
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }
    }

    @TypeConverter
    public static byte[] toBytes(Bitmap bitmap) {
        if(bitmap==null) {
            return null;
        }else {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos); //bm is the bitmap object
            return baos.toByteArray();
        }
    }
}
