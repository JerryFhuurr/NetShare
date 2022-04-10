package com.and.netshare;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.FileUtils;
import android.provider.MediaStore;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 兼容android 7.0以上获取uri异常的工具类
 */
public class FileProvider {

    /**
     * 获取uri
     *
     * @param context
     * @param s
     * @param file
     * @return
     */
    public static Uri getUriForFile(Context context, String s, File file) {
        Uri fileUri = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
                contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, file.getName());
                contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                fileUri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                fileUri = getUriForFile24(context, file);
            } else {
                fileUri = Uri.fromFile(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileUri;
    }

    /**
     * android 7.0以上获取uri的方法
     *
     * @param context
     * @param file
     * @return
     */
    private static Uri getUriForFile24(Context context, File file) {

        Uri fileUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileProvider", file);
        return fileUri;
    }


    /**
     * uri转Sting
     *
     * @param context Context
     * @param uri     Uri
     * @param path    文件路径
     */
    public static void getFilePathString(Context context, Uri uri, String path) {
        try {
            InputStream is = context.getContentResolver().openInputStream(uri);
            if (is == null) {
                return;
            }
            File file = new File(path);
            file.createNewFile();
            FileOutputStream fos = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                fos = new FileOutputStream(file);
                FileUtils.copy(is, fos);
            } else {
                ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024 * 10];
                while (true) {
                    int len = is.read(buffer);
                    if (len == -1) {
                        break;
                    }
                    arrayOutputStream.write(buffer, 0, len);
                }
                arrayOutputStream.close();
                byte[] dataByte = arrayOutputStream.toByteArray();
                if (dataByte.length > 0) {
                    fos = new FileOutputStream(file);
                    fos.write(dataByte);
                }
            }
            if (fos != null)
                fos.close();
            if (is != null)
                is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 安装app的操作,兼容7.0
     *
     * @param context
     * @param intent
     * @param type      执行的意图(application/vnd.android.package-archive-->安装app)
     * @param file
     * @param writeAble 是否需要写操作
     */
    public static void setIntentDataAndType(Context context, Intent intent, String type, File file, boolean writeAble) {
        if (Build.VERSION.SDK_INT >= 24) {
            intent.setDataAndType(getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileProvider", file), type);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            if (writeAble) {
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
        } else {
            intent.setDataAndType(Uri.fromFile(file), type);
        }
    }

    /**
     * 复制单个文件
     *
     * @param oldPath$Name String 原文件路径+文件名 如：data/user/0/com.test/files/abc.txt
     * @param newPath$Name String 复制后路径+文件名 如：data/user/0/com.test/cache/abc.txt
     * @return <code>true</code> if and only if the file was copied;
     * <code>false</code> otherwise
     */
    public static boolean copyFile(String oldPath$Name, String newPath$Name) {
        try {
            File oldFile = new File(oldPath$Name);
            if (!oldFile.exists()) {
                Log.e("--Method--", "copyFile:  oldFile not exist.");
                return false;
            } else if (!oldFile.isFile()) {
                Log.e("--Method--", "copyFile:  oldFile not file.");
                return false;
            } else if (!oldFile.canRead()) {
                Log.e("--Method--", "copyFile:  oldFile cannot read.");
                return false;
            }

        /* 如果不需要打log，可以使用下面的语句
        if (!oldFile.exists() || !oldFile.isFile() || !oldFile.canRead()) {
            return false;
        }
        */

            FileInputStream fileInputStream = new FileInputStream(oldPath$Name);    //读入原文件
            FileOutputStream fileOutputStream = new FileOutputStream(newPath$Name);
            byte[] buffer = new byte[1024];
            int byteRead;
            while ((byteRead = fileInputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, byteRead);
            }
            fileInputStream.close();
            fileOutputStream.flush();
            fileOutputStream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
