package cn.com.i_zj.udrive_az.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;

public class FileUtil {

    public static File getSaveFile(Context context) {
        return new File(context.getFilesDir(), "pic.jpg");
    }
    public static boolean externalExist() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return false;
        }
        return true;
    }

    public static String getFilePathName(String... args) {
        String[] param = args;
        String Str = param[0];
        for (int i = 1, len = param.length; i < len; i++) {
            if (null == Str)
                return null;
            if (Str.endsWith(File.separator) && param[i].startsWith(File.separator))
                Str = Str.substring(0, Str.length() - 1) + param[i];
            else if (Str.endsWith(File.separator) || param[i].startsWith(File.separator))
                Str += param[i];
            else
                Str += File.separator + param[i];
        }
        return Str;
    }

    /**
     * 递归删除 文件/文件夹
     *
     * @param file
     */
    public static void deleteFile(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteFile(files[i]);
                }
            }
            file.delete();
        }
    }
}
