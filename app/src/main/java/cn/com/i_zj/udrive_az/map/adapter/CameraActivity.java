package cn.com.i_zj.udrive_az.map.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.i_zj.udrive_az.DBSBaseActivity;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.model.CarPartPicture;
import cn.com.i_zj.udrive_az.utils.BitmapUtils;
import cn.com.i_zj.udrive_az.utils.CameraUtil;
import cn.com.i_zj.udrive_az.widget.MaskPierceView;

public class CameraActivity extends DBSBaseActivity implements SurfaceHolder.Callback, View.OnClickListener {

    @BindView(R.id.photo_view)
    ImageView imagePhoto;
    @BindView(R.id.flash_light)
    ImageView flash_light;
    @BindView(R.id.camera_back)
    ImageView camera_back;
    @BindView(R.id.surfaceView)
    SurfaceView surfaceView;
    @BindView(R.id.take_photo_layout)
    RelativeLayout takePhotoLayout;
    @BindView(R.id.sure_layout)
    RelativeLayout sureLayout;
    @BindView(R.id.finishLayout)
    LinearLayout finishLayout;
    @BindView(R.id.mask_pierce)
    MaskPierceView maskPierceView;


    @BindView(R.id.backPhoto)
    ImageView backPhoto;
    @BindView(R.id.rightFrontPhoto)
    ImageView rightFrontPhoto;
    @BindView(R.id.leftFrontPhoto)
    ImageView leftFrontPhoto;
    @BindView(R.id.innerPhoto)
    ImageView innerPhoto;

    @BindView(R.id.backPhoto_layout1)
    ImageView backPhotoLayout;
    @BindView(R.id.rightFrontPhoto_layout1)
    ImageView rightFrontPhotoLayout;
    @BindView(R.id.leftFrontPhoto_layout1)
    ImageView leftFrontPhotoLayout;
    @BindView(R.id.innerPhoto_layout1)
    ImageView innerPhotoLayout;

    private Context context;
    private Camera mCamera;
    private SurfaceHolder mHolder;
    private int mCameraId = 0;
    //闪光灯模式 0:关闭 1: 开启
    private int light_num = 0;
    private boolean isView = false;
    private String img_path;

    private int state; //0:正常拍照   1：查看图片   2: 后拍照
    private int currentPosition = 0; //state=2时使用
    private CarPartPicture carPart;
    private String backPath, rightFrontPath, leftFrontPath, innerPath;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_camera2;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        context = this;
        mHolder = surfaceView.getHolder();
        mHolder.addCallback(this);

