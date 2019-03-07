package cn.com.i_zj.udrive_az.step.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.ToastUtils;
import com.pcitc.opencvdemo.BitmapUtils;
import com.pcitc.opencvdemo.DetectionBasedTracker;
import com.pcitc.opencvdemo.EyeUtils;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.com.i_zj.udrive_az.BuildConfig;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.event.CloseActivityEvent;
import cn.com.i_zj.udrive_az.login.AccountInfoManager;
import cn.com.i_zj.udrive_az.model.AccountInfoResult;
import cn.com.i_zj.udrive_az.model.IDResult;
import cn.com.i_zj.udrive_az.model.req.AddIdCardInfo;
import cn.com.i_zj.udrive_az.network.UdriveRestClient;
import cn.com.i_zj.udrive_az.utils.Constants;
import cn.com.i_zj.udrive_az.utils.ToolsUtils;
import cn.com.i_zj.udrive_az.utils.qiniu.Auth;
import cn.com.i_zj.udrive_az.widget.CommonAlertDialog;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.yokeyword.fragmentation.SupportFragment;

public class DetectionFragment extends SupportFragment implements CameraBridgeViewBase.CvCameraViewListener2 {

    @BindView(R.id.cp_view)
    JavaCameraView mOpenCvCameraView;
    @BindView(R.id.message)
    TextView message;

    private Mat mRgba;// rgb图像
    private Mat mGray;// 灰度图像
    private DetectionBasedTracker mNativeDetector;

    private CascadeClassifier mEyeJavaDetector;//眨眼检测器
    private int mAbsoluteFaceSize = 0;// 图像人脸小于高度的多少就不检测
    private boolean overTime = false;// 标记是否超时
    private WindowManager manager;
    private int eyeCheckSuccessCount = 0;

    private Context mContext;
    private AddIdCardInfo mAddIdCardInfo;
    private int type; //0 ：身份证正面   1 ： 身份证反面   2 ： 进入活体检测
    protected Dialog progressDialog;

