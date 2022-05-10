package com.and.netshare.handlers;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.documentfile.provider.DocumentFile;

import com.and.netshare.home.homepage.images.SingleImage;

import java.util.ArrayList;
import java.util.Collections;

public class DataHandler {
    private static ArrayList<SingleImage> tmpList = new ArrayList<>();

    public static String changeDotToComaEmail(String email) {
        return email.replace('.', ',');
    }

    public static String getFileRealNameFromUri(Context context, Uri fileUri) {
        if (context == null || fileUri == null) return null;
        DocumentFile documentFile = DocumentFile.fromSingleUri(context, fileUri);
        if (documentFile == null) return null;
        return documentFile.getName();
    }

    public static String getSuffix(String fileName) {
        String prefix = fileName.substring(fileName.lastIndexOf(".") + 1);
        return prefix;
    }

    public static ArrayList<SingleImage> reserveImageList(ArrayList<SingleImage> originList) {
        ArrayList<SingleImage> tmp = new ArrayList<>();
        for (int i = originList.size() - 1; i >= 0; i--) {
            tmp.add(originList.get(i));
        }
        Log.d("list after1", tmp.toString());
        return tmp;
    }

    public static String getFileName(String pathandname) {
        int start = pathandname.lastIndexOf("/");
        int end = pathandname.lastIndexOf(".");
        if (start != -1 && end != -1) {
            return pathandname.substring(start + 1, end);
        } else {
            return null;
        }
    }

}
