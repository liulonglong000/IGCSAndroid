package com.xxs.igcsandroid.util;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.image.SmartImageView;
import com.xxs.igcsandroid.golbal.Constants;

import java.io.File;
import java.io.FileOutputStream;

public class HandlePic {
    private static final int PHOTO_REQUEST_CAREMA = 1;              // 拍照
    private static final int PHOTO_REQUEST_GALLERY = 2;             // 从相册中选择
    private static final int PHOTO_REQUEST_CUT_CAREMA = 3;          // 结果
    private static final int PHOTO_REQUEST_CUT_GALLERY = 4;         // 结果
    private static final int TAKE_PHOTO_REQUEST_CODE = 5;
    private static final int EXTERNAL_STORAGE_REQ_CODE = 6;

    private Activity mActivity;

    private SmartImageView ivImage;
    private Button btnSelPic;
    private Button btnShotPic;
    private Button btnDelPic;
    private String picSrcFile;

    private static final String PHOTO_FILE_NAME = "temp_photo.jpg";
    private File tempFile;      // 照相生成的临时文件
    private Uri tempUri;        // 照相生成的临时文件
    private Bitmap bitmapSel;   // 要上传的文件
    private File fileSel;       // 要上传的文件

    public HandlePic(Activity context) {
        mActivity = context;
    }

    /*
     * 判断sdcard是否被挂载
     */
    private boolean hasSdcard() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    public void handleSelPic(Button btn) {
        btnSelPic = btn;
        btnSelPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 激活系统图库，选择一张图片
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_GALLERY
                mActivity.startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
            }
        });
    }

    public void handleShotPic(Button btn) {
        btnShotPic = btn;
        if (hasSdcard()) {
            btnShotPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.CAMERA}, TAKE_PHOTO_REQUEST_CODE);
                    } else {
                        startTakePhoto();
                    }
                }
            });
        } else {
            btnShotPic.setEnabled(false);
        }
    }

    private void startTakePhoto() {
        // 激活相机
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        tempFile = new File(Environment.getExternalStorageDirectory(), PHOTO_FILE_NAME);
        // 判断版本大于等于7.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tempUri = FileProvider.getUriForFile(mActivity, "com.xxs.igcsandroid.fileprovider", tempFile);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            tempUri = Uri.fromFile(tempFile);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);

        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CAREMA
        mActivity.startActivityForResult(intent, PHOTO_REQUEST_CAREMA);
    }

    public void handleDelPic(Button btn) {
        btnDelPic = btn;
        btnDelPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivImage.setVisibility(View.GONE);
                picSrcFile = "";
                btnDelPic.setVisibility(View.GONE);
                btnSelPic.setVisibility(View.VISIBLE);
                btnShotPic.setVisibility(View.VISIBLE );
            }
        });
    }

    public void setInfoByPic(String picPath) {
        picSrcFile = picPath;

        if (picPath.length() > 0) {
            ivImage.setImageUrl(Constants.SERVER_URL + "downloadFile/downloadFile?inputPath=" + picPath);
            ivImage.setVisibility(View.VISIBLE);
            btnDelPic.setVisibility(View.VISIBLE);
            btnSelPic.setVisibility(View.GONE);
            btnShotPic.setVisibility(View.GONE);
        }
    }

    public void onActivityResult(int requestCode, Intent data) {
        if (requestCode == PHOTO_REQUEST_GALLERY) {
            // 从相册返回的数据
            if (data != null) {
                // 得到图片的全路径
                Uri uri = data.getData();
                crop(uri, PHOTO_REQUEST_CUT_GALLERY);
            }
        } else if (requestCode == PHOTO_REQUEST_CAREMA) {
            // 从相机返回的数据
            crop(tempUri, PHOTO_REQUEST_CUT_CAREMA);
        } else if (requestCode == PHOTO_REQUEST_CUT_GALLERY) {
            // 从剪切图片返回的数据
            updateUIAfterSelPic(data);
        } else if (requestCode == PHOTO_REQUEST_CUT_CAREMA) {
            // 从剪切图片返回的数据
            updateUIAfterSelPic(data);

            try {
                // 将临时文件删除
                tempFile.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*
     * 剪切图片
     */
    private void crop(Uri uri, int requestCode) {
        // 裁剪图片意图
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // 裁剪框的比例，1：1
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // 裁剪后输出图片的尺寸大小
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);

        intent.putExtra("outputFormat", "JPEG");// 图片格式
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
        intent.putExtra("return-data", true);

        mActivity.startActivityForResult(intent, requestCode);
    }

    private void updateUIAfterSelPic(Intent data) {
        if (data != null) {
            bitmapSel = data.getParcelableExtra("data");
            ivImage.setImageBitmap(bitmapSel);
            ivImage.setVisibility(View.VISIBLE);
            btnDelPic.setVisibility(View.VISIBLE);
            btnSelPic.setVisibility(View.GONE);
            btnShotPic.setVisibility(View.GONE );
        }
    }

    public void getFileToUpdate(String fileName) {
        if ((ivImage.getVisibility() == View.GONE) || (bitmapSel == null)) {
            resetFileSel();
            return;
        }

        if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_REQ_CODE);
        } else {
            saveBitmapToFile(fileName);
        }
    }

    private void saveBitmapToFile(String fileName) {
        resetFileSel();

        fileSel = new File(Environment.getExternalStorageDirectory(), fileName + ".jpeg");
        try {
            fileSel.createNewFile();
            FileOutputStream out = new FileOutputStream(fileSel);
            if (bitmapSel.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
                out.flush();
                out.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            resetFileSel();
            DlgUtil.showMsgInfo(mActivity, "生成文件失败：" + fileName);
        }
    }

    public void resetFileSel() {
        if (fileSel != null) {
            fileSel.delete();
        }
        fileSel = null;
    }

    public File getFileSel() {
        return fileSel;
    }

    public String getPicSrcFile() {
        return picSrcFile;
    }

    public void setImageView(SmartImageView iv) {
        ivImage = iv;
    }
}