    private CountDownTimer countDownTimer = new CountDownTimer(1000 * 20, 1) {
        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            overTime = true;
        }
    };

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(getContext()) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    System.loadLibrary("OpenCV");
                    try {
                        InputStream is = getResources().openRawResource(com.pcitc.opencvdemo.R.raw.lbpcascade_frontalface);
                        File cascadeDir = getActivity().getDir("cascade", Context.MODE_PRIVATE);
                        File mCascadeFile = new File(cascadeDir, "lbpcascade_frontalface.xml");
                        FileOutputStream os = new FileOutputStream(mCascadeFile);
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = is.read(buffer)) != -1) {
                            os.write(buffer, 0, bytesRead);
                        }
                        is.close();
                        os.close();
                        mNativeDetector = new DetectionBasedTracker(mCascadeFile.getAbsolutePath(), 0);
                        cascadeDir.delete();

                        // 眨眼检测器
                        InputStream eyeIs = getResources().openRawResource(com.pcitc.opencvdemo.R.raw.haarcascade_eye);
                        File eyeDir = getActivity().getDir("eyedir", Context.MODE_APPEND);
                        File eyeFile = new File(eyeDir, "haarcascade_eye.xml");
                        FileOutputStream eyeOs = new FileOutputStream(eyeFile);
                        byte[] bufferEye = new byte[4096];
                        int byetesReadEye;
                        while ((byetesReadEye = eyeIs.read(bufferEye)) != -1) {
                            eyeOs.write(bufferEye, 0, byetesReadEye);
                        }
                        eyeIs.close();
                        eyeOs.close();
                        mEyeJavaDetector = new CascadeClassifier(eyeFile.getAbsolutePath());
                        if (mEyeJavaDetector.empty()) {
                            Log.d("UDrive", "眨眼识别器加载失败");
                        }
                        eyeFile.delete();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "摄像头启动失败", Toast.LENGTH_SHORT).show();
                    }
                    mOpenCvCameraView.enableView();
                    countDownTimer.start();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    public static DetectionFragment newInstance(AddIdCardInfo addIdCardInfo) {
        Bundle args = new Bundle();
        args.putSerializable("data", addIdCardInfo);

        DetectionFragment fragment = new DetectionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mAddIdCardInfo = (AddIdCardInfo) getArguments().getSerializable("data");
        View view = inflater.inflate(R.layout.activity_bioassay, container, false);
        ButterKnife.bind(this, view);
        mContext = getContext();

        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        manager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);

        mOpenCvCameraView.setCameraIndex(JavaCameraView.CAMERA_ID_FRONT);
        mOpenCvCameraView.setCvCameraViewListener(this);
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null) {
            mOpenCvCameraView.disableView();
            mOpenCvCameraView = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {// 加载java库
            Toast.makeText(getActivity(), "打开摄像头失败", Toast.LENGTH_SHORT).show();
            getActivity().finish();
        } else {
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null) {
            mOpenCvCameraView.disableView();
            mOpenCvCameraView = null;
        }
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mGray = new Mat();
        mRgba = new Mat();
    }

    @Override
    public void onCameraViewStopped() {
        mGray.release();
        mRgba.release();
    }

    /**
     * 获取camera回调的每一针的图像
     *
     * @param inputFrame
     * @return
     */
    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();
        // 检查是否超时
        if (overTime) {
            getActivity().runOnUiThread(() -> showTipDialog("检测人脸超时，请重试"));
            return mRgba;
        }
        if (mOpenCvCameraView.getCameraIndex() == JavaCameraView.CAMERA_ID_FRONT) {
            // 原始opencv只识别手机横向的图像，此处是将max顺时针旋转90度
            if (mGray != null) {
                Mat mRgbaT = mGray.t();
                Core.flip(mRgbaT, mRgbaT, -1);
                mGray = mRgbaT;
            }
        } else if (mOpenCvCameraView.getCameraIndex() == JavaCameraView.CAMERA_ID_BACK) {
            if (mGray != null) {
                Mat mRgbaT = mGray.t();
                Core.flip(mRgbaT, mRgbaT, 1);
                mGray = mRgbaT;
            }
        }
        if (mAbsoluteFaceSize == 0) {
            int height = mGray.rows();
            float relativeFaceSize = 0.2f;
            if (Math.round(height * relativeFaceSize) > 0) {
                mAbsoluteFaceSize = Math.round(height * relativeFaceSize);
            }
            // 设置能检测的最小的人脸的尺寸
            mNativeDetector.setMinFaceSize(mAbsoluteFaceSize);
        }
        MatOfRect faces = new MatOfRect();
        if (mNativeDetector != null) {
            mNativeDetector.detect(mGray, faces);
        }
        final Rect[] facesArray = faces.toArray();
        dealEyeCheck(facesArray);
        return mRgba;
    }

    /**
     * 眨眼检测，活体检测
     */
    private void dealEyeCheck(Rect[] facesArray) {
        if (facesArray.length == 1) {
            Rect r = facesArray[0];
            Rect eyearea = new Rect((int) (r.x + r.width * 0.12f), (int) (r.y + (r.height * 0.17f)), (int) (r.width * 0.76f), (int) (r.height * 0.4f));
            Mat eyeMat = new Mat(mGray, eyearea);
            MatOfRect eyes = new MatOfRect();
            if (mEyeJavaDetector != null) {
                mEyeJavaDetector.detectMultiScale(eyeMat, eyes, 1.2f, 5, 2,
                        new Size(eyearea.width * 0.2f, eyearea.width * 0.2f),
                        new Size(eyearea.width * 0.5f, eyearea.height * 0.7f));
                Rect[] rects = eyes.toArray();
                int size = rects.length;
                EyeUtils.put(size);
                boolean success = EyeUtils.check();
                if (success) {
                    eyeCheckSuccessCount++;
                    if (eyeCheckSuccessCount > 3) {
                        EyeUtils.clearEyeCount();
                        // 连续两次眨眼成功认为检测成功，可以设置更大的值，保证检验正确率，但会增加检测难度
                        eyeCheckSuccessCount = 0;
                        setMessage("检测到人脸");
                        dealWithEyeCheckSuccess();
                    }
                } else {
                    setMessage("请眨眼");
                }
            }
        } else if (facesArray.length == 0) {
            EyeUtils.clearEyeCount();
            eyeCheckSuccessCount = 0;
            setMessage("未检测到人脸");
        } else {
            EyeUtils.clearEyeCount();
            eyeCheckSuccessCount = 0;
            setMessage("请保证只有一张人脸");
        }
    }

    public static Bitmap matToBitmap(Mat mat) {
        Bitmap resultBitmap = null;
        if (mat != null) {
            resultBitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
            if (resultBitmap != null)
                Utils.matToBitmap(mat, resultBitmap);
        }
        return resultBitmap;
    }


    /**
     * 眨眼检测成功后进行之后的处理
     */
    private void dealWithEyeCheckSuccess() {
//        final Bitmap bitmap = mOpenCvCameraView.Bytes2Bimap();
        final Bitmap bitmap = Bitmap.createBitmap(mRgba.width(), mRgba.height(), Bitmap.Config.ARGB_8888);
        try {
            Utils.matToBitmap(mRgba, bitmap);
        } catch (Exception e) {
            setMessage("检测失败，请重试");
            EyeUtils.clearEyeCount();
            return;
        }
        getActivity().runOnUiThread(() -> {
            if (mOpenCvCameraView != null) {
                mOpenCvCameraView.disableView();
            }
            int rotation = manager.getDefaultDisplay().getRotation();
            int degrees = 0;
            if (mOpenCvCameraView.getCameraIndex() == JavaCameraView.CAMERA_ID_FRONT) {
                switch (rotation) {
                    case Surface.ROTATION_0:
                        degrees = 270;
                        break;
                    case Surface.ROTATION_90:
                        degrees = 180;
                        break;
                    case Surface.ROTATION_180:
                        degrees = 90;
                        break;
                    case Surface.ROTATION_270:
                        degrees = 0;
                        break;
                    default:
                        break;
                }
            } else if (mOpenCvCameraView.getCameraIndex() == JavaCameraView.CAMERA_ID_BACK) {
                switch (rotation) {
                    case Surface.ROTATION_0:
                        degrees = 90;
                        break;
                    case Surface.ROTATION_90:
                        degrees = 0;
                        break;
                    case Surface.ROTATION_180:
                        degrees = 270;
                        break;
                    case Surface.ROTATION_270:
                        degrees = 180;
                        break;
                    default:
                        break;
                }
            }
            Matrix matrix = new Matrix();
            // 旋转
            matrix.postRotate(degrees);
            if (mOpenCvCameraView.getCameraIndex() == JavaCameraView.CAMERA_ID_FRONT) {
                // 水平翻转
                matrix.postScale(-1, 1);
            }
            final Bitmap rotateBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            Bitmap zoomBitmap = BitmapUtils.getZoomImage(rotateBitmap, 1024);
            String picPath = saveBitmap(getActivity(), zoomBitmap);
            if (TextUtils.isEmpty(picPath)) {
                return;
            }
            mAddIdCardInfo.setDetectionPic(picPath);

            uploadImg2QiNiu(0,  new File(getActivity().getFilesDir(), "front_pic.jpg").getAbsolutePath());
            uploadImg2QiNiu(1,  new File(getActivity().getFilesDir(), "back_pic.jpg").getAbsolutePath());
            uploadImg2QiNiu(2,  new File(getActivity().getFilesDir(), "detection_pic.jpg").getAbsolutePath());
        });
    }

    private String saveBitmap(Context context, Bitmap mBitmap) {
        File filePic;
        try {
            filePic = new File(getActivity().getFilesDir(), "detection_pic.jpg");
            if (!filePic.exists()) {
                filePic.getParentFile().mkdirs();
                filePic.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filePic);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return filePic.getAbsolutePath();
    }

    private void uploadImg2QiNiu(int type, String path) {
        showProgressDialog();
        new Thread() {
            public void run() {
                UploadManager uploadManager = new UploadManager();
                // 设置图片名字
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                String key = "nxnk"+ type +"_" + ToolsUtils.getUniqueId(mContext) + "_" + sdf.format(new Date()) + ".png";
                uploadManager.put(path, key, Auth.create(BuildConfig.AccessKey, BuildConfig.SecretKey).uploadToken("izjimage"), new UpCompletionHandler() {
                    @Override
                    public void complete(String key, ResponseInfo info, JSONObject res) {
                        if (info.isOK()) {
                            switch (type) {
                                case 0:
                                    mAddIdCardInfo.setIdentityCardPhotoFront(key);
                                    break;
                                case 1:
                                    mAddIdCardInfo.setIdentityCardPhotoBehind(key);
                                    break;
                                case 2:
                                    mAddIdCardInfo.setHandCardPhoto(key);
                                    break;
                            }
                            if (!TextUtils.isEmpty(mAddIdCardInfo.getIdentityCardPhotoFront())
                                    && !TextUtils.isEmpty(mAddIdCardInfo.getIdentityCardPhotoBehind())
                                    && !TextUtils.isEmpty(mAddIdCardInfo.getHandCardPhoto())) {
                                uploadCardInfo();
                            }
                        } else {
                            showProgressDialog();
                            ToastUtils.showShort("图片上传失败，请重试");
                        }
                    }
                }, null);
            }
        }.start();
    }

    //上传认证信息
    private void uploadCardInfo() {
        UdriveRestClient.getClentInstance().postAddIdCardInfo(mAddIdCardInfo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<IDResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(IDResult value) {
                        dissmisProgressDialog();
                        if (value != null && value.getCode() == 1) {
//                            ScreenManager.getScreenManager().popAllActivityExceptOne(ActIdentificationIDCard.class);
                            EventBus.getDefault().post(new CloseActivityEvent());
                            AccountInfoResult accountInfo = AccountInfoManager.getInstance().getAccountInfo();
                            accountInfo.data.idCardState = Constants.ID_UNDER_REVIEW;
                            AccountInfoManager.getInstance().cacheAccount(accountInfo);
                            start(DriveCardFragment.newInstance());
                        } else {
                            if (value != null) {
                                if (value.getCode() == 1030) {
                                    CommonAlertDialog.builder(getContext())
                                            .setImageTitle(true)
                                            .setTitle("证件重复")
                                            .setMsg("身份证已被注册，请致电400-614-1888")
                                            .setNegativeButton("取消", v -> {
                                                if (getActivity() != null) {
                                                    getActivity().finish();
                                                }
                                            })
                                            .setPositiveButton("重新上传", v -> {
                                                pop();
                                            })
                                            .build()
                                            .show();
                                }
                                ToastUtils.showShort("信息提交失败Code:" + value.getCode());
                            } else {
                                ToastUtils.showShort("信息提交失败");
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        dissmisProgressDialog();
                        ToastUtils.showShort("信息提交失败");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 显示超时的对话框
     */
    private void showTipDialog(String msg) {
        mOpenCvCameraView.disableView();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(msg);
        builder.setNegativeButton("取消", (dialog, which) -> {
            dialog.dismiss();
        });
        builder.setPositiveButton("确定", (dialog, which) -> {
            dialog.dismiss();
        });
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    public void showProgressDialog() {
        if (getActivity() == null) {
            return;
        }
        if (null == progressDialog) {
            progressDialog = new Dialog(getActivity(), R.style.MyDialog);
            progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            progressDialog.setContentView(R.layout.dialog_loading);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
        }
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    public void dissmisProgressDialog() {
        if (null != progressDialog && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void setMessage(final String msg) {
        getActivity().runOnUiThread(() -> message.setText(msg));
    }
}
