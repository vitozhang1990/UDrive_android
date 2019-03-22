package cn.com.i_zj.udrive_az.lz.ui.camera;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.com.i_zj.udrive_az.BaseActivity;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.lz.bean.CameraEvent;
import cn.com.i_zj.udrive_az.lz.ui.idregister.IDRegisterActivity;
import cn.com.i_zj.udrive_az.lz.view.ScanBackgroundView;
import de.hdodenhof.circleimageview.CircleImageView;
import pub.devrel.easypermissions.EasyPermissions;

public class CameraActivity extends BaseActivity implements SurfaceHolder.Callback,
        EasyPermissions.PermissionCallbacks, View.OnClickListener {

    private FrameLayout mFrameLayout;
    private CircleImageView mIvCommit;
    private Unbinder unbinder;
    private Camera camera;
    private int codeCamera;

    public static final String CAMERA_TEMP_DIR_PATH = "/tempCamera";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        unbinder = ButterKnife.bind(this);

        ScanBackgroundView scanBackgroundView = findViewById(R.id.scan_bg);

        codeCamera = getIntent().getIntExtra(IDRegisterActivity.CODE_CAMERA, -1);
        if (IDRegisterActivity.CODE_ID == codeCamera) {
            scanBackgroundView.setVisibility(View.GONE);
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.lz_camera_title);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mFrameLayout = findViewById(R.id.frameLayout);
        mIvCommit = findViewById(R.id.iv_commit);
        mIvCommit.setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        checkCameraPermission();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mFrameLayout.removeAllViews();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        if (camera != null) {
            camera.release();
        }
    }

    /**
     * 添加摄像头到surfaceview中
     */
    private void createCamera() {
        SurfaceView surfaceView = new SurfaceView(this);
        SurfaceHolder holder = surfaceView.getHolder();
        camera = getCamera();
        if (camera == null) {
            Toast.makeText(this, R.string.lz_open_camera_fail, Toast.LENGTH_SHORT).show();
        } else {
            holder.addCallback(this);
        }
        mFrameLayout.addView(surfaceView);
    }

    /**
     * 获取摄像头对象,默认获取背面摄像头
     *
     * @return
     */
    private Camera getCamera() {
        int numberOfCameras = Camera.getNumberOfCameras();
        if (numberOfCameras == 0) {
            Toast.makeText(this, R.string.lz_camera_not_exists, Toast.LENGTH_SHORT).show();
            return null;
        }
        return Camera.open();
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

        try {
            if (camera != null) {
                Camera.Parameters parameters = camera.getParameters();
                List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();

                for (int i = 0; i < supportedPreviewSizes.size(); i++) {
                    System.out.println("width = " + supportedPreviewSizes.get(i).width + " height = " + supportedPreviewSizes.get(i).height);
                }
                camera.setPreviewDisplay(surfaceHolder);
                camera.startPreview();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int width, int height) {
        if (camera != null) {
            camera.stopPreview();
            //旋转90度
            camera.setDisplayOrientation(90);
            try {
                camera.setPreviewDisplay(surfaceHolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Camera.Parameters parameters = camera.getParameters();
                Camera.Size previerOptions = getOptimalSize(parameters.getSupportedPreviewSizes(), width, height);

                //使用第一个分辨率进行预览,默认最大分辨率
                parameters.setPreviewSize(previerOptions.width, previerOptions.height);

                Camera.Size pictureOptions = getOptimalSize(parameters.getSupportedPictureSizes(), width, height);

                parameters.setPictureSize(pictureOptions.width,pictureOptions.height);

                //自动对焦
                List<String> supportedFocusModes = parameters.getSupportedFocusModes();
                if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                } else if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                }
                camera.setParameters(parameters);

            } catch (Exception e) {
                e.printStackTrace();
            }
            camera.startPreview();
            camera.cancelAutoFocus();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if (camera != null) {
            camera.release();
        }
    }


    /**
     * 检测照相机权限
     */
    private void checkCameraPermission() {
        boolean b = EasyPermissions.hasPermissions(this,
                Manifest.permission.CAMERA);
        if (b) {
            //已经有权限,不需要申请
            createCamera();
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.lz_request_permission), 1,
                    Manifest.permission.CAMERA);

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);

    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        System.out.println(perms);
        if (perms.size() > 0) {
            Toast.makeText(this, R.string.lz_camera_permission_success, Toast.LENGTH_SHORT).show();
            createCamera();
        } else {
            Toast.makeText(this, R.string.lz_camera_permission_fail, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Toast.makeText(this, R.string.lz_camera_permission_fail, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        takePhoto();
    }

    /**
     * 拍照
     */
    public void takePhoto() {

        camera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                //图片保存形式为jpg图片,质量100,默认位置是getFilesDir()路径下,文件名为瞬时毫秒值,上传成功后删除文件
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;
                options.inJustDecodeBounds = false;
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
                File dir = new File(getFilesDir() + CAMERA_TEMP_DIR_PATH);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File file = new File(dir, System.currentTimeMillis() + ".jpg");
                try {

                    if (!file.exists()) {
                        try {
                            file.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(file));
                    System.out.println(" length = " + file.length() + " width = " + bitmap.getWidth() + " height = " + bitmap.getHeight());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(CameraActivity.this, R.string.lz_picture_save_fail, Toast.LENGTH_SHORT).show();
                }

                EventBus.getDefault().post(new CameraEvent(codeCamera, file.getPath()));
                finish();
            }
        });
    }

    private static Camera.Size getOptimalSize(@NonNull List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;
        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }

        return optimalSize;
    }
}
