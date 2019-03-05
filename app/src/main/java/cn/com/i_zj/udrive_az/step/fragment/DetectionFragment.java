package cn.com.i_zj.udrive_az.step.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.pcitc.opencvdemo.BitmapUtils;
import com.pcitc.opencvdemo.DetectionBasedTracker;
import com.pcitc.opencvdemo.EyeUtils;

import org.greenrobot.eventbus.EventBus;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.com.i_zj.udrive_az.R;
import me.yokeyword.fragmentation.SupportFragment;

public class DetectionFragment extends SupportFragment implements CameraBridgeViewBase.CvCameraViewListener2{

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

    public static DetectionFragment newInstance() {
        Bundle args = new Bundle();

        DetectionFragment fragment = new DetectionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_bioassay, container, false);
        ButterKnife.bind(this, view);

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
                    if (eyeCheckSuccessCount > 2) {
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


    /**
     * 眨眼检测成功后进行之后的处理
     */
    private void dealWithEyeCheckSuccess() {
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
            // 裁剪
            int rotateBitmapWidth = rotateBitmap.getWidth();
            int rotateBitmapHeight = rotateBitmap.getHeight();
            int squareLength = Math.min(rotateBitmapWidth, rotateBitmapHeight);
            int startLenth = Math.max(rotateBitmapWidth, rotateBitmapHeight) - squareLength;
            final Bitmap squareBitmap = Bitmap.createBitmap(rotateBitmap, 0, startLenth / 2, squareLength, squareLength);
            rotateBitmap.recycle();
            Bitmap zoomBitmap = BitmapUtils.getZoomImage(squareBitmap, 1024);
            Toast.makeText(getActivity(), "嗯，你是人", Toast.LENGTH_LONG).show();
            startWithPop(DriveCardFragment.newInstance());
//            finish();
//            dealWtihZoomBitmap(zoomBitmap);
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

    private void setMessage(final String msg) {
        getActivity().runOnUiThread(() -> message.setText(msg));
    }
}
