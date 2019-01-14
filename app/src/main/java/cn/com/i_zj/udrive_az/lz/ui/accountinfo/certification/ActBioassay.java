package cn.com.i_zj.udrive_az.lz.ui.accountinfo.certification;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.arcsoft.facetracking.AFT_FSDKEngine;
import com.arcsoft.facetracking.AFT_FSDKError;
import com.arcsoft.facetracking.AFT_FSDKFace;
import com.arcsoft.liveness.ErrorInfo;
import com.arcsoft.liveness.FaceInfo;
import com.arcsoft.liveness.LivenessEngine;
import com.arcsoft.liveness.LivenessInfo;
import com.blankj.utilcode.util.ToastUtils;
import com.zjcx.face.listener.FaceInterface;
import com.zjcx.face.listener.OnFrameListener;
import com.zjcx.face.util.Contants;
import com.zjcx.face.view.CameraPreview;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import butterknife.BindView;
import cn.com.i_zj.udrive_az.DBSBaseActivity;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.event.CloseActivityEvent;
import cn.com.i_zj.udrive_az.login.AccountInfoManager;
import cn.com.i_zj.udrive_az.login.SessionManager;
import cn.com.i_zj.udrive_az.model.AccountInfoResult;
import cn.com.i_zj.udrive_az.model.IDResult;
import cn.com.i_zj.udrive_az.model.ImageUrlResult;
import cn.com.i_zj.udrive_az.model.req.AddIdCardInfo;
import cn.com.i_zj.udrive_az.network.UdriveRestClient;
import cn.com.i_zj.udrive_az.utils.Constants;
import cn.com.i_zj.udrive_az.utils.image.ImageUtils;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pub.devrel.easypermissions.EasyPermissions;

import static cn.com.i_zj.udrive_az.utils.RotateYUV420Degree.rotateYUV420Degree180;
import static cn.com.i_zj.udrive_az.utils.RotateYUV420Degree.rotateYUV420Degree270;
import static cn.com.i_zj.udrive_az.utils.RotateYUV420Degree.rotateYUV420Degree90;

/**
 * @author JayQiu
 * @create 2018/11/1
 * @Describe 活体人脸检测
 */
