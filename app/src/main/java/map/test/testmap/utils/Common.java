package map.test.testmap.utils;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Common {
    private static final String TAG = "Common";
    private static volatile Common manager = null;

    private Common(){
    }

    public static Common getInstance(){
        if (manager == null){
            synchronized (Common.class){
                if (manager == null){
                    manager = new Common();
                }
            }
        }
        return manager;
    }

    /**
     * 将bitmap保存在本地
     * @param bm
     */
    public String saveBitmap(Context context,Bitmap bm){
        String filePath = getFilePath(context);

        Bitmap compressBitmap = ImageFactory.ratio(bm,1920,1080);


        Log.d(TAG, "saveBitmap: " + filePath+",bitmap width:" + compressBitmap.getWidth()+",height is:" + compressBitmap.getHeight());
        File f = new File(filePath);
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            compressBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            Log.i(TAG, "已经保存");
            return filePath;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 读取照片旋转角度
     *
     * @param path 照片路径
     * @return 角度
     */
    public int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 旋转图片
     * @param angle 被旋转角度
     * @param bitmap 图片对象
     * @return 旋转后的图片
     */
    public Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        Bitmap returnBm = null;
        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
        }
        if (returnBm == null) {
            returnBm = bitmap;
        }
        if (bitmap != returnBm) {
            bitmap.recycle();
        }
        return returnBm;
    }

    public String getOriginalPath(){
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        String fileName = format.format(new Date())+".jpg";
        return Environment.getExternalStorageDirectory().toString()+File.separator+"image/"+fileName;
    }

    public String getFilePath(Context context){
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        String fileName = format.format(new Date())+".jpg";
        String dirPath = context.getExternalFilesDir("pic").getPath();
        Log.d(TAG, "getFilePath: "+dirPath);
        File dir = new File(dirPath);
        if(!dir.exists()){
            dir.mkdirs();
        }
        String path =dirPath + File.separator+fileName;
        return path;
    }

    public void setEditTextFalse(EditText cur){
        cur.setFocusableInTouchMode(false);
        cur.setFocusable(false);
    }

    public void setEditTextTrue(EditText cur){
        cur.setFocusableInTouchMode(true);
        cur.setFocusable(true);
        cur.requestFocus();

    }

    public String getMacAddress() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface
                    .getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")){
                    continue;
                }

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    String num = Integer.toHexString(b & 0xFF);
                    if (num.length() == 1) {
                        num = "0" + num;
                    }
                    res1.append(num + ":");
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
            return "";
        }
        return "";
    }

    private String getDownloadDir(Context context){
        return context.getExternalFilesDir("download").getPath();
    }

    private String getDownloadName(){
        return "map.apk";
    }

    public String getDownloadPath(Context context){
        String downloadPath = getDownloadDir(context) + File.separator + getDownloadName();
        Log.d(TAG, "getDownloadPath: "+ downloadPath);
        return downloadPath;
    }

    public void downloadApk(
            Context context,
            String downLoadUrl,
            String description,
            String infoName) {

        DownloadManager.Request request;
        try {
            request = new DownloadManager.Request(Uri.parse(downLoadUrl));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        request.setTitle(infoName);
        request.setDescription(description);

        //在通知栏显示下载进度
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }

        //设置保存下载apk保存路径
        File file = new File(getDownloadDir(context), getDownloadName());
        request.setDestinationUri(Uri.fromFile(file));

        Context appContext = context.getApplicationContext();
        DownloadManager manager = (DownloadManager)
                appContext.getSystemService(Context.DOWNLOAD_SERVICE);
        //进入下载队列
        manager.enqueue(request);
    }

    public int getVersionCode(Context context){
        PackageManager pm = context.getPackageManager();
        PackageInfo pi = null;
        try {
            pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);
        } catch (PackageManager.NameNotFoundException e1) {
            e1.printStackTrace();
        }
        return pi.versionCode;
    }

    public String[] splitStr(String string){
        return string == null?new String[0]:string.split(",");
    }

    public String combine(String[] strings){
        String result = "";
        for(String item:strings){
            result+=item+",";
        }
        return result.substring(0,result.length()-1);
    }

    /**
     * 获取应用程序名称
     */
    public synchronized String getAppName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * [获取应用程序版本名称信息]
     * @param context
     * @return 当前应用的版本名称
     */
    public synchronized String getVersionName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * [获取应用程序版本名称信息]
     * @param context
     * @return 当前应用的版本名称
     */
    public static synchronized String getPackageName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.packageName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public File saveImage(Context context, Bitmap source) throws Exception{
        String fileParentPath =
                Environment.getExternalStorageDirectory().getAbsolutePath() + "/TestMap/Download";
        File appDir = new File(fileParentPath);
        if (!appDir.exists()) {
            appDir.mkdirs();
        }
        //保存的文件名
        String fileName = "map" + System.currentTimeMillis() + ".jpg";
        //目标文件
        File targetFile = new File(appDir, fileName);
        //输出文件流
        FileOutputStream fos = new FileOutputStream(targetFile);
        // 缓冲数组
        source.compress(Bitmap.CompressFormat.JPEG, 90, fos);
        fos.flush();
        fos.close();
        //扫描媒体库
        String extension = MimeTypeMap.getFileExtensionFromUrl(targetFile.getAbsolutePath());
        String mimeTypes = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        MediaScannerConnection.scanFile(context, new String[]{targetFile.getAbsolutePath()},
                new String[]{mimeTypes},null);

        return targetFile;
    }


    public String getDateFromLong(long time){
        if (time == 0f) {
            return "";
        }
        Date curDate = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        return format.format(curDate);
    }

    /**
     * 去掉字符串里面的html代码。<br>
     * 要求数据要规范，比如大于小于号要配套,否则会被集体误杀。
     *
     * @param content 内容
     * @return 去掉后的内容
     */

    public String stripHtml(String content) {
        // <p>段落替换为换行
        content = content.replaceAll("<p .*?>", "\r\n");
        // <br><br/>替换为换行
        content = content.replaceAll("<br\\s*/?>", "\n");
        // 去掉其它的<>之间的东西
        content = content.replaceAll("\\<.*?>", "\n");
        // 还原HTML
        // content = HTMLDecoder.decode(content);
        //&ldquo;&quot;&nbsp;
        content = content.replaceAll("&.dquo;", "\"");
        content = content.replaceAll("&nbsp;", " ");
        return content;
    }

    public String getUserAgent(){
        String userAgent = "";
        StringBuffer sb = new StringBuffer();
        userAgent = System.getProperty("http.agent");//Dalvik/2.1.0 (Linux; U; Android 6.0.1; vivo X9L Build/MMB29M)

        for (int i = 0, length = userAgent.length(); i < length; i++) {
            char c = userAgent.charAt(i);
            if (c <= '\u001f' || c >= '\u007f') {
                sb.append(String.format("\\u%04x", (int) c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
