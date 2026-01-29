package com.example.photosandroid.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.InputStream;

public class ImageLoader {

    public static Bitmap loadThumbnail(Context context, Uri uri, int reqSize) {
        try {
            // Step 1: Decode bounds only
            InputStream is = context.getContentResolver().openInputStream(uri);

            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(is, null, opts);
            is.close();

            // Step 2: Calculate sample size
            int inSampleSize = 1;
            int w = opts.outWidth;
            int h = opts.outHeight;

            while (w / inSampleSize > reqSize && h / inSampleSize > reqSize) {
                inSampleSize *= 2;
            }

            // Step 3: Decode downsampled bitmap
            BitmapFactory.Options opts2 = new BitmapFactory.Options();
            opts2.inSampleSize = inSampleSize;

            InputStream is2 = context.getContentResolver().openInputStream(uri);
            Bitmap bmp = BitmapFactory.decodeStream(is2, null, opts2);
            is2.close();

            return bmp;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
