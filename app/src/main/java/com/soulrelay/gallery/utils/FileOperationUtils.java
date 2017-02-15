
package com.soulrelay.gallery.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * Author:    SuS
 * Version    V1.0
 * Date:      17/2/14
 * Description:  文件操作类
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 17/2/14          SuS                 1.0               1.0
 * Why & What is modified:
 */
public class FileOperationUtils {

    private static final String TAG = "FileOperationUtils";
    /**
     * json字符串转对象，可使用泛型
     *
     * @return 如果为null，则发生异常
     */
    public static <T> T fromJson(String jsonString, Class<T> c) {
        T t = null;
        try {
            Gson gson = new Gson();
            t = gson.fromJson(jsonString, c);
        } catch (Exception e) {
            Log.d(TAG, "fromJson() call Exception : " + e.getMessage());
            e.printStackTrace();
        }
        return t;
    }

}
