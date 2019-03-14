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
import com.bumptech.glide.Glide;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

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
import cn.com.i_zj.udrive_az.map.MapUtils;
import cn.com.i_zj.udrive_az.map.adapter.CameraActivity;
import cn.com.i_zj.udrive_az.model.CarPartPicture;
import cn.com.i_zj.udrive_az.model.ret.BaseRetObj;
import cn.com.i_zj.udrive_az.model.ret.RefuelObj;
import cn.com.i_zj.udrive_az.network.UObserver;
import cn.com.i_zj.udrive_az.network.UdriveRestClient;
import cn.com.i_zj.udrive_az.utils.ToolsUtils;
import cn.com.i_zj.udrive_az.utils.qiniu.Auth;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class RefuelActivity extends DBSBaseActivity {

    @BindView(R.id.iv_empty)
    ImageView iv_empty;
    @BindView(R.id.iv_course)
    ImageView iv_course;
    @BindView(R.id.iv_ticket)
    ImageView iv_ticket;
    @BindView(R.id.iv_fuel)
    ImageView iv_fuel;

    private Context mContext;
    private String orderNum;
    private int REQUEST_CODE = 1002;
    private RefuelObj mRefuelObj;
    private Map<String, String> picMap = new HashMap<>(); //7牛上的key
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
        mRefuelObj = (RefuelObj) getIntent().getSerializableExtra("data");
        if (mRefuelObj == null) {
            finish();
            return;
        }
        mCarParts.put("refuelBeforePhoto", new CarPartPicture("refuelBeforePhoto", 1001, mRefuelObj.getRefuelBeforePhoto()));
        if (!TextUtils.isEmpty(mRefuelObj.getRefuelBeforePhoto())) {
            picMap.put("refuelBeforePhoto", mRefuelObj.getRefuelBeforePhoto());
            setImageUrl(mRefuelObj.getRefuelBeforePhoto(), iv_empty);
        }
        mCarParts.put("pnPhoto", new CarPartPicture("pnPhoto", 1002, mRefuelObj.getPnPhoto()));
        if (!TextUtils.isEmpty(mRefuelObj.getPnPhoto())) {
            picMap.put("pnPhoto", mRefuelObj.getPnPhoto());
            setImageUrl(mRefuelObj.getPnPhoto(), iv_course);
        }
        mCarParts.put("receiptPhoto", new CarPartPicture("receiptPhoto", 1003, mRefuelObj.getReceiptPhoto()));
        if (!TextUtils.isEmpty(mRefuelObj.getReceiptPhoto())) {
            picMap.put("receiptPhoto", mRefuelObj.getReceiptPhoto());
            setImageUrl(mRefuelObj.getReceiptPhoto(), iv_ticket);
        }
        mCarParts.put("refuelAfterPhoto", new CarPartPicture("refuelAfterPhoto", 1004, mRefuelObj.getRefuelAfterPhoto()));
        if (!TextUtils.isEmpty(mRefuelObj.getRefuelAfterPhoto())) {
            picMap.put("refuelAfterPhoto", mRefuelObj.getRefuelAfterPhoto());
            setImageUrl(mRefuelObj.getRefuelAfterPhoto(), iv_fuel);
        }
    }

    @OnClick({R.id.iv_back, R.id.btn_empty, R.id.btn_course, R.id.btn_ticket, R.id.btn_fuel, R.id.oil_park, R.id.btnSubmit})
    public void onClick(View view) {
        Intent intent = new Intent();
        intent.setClass(this, CameraActivity.class);
        intent.putExtra("state", 2);
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_empty:
                intent.putExtra("part", mCarParts.get("refuelBeforePhoto"));
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.btn_course:
                intent.putExtra("part", mCarParts.get("pnPhoto"));
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.btn_ticket:
                intent.putExtra("part", mCarParts.get("receiptPhoto"));
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.btn_fuel:
                intent.putExtra("part", mCarParts.get("refuelAfterPhoto"));
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
                if ((!picMap.containsKey("refuelBeforePhoto") && !mCarParts.get("refuelBeforePhoto").hasPhoto())
                        || (!picMap.containsKey("pnPhoto") && !mCarParts.get("pnPhoto").hasPhoto())
                        || (!picMap.containsKey("receiptPhoto") && !mCarParts.get("receiptPhoto").hasPhoto())
                        || (!picMap.containsKey("refuelAfterPhoto") && !mCarParts.get("refuelAfterPhoto").hasPhoto())) {
                    showToast("请添加所有图片");
                    return;
                }
                showProgressDialog();
                if (mCarParts.get("refuelBeforePhoto").hasPhoto()) {
                    uploadImg2QiNiu("refuelBeforePhoto", mCarParts.get("refuelBeforePhoto").getPhotoPath());
                }
                if (mCarParts.get("pnPhoto").hasPhoto()) {
                    uploadImg2QiNiu("pnPhoto", mCarParts.get("pnPhoto").getPhotoPath());
                }
                if (mCarParts.get("receiptPhoto").hasPhoto()) {
                    uploadImg2QiNiu("receiptPhoto", mCarParts.get("receiptPhoto").getPhotoPath());
                }
                if (mCarParts.get("refuelAfterPhoto").hasPhoto()) {
                    uploadImg2QiNiu("refuelAfterPhoto", mCarParts.get("refuelAfterPhoto").getPhotoPath());
                }
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
                            setImage(carPart.getPhotoPath(), iv_empty);
                        }
                        break;
                    case 1002:
                        if (!TextUtils.isEmpty(carPart.getPhotoPath())) {
                            mCarParts.put(carPart.getKey(), carPart);
                            setImage(carPart.getPhotoPath(), iv_course);
                        }
                        break;
                    case 1003:
                        if (!TextUtils.isEmpty(carPart.getPhotoPath())) {
                            mCarParts.put(carPart.getKey(), carPart);
                            setImage(carPart.getPhotoPath(), iv_ticket);
                        }
                        break;
                    case 1004:
                        if (!TextUtils.isEmpty(carPart.getPhotoPath())) {
                            mCarParts.put(carPart.getKey(), carPart);
                            setImage(carPart.getPhotoPath(), iv_fuel);
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
                                    Log.e("zhangwei", "11111111111111");
                                    commit();
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

    private void commit() {
        Map<String, Object> map = new HashMap<>();
        if (mRefuelObj.getId() != 0) {
            map.put("id", mRefuelObj.getId());
        }
        map.put("orderNumber", mRefuelObj.getOrderNumber());
        map.put("refuelBeforePhoto", picMap.get("refuelBeforePhoto"));
        map.put("pnPhoto", picMap.get("pnPhoto"));
        map.put("receiptPhoto", picMap.get("receiptPhoto"));
        map.put("refuelAfterPhoto", picMap.get("refuelAfterPhoto"));
        UdriveRestClient.getClentInstance().refuel(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseRetObj<RefuelObj>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseRetObj<RefuelObj> refuelObjBaseRetObj) {
                        if (refuelObjBaseRetObj != null && refuelObjBaseRetObj.getCode() == 1) {
                            showToast("提交成功");
                            finish();
                        } else if (refuelObjBaseRetObj != null) {
                            showToast(refuelObjBaseRetObj.getMessage());
                        } else {
                            showToast("提交失败，请重试");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        showToast("提交失败，请重试");
                        dissmisProgressDialog();
                    }

                    @Override
                    public void onComplete() {
                        dissmisProgressDialog();
                    }
                });
    }

    private void setImageUrl(String path, ImageView imageView) {
        Glide.with(this).load(path).transform(new RotateTransformation(this, 270)).into(imageView);
    }

    private void setImage(String path, ImageView imageView) {
        Glide.with(this).load(path).transform(new RotateTransformation(this, 270)).into(imageView);
    }
}
