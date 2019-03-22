package cn.com.i_zj.udrive_az.utils;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

/**
 * @author JayQiu
 * @create 2018/11/6
 * @Describe
 */
public class AppDownloadManager {
    public void downloadManager(Activity context, String url) {
        try {
            Uri uri = Uri.parse(url);
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setAllowedOverRoaming(true);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            String updateFilePath = createDir(context);
            request.setDestinationInExternalFilesDir(context, updateFilePath, "nxnk.apk");
            request.setTitle("你行你开");
            request.setDescription("正在更新");
            request.setMimeType("application/vnd.android.package-archive");
            //在通知栏显示下载进度
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            }

            DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            long downloadId = dm.enqueue(request);
            downloadQuery(context, downloadId);
        } catch (Exception e) {

        }

    }

    /**
     * 监听下载进度
     *
     * @param activity
     * @param downloadId
     */
    private void downloadQuery(final Activity activity, final long downloadId) {


        // 注册广播监听系统的下载完成事件。
        IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                long ID = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (ID == downloadId) {

                    getApkFile(activity,ID);
                }
            }
        };

        activity.registerReceiver(broadcastReceiver, intentFilter);

    }

    private void getApkFile(Activity context, final long downloadId) {
        DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
        Cursor c = dm.query(query);
        if (c != null) {
            if (c.moveToFirst()) {
                String fileUri = c.getString(c.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI));
                //
                if (fileUri != null) {
                    installFile(context, fileUri);
                }else {
                    Toast.makeText(context,  " 下载失败!", Toast.LENGTH_LONG).show();
                }
                Log.e("getApkFile=====>", fileUri);

            }
            c.close();
        }
    }

    private String createFile(Context context, String name) {
        String path;
        if (FileUtil.externalExist()) {
            path = FileUtil.getFilePathName(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString(), "tempApk");
        } else {
            path = FileUtil.getFilePathName(context.getFilesDir().getAbsolutePath(), "tempApk");
        }
        File updateDir = new File(path);
        if (!updateDir.exists()) {
            updateDir.mkdirs();
        }
        File updateFile = new File(FileUtil.getFilePathName(path, name));
        if (updateFile.exists()) {
            updateFile.delete();
        }
        try {
            updateFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return updateFile.getAbsolutePath();
    }
    private String createDir(Context context){
        String path;
        if (FileUtil.externalExist()) {
            path = FileUtil.getFilePathName(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString(), "tempApk");
        } else {
            path = FileUtil.getFilePathName(context.getFilesDir().getAbsolutePath(), "tempApk");
        }
        File updateDir = new File(path);
        if (!updateDir.exists()) {
            updateDir.mkdirs();
        }
        return path;
    }

    private void installFile(Activity context, String updateFilePath) {
//        updateFilePath=context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)+"/tempApk/nxnk.apk";
        updateFilePath = updateFilePath.replace("file://", "");
        File apkFile = new File(updateFilePath);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        //判断是否是AndroidN以及更高的版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(context, "cn.com.i_zj.udrive_az", apkFile);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }


}
