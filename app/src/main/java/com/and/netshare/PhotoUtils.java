package com.and.netshare;

import static android.os.Environment.DIRECTORY_PICTURES;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class PhotoUtils {


    /**
     * 调用系统裁剪
     *
     * @param uri        需要裁剪的图片路径
     * @param mImagePath 图片输出路径
     * @param size       裁剪框大小
     */
    public static Intent startPhotoZoom(Activity a,Uri uri, Uri mImagePath, int size) {
        return startPhotoZoom(a, uri, mImagePath, size, size);
    }

    public static Intent startPhotoZoom(Activity a, Uri uri, String mImagePath, int size) {
        return startPhotoZoom(a, uri, Uri.fromFile(new File(mImagePath)), size, size);
    }

    /**
     * 调用系统裁剪<br>
     * 注:华为手机默认
     *
     * @param uri        需要裁剪的图片路径
     * @param mImagePath 图片输出路径
     * @param sizeX      裁剪x
     * @param sizeY      裁剪y
     */
    public static Intent startPhotoZoom(Context c, Uri uri, Uri mImagePath, int sizeX, int sizeY) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            //intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        //裁剪后输出路径
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImagePath);
        //输入图片路径
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
//        intent.putExtra("circleCrop", "true");

        intent.putExtra("aspectX", 9998);//2019/5/8 修复华为手机默认为圆角裁剪的问题
        intent.putExtra("aspectY", 9999);//
        intent.putExtra("outputX", sizeX);
        intent.putExtra("outputY", sizeY);
        intent.putExtra("scale", true);
        intent.putExtra("scaleUpIfNeeded", true);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("return-data", false);
        return intent;
    }


    /**
     * 保存图片到指定的路径
     * @param context           当前的context
     * @param bmp               需要保存的bitmap
     * @param parentPath    保存到的路径
     */
    public static void saveImageToGallery(Context context, Bitmap bmp, String parentPath) {
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), parentPath);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = "NetShare_" + System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(file.getPath()))));
        Toast.makeText(context, R.string.zoom_save_ok, Toast.LENGTH_SHORT).show();
    }
}
