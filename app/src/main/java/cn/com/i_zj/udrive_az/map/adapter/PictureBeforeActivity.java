package cn.com.i_zj.udrive_az.map.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.greenrobot.eventbus.EventBus;
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
import cn.com.i_zj.udrive_az.event.OrderFinishEvent;
import cn.com.i_zj.udrive_az.lz.ui.violation.ViolationActivity;
import cn.com.i_zj.udrive_az.map.MapUtils;
import cn.com.i_zj.udrive_az.map.TravelingActivity;
import cn.com.i_zj.udrive_az.model.CarPartPicture;
import cn.com.i_zj.udrive_az.model.CreateOderBean;
import cn.com.i_zj.udrive_az.model.PhotoBean;
import cn.com.i_zj.udrive_az.model.UnFinishOrderResult;
import cn.com.i_zj.udrive_az.network.UdriveRestClient;
import cn.com.i_zj.udrive_az.utils.ToolsUtils;
import cn.com.i_zj.udrive_az.utils.qiniu.Auth;
import cn.com.i_zj.udrive_az.widget.CommonAlertDialog;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class PictureBeforeActivity extends DBSBaseActivity implements CompoundButton.OnCheckedChangeListener {

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

    @BindView(R.id.checkbox)
    CheckBox checkbox;
    @BindView(R.id.btnSubmit)
    Button btnSubmit;

    private Context mContext;
    private int REQUEST_CODE = 1000;
    private String destinationParkId;
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
        destinationParkId = getIntent().getStringExtra("destinationParkId");

        mCarParts.put("leftFrontBumper", new CarPartPicture("leftFrontBumper", 1001));
        mCarParts.put("rightFrontBumper", new CarPartPicture("rightFrontBumper", 1002));
        mCarParts.put("leftFrontDoor", new CarPartPicture("leftFrontDoor", 1003));
        mCarParts.put("rightFrontDoor", new CarPartPicture("rightFrontDoor", 1004));
        mCarParts.put("leftBackDoor", new CarPartPicture("leftBackDoor", 1005));
        mCarParts.put("rightBackDoor", new CarPartPicture("rightBackDoor", 1006));
        mCarParts.put("backBumper", new CarPartPicture("backBumper", 1007));

        checkbox.setOnCheckedChangeListener(this);
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
                if (checkbox.isChecked()) {
                    return;
                }
                intent.putExtra("part", mCarParts.get("leftFrontBumper"));
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.text_right:
                if (checkbox.isChecked()) {
                    return;
                }
                intent.putExtra("part", mCarParts.get("rightFrontBumper"));
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.text_left_before:
                if (checkbox.isChecked()) {
                    return;
                }
                intent.putExtra("part", mCarParts.get("leftFrontDoor"));
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.text_left_after:
                if (checkbox.isChecked()) {
                    return;
                }
                intent.putExtra("part", mCarParts.get("leftBackDoor"));
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.text_right_before:
                if (checkbox.isChecked()) {
                    return;
                }
                intent.putExtra("part", mCarParts.get("rightFrontDoor"));
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.text_right_after:
                if (checkbox.isChecked()) {
                    return;
                }
                intent.putExtra("part", mCarParts.get("rightBackDoor"));
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.text_after:
                if (checkbox.isChecked()) {
                    return;
                }
                intent.putExtra("part", mCarParts.get("backBumper"));
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.btnSubmit:
                createTripOrder();
                break;
        }
    }

    private void createTripOrder() {
        Map<String, Object> map = new HashMap<>();
        map.put("destinationParkId", destinationParkId);
        boolean hasPhoto = false;
        PhotoBean photoBean = new PhotoBean();
        for (Map.Entry<String, CarPartPicture> entry : mCarParts.entrySet()) {
            if (entry.getValue().hasPhoto()) {
                hasPhoto = true;
                switch (entry.getKey()) {
                    case "leftFrontBumper":
                        photoBean.setLeftFrontBumper(entry.getValue().getPhotoName());
                        break;
                    case "rightFrontBumper":
                        photoBean.setRightFrontBumper(entry.getValue().getPhotoName());
                        break;
                    case "leftFrontDoor":
                        photoBean.setLeftFrontDoor(entry.getValue().getPhotoName());
                        break;
                    case "rightFrontDoor":
                        photoBean.setRightFrontDoor(entry.getValue().getPhotoName());
                        break;
                    case "leftBackDoor":
                        photoBean.setLeftBackDoor(entry.getValue().getPhotoName());
                        break;
                    case "rightBackDoor":
                        photoBean.setRightBackDoor(entry.getValue().getPhotoName());
                        break;
                    case "backBumper":
                        photoBean.setBackBumper(entry.getValue().getPhotoName());
                        break;
                }
            }
        }
        if (hasPhoto) {
            map.put("protocol", "1");
            map.put("photo", photoBean);
        } else {
            map.put("protocol", "2");
        }
        showProgressDialog();
        UdriveRestClient.getClentInstance().createTripOrder(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(result -> {
                    if (result == null) {
                        ToastUtils.showShort("数据请求失败");
                        return false;
                    }
                    if (result.getCode() != 1) {
                        if (result.getCode() == 2005) {
                            CommonAlertDialog.builder(this)
                                    .setTitle("违章处理")
                                    .setMsg("尊敬的用户您好，您有待处理的违章，请及时处理。")
                                    .setNegativeButton("取消", null)
                                    .setPositiveButton("去处理", v -> startActivity(ViolationActivity.class))
                                    .build()
                                    .show();
//                            CommonAlertDialog.builder(this)
//                                    .setMsg("存在违章记录")
//                                    .setMsgCenter(true)
//                                    .setNegativeButton("取消", null)
//                                    .setPositiveButton("去处理", v -> {
//                                        Violation1 violation = new Gson().fromJson(result.getData(), Violation1.class);
//                                        Intent intent = new Intent();
//                                        intent.setClass(this, ViolationDetailActivity.class);
//                                        intent.putExtra("id", violation.getId());
//                                        startActivity(intent);
//                                        finish();
//                                    })
//                                    .build()
//                                    .show();
                        } else if (!TextUtils.isEmpty(result.getMessage())) {
                            ToastUtils.showShort(result.getMessage());
                        }
                        return false;
                    }
                    return true;
                })
                .flatMap((Function<CreateOderBean, ObservableSource<UnFinishOrderResult>>) createOderBean ->
                        UdriveRestClient.getClentInstance().getUnfinishedOrder()
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread()))
                .subscribe(new Observer<UnFinishOrderResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(UnFinishOrderResult result) {
                        if (result == null) {
                            ToastUtils.showShort("数据请求失败");
                            return;
                        }
                        if (result.getCode() == 1) {
                            if (result.getData() != null && result.getData().getId() > 0) {
                                startActivity(TravelingActivity.class);
                                EventBus.getDefault().post(new OrderFinishEvent(true));
                            }
                        } else {
                            if (!TextUtils.isEmpty(result.getMessage())) {
                                ToastUtils.showShort(result.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        dissmisProgressDialog();
                    }

                    @Override
                    public void onComplete() {
                        dissmisProgressDialog();
                    }
                });
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
                            updateUI(checkBoxLeft, textViewLeft);
                            mCarParts.put(carPart.getKey(), carPart); //更新map
                            uploadImg2QiNiu(carPart);
                        }
                        break;
                    case 1002:
                        if (!TextUtils.isEmpty(carPart.getPhotoPath())) {
                            updateUI(checkBoxRight, textViewRight);
                            mCarParts.put(carPart.getKey(), carPart); //更新map
                            uploadImg2QiNiu(carPart);
                        }
                        break;
                    case 1003:
                        if (!TextUtils.isEmpty(carPart.getPhotoPath())) {
                            updateUI(checkBoxLeftBefore, textViewLeftBefore);
                            mCarParts.put(carPart.getKey(), carPart); //更新map
                            uploadImg2QiNiu(carPart);
                        }
                        break;
                    case 1004:
                        if (!TextUtils.isEmpty(carPart.getPhotoPath())) {
                            updateUI(checkBoxRightBefore, textViewRightBefore);
                            mCarParts.put(carPart.getKey(), carPart); //更新map
                            uploadImg2QiNiu(carPart);
                        }
                        break;
                    case 1005:
                        if (!TextUtils.isEmpty(carPart.getPhotoPath())) {
                            updateUI(checkBoxLeftAfter, textViewLeftAfter);
                            mCarParts.put(carPart.getKey(), carPart); //更新map
                            uploadImg2QiNiu(carPart);
                        }
                        break;
                    case 1006:
                        if (!TextUtils.isEmpty(carPart.getPhotoPath())) {
                            updateUI(checkBoxRightAfter, textViewRightAfter);
                            mCarParts.put(carPart.getKey(), carPart); //更新map
                            uploadImg2QiNiu(carPart);
                        }
                        break;
                    case 1007:
                        if (!TextUtils.isEmpty(carPart.getPhotoPath())) {
                            updateUI(checkBoxAfter, textViewAfter);
                            mCarParts.put(carPart.getKey(), carPart); //更新map
                            uploadImg2QiNiu(carPart);
                        }
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void updateUI(CheckBox checkBox, TextView textView) {
        checkBox.setChecked(true);
        textView.setTextColor(Color.WHITE);
        textView.setBackgroundResource(R.drawable.bg_gray_stroke1);
    }

    private void uploadImg2QiNiu(final CarPartPicture carPart) {
        btnSubmit.setEnabled(true);
        checkbox.setEnabled(false);
        showProgressDialog();
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

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        btnSubmit.setEnabled(isChecked);
    }
}
