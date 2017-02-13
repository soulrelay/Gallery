
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
 * 文件操作类
 *
 * @author 贾斌
 */
public class FileOperationUtils {

    private static final String TAG = "FileOperationUtils";

    // 默认的磁盘缓存大小，预留20M
    private static final int DEFAULT_REST_DISK_CACHE_SIZE = 1024 * 1024 * 20; // 20MB
    /**
     * 私有视频更改后的扩展名
     */
    public static String PRIVATE_MODE_SUFFIX_NAME = "db";
    /**
     * 私有视频文件信息的扩展名
     */
    public static String PRIVATE_MODE_INFO_SUFFIX_NAME = "info";
    /**
     * 隐私文件夹
     */
    public static String PIRVATE_MODE_DIR = ".baofengtemp";
    /**
     * 移动后文件的最终路径
     */
    private static String finalFileName = null;// 移动后文件的最终路径，即改名，重命名，改扩展名后的的路径
    /**
     * 对象转json字符串，可使用泛型
     *
     * @param object
     * @return 如果为null，则发生异常
     */
    public static String toJson(Object object) {
        try {
            Gson gson = new Gson();
            return gson.toJson(object);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

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
            Log.d(TAG, "zql fromJson() call Exception : " + e.getMessage());
            e.printStackTrace();
        }
        return t;
    }

