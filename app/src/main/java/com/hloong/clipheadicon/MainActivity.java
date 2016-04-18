package com.hloong.clipheadicon;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;


import com.hloong.clipheadicon.R;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * http://blog.csdn.net/lmj623565791/article/details/39761281
 * 基于http://blog.csdn.net/xiechengfa/article/details/45702427
 * 修改
 */
public class MainActivity extends Activity {
    private final int LOCAL = 1;
    private final int CAMERA = 2;
    private final int CUT = 3;
    public static final String IMAGE_FILE_NAME = "clip_temp.jpg";
    public static final String RESULT_PATH = "result_path";
    public static final String PASS_PATH = "pass_path";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_local).setOnClickListener(l);
        findViewById(R.id.btn_camera).setOnClickListener(l);
    }

    View.OnClickListener l = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_local:
                    Intent intentFromGallery;
                    if (android.os.Build.VERSION.SDK_INT >= 19) { // 判断是不是4.4
                        intentFromGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    } else {
                        intentFromGallery = new Intent(Intent.ACTION_GET_CONTENT);
                    }
                    intentFromGallery.setType("image/*"); // 设置文件类型
                    startActivityForResult(intentFromGallery,LOCAL);
                    break;
                case R.id.btn_camera:
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getFile()));
                    startActivityForResult(intent, CAMERA);
                    break;
            }
        }
    };

    /**
     * 获取file的时候如果没有路径就重新创建
     * @return
     */
    private File getFile() {
        File file = new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }
        return file;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case CUT:
                String path = data.getStringExtra(RESULT_PATH);
                Bitmap photo = BitmapFactory.decodeFile(path);
                ImageView imageView = (ImageView) findViewById(R.id.iv);
                imageView.setImageBitmap(photo);
                //在此处来做图片的上传处理

                break;
            case LOCAL:
                startCropImageActivity(getFilePath(data.getData()));
                break;
            case CAMERA:
                // 照相机程序返回的,再次调用图片剪辑程序去修剪图片
                startCropImageActivity(Environment.getExternalStorageDirectory()+ "/" + IMAGE_FILE_NAME);
                break;
        }
    }

    private void startCropImageActivity(String path){
        Intent intent = new Intent(this, ClipImageActivity.class);
        intent.putExtra(PASS_PATH, path);
        startActivityForResult(intent, CUT);
    }

    /**
     * 通过uri获取文件路径
     *
     * @param mUri
     * @return
     */
    public String getFilePath(Uri mUri) {
        try {
            if (mUri.getScheme().equals("file")) {
                return mUri.getPath();
            } else {
                return getFilePathByUri(mUri);
            }
        } catch (FileNotFoundException ex) {
            return null;
        }
    }

    // 获取文件路径通过url
    private String getFilePathByUri(Uri mUri) throws FileNotFoundException {
        Cursor cursor = getContentResolver() .query(mUri, null, null, null, null);
        cursor.moveToFirst();
        return cursor.getString(1);
    }
}
