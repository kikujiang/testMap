package map.test.testmap.receiver;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;

import map.test.testmap.utils.Common;

/**
 * 下载完成后对
 */
public class InstallReceiver extends BroadcastReceiver {

    private static final String TAG = "InstallReceiver";
    
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
            installApk(context);
        }
    }

    /**
     *   安装apk
     * @param context
     */
    private void installApk(Context context) {
        try {
            Intent i = new Intent(Intent.ACTION_VIEW);
            String filePath = Common.getInstance().getDownloadPath(context);
            Log.d(TAG, "installApk: file path is:" + filePath);
            File file = new File(filePath);
            Uri fileUri = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                fileUri = FileProvider.getUriForFile(context, "map.provider", file);
            } else {
                fileUri = Uri.fromFile(file);
            }
            i.setDataAndType(fileUri, "application/vnd.android.package-archive");
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            context.startActivity(i);
        }catch (Exception e){
            Log.e(TAG,"安装失败");
            e.printStackTrace();
        }
    }
}
