package cn.com.i_zj.udrive_az.lz.ui.accountinfo;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.zjcx.face.camera.CameraThreadPool;
import com.zjcx.face.camera.CameraView;
import com.zjcx.face.camera.ICameraControl;
import com.zjcx.face.camera.MaskView;
import com.zjcx.face.camera.OCRCameraLayout;
import com.zjcx.face.crop.CropView;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.i_zj.udrive_az.DBSBaseActivity;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.lz.bean.CameraEvent;
import cn.com.i_zj.udrive_az.view.FrameOverlayView;

/**
 * @author JayQiu
 * @create 2018/10/31
 * @Describe 认证拍照相机
 */
public class ActIdentificationCameraTwo extends DBSBaseActivity {
    @BindView(R.id.camera_view)
    CameraView cameraView;
    @BindView(R.id.iv_light)
    ImageView ivLight;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.iv_camera)
    ImageView ivCamera;
    public static final String TYPE_KEY = "type";
    @BindView(R.id.tv_ok)
    TextView tvOk;
    @BindView(R.id.tv_remake)
    TextView tvRemake;
    @BindView(R.id.crop_view)
    CropView cropView;
    @BindView(R.id.crop_mask_view)
    MaskView cropMaskView;
    @BindView(R.id.overlay_view)
    com.zjcx.face.crop.FrameOverlayView overlayView;
    @BindView(R.id.display_image_view)
    ImageView displayImageView;
    private int actionType = 0;
    private boolean isFlashlight = false;
    public static final int IDCARD_POSITIVE = 1;//身份证正面
    public static final int IDCARD_REVERSE = 2;//身份证反面
    public static final int DRIVING_LICENSE_POSITIVE = 3;//驾驶证正面
    public static final int DRIVING_LICENSE_REVERSE = 4;//4驾驶证反面
    private File iamgeFile;
    private Handler handler = new Handler();

    /**
     * @param context
     * @param type    1.身份证正面，2.身份证反面，3.驾驶证正面，4驾驶证反面
     */
    public static void startActIdentificationCamera(Context context, int type) {
        Intent intent = new Intent(context, ActIdentificationCameraTwo.class);
        intent.putExtra(TYPE_KEY, type);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_identification_camera_two;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        String fileName = Environment.getExternalStorageDirectory().toString()
                + File.separator
                + "tempCamera"
                + File.separator
                + "nxnk_temp_" + System.currentTimeMillis() + ".jpg";
        iamgeFile = new File(fileName);
        actionType = getIntent().getIntExtra(TYPE_KEY, 0);
        setOrientation(getResources().getConfiguration());
        initView();
    }

    private void initView() {
        int maskType = MaskView.MASK_TYPE_ID_CARD_FRONT;
        switch (actionType) {
            case IDCARD_POSITIVE:
                maskType = MaskView.MASK_TYPE_ID_CARD_FRONT;
                overlayView.setVisibility(View.INVISIBLE);
                break;

            case IDCARD_REVERSE:
                maskType = MaskView.MASK_TYPE_ID_CARD_BACK;
                overlayView.setVisibility(View.INVISIBLE);
                break;
            case DRIVING_LICENSE_POSITIVE:
                maskType = MaskView.MASK_TYPE_DRIVING_LICENSE_FRONT;
                overlayView.setVisibility(View.INVISIBLE);
                break;
            case DRIVING_LICENSE_REVERSE:
                maskType = MaskView.MASK_TYPE_DRIVING_LICENSE_BACK;
                overlayView.setVisibility(View.INVISIBLE);
                break;

        }
        cameraView.setEnableScan(true);
        cameraView.setMaskType(maskType, this);
        cropMaskView.setMaskType(maskType);
    }

    private void setOrientation(Configuration newConfig) {
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        int cameraViewOrientation = CameraView.ORIENTATION_PORTRAIT;
        switch (newConfig.orientation) {
            case Configuration.ORIENTATION_PORTRAIT:
                cameraViewOrientation = CameraView.ORIENTATION_PORTRAIT;
                break;
            case Configuration.ORIENTATION_LANDSCAPE:
                if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_90) {
                    cameraViewOrientation = CameraView.ORIENTATION_HORIZONTAL;
                } else {
                    cameraViewOrientation = CameraView.ORIENTATION_INVERT;
                }
                break;
            default:
                cameraView.setOrientation(CameraView.ORIENTATION_PORTRAIT);
                break;
        }
        cameraView.setOrientation(cameraViewOrientation);
    }

    @OnClick({R.id.iv_light, R.id.iv_back, R.id.iv_camera, R.id.tv_ok, R.id.tv_remake})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_light:

                if (cameraView.getCameraControl().getFlashMode() == ICameraControl.FLASH_MODE_OFF) {
                    cameraView.getCameraControl().setFlashMode(ICameraControl.FLASH_MODE_TORCH);
                } else {
                    cameraView.getCameraControl().setFlashMode(ICameraControl.FLASH_MODE_OFF);
                }
                updateFlashMode();
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_camera:
                tackPicture();
                break;
            case R.id.tv_ok:
                doConfirmResult();


                break;
            case R.id.tv_remake:
                if (iamgeFile != null) {
                    if (iamgeFile.exists()) {
                        iamgeFile.delete();
                    }
                }
                tvOk.setVisibility(View.INVISIBLE);
                tvRemake.setVisibility(View.INVISIBLE);
                ivCamera.setVisibility(View.VISIBLE);
                displayImageView.setVisibility(View.GONE);
                cameraView.getCameraControl().resume();
                break;
        }
    }
    private void updateFlashMode() {
        int flashMode = cameraView.getCameraControl().getFlashMode();
        if (flashMode == ICameraControl.FLASH_MODE_TORCH) {
            ivLight.setImageResource(R.mipmap.ic_lighton);
        } else {
            ivLight.setImageResource(R.mipmap.ic_lightoff);
        }
    }

    private void tackPicture() {
        cameraView.takePicture(iamgeFile, takePictureCallback);
    }

    private CameraView.OnTakePictureCallback takePictureCallback = new CameraView.OnTakePictureCallback() {
        @Override
        public void onPictureTaken(final Bitmap bitmap) {
            handler.post(new Runnable() {
                @Override
                public void run() {

                    if (cropMaskView.getMaskType() == MaskView.MASK_TYPE_NONE) {
                        cropView.setFilePath(iamgeFile.getAbsolutePath());
                        showResultConfirm();
                    } else if (cropMaskView.getMaskType() == MaskView.MASK_TYPE_BANK_CARD) {
                        cropView.setFilePath(iamgeFile.getAbsolutePath());
                        displayImageView.setImageBitmap(bitmap);
                        cropMaskView.setVisibility(View.INVISIBLE);
                        overlayView.setVisibility(View.VISIBLE);
                        overlayView.setTypeWide();
                        showResultConfirm();
                    } else if (cropMaskView.getMaskType() == MaskView.MASK_TYPE_DRIVING_LICENSE_FRONT || cropMaskView.getMaskType() == MaskView.MASK_TYPE_DRIVING_LICENSE_BACK) {
                        displayImageView.setImageBitmap(bitmap);
                        showResultConfirm();
                    } else {
                        displayImageView.setImageBitmap(bitmap);
                        showResultConfirm();
                    }
                }
            });
        }
    };

    private void showResultConfirm() {
        cameraView.getCameraControl().pause();
        tvOk.setVisibility(View.VISIBLE);
        tvRemake.setVisibility(View.VISIBLE);
        ivCamera.setVisibility(View.INVISIBLE);
    }


    private void doConfirmResult() {
        CameraThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(iamgeFile);
                    Bitmap bitmap = ((BitmapDrawable) displayImageView.getDrawable()).getBitmap();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                    fileOutputStream.close();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                            EventBus.getDefault().post(new CameraEvent(actionType, iamgeFile.getPath()));
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        cameraView.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        cameraView.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CameraThreadPool.cancelAutoFocusTimer();
    }

}
