package map.test.testmap.utils;

import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.EditText;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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
        Log.d(TAG, "saveBitmap: " + filePath);
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
}