        state = getIntent().getIntExtra("state", 0);
        if (state != 2) {
            carPart = (CarPartPicture) getIntent().getSerializableExtra("part");
            if (carPart.hasPhoto() && !TextUtils.isEmpty(carPart.getPhotoPath())) {
                state = 1;
                camera_back.setVisibility(View.INVISIBLE);
                finishLayout.setVisibility(View.GONE);
                takePhotoLayout.setVisibility(View.GONE);
                sureLayout.setVisibility(View.VISIBLE);
                finishLayout.setVisibility(View.GONE);


                Uri uri = Uri.fromFile(new File(carPart.getPhotoPath()));
                imagePhoto.setVisibility(View.VISIBLE);
                imagePhoto.setScaleType(ImageView.ScaleType.FIT_XY);
                imagePhoto.setImageURI(uri);
                maskPierceView.black(true);
            }
        } else {
            if (!TextUtils.isEmpty(getIntent().getStringExtra("backPath"))) {
                backPath = getIntent().getStringExtra("backPath");
                backPhoto.setImageURI(Uri.fromFile(new File(backPath)));
            }
            if (!TextUtils.isEmpty(getIntent().getStringExtra("rightFrontPath"))) {
                rightFrontPath = getIntent().getStringExtra("rightFrontPath");
                rightFrontPhoto.setImageURI(Uri.fromFile(new File(rightFrontPath)));
            }
            if (!TextUtils.isEmpty(getIntent().getStringExtra("leftFrontPath"))) {
                leftFrontPath = getIntent().getStringExtra("leftFrontPath");
                leftFrontPhoto.setImageURI(Uri.fromFile(new File(leftFrontPath)));
            }
            if (!TextUtils.isEmpty(getIntent().getStringExtra("innerPath"))) {
                innerPath = getIntent().getStringExtra("innerPath");
                innerPhoto.setImageURI(Uri.fromFile(new File(innerPath)));
            }
            finishLayout.setVisibility(View.VISIBLE);
        }
    }

    @OnClick({R.id.img_camera, R.id.camera_back, R.id.flash_light, R.id.retake_picture, R.id.sure})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_camera:
                if (isView) {
                    switch (light_num) {
                        case 0:
                            CameraUtil.getInstance().turnLightOff(mCamera);
                            break;
                        case 1:
                            CameraUtil.getInstance().turnLightOn(mCamera);
                            break;
                    }
                    capture();
                    isView = false;
                }
                break;
            //退出相机界面 释放资源
            case R.id.camera_back:
                if (state == 2) {
                    Intent intent = getIntent();//backPath, rightFrontPath, leftFrontPath, innerPath
                    intent.putExtra("backPath", backPath);
                    intent.putExtra("rightFrontPath", rightFrontPath);
                    intent.putExtra("leftFrontPath", leftFrontPath);
                    intent.putExtra("innerPath", innerPath);
                    setResult(RESULT_OK, intent);
                }
                finish();
                break;
            //闪光灯
            case R.id.flash_light:
                Camera.Parameters parameters = mCamera.getParameters();
                switch (light_num) {
                    case 0:
                        //打开
                        light_num = 1;
                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);//开启
                        mCamera.setParameters(parameters);
                        flash_light.setImageResource(R.drawable.ic_on_flashlight);
                        break;
                    case 1:
                        //关闭
                        light_num = 0;
                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                        mCamera.setParameters(parameters);
                        flash_light.setImageResource(R.drawable.ic_off_flashlight);
                        break;
                }
                break;
            case R.id.retake_picture:
                takePhotoLayout.setVisibility(View.VISIBLE);
                camera_back.setVisibility(View.VISIBLE);
                sureLayout.setVisibility(View.GONE);
                imagePhoto.setVisibility(View.GONE);
                maskPierceView.black(false);
                startPreview(mCamera, mHolder);
                break;
            case R.id.sure:
                if (state == 0) {
                    carPart.setPhotoPath(img_path);
                    carPart.setHasPhoto(true);
                    Intent intent = getIntent();
                    intent.putExtra("part", carPart);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCamera == null) {
            mCamera = getCamera(mCameraId);
            if (mHolder != null && state != 1) {
                startPreview(mCamera, mHolder);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    /**
     * 获取Camera实例
     *
     * @return
     */
    private Camera getCamera(int id) {
        Camera camera = null;
        try {
            camera = Camera.open(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return camera;
    }

    /**
     * 预览相机
     */
    private void startPreview(Camera camera, SurfaceHolder holder) {
        try {
            setupCamera(camera);
            camera.setPreviewDisplay(holder);
            CameraUtil.getInstance().setCameraDisplayOrientation(this, mCameraId, camera);
            camera.startPreview();
            isView = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void capture() {
        mCamera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                isView = false;
                //将data 转换为位图 或者你也可以直接保存为文件使用 FileOutputStream
                //这里我相信大部分都有其他用处把 比如加个水印 后续再讲解
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                Bitmap saveBitmap = CameraUtil.getInstance().setTakePicktrueOrientation(mCameraId, bitmap);

                img_path = getExternalFilesDir(Environment.DIRECTORY_DCIM).getPath() +
                        File.separator + System.currentTimeMillis() + ".jpeg";

                BitmapUtils.saveJPGE_After(context, saveBitmap, img_path, 100);

                if (!bitmap.isRecycled()) {
                    bitmap.recycle();
                }

                if (!saveBitmap.isRecycled()) {
                    saveBitmap.recycle();
                }

                if (state == 2) {
                    updateImage(img_path);
                    mCamera.stopPreview();
                    startPreview(mCamera, mHolder);
                } else {
                    mCamera.stopPreview();
                    maskPierceView.black(true);
                    camera_back.setVisibility(View.GONE);
                    finishLayout.setVisibility(View.GONE);
                    takePhotoLayout.setVisibility(View.GONE);
                    sureLayout.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void updateImage(String imgPath) {
        switch (currentPosition) {
            case 0:
                innerPhoto.setImageURI(Uri.fromFile(new File(imgPath)));
                innerPhotoLayout.setVisibility(View.GONE);
                leftFrontPhotoLayout.setVisibility(View.VISIBLE);
                rightFrontPhotoLayout.setVisibility(View.GONE);
                backPhotoLayout.setVisibility(View.GONE);

                innerPath = imgPath;
                currentPosition++;
                break;
            case 1:
                leftFrontPhoto.setImageURI(Uri.fromFile(new File(imgPath)));
                innerPhotoLayout.setVisibility(View.GONE);
                leftFrontPhotoLayout.setVisibility(View.GONE);
                rightFrontPhotoLayout.setVisibility(View.VISIBLE);
                backPhotoLayout.setVisibility(View.GONE);

                leftFrontPath = imgPath;
                currentPosition++;
                break;
            case 2:
                rightFrontPhoto.setImageURI(Uri.fromFile(new File(imgPath)));
                innerPhotoLayout.setVisibility(View.GONE);
                leftFrontPhotoLayout.setVisibility(View.GONE);
                rightFrontPhotoLayout.setVisibility(View.GONE);
                backPhotoLayout.setVisibility(View.VISIBLE);

                rightFrontPath = imgPath;
                currentPosition++;
                break;
            case 3:
                backPhoto.setImageURI(Uri.fromFile(new File(imgPath)));
                innerPhotoLayout.setVisibility(View.VISIBLE);
                leftFrontPhotoLayout.setVisibility(View.GONE);
                rightFrontPhotoLayout.setVisibility(View.GONE);
                backPhotoLayout.setVisibility(View.GONE);

                backPath = imgPath;
                currentPosition = 0;
                break;
        }
    }

    private void setupCamera(Camera camera) {
        Camera.Parameters parameters = camera.getParameters();

        if (parameters.getSupportedFocusModes().contains(
                Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }

        //这里第三个参数为最小尺寸 getPropPreviewSize方法会对从最小尺寸开始升序排列 取出所有支持尺寸的最小尺寸
        Camera.Size previewSize = CameraUtil.getInstance().getPropSizeForHeight(parameters.getSupportedPreviewSizes(), 800);
        parameters.setPreviewSize(previewSize.width, previewSize.height);

        Camera.Size pictureSize = CameraUtil.getInstance().getPropSizeForHeight(parameters.getSupportedPictureSizes(), 800);
        parameters.setPictureSize(pictureSize.width, pictureSize.height);

        camera.setParameters(parameters);
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        startPreview(mCamera, holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mCamera.stopPreview();
        startPreview(mCamera, holder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        releaseCamera();
    }
}
