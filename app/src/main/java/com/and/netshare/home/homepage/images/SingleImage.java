package com.and.netshare.home.homepage.images;

import android.content.Context;

public class SingleImage {
    private String path;
    private static String pathStatic;
    private static String category;

    public SingleImage(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public static void setPathStatic(String pathStatic) {
        SingleImage.pathStatic = pathStatic;
    }

    public static String getStaticPath() {
        return pathStatic;
    }

    public static String getCategory() {
        return category;
    }

    public static void setCategory(String category) {
        SingleImage.category = category;
    }
}
