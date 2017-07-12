package com.march.gifmaker.utils;

import android.graphics.Bitmap;

/**
 * CreateAt : 7/12/17
 * Describe :
 *
 * @author chendong
 */
public class Util {


    public static void recycleBitmaps(Bitmap... bitmaps) {
        for (Bitmap bitmap : bitmaps) {
            try {
                if (bitmap != null && !bitmap.isRecycled()) {
                    bitmap.recycle();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
