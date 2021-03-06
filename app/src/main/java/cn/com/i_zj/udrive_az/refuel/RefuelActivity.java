package cn.com.i_zj.udrive_az.refuel;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
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
import cn.com.i_zj.udrive_az.network.UdriveRestClient;
import cn.com.i_zj.udrive_az.utils.ToolsUtils;
import cn.com.i_zj.udrive_az.utils.dialog.OilParkDialog;
import cn.com.i_zj.udrive_az.utils.qiniu.Auth;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class RefuelActivity extends DBSBaseActivity {

    @BindView(R.id.header_title)
    TextView header_title;
    @BindView(R.id.header_image)
    ImageView header_image;

    @BindView(R.id.iv_empty)
    ImageView iv_empty;
    @BindView(R.id.iv_course)
    ImageView iv_course;
    @BindView(R.id.iv_ticket)
    ImageView iv_ticket;
    @BindView(R.id.iv_fuel)
    ImageView iv_fuel;
    @BindView(R.id.error_empty)
    ImageView error_empty;
    @BindView(R.id.error_course)
    ImageView error_course;
    @BindView(R.id.error_ticket)
    ImageView error_ticket;
    @BindView(R.id.error_fuel)
    ImageView error_fuel;
    @BindView(R.id.btnSubmit)
    Button btnSubmit;

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

        header_title.setText("自助加油");
        header_image.setImageResource(R.mipmap.ic_service);
        mCarParts.put("refuelBeforePhoto", new CarPartPicture("refuelBeforePhoto", 1001,
                TextUtils.isEmpty(mRefuelObj.getRefuelBeforePhoto()) ? null :
                        BuildConfig.IMAGE_DOMAIN + mRefuelObj.getRefuelBeforePhoto()));
        mCarParts.put("pnPhoto", new CarPartPicture("pnPhoto", 1002,
                TextUtils.isEmpty(mRefuelObj.getPnPhoto()) ? null :
                        BuildConfig.IMAGE_DOMAIN + mRefuelObj.getPnPhoto()));
        mCarParts.put("receiptPhoto", new CarPartPicture("receiptPhoto", 1003,
                TextUtils.isEmpty(mRefuelObj.getReceiptPhoto()) ? null :
                        BuildConfig.IMAGE_DOMAIN + mRefuelObj.getReceiptPhoto()));
        mCarParts.put("refuelAfterPhoto", new CarPartPicture("refuelAfterPhoto", 1004,
                TextUtils.isEmpty(mRefuelObj.getRefuelAfterPhoto()) ? null :
                        BuildConfig.IMAGE_DOMAIN + mRefuelObj.getRefuelAfterPhoto()));

        if (!TextUtils.isEmpty(mRefuelObj.getRefuelBeforePhoto())) {
            boolean error = !TextUtils.isEmpty(mRefuelObj.getAuditResult()) && mRefuelObj.getAuditResult().contains("refuelBeforePhoto");
            if (!error) {
                picMap.put("refuelBeforePhoto", mRefuelObj.getRefuelBeforePhoto()); //不是错误图片
            }
            setImageUrl(mRefuelObj.getRefuelBeforePhoto(), iv_empty, error ? error_empty : null);
        }

        if (!TextUtils.isEmpty(mRefuelObj.getPnPhoto())) {
            boolean error = !TextUtils.isEmpty(mRefuelObj.getAuditResult()) && mRefuelObj.getAuditResult().contains("pnPhoto");
            if (!error) {
                picMap.put("pnPhoto", mRefuelObj.getPnPhoto()); //不是错误图片
            }
            setImageUrl(mRefuelObj.getPnPhoto(), iv_course, error ? error_course : null);
        }

        if (!TextUtils.isEmpty(mRefuelObj.getReceiptPhoto())) {
            boolean error = !TextUtils.isEmpty(mRefuelObj.getAuditResult()) && mRefuelObj.getAuditResult().contains("receiptPhoto");
            if (!error) {
                picMap.put("receiptPhoto", mRefuelObj.getReceiptPhoto()); //不是错误图片
            }
            setImageUrl(mRefuelObj.getReceiptPhoto(), iv_ticket, error ? error_ticket : null);
        }

        if (!TextUtils.isEmpty(mRefuelObj.getRefuelAfterPhoto())) {
            boolean error = !TextUtils.isEmpty(mRefuelObj.getAuditResult()) && mRefuelObj.getAuditResult().contains("refuelAfterPhoto");
            if (!error) {
                picMap.put("refuelAfterPhoto", mRefuelObj.getRefuelAfterPhoto()); //不是错误图片
            }
            setImageUrl(mRefuelObj.getRefuelAfterPhoto(), iv_fuel, error ? error_fuel : null);
        }
        resetButtonStatus();
    }

    @OnClick({R.id.header_left, R.id.header_right, R.id.btn_empty, R.id.btn_course, R.id.btn_ticket, R.id.btn_fuel, R.id.oil_park, R.id.btnSubmit})
    public void onClick(View view) {
        Intent intent = new Intent();
        intent.setClass(this, CameraActivity.class);
        intent.putExtra("state", 2);
        switch (view.getId()) {
            case R.id.header_left:
                finish();
                break;
            case R.id.header_right:
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                Uri data = Uri.parse("tel:" + getResources().getString(R.string.about_phone));
                callIntent.setData(data);
                startActivity(callIntent);
                break;
            case R.id.btn_empty:
                intent.putExtra("part", picMap.containsKey("refuelBeforePhoto")
                        ? mCarParts.get("refuelBeforePhoto")
                        : new CarPartPicture("refuelBeforePhoto", 1001));
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.btn_course:
                intent.putExtra("part", picMap.containsKey("pnPhoto")
                        ? mCarParts.get("pnPhoto")
                        : new CarPartPicture("pnPhoto", 1002));
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.btn_ticket:
                intent.putExtra("part", picMap.containsKey("receiptPhoto")
                        ? mCarParts.get("receiptPhoto")
                        : new CarPartPicture("receiptPhoto", 1003));
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.btn_fuel:
                intent.putExtra("part", picMap.containsKey("refuelAfterPhoto")
                        ? mCarParts.get("refuelAfterPhoto")
                        : new CarPartPicture("refuelAfterPhoto", 1004));
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.oil_park:
                if (!new File("/data/data/com.baidu.BaiduMap").exists()
                        && !new File("/data/data/com.autonavi.minimap").exists()) {
                    ToastUtils.showShort("尚未安装高德或百度地图");
                    return;
                }
                OilParkDialog oilParkDialog = new OilParkDialog();
                oilParkDialog.show(getSupportFragmentManager(), "oilPark");
                break;
            case R.id.btnSubmit:
                if ((!picMap.containsKey("refuelBeforePhoto") && !mCarParts.get("refuelBeforePhoto").hasPhoto())
                        || (!picMap.containsKey("pnPhoto") && !mCarParts.get("pnPhoto").hasPhoto())
                        || (!picMap.containsKey("receiptPhoto") && !mCarParts.get("receiptPhoto").hasPhoto())
                        || (!picMap.containsKey("refuelAfterPhoto") && !mCarParts.get("refuelAfterPhoto").hasPhoto())) {
                    showToast("请修改红框里的图片");
                    return;
                }
                showProgressDialog();
                if (mCarParts.get("refuelBeforePhoto").hasPhoto()) {
                    picMap.remove("refuelBeforePhoto");
                    uploadImg2QiNiu("refuelBeforePhoto", mCarParts.get("refuelBeforePhoto").getPhotoPath());
                }
                if (mCarParts.get("pnPhoto").hasPhoto()) {
                    picMap.remove("pnPhoto");
                    uploadImg2QiNiu("pnPhoto", mCarParts.get("pnPhoto").getPhotoPath());
                }
                if (mCarParts.get("receiptPhoto").hasPhoto()) {
                    picMap.remove("receiptPhoto");
                    uploadImg2QiNiu("receiptPhoto", mCarParts.get("receiptPhoto").getPhotoPath());
                }
                if (mCarParts.get("refuelAfterPhoto").hasPhoto()) {
                    picMap.remove("refuelAfterPhoto");
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
                            setImage(carPart.getPhotoPath(), iv_empty, error_empty);
                        }
                        break;
                    case 1002:
                        if (!TextUtils.isEmpty(carPart.getPhotoPath())) {
                            mCarParts.put(carPart.getKey(), carPart);
                            setImage(carPart.getPhotoPath(), iv_course, error_course);
                        }
                        break;
                    case 1003:
                        if (!TextUtils.isEmpty(carPart.getPhotoPath())) {
                            mCarParts.put(carPart.getKey(), carPart);
                            setImage(carPart.getPhotoPath(), iv_ticket, error_ticket);
                        }
                        break;
                    case 1004:
                        if (!TextUtils.isEmpty(carPart.getPhotoPath())) {
                            mCarParts.put(carPart.getKey(), carPart);
                            setImage(carPart.getPhotoPath(), iv_fuel, error_fuel);
                        }
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            resetButtonStatus();
        }
    }

    private void resetButtonStatus() {
        boolean defect = TextUtils.isEmpty(mCarParts.get("refuelBeforePhoto").getPhotoPath())
                || TextUtils.isEmpty(mCarParts.get("pnPhoto").getPhotoPath())
                || TextUtils.isEmpty(mCarParts.get("receiptPhoto").getPhotoPath())
                || TextUtils.isEmpty(mCarParts.get("refuelAfterPhoto").getPhotoPath());
        btnSubmit.setEnabled(!defect);
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
                            RefuelObj refuelObj = new RefuelObj();
                            refuelObj.setState(1);
                            Intent refuelIntent = new Intent();
                            refuelIntent.putExtra("data", refuelObj);
                            refuelIntent.setClass(RefuelActivity.this, RefuelStatusActivity.class);
                            startActivity(refuelIntent);
                            finish();
                        } else if (refuelObjBaseRetObj != null
                                && !TextUtils.isEmpty(refuelObjBaseRetObj.getMessage())) {
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

    private void setImageUrl(String path, ImageView imageView, ImageView imageView1) {
        Glide.with(this).load(BuildConfig.IMAGE_DOMAIN + path).into(imageView);
        if (imageView1 != null) imageView1.setVisibility(View.VISIBLE);
    }

    private void setImage(String path, ImageView imageView, ImageView imageView1) {
        Glide.with(this).load(path).into(imageView);
        if (imageView1 != null) imageView1.setVisibility(View.GONE);
    }
}