    /**
     * list转json数组，可使用泛型. 用法例： ArrayList<UgcItem> list; String jsonString =
     * FileOperationUtils.toJsonList(list);
     *
     * @return 如果为null，则发生异常
     * @author jiabin
     */
    public static String toJsonList(ArrayList<?> list) {
        try {
            Gson gson = new Gson();
            return gson.toJson(list);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * json数组转list，可使用泛型. 用法例： ArrayList<UgcItem> list = (ArrayList<UgcItem>)
     * FileOperationUtils.fromJsonList(jsonString);
     *
     * @return 如果为null，则发生异常
     * @author jiabin
     */
    public static List<?> fromJsonList(String jsonString) {
        try {
            Gson gson = new Gson();
            List<?> list = gson.fromJson(jsonString, new TypeToken<List<?>>() {
            }.getType());
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



    public static String getGifImageDirNew() {
        return getBaofengCacheDir() + "gif_imageview/";
    }

    public static String getTopfingerImageDirNew() {
        String path = getBaofengCacheDir() + "topfinger_imageview/";
        File f = new File(path);
        if(!f.exists()){
            f.mkdir();
        }

        return getBaofengCacheDir() + "topfinger_imageview/";
    }

//    public static String getGifImageDir() {
//        LogHelper.v("xq","getGifImageDir r "+getExternalStoragePath() + "/bfsports/gif_imageview/");
//        return getExternalStoragePath() + "/bfsports/gif_imageview/";
//    }

    public static String getBaofengCacheDir() {
        return Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/Android/data/com.sports.baofeng/cache/";
    }

    public static String getBaofengDownloadDir() {
        String downloadDir = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/Download/";
        File f = new File(downloadDir);
        if(!f.exists()){
            f.mkdir();
        }
        return downloadDir;
    }

    /**
     * 替换路径文件的扩展名
     *
     * @param path   文件路径
     * @param suffix 需要替换的扩展名，不需要加. （例如“apk”）
     * @return 返回替换扩展名后的路径
     * @author jiabin
     */
    public static String getNewSuffixFilePath(String path, String suffix) {
        if (path == null || path.length() <= 0) {
            return "";
        }
        int index = path.lastIndexOf(".");
        return path.substring(0, index + 1) + suffix;
    }

    /**
     * 替换路径文件的文件名
     *
     * @param path       文件路径
     * @param changeName 需要替换的名字，不需要加扩展名
     * @return 返回替换文件名后的路径
     * @author jiabin
     */

    private static String getNewNameFilePath(String path, String changeName) {
        int start = path.lastIndexOf("/") + 1;
        int end = path.lastIndexOf(".");
        StringBuilder sb = new StringBuilder(path);
        sb.replace(start, end, changeName);
        return sb.toString();
    }

    /**
     * 获取无扩展名的文件名
     *
     * @param path
     * @return
     * @author jiabin
     */
    public static String getFileName(String path) {
        int start = path.lastIndexOf("/") + 1;
        int end = path.lastIndexOf(".");
        return path.substring(start, end);
    }

    /**
     * 获取文件大小
     *
     * @param f
     * @return -1：文件不存在
     * @author jiabin
     */
    private static long getFileSizes(File f) {
        long s = -1;
        if (f.exists()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(f);
                s = fis.available();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    fis.close();
                } catch (IOException e) {

                    e.printStackTrace();
                }
            }
        } else {
            Log.i("FileOperateionUtil", "文件不存在");
        }
        return s;
    }

    /**
     * 获取文件路径空间大小
     *
     * @param path
     * @return -1异常
     * @author jiabin
     */
    private static long getUsableSpace(File path) {
        try {
            final StatFs stats = new StatFs(path.getPath());
            return (long) stats.getBlockSize() * (long) stats.getAvailableBlocks();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }

    }

    public static String getExternalStoragePath() {
        if (isExsit()) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        return "/";
    }


    /**
     * 获取系统中所有可用SD卡磁盘目录。 例如：/mnt/sdcard 。 /mnt/external_sd 。 /mnt/usb_storage 。
     *
     * @param context
     * @param isContainESD 是否包括getExternalStorageDirectory()方法获得的路径
     *                     例如：/mnt/sdcard
     * @return result 如果list为空则没有SD卡，如果为null则出现异常
     * @author wanghb
     */
    @SuppressLint("NewApi")
    private static ArrayList<String> getSdPathListByInvoke(Context context, boolean isContainESD) {
        StorageManager sm = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        // 获取sdcard的路径：外置和内置
        String[] paths;
        ArrayList<String> result = new ArrayList<String>();
        result.clear();
        try {
            paths = (String[]) sm.getClass().getMethod("getVolumePaths", new Class[]{}).invoke(sm, new Object[]{});
            String esd = Environment.getExternalStorageDirectory().getAbsolutePath();
            for (int i = 0; i < paths.length; i++) {
                if (!isContainESD && paths[i].equals(esd)) {
                    continue;
                }
                File sdFile = new File(paths[i]);
                if (sdFile.canWrite() && !result.contains(paths[i])) {
                    result.add(paths[i]);
                }
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            return null;
        } catch (NoSuchMethodException e) {
            // e.printStackTrace();
            return null;
        }
        return result;
    }

    /**
     * 获取系统中所有可用SD卡磁盘目录。 例如：/mnt/sdcard 。 /mnt/external_sd 。 /mnt/usb_storage 。
     *
     * @param isContainESD 是否包括getExternalStorageDirectory()方法获得的路径
     * @return result 如果list为空则没有SD卡，如果为null则出现异常
     * @author Wanghb
     */
    private static ArrayList<String> getSdpathListByCanWrite(boolean isContainESD) {
        ArrayList<String> result = new ArrayList<String>();
        result.clear();
        try {
            File sdfile = Environment.getExternalStorageDirectory().getAbsoluteFile();
            File parentFile = sdfile.getParentFile();
            File[] listFiles = parentFile.listFiles();

            for (int i = 0; i < listFiles.length; i++) {
                if (!isContainESD && listFiles[i].equals(sdfile)) {
                    continue;
                }

                if (listFiles[i].exists() && listFiles[i].isDirectory() && listFiles[i].canWrite()) {
                    String path = listFiles[i].getAbsolutePath();
                    if (!result.contains(path)) {
                        result.add(path);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static boolean isExsit() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     * 解压缩一个文件
     *
     * @param zipFile 压缩文件
     * @param folderPath 解压缩的目标目录
     * @throws IOException 当解压缩过程出错时抛出
     */
    public static void unZipFile(File zipFile, String folderPath) throws ZipException, IOException {
        File desDir = new File(folderPath);
        if (!desDir.exists()) {
            desDir.mkdirs();
        }
        ZipFile zf = new ZipFile(zipFile);
        for (Enumeration<?> entries = zf.entries(); entries.hasMoreElements();) {
            ZipEntry entry = ((ZipEntry)entries.nextElement());
            InputStream in = zf.getInputStream(entry);
            String str = folderPath + File.separator + entry.getName();
            str = new String(str.getBytes("8859_1"), "GB2312");
            File desFile = new File(str);
            if (!desFile.exists()) {
                File fileParentDir = desFile.getParentFile();
                if (!fileParentDir.exists()) {
                    fileParentDir.mkdirs();
                }
                desFile.createNewFile();
            }
            OutputStream out = new FileOutputStream(desFile);
            byte buffer[] = new byte[DEFAULT_REST_DISK_CACHE_SIZE];
            int realLength;
            while ((realLength = in.read(buffer)) > 0) {
                out.write(buffer, 0, realLength);
            }
            in.close();
            out.close();
        }
    }

    public static String topfingerFileNameTransfer(Context context , int topfinger, String filePath){
        try {
            InputStream is = context.getAssets().open(filePath+"config.json");
            InputStreamReader reader = new InputStreamReader(is);
            BufferedReader bufferedReader = new BufferedReader(reader);
            StringBuffer buffer = new StringBuffer("");
            String str;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
                buffer.append("\n");
            }
            is.close();
            JSONObject jo = new JSONObject(buffer.toString());
            if(jo.isNull("data")){
                JSONArray ja = jo.getJSONArray("data");
                String name = "";
                for(int i = 0;i<ja.length();i++){
                    JSONObject subJO = (JSONObject)ja.get(i);

                }
            }
            return buffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static void saveFile(Context context, Bitmap bm, String fileName, String path) throws IOException {
        File foder = new File(path);
        if (!foder.exists()) {
            foder.mkdirs();
        }
        File myCaptureFile = new File(path, fileName);
        if (!myCaptureFile.exists()) {
            myCaptureFile.createNewFile();
        }
        FileOutputStream out = new FileOutputStream(myCaptureFile);
//        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
        bm.compress(Bitmap.CompressFormat.PNG, 100, out);
//        bos.flush();
//        bos.close();
        try {
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri uri = Uri.fromFile(myCaptureFile);
            intent.setData(uri);
            context.sendBroadcast(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 复制文件,耗时操作,需要另开线程 #####暂时没有用到#####
     *
     * @param oldPath
     * @param newPath
     * @author jiabin
     */
    private boolean copyFile(String oldPath, String newPath) {

        File a = new File(oldPath);
        File temp = new File(newPath);

        // 需要判断sd卡内存是否够
        if (getUsableSpace(temp) - getFileSizes(temp) < DEFAULT_REST_DISK_CACHE_SIZE) {
            return false;
        }
        // 实例输入输出的文件流
        InputStream in = null;
        OutputStream out = null;
        BufferedInputStream inb = null;
        BufferedOutputStream oub = null;
        try {
            if (!temp.exists())
                temp.mkdir();
            File b = new File(temp, a.getName());
            // 构建文件输入流
            in = new FileInputStream(a);
            // 构建文件输出流
            out = new FileOutputStream(b);
            inb = new BufferedInputStream(in);
            oub = new BufferedOutputStream(out);

            // 开始复制
            byte[] read = new byte[1024 * 8];
            int len = in.read(read);
            while (len != -1) {
                oub.write(read, 0, len);
                len = in.read(read);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                oub.close();
                inb.close();
                out.close();
                in.close();
            } catch (Exception e0) {
                e0.printStackTrace();
            }
        }
        return true;

    }
}
