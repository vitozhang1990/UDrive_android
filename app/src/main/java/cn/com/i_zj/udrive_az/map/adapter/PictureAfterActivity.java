package cn.com.i_zj.udrive_az.map.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
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
import cn.com.i_zj.udrive_az.event.WebSocketEvent;
import cn.com.i_zj.udrive_az.lz.ui.payment.ActConfirmOrder;
import cn.com.i_zj.udrive_az.lz.ui.payment.PaymentActivity;
import cn.com.i_zj.udrive_az.map.MapUtils;
import cn.com.i_zj.udrive_az.model.OrderDetailResult;
import cn.com.i_zj.udrive_az.model.PhotoBean;
import cn.com.i_zj.udrive_az.network.UdriveRestClient;
import cn.com.i_zj.udrive_az.utils.ToolsUtils;
import cn.com.i_zj.udrive_az.utils.qiniu.Auth;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class PictureAfterActivity extends DBSBaseActivity {

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
    private String backPath, rightFrontPath, leftFrontPath, innerPath;
    private Map<String, String> picMap = new HashMap<>();
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_picture_after;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapUtils.statusBarColor(this);
        this.mContext = this;

        orderNum = getIntent().getStringExtra("orderNum");

        Intent intent = new Intent();
        intent.setClass(this, CameraActivity.class);
        intent.putExtra("state", 1);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @OnClick({R.id.iv_back, R.id.btn_neishi, R.id.btn_left, R.id.btn_right, R.id.btn_tail, R.id.btnSubmit})
    public void onClick(View view) {
        Intent intent = new Intent();
        intent.setClass(this, CameraActivity.class);
        intent.putExtra("state", 1);
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_neishi:
                if (!TextUtils.isEmpty(innerPath)) intent.putExtra("innerPath", innerPath);
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.btn_left:
                if (!TextUtils.isEmpty(leftFrontPath)) intent.putExtra("leftFrontPath", leftFrontPath);
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.btn_right:
                if (!TextUtils.isEmpty(rightFrontPath)) intent.putExtra("rightFrontPath", rightFrontPath);
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.btn_tail:
                if (!TextUtils.isEmpty(backPath)) intent.putExtra("backPath", backPath);
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.btnSubmit:
                showProgressDialog();
                picMap.clear();
                uploadImg2QiNiu("innerPath", innerPath);
                uploadImg2QiNiu("backPath", backPath);
                uploadImg2QiNiu("rightFrontPath", rightFrontPath);
                uploadImg2QiNiu("leftFrontPath", leftFrontPath);
                break;
        }
    }

    private void finishOder() {
        PhotoBean photoBean = new PhotoBean();
        if (picMap.containsKey("backPath")) {
            photoBean.setBackPhoto(picMap.get("backPath"));
        }
        if (picMap.containsKey("rightFrontPath")) {
            photoBean.setRightFrontPhoto(picMap.get("rightFrontPath"));
        }
        if (picMap.containsKey("leftFrontPath")) {
            photoBean.setLeftFrontPhoto(picMap.get("leftFrontPath"));
        }
        if (picMap.containsKey("innerPath")) {
            photoBean.setInnerPhoto(picMap.get("innerPath"));
        }
        Map<String, Object> map = new HashMap<>();
        map.put("orderNum", orderNum);
        map.put("photo", photoBean);
        UdriveRestClient.getClentInstance().finishTripOrder(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<OrderDetailResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(OrderDetailResult bean) {
                        dissmisProgressDialog();
                        if (bean != null) {
                            if (bean.code == 1) {
                                ToastUtils.showShort("还车成功");
                                Intent intent1 = new Intent(PictureAfterActivity.this, ActConfirmOrder.class);
                                intent1.putExtra(PaymentActivity.ORDER_NUMBER, orderNum);
                                startActivity(intent1);
                                EventBus.getDefault().post(new WebSocketEvent());
                                EventBus.getDefault().post(new OrderFinishEvent());
                                finish();
                            } else if (bean.code == 1002) {
                                ToastUtils.showShort(bean.message);
                            } else {
                                ToastUtils.showShort(bean.message);
                            }
                        } else {
                            ToastUtils.showShort("还车失败");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        dissmisProgressDialog();
                        e.printStackTrace();
                        ToastUtils.showShort("还车失败了");
                    }

                    @Override
                    public void onComplete() {
                        dissmisProgressDialog();
                    }
                });
    }

    private void updateUI() {
        if (!TextUtils.isEmpty(backPath)) setImage(backPath, iv_tail);
        if (!TextUtils.isEmpty(rightFrontPath)) setImage(rightFrontPath, iv_right);
        if (!TextUtils.isEmpty(leftFrontPath)) setImage(leftFrontPath, iv_left);
        if (!TextUtils.isEmpty(innerPath)) setImage(innerPath, iv_neishi);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            finish();
            return;
        }
        if (data == null) {
            return;
        }
        if (requestCode == REQUEST_CODE) {
            if (!TextUtils.isEmpty(data.getStringExtra("backPath"))) {
                backPath = data.getStringExtra("backPath");
            }
            if (!TextUtils.isEmpty(data.getStringExtra("rightFrontPath"))) {
                rightFrontPath = data.getStringExtra("rightFrontPath");
            }
            if (!TextUtils.isEmpty(data.getStringExtra("leftFrontPath"))) {
                leftFrontPath = data.getStringExtra("leftFrontPath");
            }
            if (!TextUtils.isEmpty(data.getStringExtra("innerPath"))) {
                innerPath = data.getStringExtra("innerPath");
            }
            updateUI();
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
                                    finishOder();
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
