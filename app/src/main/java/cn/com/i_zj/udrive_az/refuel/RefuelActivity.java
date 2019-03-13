package cn.com.i_zj.udrive_az.refuel;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.blankj.utilcode.util.ToastUtils;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.i_zj.udrive_az.BuildConfig;
import cn.com.i_zj.udrive_az.DBSBaseActivity;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.event.OrderFinishEvent;
import cn.com.i_zj.udrive_az.event.WebSocketCloseEvent;
import cn.com.i_zj.udrive_az.lz.ui.payment.ActConfirmOrder;
import cn.com.i_zj.udrive_az.lz.ui.payment.PaymentActivity;
import cn.com.i_zj.udrive_az.map.MapUtils;
import cn.com.i_zj.udrive_az.map.adapter.CameraActivity;
import cn.com.i_zj.udrive_az.model.CarPartPicture;
import cn.com.i_zj.udrive_az.model.OrderDetailResult;
import cn.com.i_zj.udrive_az.model.PhotoBean;
import cn.com.i_zj.udrive_az.network.UdriveRestClient;
import cn.com.i_zj.udrive_az.utils.ToolsUtils;
import cn.com.i_zj.udrive_az.utils.qiniu.Auth;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class RefuelActivity extends DBSBaseActivity {

    @BindView(R.id.iv_neishi)
    ImageView iv_neishi;
    @BindView(R.id.iv_left)
    ImageView iv_left;
    @BindView(R.id.iv_right)
    ImageView iv_right;
    @BindView(R.id.iv_tail)
    ImageView iv_tail;

    private Context mContext;
    private String orderNum;
    private int REQUEST_CODE = 1002;
    private Map<String, String> picMap = new HashMap<>();
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    private Map<String, CarPartPicture> mCarParts = new HashMap<>();

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_refuel;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapUtils.statusBarColor(this);
        this.mContext = this;
        mCarParts.put("leftFrontBumper", new CarPartPicture("leftFrontBumper", 1001));
        mCarParts.put("rightFrontBumper", new CarPartPicture("rightFrontBumper", 1002));
        mCarParts.put("leftFrontDoor", new CarPartPicture("leftFrontDoor", 1003));
        mCarParts.put("rightFrontDoor", new CarPartPicture("rightFrontDoor", 1004));
    }

    @OnClick({R.id.iv_back, R.id.btn_neishi, R.id.btn_left, R.id.btn_right, R.id.btn_tail, R.id.oil_park, R.id.btnSubmit})
    public void onClick(View view) {
        Intent intent = new Intent();
        intent.setClass(this, CameraActivity.class);
        intent.putExtra("state", 2);
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_neishi:
                intent.putExtra("part", mCarParts.get("leftFrontBumper"));
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.btn_left:
                intent.putExtra("part", mCarParts.get("rightFrontBumper"));
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.btn_right:
                intent.putExtra("part", mCarParts.get("leftFrontDoor"));
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.btn_tail:
                intent.putExtra("part", mCarParts.get("rightFrontDoor"));
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.oil_park:
                Intent intent1 = new Intent();
                intent1.setAction(Intent.ACTION_VIEW);
                intent1.addCategory(Intent.CATEGORY_DEFAULT);
                intent1.setData(Uri.parse("androidamap://poi?sourceApplication=你行你开&keywords=加油站&dev=0"));
                startActivity(intent1);
                break;
            case R.id.btnSubmit:
                showProgressDialog();
                picMap.clear();
//                uploadImg2QiNiu("innerPath", innerPath);
//                uploadImg2QiNiu("backPath", backPath);
//                uploadImg2QiNiu("rightFrontPath", rightFrontPath);
//                uploadImg2QiNiu("leftFrontPath", leftFrontPath);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        if (requestCode == REQUEST_CODE) {
            if (data.getSerializableExtra("part") == null) {
                return;
            }
            try {
                CarPartPicture carPart = (CarPartPicture) data.getSerializableExtra("part");
                if (carPart == null) {
                    return;
                }
                switch (carPart.getRequestCode()) {
                    case 1001:
                        if (!TextUtils.isEmpty(carPart.getPhotoPath())) {
                            mCarParts.put(carPart.getKey(), carPart);
                            setImage(carPart.getPhotoPath(), iv_neishi);
                        }
                        break;
                    case 1002:
                        if (!TextUtils.isEmpty(carPart.getPhotoPath())) {
                            mCarParts.put(carPart.getKey(), carPart);
                            setImage(carPart.getPhotoPath(), iv_left);
                        }
                        break;
                    case 1003:
                        if (!TextUtils.isEmpty(carPart.getPhotoPath())) {
                            mCarParts.put(carPart.getKey(), carPart);
                            setImage(carPart.getPhotoPath(), iv_right);
                        }
                        break;
                    case 1004:
                        if (!TextUtils.isEmpty(carPart.getPhotoPath())) {
                            mCarParts.put(carPart.getKey(), carPart);
                            setImage(carPart.getPhotoPath(), iv_tail);
                        }
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImg2QiNiu(final String type, final String path) {
        new Thread() {
            public void run() {
                UploadManager uploadManager = new UploadManager();
                // 设置图片名字
                String key = type + ToolsUtils.getUniqueId(mContext) + "_" + sdf.format(new Date()) + ".png";
                uploadManager.put(path, key, Auth.create(BuildConfig.AccessKey, BuildConfig.SecretKey).uploadToken("izjimage"), new UpCompletionHandler() {
                    @Override
                    public void complete(String key, ResponseInfo info, JSONObject res) {
                        Log.i("qiniu", key + ",\r\n " + info + ",\r\n " + res);
                        if (info.isOK()) {
                            try {
                                picMap.put(type, res.getString("key"));
                                if (picMap.size() == 4) {
//                                    finishOder();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            dissmisProgressDialog();
                            ToastUtils.showShort("图片上传失败，请重试");
                        }
                    }
                }, null);
            }
        }.start();
    }

    private void setImage(String path, ImageView imageView) {
        Bitmap bitmap = getLocalBitmap(path);
        bitmap = rotateBitmap(bitmap, 270);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setImageBitmap(bitmap);
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
}
