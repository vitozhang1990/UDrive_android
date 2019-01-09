package cn.com.i_zj.udrive_az.map.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.i_zj.udrive_az.DBSBaseActivity;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.utils.BitmapUtils;
import cn.com.i_zj.udrive_az.utils.CameraUtil;

public class CameraActivity extends DBSBaseActivity implements SurfaceHolder.Callback, View.OnClickListener {

    @BindView(R.id.image_photo)
    ImageView imagePhoto;
    @BindView(R.id.flash_light)
    ImageView flash_light;
    @BindView(R.id.surfaceView)
    SurfaceView surfaceView;

    private Context context;
    private Camera mCamera;
    private SurfaceHolder mHolder;
    private int mCameraId = 0;
    //闪光灯模式 0:关闭 1: 开启
    private int light_num = 0;
    private boolean isView = false;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_camera2;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        mHolder = surfaceView.getHolder();
        mHolder.addCallback(this);
    }

    @OnClick({R.id.img_camera, R.id.camera_back, R.id.flash_light})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_camera:
                if (isView) {
                    switch (light_num) {
                        case 0:
                            CameraUtil.getInstance().turnLightOn(mCamera);
                            break;
                        case 1:
                            CameraUtil.getInstance().turnLightOff(mCamera);
                            break;
                    }
                    capture();
                    isView = false;
                }
                break;
            //退出相机界面 释放资源
            case R.id.camera_back:
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
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCamera == null) {
            mCamera = getCamera(mCameraId);
            if (mHolder != null) {
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

                String img_path = getExternalFilesDir(Environment.DIRECTORY_DCIM).getPath() +
                        File.separator + System.currentTimeMillis() + ".jpeg";

                imagePhoto.setVisibility(View.VISIBLE);
                imagePhoto.setScaleType(ImageView.ScaleType.FIT_XY);
                imagePhoto.setImageBitmap(saveBitmap);

                BitmapUtils.saveJPGE_After(context, saveBitmap, img_path, 100);

//                if (!bitmap.isRecycled()) {
//                    bitmap.recycle();
//                }

//                if (!saveBitmap.isRecycled()) {
//                    saveBitmap.recycle();
//                }


//                Intent intent = new Intent();
//                intent.putExtra(AppConstant.KEY.IMG_PATH, img_path);
//                intent.putExtra(AppConstant.KEY.PIC_WIDTH, screenWidth);
//                intent.putExtra(AppConstant.KEY.PIC_HEIGHT, picHeight);
//                setResult(AppConstant.RESULT_CODE.RESULT_OK, intent);
//                finish();
            }
        });
    }

    /**
     * 设置
     */
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
