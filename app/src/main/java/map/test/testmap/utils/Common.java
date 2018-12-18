package map.test.testmap.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.widget.EditText;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    public void setEdittextFalse(EditText cur){
        cur.setEnabled(false);
        cur.setFocusable(false);
        cur.setKeyListener(null);
    }
}
