package cn.com.i_zj.udrive_az.utils;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.utils.dialog.DownAppDialog;

/**
 * apk检查更新类
 */
public class DownloadApk {
    private Activity context;
    private String updateFilePath;
    private static final int DOWN_OK = 1;
    private static final int DOWN_ERROR = 0;
    private static final int DOWN_STEP = 2;
    private String down_url;
    private static final int TIMEOUT = 10 * 1000;
    private DownAppDialog downloadDialog;
    private OnDownloadApkListener onDownloadApkListener;

    public DownloadApk(Activity context) {
        this.context = context;
        try {
//            onDownloadApkListener = (OnDownloadApkListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.getClass().getName()
                    + " must implement OnDownloadApkListener");
        }
    }

    /**
     * 强制更新
     */
    public void isForce() {
        if (downloadDialog != null)
            downloadDialog.isForce(true);
    }

    public boolean isShow() {
        return downloadDialog.isShowing();
    }
    public void downloadApk(String downUrl) {
        this.down_url = downUrl;

        downloadDialog = new DownAppDialog(context, R.style.UpdateDialogStytle);
        isForce();
        updateFilePath = createFile("nxnk" + ".apk");
        doDownloadApk(updateFilePath);
    }

    private String createFile(String name) {
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

    private void doDownloadApk(final String filePath) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean error = false;
                final Message message = new Message();
                try {
                    if (downloadUpdateFile(down_url, filePath) > 0) {
                        message.what = DOWN_OK;
                        handler.sendMessage(message);
                        error = false;
                    } else {
                        error = true;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    error = true;
                }
                if (error) {
                    File file = new File(filePath);
                    if (null != file && file.exists()) {
                        file.delete();
                    }
                    message.what = DOWN_ERROR;
                    handler.sendMessage(message);
                }
            }
        }).start();
    }

    private long downloadUpdateFile(String down_url, String file)
            throws IOException {
        URL url;
        try {
            url = new URL(down_url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return 0;
        }

        int down_step = 5;
        int totalSize;
        long downloadCount = 0;
        int updateCount = 0;
        InputStream inputStream;
        OutputStream outputStream;

        HttpURLConnection httpURLConnection = (HttpURLConnection) url
                .openConnection();
        httpURLConnection.setConnectTimeout(TIMEOUT);
        httpURLConnection.setReadTimeout(TIMEOUT);
        // get the total size of file
        totalSize = httpURLConnection.getContentLength();
        if (httpURLConnection.getResponseCode() == 404) {
            return 0;
        }
        inputStream = httpURLConnection.getInputStream();
        outputStream = new FileOutputStream(file, false);// overlay if file
        byte buffer[] = new byte[1024];
        int readsize = 0;
        while ((readsize = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, readsize);
            downloadCount += readsize;
            if (updateCount == 0 ||
                    (downloadCount * 100 / totalSize - down_step) >= updateCount) {
                updateCount += down_step;

            }

            if (downloadCount < totalSize) {

                final Message message = new Message();
                message.what = DOWN_STEP;
                message.arg1 = (int) (downloadCount * 100 / totalSize);
                message.obj = (downloadCount / 1024 / 1024) + "MB/" + (totalSize / 1024 / 1024) + "MB";
                handler.sendMessage(message);
            }


        }
        if (httpURLConnection != null) {
            httpURLConnection.disconnect();
        }
        inputStream.close();
        outputStream.close();

        return downloadCount;

    }


    private void installFile() {
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


    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case DOWN_STEP:
                    downStep(msg);
                    break;
                case DOWN_OK:
                    downOk();
                    installFile();
                    break;
                case DOWN_ERROR:
                    downError();
                    break;
            }
            return false;
        }

    });

    private void downStep(Message msg) {
//		mApkStep.setText(String.format("正在下载客户端(%1$d %2$s)", msg.arg1, "%"));
        downloadDialog.setInfo(msg.obj.toString(), msg.arg1);
    }

    private void downOk() {
        if (null != downloadDialog && downloadDialog.isShowing()) {
            downloadDialog.dismiss();
        }
        downloadDialog = null;
    }


    private void downError() {

        Toast.makeText(context,
                "下载客户端失败！",
                Toast.LENGTH_SHORT).show();
        if (onDownloadApkListener != null) {
            onDownloadApkListener.onDownError();
        }

    }


    public interface OnDownloadApkListener {
        void onDownError();//下载失败
    }

}
