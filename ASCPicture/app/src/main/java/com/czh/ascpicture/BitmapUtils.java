package com.czh.ascpicture;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2018/10/22.
 */

public class BitmapUtils {
    private static final int DEFAULT_SCALE = 7;

    public static DisplayMetrics getScreenMetrics(Context context) {
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

        return inSampleSize;
    }

    public static Bitmap decodeBitmapFromRes(Resources resources, int resId, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(resources, resId, options);
        options.inSampleSize = getInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(resources, resId, options);
    }

    public static Bitmap decodeBitmapFromPath(Context context, String path) {
        Bitmap originBitmap = BitmapFactory.decodeFile(path);
        return scale(context, originBitmap);
    }

    public static Bitmap decodeBitmapFromResource(Context context, Resources res, int resId) {
        Bitmap originBitmap = BitmapFactory.decodeResource(res, resId);
        return scale(context, originBitmap);
    }

    public static Bitmap decodeBitmapFromStream(Context context, InputStream stream) {
        Bitmap originBitmap = BitmapFactory.decodeStream(stream);
        return scale(context, originBitmap);
    }

    public static Bitmap createAsciiPic(Context context, Bitmap bitmap) {
        final String base = "#8XOHLTI)i=+;:,.";// 字符串由复杂到简单
//        final String base = "#MYLI)eearuvooi=+;:,.";// 字符串由复杂到简单
        StringBuilder text = new StringBuilder();
        // 根据Bitmap的灰度将Bitmap转换成字符串
        for (int y = 0; y < bitmap.getHeight(); y += 2) {
            for (int x = 0; x < bitmap.getWidth(); x++) {
                final int pixel = bitmap.getPixel(x, y);
                final int r = (pixel & 0xff0000) >> 16, g = (pixel & 0xff00) >> 8, b = pixel & 0xff;
                final float gray = 0.299f * r + 0.578f * g + 0.114f * b;
                final int index = Math.round(gray * (base.length() + 1) / 255);
                String s = index >= base.length() ? " " : String.valueOf(base.charAt(index));
                text.append(s);
            }
            text.append("\n");
        }
        return textAsBitmap(context, text);
    }

    public static Bitmap textAsBitmap(Context context, StringBuilder text) {
        TextPaint textPaint = new TextPaint();
        textPaint.setColor(Color.GRAY);
        textPaint.setAntiAlias(true);
        textPaint.setTypeface(Typeface.MONOSPACE);
        // FIXME: 2018/10/23 textSize如何得出？一个字符大约在7px左右
        // TODO: 2018/10/24 字符宽度、originBitmap宽度和屏幕宽度之间的关系
        textPaint.setTextSize(12);
        int screenWidth = getScreenMetrics(context).widthPixels;

        StaticLayout layout = new StaticLayout(text, textPaint, screenWidth,
                Layout.Alignment.ALIGN_CENTER, 1f, 0.0f, true);

        Bitmap bitmap = Bitmap.createBitmap(layout.getWidth() + 20,
                layout.getHeight() + 20, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        canvas.translate(10, 10);
        canvas.drawColor(Color.WHITE);
//        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);//绘制透明色
        /**
         * 若一行文字的内容超出行宽限制，则自动换行，且Layout设置了居中对齐
         */
        layout.draw(canvas);

        return bitmap;

    }

    public static void saveBitmapToSysAlbum(Context context, Bitmap bitmap) {
        if(bitmap == null)
            return;
        String fileName = System.currentTimeMillis() / 1000 + ".jpeg";
        String filePath;
        if (Build.BRAND.equals("Xiaomi")) {//小米
            filePath = Environment.getExternalStorageDirectory() + "/DCIM/Camera/" + fileName;
        } else {// 魅族、OPPO
            filePath = Environment.getExternalStorageDirectory() + "/DCIM/" + fileName;
        }
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream fos = new FileOutputStream(file);
            // 将Bitmap转换输出到JPEG格式的文件中
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)) {
                fos.flush();
                fos.close();
                // optional:将图片插入图库
                //MediaStore.Images.Media.insertImage(context.getContentResolver(), filePath, fileName, null);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // optional:通知图库刷新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + filePath)));
    }

    public static Bitmap scale(Context context, Bitmap originBitmap) {
        // 比较原图宽度和屏幕宽度/scale,取小值，要求保持原图宽高比例不变
        // FIXME: 2018/10/23 scale如何得出
        int reqWidth, reqHeight;
        DisplayMetrics metrics = getScreenMetrics(context);
        if (originBitmap.getWidth() <= metrics.widthPixels / DEFAULT_SCALE) {
            reqWidth = originBitmap.getWidth();
            reqHeight = originBitmap.getHeight();
        } else {
            reqWidth = (int) metrics.widthPixels / DEFAULT_SCALE;
            reqHeight = originBitmap.getHeight() * reqWidth / originBitmap.getWidth();
        }

        return Bitmap.createScaledBitmap(originBitmap, reqWidth, reqHeight, true);
    }
}
