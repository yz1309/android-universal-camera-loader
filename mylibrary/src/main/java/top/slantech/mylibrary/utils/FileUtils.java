package top.slantech.mylibrary.utils;

import android.os.Environment;

import java.io.File;

/**
 * Created by admin on 2016/7/11 0011.
 */
public class FileUtils {
    private static String mFilePath = "slantech/ota/camera_img/";

    /**
     * 获取拍照图片保存的路径
     * @return
     */
    public static String getFilePath() {
        if (!isCanUseSD())
            return "";
        else {
            String path = Environment.getExternalStorageDirectory() + "/" + mFilePath;
            File file = new File(path);
            if (!file.exists())
                file.mkdirs();

            return file.getAbsolutePath();
        }
    }

    /**
     * 描述：SD卡是否能用.
     *
     * @return true 可用,false不可用
     */
    public static boolean isCanUseSD() {
        try {
            return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
