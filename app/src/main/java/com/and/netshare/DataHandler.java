package com.and.netshare;

import android.content.Context;
import android.net.Uri;

import androidx.documentfile.provider.DocumentFile;

public class DataHandler {
    public static String changeDotToComaEmail(String email){
        return email.replace('.', ',');
    }

    public static String getFileRealNameFromUri(Context context, Uri fileUri) {
        if (context == null || fileUri == null) return null;
        DocumentFile documentFile = DocumentFile.fromSingleUri(context, fileUri);
        if (documentFile == null) return null;
        return documentFile.getName();
    }

    public static String getSuffix(String fileName){
        String prefix=fileName.substring(fileName.lastIndexOf(".")+1);
        return prefix;
    }
}
