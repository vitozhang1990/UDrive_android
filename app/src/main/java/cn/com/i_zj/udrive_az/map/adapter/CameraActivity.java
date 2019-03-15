package cn.com.i_zj.udrive_az.map.adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.i_zj.udrive_az.DBSBaseActivity;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.model.CarPartPicture;
import cn.com.i_zj.udrive_az.refuel.RotateTransformation;
import cn.com.i_zj.udrive_az.utils.BitmapUtils;
import cn.com.i_zj.udrive_az.utils.CameraUtil;
import cn.com.i_zj.udrive_az.utils.Constants;
import cn.com.i_zj.udrive_az.utils.LocalCacheUtils;
import cn.com.i_zj.udrive_az.utils.dialog.PictureTipDialog;
import cn.com.i_zj.udrive_az.widget.MaskPierceView;
import pub.devrel.easypermissions.EasyPermissions;

public class CameraActivity extends DBSBaseActivity implements SurfaceHolder.Callback
        , View.OnClickListener, EasyPermissions.PermissionCallbacks {

    private static final int REQUEST_CODE_PICK_IMAGE = 100;
    private static final int PERMISSIONS_EXTERNAL_STORAGE = 801;

    @BindView(R.id.photo_view)
    ImageView imagePhoto;
    @BindView(R.id.flash_light)
    ImageView flash_light;
    @BindView(R.id.pick_gallery)
    ImageView gallery;
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
    private boolean isCameraUseful = false;
    private String img_path;

    private int state; //0:正常拍照 1: 后拍照
    private boolean imageModel;
    private String imagePath;

    private int currentPosition = 0; //state=2时使用
    private CarPartPicture carPart;
    private String backPath, rightFrontPath, leftFrontPath, innerPath;
    private int qipa; //奇葩，为了兼容四图连拍时更改之前的

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
        if (state == 0 || state == 2) {
            carPart = (CarPartPicture) getIntent().getSerializableExtra("part");
            if (!TextUtils.isEmpty(carPart.getPhotoPath())) {
                imageModel = true;
                imagePath = carPart.getPhotoPath();
                showImageModel();
            } else {
                showSingle();
            }
            if (state == 2) {
                gallery.setVisibility(View.VISIBLE);
            }
        } else {
            if (!TextUtils.isEmpty(getIntent().getStringExtra("backPath"))) {
                currentPosition = 3;
                backPath = getIntent().getStringExtra("backPath");
                imagePath = backPath;
            }
            if (!TextUtils.isEmpty(getIntent().getStringExtra("rightFrontPath"))) {
                currentPosition = 2;
                rightFrontPath = getIntent().getStringExtra("rightFrontPath");
                imagePath = rightFrontPath;
            }
            if (!TextUtils.isEmpty(getIntent().getStringExtra("leftFrontPath"))) {
                currentPosition = 1;
                leftFrontPath = getIntent().getStringExtra("leftFrontPath");
                imagePath = leftFrontPath;
            }
            if (!TextUtils.isEmpty(getIntent().getStringExtra("innerPath"))) {
                currentPosition = 0;
                innerPath = getIntent().getStringExtra("innerPath");
                imagePath = innerPath;
            }
            if (TextUtils.isEmpty(imagePath)) {
                if (!LocalCacheUtils.getPersistentSettingBoolean(Constants.SP_GLOBAL_NAME, Constants.SP_NOT_SHOW_PICTURE, false)) {
                    new PictureTipDialog(this).show();
                }
                showFour();
            } else { //查看图片模式
                imageModel = true;
                showImageModel();
            }
        }

        checkPermission();
    }

    private void checkPermission() {
        boolean external = EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA);

        if (!external) {
            EasyPermissions.requestPermissions(this, getString(R.string.lz_request_permission), 1, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA);
        } else {
            mCamera = getCamera(mCameraId);
            if (mHolder != null && !imageModel) {
                startPreview(mCamera, mHolder);
            }
        }
    }

    private void showImageModel() {
        camera_back.setVisibility(View.GONE);
        takePhotoLayout.setVisibility(View.GONE);
        finishLayout.setVisibility(View.GONE);
        sureLayout.setVisibility(View.VISIBLE);

        imagePhoto.setVisibility(View.VISIBLE);
        imagePhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(this).load(imagePath).transform(new RotateTransformation(this, 90)).into(imagePhoto);
        maskPierceView.black(true);
    }

    private void showImageModel1() {
        camera_back.setVisibility(View.GONE);
        takePhotoLayout.setVisibility(View.GONE);
        finishLayout.setVisibility(View.GONE);
        sureLayout.setVisibility(View.VISIBLE);
        maskPierceView.black(true);
    }

    private void showSingle() {
        imagePhoto.setVisibility(View.GONE);
        camera_back.setVisibility(View.VISIBLE);
        takePhotoLayout.setVisibility(View.VISIBLE);
        finishLayout.setVisibility(View.GONE);
        sureLayout.setVisibility(View.GONE);
        maskPierceView.black(false);
    }

    private void showSingle1() {
        imagePhoto.setVisibility(View.GONE);
        camera_back.setVisibility(View.GONE);
        takePhotoLayout.setVisibility(View.VISIBLE);
        finishLayout.setVisibility(View.GONE);
        sureLayout.setVisibility(View.GONE);
        maskPierceView.black(false);
    }

    private void showFour() {
        imagePhoto.setVisibility(View.GONE);
        camera_back.setVisibility(View.VISIBLE);
        takePhotoLayout.setVisibility(View.VISIBLE);
        finishLayout.setVisibility(View.VISIBLE);
        sureLayout.setVisibility(View.GONE);
        maskPierceView.black(false);
    }

    @OnClick({R.id.img_camera, R.id.camera_back, R.id.flash_light, R.id.pick_gallery, R.id.retake_picture, R.id.sure
            , R.id.rightFrontPhoto_layout, R.id.leftFrontPhoto_layout, R.id.innerPhoto_layout})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_camera:
                if (mCamera == null) {
                    return;
                }
                if (isCameraUseful && currentPosition < 4) {
                    switch (light_num) {
                        case 0:
                            CameraUtil.getInstance().turnLightOff(mCamera);
                            break;
                        case 1:
                            CameraUtil.getInstance().turnLightOn(mCamera);
                            break;
                    }
                    capture();
                    isCameraUseful = false;
                }
                break;
            //退出相机界面 释放资源
            case R.id.camera_back:
                if (state == 1 && imageModel) setResult(RESULT_OK);
                finish();
                break;
            //闪光灯
            case R.id.flash_light:
                if (mCamera == null) {
                    return;
                }
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
            case R.id.pick_gallery:
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        ActivityCompat.requestPermissions(CameraActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                PERMISSIONS_EXTERNAL_STORAGE);
                        return;
                    }
                }
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
                break;
            case R.id.retake_picture:
                if (mCamera == null) {
                    return;
                }
                if (state == 1) {
                    showSingle1();
                } else {
                    showSingle();
                }
                startPreview(mCamera, mHolder);
                break;
            case R.id.sure:
                if (mCamera == null) {
                    return;
                }
                if (qipa > 0) {
                    switch (qipa) {
                        case 1:
                            innerPath = imagePath;
                            innerPhoto.setScaleType(ImageView.ScaleType.FIT_XY);
                            innerPhoto.setImageURI(Uri.fromFile(new File(innerPath)));
                            break;
                        case 2:
                            leftFrontPath = imagePath;
                            leftFrontPhoto.setScaleType(ImageView.ScaleType.FIT_XY);
                            leftFrontPhoto.setImageURI(Uri.fromFile(new File(leftFrontPath)));
                            leftFrontPath = imagePath;
                            break;
                        case 3:
                            rightFrontPath = imagePath;
                            rightFrontPhoto.setScaleType(ImageView.ScaleType.FIT_XY);
                            rightFrontPhoto.setImageURI(Uri.fromFile(new File(rightFrontPath)));
                            rightFrontPath = imagePath;
                            break;
                    }
                    qipa = 0;
                    showFour();
                    startPreview(mCamera, mHolder);
                } else {
                    returnLastPage();
                }
                break;
            case R.id.innerPhoto_layout:
                if (mCamera == null) {
                    return;
                }
                if (currentPosition > 0) {
                    qipa = 1;
                    mCamera.stopPreview();
                    imagePath = innerPath;
                    showImageModel();
                }
                break;
            case R.id.leftFrontPhoto_layout:
                if (mCamera == null) {
                    return;
                }
                if (currentPosition > 1) {
                    qipa = 2;
                    mCamera.stopPreview();
                    imagePath = leftFrontPath;
                    showImageModel();
                }
                break;
            case R.id.rightFrontPhoto_layout:
                if (mCamera == null) {
                    return;
                }
                if (currentPosition == 3) {
                    qipa = 3;
                    mCamera.stopPreview();
                    imagePath = rightFrontPath;
                    showImageModel();
                }
                break;
        }
    }

    private void returnLastPage() {
        if (TextUtils.isEmpty(img_path)) {//若是查看模式进入，且没有重拍，退出时不返回任何内容
            setResult(RESULT_OK);
            finish();
            return;
        }
        Intent intent = getIntent();
        switch (state) {
            case 0:
                carPart.setPhotoPath(img_path);
                carPart.setHasPhoto(true);
                intent.putExtra("part", carPart);
                break;
            case 1:
                if (!TextUtils.isEmpty(backPath)) intent.putExtra("backPath", backPath);
                if (!TextUtils.isEmpty(rightFrontPath)) intent.putExtra("rightFrontPath", rightFrontPath);
                if (!TextUtils.isEmpty(leftFrontPath)) intent.putExtra("leftFrontPath", leftFrontPath);
                if (!TextUtils.isEmpty(innerPath)) intent.putExtra("innerPath", innerPath);
                break;
            default:
                carPart.setPhotoPath(img_path);
                carPart.setHasPhoto(true);
                intent.putExtra("part", carPart);
                break;
        }
        setResult(RESULT_OK, intent);
        finish();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getData();
                try {
                    imageModel = true;
                    carPart.setPhotoPath(getRealPathFromURI(uri));
                    carPart.setHasPhoto(true);
                    img_path = carPart.getPhotoPath();
                    imagePath = carPart.getPhotoPath();
                    showImageModel();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int rotate) {
        if (bitmap == null)
            return null;

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Matrix mtx = new Matrix();
        mtx.postRotate(rotate);
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    }

    @SuppressWarnings("finally")
    public static boolean saveBmpToPath(final Bitmap bitmap, final String filePath) {
        if (bitmap == null || filePath == null) {
            return false;
        }
        boolean result = false; // 默认结果
        File file = new File(filePath);
        OutputStream outputStream = null; // 文件输出流
        try {
            outputStream = new FileOutputStream(file);
            result = bitmap.compress(Bitmap.CompressFormat.JPEG, 100,
                    outputStream); // 将图片压缩为JPEG格式写到文件输出流，100是最大的质量程度
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close(); // 关闭输出流
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return result;
        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(contentURI, null, null, null, null);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
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
        if (camera == null) {
            return;
        }
        try {
            setupCamera(camera);
            camera.setPreviewDisplay(holder);
            CameraUtil.getInstance().setCameraDisplayOrientation(this, mCameraId, camera);
            camera.startPreview();
            isCameraUseful = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void capture() {
        mCamera.takePicture(null, null, (data, camera) -> {
            isCameraUseful = false;
            //将data 转换为位图 或者你也可以直接保存为文件使用 FileOutputStream
            //这里我相信大部分都有其他用处把 比如加个水印 后续再讲解
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            Bitmap saveBitmap = CameraUtil.getInstance().setTakePicktrueOrientation(mCameraId, bitmap);

            img_path = getExternalFilesDir(Environment.DIRECTORY_DCIM).getPath() +
                    File.separator + System.currentTimeMillis() + ".jpeg";

            boolean saveSuccess = saveBmpToPath(rotateBitmap(saveBitmap, 270), img_path);
            if (!saveSuccess) BitmapUtils.saveJPGE_After(context, saveBitmap, img_path, 100);

            if (!bitmap.isRecycled()) {
                bitmap.recycle();
            }

            if (!saveBitmap.isRecycled()) {
                saveBitmap.recycle();
            }

            if (state == 1) {
                if (imageModel) {
                    imagePath = img_path;
                    showImageModel1();
                    switch (currentPosition) {
                        case 0:
                            innerPath = img_path;
                            break;
                        case 1:
                            leftFrontPath = img_path;
                            break;
                        case 2:
                            rightFrontPath = img_path;
                            break;
                        case 3:
                            backPath = img_path;
                            break;
                    }
                    mCamera.stopPreview();
                } else {
                    if (qipa > 0) {
                        imagePath = img_path;
                        showImageModel1();
                        mCamera.stopPreview();
                    } else {
                        updateImage(img_path);
                        mCamera.stopPreview();
                        startPreview(mCamera, mHolder);
                    }
                }
            } else {
                imagePath = img_path;
                showImageModel1();
                mCamera.stopPreview();
            }
        });
    }

    private void updateImage(String imgPath) {
        switch (currentPosition) {
            case 0:
                innerPhoto.setScaleType(ImageView.ScaleType.FIT_XY);
                innerPhoto.setImageBitmap(rotateBitmap(getLocalBitmap(imgPath), 90));
                innerPhotoLayout.setVisibility(View.GONE);
                leftFrontPhotoLayout.setVisibility(View.VISIBLE);
                rightFrontPhotoLayout.setVisibility(View.GONE);
                backPhotoLayout.setVisibility(View.GONE);

                innerPath = imgPath;
                currentPosition++;
                break;
            case 1:
                leftFrontPhoto.setScaleType(ImageView.ScaleType.FIT_XY);
                leftFrontPhoto.setImageBitmap(rotateBitmap(getLocalBitmap(imgPath), 90));
                innerPhotoLayout.setVisibility(View.GONE);
                leftFrontPhotoLayout.setVisibility(View.GONE);
                rightFrontPhotoLayout.setVisibility(View.VISIBLE);
                backPhotoLayout.setVisibility(View.GONE);

                leftFrontPath = imgPath;
                currentPosition++;
                break;
            case 2:
                rightFrontPhoto.setScaleType(ImageView.ScaleType.FIT_XY);
                rightFrontPhoto.setImageBitmap(rotateBitmap(getLocalBitmap(imgPath), 90));
                innerPhotoLayout.setVisibility(View.GONE);
                leftFrontPhotoLayout.setVisibility(View.GONE);
                rightFrontPhotoLayout.setVisibility(View.GONE);
                backPhotoLayout.setVisibility(View.VISIBLE);

                rightFrontPath = imgPath;
                currentPosition++;
                break;
            case 3:
                backPhoto.setScaleType(ImageView.ScaleType.FIT_XY);
                backPhoto.setImageBitmap(rotateBitmap(getLocalBitmap(imgPath), 90));
//                innerPhotoLayout.setVisibility(View.VISIBLE);
//                leftFrontPhotoLayout.setVisibility(View.GONE);
//                rightFrontPhotoLayout.setVisibility(View.GONE);
//                backPhotoLayout.setVisibility(View.GONE);

                backPath = imgPath;
                currentPosition++;
                returnLastPage();
                break;
        }
    }

    public static Bitmap getLocalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            return BitmapFactory.decodeStream(fis, null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void setupCamera(Camera camera) {
        Camera.Parameters parameters = camera.getParameters();

        if (parameters.getSupportedFocusModes().contains(
                Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }

        //这里第三个参数为最小尺寸 getPropPreviewSize方法会对从最小尺寸开始升序排列 取出所有支持尺寸的最小尺寸
        Camera.Size previewSize = CameraUtil.getInstance().getPropSizeForHeight(parameters.getSupportedPreviewSizes(), 1000);
        parameters.setPreviewSize(previewSize.width, previewSize.height);

        Camera.Size pictureSize = CameraUtil.getInstance().getPropSizeForHeight(parameters.getSupportedPictureSizes(), 1000);
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
        if (mCamera != null) {
            startPreview(mCamera, holder);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mCamera != null) {
            mCamera.stopPreview();
            startPreview(mCamera, holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        releaseCamera();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if (requestCode == PERMISSIONS_EXTERNAL_STORAGE) {
            return;
        }
        if (perms.size() == 0) {
            ToastUtils.showShort(R.string.permission_request_fail1);
            return;
        }

        mCamera = getCamera(mCameraId);
        if (mHolder != null && !imageModel) {
            startPreview(mCamera, mHolder);
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        ToastUtils.showShort(R.string.permission_request_fail1);
    }
}
