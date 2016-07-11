package top.slantech.android_universal_camera_loader;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import top.slantech.mylibrary.activity.CameraActivity;
import top.slantech.mylibrary.utils.ImageCompressUtil;

public class MainActivity extends AppCompatActivity {

    private ImageView iv;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1)
            {
                iv.setImageBitmap(BitmapFactory.decodeFile(msg.obj.toString()));
            }
            super.handleMessage(msg);

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_main);
        iv = (ImageView) findViewById(R.id.iv);
    }

    public void openCameraClick(View view) {
        Intent intent = new Intent(this, CameraActivity.class);
        startActivityForResult(intent, CameraActivity.Camera_Request_Code);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CameraActivity.Camera_Request_Code && resultCode == RESULT_OK) {
            final String path = data.getStringExtra("filepath");

            // 显示时，对图片进行压缩处理

            ImageCompressUtil.compressImage(this, path, new ImageCompressUtil.ProcessImgCallBack() {

                public void compressSuccess(String imgPath) {
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = imgPath;
                    handler.sendMessage(msg);
                }
            });

        } else
            super.onActivityResult(requestCode, resultCode, data);
    }

}
