package com.czh.ascpicture;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/10/22.
 */

public class BitmapUtils {
    public static DisplayMetrics getSreenMethics(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        return dm;
    }

    public static int getInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int inSampleSize = 1;
        final int outWidth = options.outWidth;
        final int outHeight = options.outHeight;
        if (outWidth > reqWidth || outHeight > reqHeight) {
            final int halfWidth = outWidth / 2;
            final int halfHeight = outHeight / 2;
            while (halfWidth / inSampleSize >= reqWidth && halfHeight / inSampleSize >= reqHeight) {
                inSampleSize *= 2;
            }
        }

        return 8;
    }

    public static Bitmap decodeBitmapFromRes(Resources resources, int resId, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(resources, resId, options);
        options.inSampleSize = getInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        Bitmap bm = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, resId, options), reqWidth / 7, reqHeight / 7, true);
        return bm;
    }

    public static Bitmap createAsciiPic(Resources resources,int resId, Context context) {
        final String base = "#8XOHLTI)i=+;:,.";// 字符串由复杂到简单
//        final String base = "#,.0123456789:;@ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";// 字符串由复杂到简单
        StringBuilder text = new StringBuilder();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        Bitmap image = BitmapFactory.decodeResource(resources, resId);  //读取图片
        int width0 = image.getWidth();
        int height0 = image.getHeight();
        int width1, height1;
        int scale = 7;
        if (width0 <= width / scale) {
            width1 = width0;
            height1 = height0;
        } else {
            width1 = width / scale;
            height1 = width1 * height0 / width0;
        }
        image = scale(image, width1, height1);  //读取图片
        //输出到指定文件中
        for (int y = 0; y < image.getHeight(); y += 2) {
            for (int x = 0; x < image.getWidth(); x++) {
                final int pixel = image.getPixel(x, y);
                final int r = (pixel & 0xff0000) >> 16, g = (pixel & 0xff00) >> 8, b = pixel & 0xff;
                final float gray = 0.299f * r + 0.578f * g + 0.114f * b;
                final int index = Math.round(gray * (base.length() + 1) / 255);
                String s = index >= base.length() ? " " : String.valueOf(base.charAt(index));
                text.append(s);
            }
            text.append("\n");
        }
        return textAsBitmap(text, context);
//        return creatCodeBitmap(text,context,colors);
//        return image;
    }

    public static Bitmap scale(Bitmap bitmap, int newWidth, int newHeight) {
        Bitmap ret = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
        return ret;
    }

//    public static Bitmap createAsciiPic(Context context, Bitmap bitmap) {
//        final String base = "#8XOHLTI)i=+;:,.";// 字符串由复杂到简单
////        final String base = "#,.0123456789:;@ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";// 字符串由复杂到简单
//        StringBuilder text = new StringBuilder();
//        //输出到指定文件中
//        for (int y = 0; y < bitmap.getHeight(); y += 2) {
//            for (int x = 0; x < bitmap.getWidth(); x++) {
//                final int pixel = bitmap.getPixel(x, y);
//                final int r = (pixel & 0xff0000) >> 16, g = (pixel & 0xff00) >> 8, b = pixel & 0xff;
//                final float gray = 0.299f * r + 0.578f * g + 0.114f * b;
//                final int index = Math.round(gray * (base.length() + 1) / 255);
//                String s = index >= base.length() ? " " : String.valueOf(base.charAt(index));
//                text.append(s);
//            }
//            text.append("\n");
//        }
//        return textAsBitmap(text, context);
//    }

    public static Bitmap textAsBitmap(StringBuilder text, Context context) {
        TextPaint textPaint = new TextPaint();
        textPaint.setColor(Color.GRAY);
        textPaint.setAntiAlias(true);
        textPaint.setTypeface(Typeface.MONOSPACE);
        textPaint.setTextSize(12);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;         //

        StaticLayout layout = new StaticLayout(text, textPaint, width,
                Layout.Alignment.ALIGN_CENTER, 1f, 0.0f, true);

        Bitmap bitmap = Bitmap.createBitmap(layout.getWidth() + 20,
                layout.getHeight() + 20, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);

        canvas.translate(10, 10);

        canvas.drawColor(Color.WHITE);

//        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);//绘制透明色

        layout.draw(canvas);

        return bitmap;

    }
}