public class ActBioassay extends DBSBaseActivity implements OnFrameListener<byte[]>, EasyPermissions.PermissionCallbacks {
    public static final String ACTION_ADD_IDCARDINFO_DATE = "action_add_idcardinfo_date";
    private static final String TAG = "ActBioassay=====>";
    @BindView(R.id.cp_view)
    CameraPreview vCamera;
    @BindView(R.id.iv_image)
    ImageView ivImage;
    @BindView(R.id.tv_msg)
    TextView tvMsg;
    private AFT_FSDKEngine ftEngine;
    private LivenessEngine arcFaceEngine;
    private boolean isLive = false;// 是否是活体
    private CountDownTimer countDownTimer;
    private AnimationDrawable anim;
    private int timeindex = 0;
    private boolean live = true;// 摄像头返回数据开关
    private AddIdCardInfo addIdCardInfo;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_bioassay;
    }

    public static void startActBioassay(Context context, AddIdCardInfo addIdCardInfo) {
        Intent intent = new Intent(context, ActBioassay.class);
        intent.putExtra(ACTION_ADD_IDCARDINFO_DATE, addIdCardInfo);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.lz_account_true_name);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                finish();
            }
        });
        addIdCardInfo = (AddIdCardInfo) getIntent().getSerializableExtra(ACTION_ADD_IDCARDINFO_DATE);
        if (addIdCardInfo == null) {
            showToast("数据传输有误");
            finish();
            return;
        }
        startNot();
        checkPermission();


    }

    private void initView() {
        int orientation = this.getResources().getConfiguration().orientation;
        DisplayMetrics dm = new DisplayMetrics();
        vCamera.setCaremaId(Camera.CameraInfo.CAMERA_FACING_FRONT);
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);// this指当前activity
        int width = dm.widthPixels;
        int height = dm.heightPixels;

        int previewHight, previewWidth;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {

            //previewHight = height - Util.getStatusBarHeight(this);
            previewHight = height;
            previewWidth = previewHight * Contants.PREVIEW_W / Contants.PREVIEW_H;

        } else {

            previewWidth = width;
            previewHight = width * Contants.PREVIEW_W / Contants.PREVIEW_H;
        }

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(previewWidth, previewHight);
        vCamera.setLayoutParams(params);

        vCamera.setCaremaId(Camera.CameraInfo.CAMERA_FACING_FRONT);
        vCamera.setScreenOrientation(orientation);
        vCamera.setOnFrameListener(this);
        vCamera.cwStartCamera();
    }

    private void initSDK() {
        ftEngine = new AFT_FSDKEngine();
        int ftInitErrorCode = ftEngine.AFT_FSDK_InitialFaceEngine(Constants.FREESDKAPPID,
                Constants.FTSDKKEY, AFT_FSDKEngine.AFT_OPF_0_HIGHER_EXT,
                16, 5).getCode();
        if (ftInitErrorCode != 0) {
            showToast("初始化失败，errorcode：" + ftInitErrorCode);
            return;
        }
        //活体引擎初始化(视频)
        arcFaceEngine = new LivenessEngine();
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                final long activeCode = arcFaceEngine.activeEngine(ActBioassay.this, Constants.LIVENESSAPPID,
                        Constants.LIVENESSSDKKEY).getCode();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (activeCode == ErrorInfo.MOK) {
//                            showToast("活体引擎激活成功");
                            startTimer();
                        } else if (activeCode == ErrorInfo.MERR_AL_BASE_ALREADY_ACTIVATED) {
//                            showToast("活体引擎已激活");
                            startTimer();
                        } else {
                            showToast("活体引擎激活失败，errorcode：" + activeCode);
                        }

                        ErrorInfo error = arcFaceEngine.initEngine(ActBioassay.this, LivenessEngine.AL_DETECT_MODE_VIDEO);
                        if (error.getCode() != 0) {
                            showToast("活体初始化失败，errorcode：" + error.getCode());
                            return;
                        }
                    }
                });
            }
        });


    }

    /**
     * 检测
     */
    private void checkPermission() {
        boolean external = EasyPermissions.hasPermissions(this, Manifest.permission.READ_PHONE_STATE);

        if (!external) {
            EasyPermissions.requestPermissions(this, getString(R.string.lz_request_permission), 1, Manifest.permission.READ_PHONE_STATE);
        } else {
            initView();
            initTimerCount();
            initSDK();


        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);

    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if (perms.size() > 0) {
            ToastUtils.showShort(R.string.permission_success);
            initView();
            initTimerCount();
            initSDK();
        } else {
            ToastUtils.showShort(R.string.permission_file);
        }

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        ToastUtils.showShort(R.string.permission_request_fail);
    }

    @Override
    public void onPreviewFrame(byte[] data, int rotation, int width, int height) {
        if (!live) {// 停止
            return;
        }
        switch (rotation) {
            case FaceInterface.cw_img_angle_t.CW_IMAGE_ANGLE_90:
                data = rotateYUV420Degree90(data, width, height);
                int widthTemp = height;
                height = width;
                width = widthTemp;
                break;
            case FaceInterface.cw_img_angle_t.CW_IMAGE_ANGLE_180:
                data = rotateYUV420Degree180(data, width, height);
                break;
            case FaceInterface.cw_img_angle_t.CW_IMAGE_ANGLE_270:
                data = rotateYUV420Degree270(data, width, height);
                int widthTempTwo = height;
                height = width;
                width = widthTempTwo;

                break;
        }

        detect(data, width, height);


    }

    private void detect(final byte[] data, int width, int height) {
        List<AFT_FSDKFace> ftFaceList = new ArrayList<>();
        //视频FT检测人脸
        int ftCode = ftEngine.AFT_FSDK_FaceFeatureDetect(data, width, height,
                AFT_FSDKEngine.CP_PAF_NV21, ftFaceList).getCode();
        if (ftCode != AFT_FSDKError.MOK) {
            Log.i(TAG, "AFT_FSDK_FaceFeatureDetect: errorcode " + ftCode);
            return;
        }
        int maxIndex = ImageUtils.findFTMaxAreaFace(ftFaceList);

        final List<FaceInfo> faceInfos = new ArrayList<>();
        if (maxIndex != -1) {
            AFT_FSDKFace face = ftFaceList.get(maxIndex);
            FaceInfo faceInfo = new FaceInfo(face.getRect(), face.getDegree());
            faceInfos.add(faceInfo);
        }
        //活体检测(目前只支持单人脸，且无论有无人脸都需调用)
        List<LivenessInfo> livenessInfos = new ArrayList<>();
        ErrorInfo livenessError = arcFaceEngine.startLivenessDetect(data, width, height,
                LivenessEngine.CP_PAF_NV21, faceInfos, livenessInfos);
        Log.i(TAG, "startLiveness: errorcode " + livenessError.getCode());
        if (livenessError.getCode() == ErrorInfo.MOK) {
            if (livenessInfos.size() == 0) {
//                tvMsg.setText("无人脸");
                timeindex = 0;
                isLive = false;
                cancelTimer();
                return;
            }
            final int liveness = livenessInfos.get(0).getLiveness();
            Log.i(TAG, "getLivenessScore: liveness " + liveness);
            if (liveness == LivenessInfo.NOT_LIVE) {
                tvMsg.setText("请活体人脸进行失败");
                timeindex = 0;
            } else if (liveness == LivenessInfo.LIVE) {
//                tvMsg.setText("活体");
                if (!isLive) {
                    isLive = true;
                    startTimer();
                }
                if (timeindex >= 1) {
                    File file = saveImage(data, width, height);
                    if (file != null) {
                        timeindex = 0;
                        live = false;
                        uploadImage(file.getAbsolutePath());
                    }

                }

            } else if (liveness == LivenessInfo.MORE_THAN_ONE_FACE) {
                timeindex = 0;
                tvMsg.setText("请指定一个人脸信息");
            } else {
                timeindex = 0;
//                tvMsg.setText("未知");
            }
        }
    }

    private void uploadImage(final String path) {

        final File file = new File(path);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);

        MultipartBody.Part body =
                MultipartBody.Part.createFormData("filename", file.getName(), requestFile);
        showProgressDialog();
        UdriveRestClient.getClentInstance().postImage(SessionManager.getInstance().getAuthorization(), body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ImageUrlResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ImageUrlResult value) {
                        dissmisProgressDialog();
                        if (value != null) {
                            if (addIdCardInfo == null) {
                                addIdCardInfo = new AddIdCardInfo();
                            }
                            addIdCardInfo.setHandCardPhoto(value.data);
                            uploadCardInfo();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        dissmisProgressDialog();
                        showToast("数据上传失败重新进行人脸识别");
                        live = true;
                        timeindex = 0;
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private File saveImage(byte[] date, int width, int height) {

        String fileName = Environment.getExternalStorageDirectory().toString()
                + File.separator
                + "tempCamera"
                + File.separator
                + "nxnk_temp_" + System.currentTimeMillis() + ".jpg";
        File iamgeFile = new File(fileName);
        if (!iamgeFile.getParentFile().exists()) {
            iamgeFile.getParentFile().mkdir();//创建文件夹
        }
        try {
            BitmapFactory.Options newOpts = new BitmapFactory.Options();
            newOpts.inJustDecodeBounds = true;
            YuvImage yuvimage = new YuvImage(
                    date,
                    ImageFormat.NV21,
                    width,
                    height,
                    null);//data是onPreviewFrame参数提供
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            yuvimage.compressToJpeg(
                    new Rect(0, 0, width, height),
                    80,
                    baos);// 80--JPG图片的质量[0-100],100最高

            byte[] rawImage = baos.toByteArray();

            Bitmap mBitmap = BitmapFactory.decodeByteArray(rawImage, 0,
                    rawImage.length);
            FileOutputStream fos = new FileOutputStream(iamgeFile);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            baos.close();
            fos.flush();
            fos.close();
            return iamgeFile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }


    }

    //上传认证信息
    private void uploadCardInfo() {
        UdriveRestClient.getClentInstance().postAddIdCardInfo(SessionManager.getInstance().getAuthorization(), addIdCardInfo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<IDResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(IDResult value) {
                        if (value != null && value.getCode() == 1) {
                            showToast("信息提交成功");
//                            ScreenManager.getScreenManager().popAllActivityExceptOne(ActIdentificationIDCard.class);
                            EventBus.getDefault().post(new CloseActivityEvent());
                            AccountInfoResult accountInfo = AccountInfoManager.getInstance().getAccountInfo();
                            accountInfo.data.idCardState = Constants.ID_UNDER_REVIEW;
                            AccountInfoManager.getInstance().cacheAccount(accountInfo);
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            if (value != null) {
                                showToast("信息提交失败Code:" + value.getCode());
                            } else {
                                showToast("信息提交失败");
                            }

                            live = true;
                            timeindex = 0;
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        showToast("信息提交失败重新进行人脸识别");
                        live = true;
                        timeindex = 0;
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (vCamera != null) {
            vCamera.cwStopCamera();
        }
        if (ftEngine != null) {
            ftEngine.AFT_FSDK_UninitialFaceEngine();
        }

        if (arcFaceEngine != null) {
            arcFaceEngine.unInitEngine();
        }
        cancelTimer();
        super.onDestroy();
    }

    private void startNot() {
        anim = new AnimationDrawable();
        for (int i = 0; i <= 74; i++) {
            String name = "nod_";
            if (i <= 9) {
                name = name + "0000" + i;
            } else {
                name = name + "000" + i;
            }
            int id = getResources().getIdentifier(name, "mipmap", getPackageName());
            Drawable drawable = getResources().getDrawable(id);
            anim.addFrame(drawable, 40);
        }
        for (int i = 0; i <= 74; i++) {
            String name = "shakehead_";
            if (i <= 9) {
                name = name + "0000" + i;
            } else {
                name = name + "000" + i;
            }
            int id = getResources().getIdentifier(name, "mipmap", getPackageName());
            Drawable drawable = getResources().getDrawable(id);
            anim.addFrame(drawable, 40);
        }
        anim.setOneShot(false);


    }


    private void initTimerCount() {
        if (null != countDownTimer) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(7000, 10) {
            @Override
            public void onTick(long millisUntilFinished) {
                millisUntilFinished = millisUntilFinished / 10;
                Log.e(TAG, millisUntilFinished + "==========");
                if (millisUntilFinished > 600 && isLive) {//
                    if (anim != null && !anim.isRunning()) {
                        ivImage.setImageDrawable(anim);
                        anim.start();
                    }
                } else if (millisUntilFinished < 600 && millisUntilFinished > 300 && isLive) {
                    tvMsg.setText("请重复上下抬头动作");
                } else if (millisUntilFinished < 300 && millisUntilFinished > 0 && isLive) {
                    tvMsg.setText("请重复左右摇头动作");

                }
            }

            @Override
            public void onFinish() {
                timeindex++;
                startTimer();
            }
        };


    }

    private void startTimer() {
        if (countDownTimer != null) {
            countDownTimer.start();
        }

    }

    private void cancelTimer() {
        if (null != countDownTimer) {
            ivImage.setImageResource(R.mipmap.ic_adjustment);
            tvMsg.setText("请调整距离将脸部对准虚线框");
            countDownTimer.cancel();
        }
    }
}
