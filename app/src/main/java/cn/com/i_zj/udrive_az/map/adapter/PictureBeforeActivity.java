package cn.com.i_zj.udrive_az.map.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.i_zj.udrive_az.BuildConfig;
import cn.com.i_zj.udrive_az.DBSBaseActivity;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.login.SessionManager;
import cn.com.i_zj.udrive_az.map.MapUtils;
import cn.com.i_zj.udrive_az.model.CarPartPicture;
import cn.com.i_zj.udrive_az.model.DriverResult;
import cn.com.i_zj.udrive_az.network.UdriveRestClient;
import cn.com.i_zj.udrive_az.utils.ToolsUtils;
import cn.com.i_zj.udrive_az.utils.qiniu.Auth;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class PictureBeforeActivity extends DBSBaseActivity {

    @BindView(R.id.left)
    CheckBox checkBoxLeft;
    @BindView(R.id.right)
    CheckBox checkBoxRight;
    @BindView(R.id.left_before)
    CheckBox checkBoxLeftBefore;
    @BindView(R.id.left_after)
    CheckBox checkBoxLeftAfter;
    @BindView(R.id.right_before)
    CheckBox checkBoxRightBefore;
    @BindView(R.id.right_after)
    CheckBox checkBoxRightAfter;
    @BindView(R.id.after)
    CheckBox checkBoxAfter;


    @BindView(R.id.text_left)
    TextView textViewLeft;
    @BindView(R.id.text_right)
    TextView textViewRight;
    @BindView(R.id.text_left_before)
    TextView textViewLeftBefore;
    @BindView(R.id.text_left_after)
    TextView textViewLeftAfter;
    @BindView(R.id.text_right_before)
    TextView textViewRightBefore;
    @BindView(R.id.text_right_after)
    TextView textViewRightAfter;
    @BindView(R.id.text_after)
    TextView textViewAfter;

    private Context mContext;
    private int REQUEST_CODE = 1000;
    private Map<String, CarPartPicture> mCarParts = new HashMap<>();

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_picture_before;
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
        mCarParts.put("leftBackDoor", new CarPartPicture("leftBackDoor", 1005));
        mCarParts.put("rightBackDoor", new CarPartPicture("rightBackDoor", 1006));
        mCarParts.put("backBumper", new CarPartPicture("backBumper", 1007));
    }

    @OnClick({R.id.iv_back, R.id.text_left, R.id.text_right, R.id.text_left_before, R.id.text_left_after,
            R.id.text_right_before, R.id.text_right_after, R.id.text_after, R.id.btnSubmit})
    public void onClick(View view) {
        Intent intent = new Intent();
        intent.setClass(this, CameraActivity.class);
        intent.putExtra("state", 0);
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.text_left:
                intent.putExtra("part", mCarParts.get("leftFrontBumper"));
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.text_right:
                intent.putExtra("part", mCarParts.get("rightFrontBumper"));
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.text_left_before:
                intent.putExtra("part", mCarParts.get("leftFrontDoor"));
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.text_left_after:
                intent.putExtra("part", mCarParts.get("leftBackDoor"));
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.text_right_before:
                intent.putExtra("part", mCarParts.get("rightFrontDoor"));
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.text_right_after:
                intent.putExtra("part", mCarParts.get("rightBackDoor"));
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.text_after:
                intent.putExtra("part", mCarParts.get("backBumper"));
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.btnSubmit:
                createTripOrder();
                break;
        }
    }

    private void createTripOrder() {
        Map<String, String> map = new HashMap<>();
        showProgressDialog("正在提交");
        String token = SessionManager.getInstance().getAuthorization();
        UdriveRestClient.getClentInstance().createTripOrder(token, map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DriverResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(DriverResult driverResult) {
                        dissmisProgressDialog();
                        if (driverResult == null) {
                            return;
                        }
                        if (driverResult.getCode() == 1) {

                        } else {
                            ToastUtils.showShort(driverResult.getMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CODE) {
            CarPartPicture carPart = (CarPartPicture) data.getSerializableExtra("part");
            switch (carPart.getRequestCode()) {
                case 1001:
                    if (!TextUtils.isEmpty(carPart.getPhotoPath())) {
                        checkBoxLeft.setChecked(true);
                        mCarParts.put(carPart.getKey(), carPart); //更新map
                        uploadImg2QiNiu(carPart);
                    }
                    break;
                case 1002:
                    if (!TextUtils.isEmpty(carPart.getPhotoPath())) {
                        checkBoxRight.setChecked(true);
                        mCarParts.put(carPart.getKey(), carPart); //更新map
                        uploadImg2QiNiu(carPart);
                    }
                    break;
                case 1003:
                    if (!TextUtils.isEmpty(carPart.getPhotoPath())) {
                        checkBoxLeftBefore.setChecked(true);
                        mCarParts.put(carPart.getKey(), carPart); //更新map
                        uploadImg2QiNiu(carPart);
                    }
                    break;
                case 1004:
                    if (!TextUtils.isEmpty(carPart.getPhotoPath())) {
                        checkBoxRightBefore.setChecked(true);
                        mCarParts.put(carPart.getKey(), carPart); //更新map
                        uploadImg2QiNiu(carPart);
                    }
                    break;
                case 1005:
                    if (!TextUtils.isEmpty(carPart.getPhotoPath())) {
                        checkBoxLeftAfter.setChecked(true);
                        mCarParts.put(carPart.getKey(), carPart); //更新map
                        uploadImg2QiNiu(carPart);
                    }
                    break;
                case 1006:
                    if (!TextUtils.isEmpty(carPart.getPhotoPath())) {
                        checkBoxRightAfter.setChecked(true);
                        mCarParts.put(carPart.getKey(), carPart); //更新map
                        uploadImg2QiNiu(carPart);
                    }
                    break;
                case 1007:
                    if (!TextUtils.isEmpty(carPart.getPhotoPath())) {
                        checkBoxAfter.setChecked(true);
                        mCarParts.put(carPart.getKey(), carPart); //更新map
                        uploadImg2QiNiu(carPart);
                    }
                    break;
            }
        }
    }

    private void uploadImg2QiNiu(final CarPartPicture carPart) {
        showProgressDialog("上传中...", true);
        UploadManager uploadManager = new UploadManager();
        // 设置图片名字
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String key = ToolsUtils.getUniqueId(mContext) + "_" + sdf.format(new Date()) + ".png";
        uploadManager.put(carPart.getPhotoPath(), key, Auth.create(BuildConfig.AccessKey, BuildConfig.SecretKey).uploadToken("izjimage"), new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject res) {
                dissmisProgressDialog();
                if (info.isOK()) {
                    mCarParts.get(carPart.getKey()).setPhotoName(key);
                }
            }
        }, null);
    }
}
