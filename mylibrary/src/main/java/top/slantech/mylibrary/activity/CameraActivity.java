package top.slantech.mylibrary.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;
import java.io.File;
import java.util.List;
import top.slantech.mylibrary.R;
import top.slantech.mylibrary.utils.FileUtils;
import top.slantech.mylibrary.utils.ImageRotateUtil;


/**
 * Created by admin on 2016/7/11 0011.
 */
public class CameraActivity extends Activity {

    private static final String Tag = CameraActivity.class.getName();
    private String mFilePath;//临时图片保存路径，参数可不传
    private String mFileName;//文件名称，参数可不传
    private String mPath;//包含文件名称的路径

    public static final int Camera_Request_Code = 1;
    /**
     * 运行时权限申请码
     */
    private final static int RUNTIME_PERMISSION_REQUEST_CODE = 0x1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();

        checkPerssion();
    }

    /**
     * 初始化字段
     */
    private void initData() {
        mFilePath = getIntent().getStringExtra("filepath");
        if (mFilePath == null || mFilePath.equals(""))
            mFilePath = FileUtils.getFilePath();
        if (mFilePath == null || mFilePath.equals("")) {
            Toast.makeText(this, getString(R.string.sd_not_exist), Toast.LENGTH_SHORT).show();
            finish();
        }

        mFileName = getIntent().getStringExtra("filename");
        if (mFileName == null || mFileName.equals(""))
            mFileName = System.currentTimeMillis() + ".jpg";

        mPath = mFilePath + "/" + mFileName;
        Log.e(Tag, "path-" + mPath);
    }

    /**
     * 检查权限
     */
    private void checkPerssion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //Android M 处理Runtime Permission
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //检查是否有写入SD卡的授权
                openCamera();
            } else {
                requestPermission();
            }
        } else {
            openCamera();
        }
    }

    /**
     * 打开相机
     */
    private void openCamera() {
        // 检查相机是否可用
        if (hasCamera()) {
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            Uri uri = Uri.fromFile(new File(mPath));
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(intent, Camera_Request_Code);
        } else
            Toast.makeText(this, getString(R.string.camera_not_exist), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Camera_Request_Code && resultCode == RESULT_OK) {
            // 获取图片的角度
            int degree = ImageRotateUtil.getBitmapDegree(mPath); // 检查图片的旋转角度
            if (degree != 0)
                mPath = ImageRotateUtil.rotateBitmapByDegree(mPath, degree);
            Intent intent = new Intent();
            intent.putExtra("filepath", mPath);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    /**
     * 判断系统中是否存在可以启动的相机应用
     *
     * @return 存在返回true，不存在返回false
     */
    public boolean hasCamera() {
        PackageManager packageManager = getPackageManager();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    /**
     * 申请写入sd卡的权限
     */
    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, RUNTIME_PERMISSION_REQUEST_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RUNTIME_PERMISSION_REQUEST_CODE) {
            for (int index = 0; index < permissions.length; index++) {
                String permission = permissions[index];
                if (Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(permission)) {
                    if (grantResults[index] == PackageManager.PERMISSION_GRANTED) {
                        openCamera();
                    } else {
                        showMissingPermissionDialog();
                    }
                }
            }
        }
    }

    /**
     * 显示打开权限提示的对话框
     */
    private void showMissingPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.help);
        builder.setMessage(R.string.help_content);

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(CameraActivity.this, R.string.camera_not_exist, Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        builder.setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                turnOnSettings();
            }
        });

        builder.show();
    }

    /**
     * 启动系统权限设置界面
     */
    private void turnOnSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

}
