package com.think.core.cache.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;

public class ImageLoader {

    public static Bitmap decodeBitmap(Context context, int resId, int w, int h, boolean hasAlpha) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        // 是否只获取图片信息，不获取实际图片数据
        options.inJustDecodeBounds = false;
        Bitmap detailBitmap = BitmapFactory.decodeResource(context.getResources(), resId, options);
        int originWidth = detailBitmap.getWidth();
        int originHeight = detailBitmap.getHeight();
        // 是否设置成一个可复用的需要配置config参数
        options.inMutable = true;
        if (!hasAlpha) {
            // 不使用透明度
            options.inPreferredConfig = Bitmap.Config.RGB_565;
        }
        // 采样率
        options.inSampleSize = calculateSampleSize(originWidth, originHeight, w, h);

        return null;
    }

    public static Bitmap compressSimpleSizeRes(Context context, int resId, int w, int h, boolean hasAlpha) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        // 是否只获取图片信息，不获取实际图片数据
        options.inJustDecodeBounds = false;
        Bitmap detailBitmap = BitmapFactory.decodeResource(context.getResources(), resId, options);
        int originWidth = detailBitmap.getWidth();
        int originHeight = detailBitmap.getHeight();
        // 是否设置成一个可复用的需要配置config参数
//        options.inMutable = true;
        if (!hasAlpha) {
            // 不使用透明度
            options.inPreferredConfig = Bitmap.Config.RGB_565;
        }
        // 采样率
        options.inSampleSize = calculateSampleSize(originWidth, originHeight, w, h);
        options.inJustDecodeBounds = true;

        return BitmapFactory.decodeResource(context.getResources(), resId, options);
    }

    public static Bitmap compressSimpleSize(String filePath, int w, int h, boolean hasAlpha) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        // 是否只获取图片信息，不获取实际图片数据
        options.inJustDecodeBounds = false;
        Bitmap detailBitmap = BitmapFactory.decodeFile(filePath, options);
        int originWidth = detailBitmap.getWidth();
        int originHeight = detailBitmap.getHeight();
        // 是否设置成一个可复用的需要配置config参数
//        options.inMutable = true;
        if (!hasAlpha) {
            // 不使用透明度
            options.inPreferredConfig = Bitmap.Config.RGB_565;
        }
        // 采样率
        options.inSampleSize = calculateSampleSize(originWidth, originHeight, w, h);
        options.inJustDecodeBounds = true;
        return BitmapFactory.decodeFile(filePath, options);
    }

    public static Bitmap decodeByMutable(Context context, int resId, Bitmap srcBitmap){
        BitmapFactory.Options options = new BitmapFactory.Options();
        int srcMemSize = srcBitmap.getRowBytes();
        options.inJustDecodeBounds = false;
        Bitmap detailBitmap = BitmapFactory.decodeResource(context.getResources(), resId,options);
        int currentMemSize = detailBitmap.getRowBytes();
        if(currentMemSize <= srcMemSize){
            options.inMutable = true;
            options.inBitmap = srcBitmap;
        }
        options.inJustDecodeBounds = true;
        return BitmapFactory.decodeResource(context.getResources(),resId,options);
    }

    /**
     * 计算图片缩放比例
     *
     * @param oldW 源图片宽度
     * @param oldH 源图片高度
     * @param newW 新宽度
     * @param newH 新高度
     * @return simpleSize
     */
    private static int calculateSampleSize(int oldW, int oldH, int newW, int newH) {
        int simpleSize = 1;
        if (oldW <= newW || oldH <= newH) {
            return simpleSize;
        }
        return simpleSize + calculateSampleSize(oldW / 2, oldH / 2, newW, newH);
    }

}
