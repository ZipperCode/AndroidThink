package com.think.core.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class BitmapUtil {
    private static final String TAG = BitmapUtil.class.getSimpleName();

    /**
     * 图片压缩采样率的计算
     *
     * @param options 包含图片参数
     * @param newW    要设置的新宽度
     * @param newH    要设置的新高度
     * @return 计算的压缩采样率
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int newW, int newH) {
        int originW = options.outWidth;
        int originH = options.outHeight;
        int inSampleSize = 1;
        if (originH > newH || originW > newW) {
            float widthRadio = Math.round(originW * 1.0 / newW);
            float heightRadio = Math.round(originH * 1.0 / newW);
            inSampleSize = (int) Math.max(widthRadio, heightRadio);
        }
        return inSampleSize;
    }

    public static Bitmap drawable2Bitmap(Resources resources, int resId) {
        return BitmapFactory.decodeResource(resources, resId);
    }

    public static Bitmap drawable2Bitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = null;
        try {
            int width = drawable.getIntrinsicWidth();
            int height = drawable.getIntrinsicHeight();
            Bitmap.Config config;
            if (drawable.getOpacity() != PixelFormat.OPAQUE) {
                config = Bitmap.Config.ARGB_8888;
            } else {
                config = Bitmap.Config.RGB_565;
            }
            bitmap = Bitmap.createBitmap(width, height, config);
            //注意，下面三行代码要用到，否则在View或者SurfaceView里的canvas.drawBitmap会看不到图
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, width, height);
            drawable.draw(canvas);
        } catch (OutOfMemoryError e) {
            Log.i(TAG, "drawable to Bitmap OutOfMemoryError");
        }
        return bitmap;
    }

    public static byte[] bitmap2Bytes(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    /**
     * 从字节中获取Bitmap图片缓存
     *
     * @param data 数据
     * @return bitmap
     */
    public static Bitmap byteToBitmap(byte[] data) {
        if (data == null || data.length == 0) {
            return null;
        }
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    /**
     * 获取新尺寸的bitmap图片
     *
     * @param resources res资源
     * @param resId     资源id
     * @param newWidth  新宽度
     * @param newHeight 新高度
     * @return bitmap
     */
    public static Bitmap loadNewSizeByResource(Resources resources, int resId,
                                               int newWidth, int newHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(resources, resId, options);
        options.inSampleSize = calculateInSampleSize(options, newWidth, newHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(resources, resId, options);
    }


    public static Bitmap loadNewSizeByFile(String fileName, int newWidth, int newHeight) {
        File file = new File(fileName);
        if (!file.exists()) {
            return null;
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(fileName, options);
        options.inSampleSize = calculateInSampleSize(options, newWidth, newHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(fileName, options);
    }


    /**
     * 图片质量压缩,主要用于传输,因为图片压缩后虽然bitmap图片大小没有改变,<br></br>
     * 不过压缩后得到的字节数会随着压缩质量的降低而减少<br/>
     * 注意: 压缩图片大小不变
     *
     * @param bitmap  位图
     * @param quality 压缩质量
     * @return 压缩后的图片字节数
     */
    public static byte[] compressByQuality(Bitmap bitmap, int quality) {
        if (quality < 0 || quality > 100) {
            quality = 100;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //png 图片是无损的压缩后其字节数不变
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        byte[] result = baos.toByteArray();
        Log.i(TAG, "图片质量压缩 原图片字节数为 ${bitmap.byteCount} , 压缩后图片字节数为 ${result.size}");
        return result;
    }

    public static Bitmap compressByMatrix(Bitmap bitmap, int scaleWidth, int scaleHeight) {
        Matrix matrix = new Matrix();
        matrix.setScale(scaleWidth, scaleHeight);
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        Log.i(TAG, "图片质量压缩 原图片字节数为 ${bitmap.byteCount} , 压缩后图片字节数为 ${newBitmap.byteCount}");
        return newBitmap;
    }
}
