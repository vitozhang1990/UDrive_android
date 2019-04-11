package cn.com.i_zj.udrive_az.lz.ui.violation;

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
import cn.com.i_zj.udrive_az.model.ret.ViolationDetailObj;
import cn.com.i_zj.udrive_az.model.ret.ViolationObj;
import cn.com.i_zj.udrive_az.network.UdriveRestClient;
import cn.com.i_zj.udrive_az.refuel.RefuelActivity;
import cn.com.i_zj.udrive_az.refuel.RefuelStatusActivity;
import cn.com.i_zj.udrive_az.utils.ToolsUtils;
import cn.com.i_zj.udrive_az.utils.qiniu.Auth;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ViolationDealActivity extends DBSBaseActivity {

    @BindView(R.id.header_title)
    TextView header_title;
    @BindView(R.id.header_image)
    ImageView header_image;

    @BindView(R.id.iv_empty)
    ImageView iv_empty;
    @BindView(R.id.error_empty)
    ImageView error_empty;

    @BindView(R.id.btn_commit)
    Button btnSubmit;

    private ViolationDetailObj detailObj;
    private CarPartPicture picture;
    private int REQUEST_CODE = 1002;
    private Context mContext;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_violation_deal;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapUtils.statusBarColor(this);
        this.mContext = this;

        header_title.setText("违章处理");
        header_image.setImageResource(R.mipmap.ic_service);

        detailObj = (ViolationDetailObj) getIntent().getSerializableExtra("data");
        if (detailObj == null) {
            finish();
            return;
        }

        picture = new CarPartPicture("processSheetPhoto", 1001,
                detailObj.getState() == 3 && !TextUtils.isEmpty(detailObj.getProcessSheetPhoto())
                        ? BuildConfig.IMAGE_DOMAIN + detailObj.getProcessSheetPhoto()
                        : null);
        if (detailObj.getState() == 3 && !TextUtils.isEmpty(detailObj.getProcessSheetPhoto())) {
            Glide.with(this).load(BuildConfig.IMAGE_DOMAIN + detailObj.getProcessSheetPhoto()).into(iv_empty);
            if (error_empty != null) error_empty.setVisibility(View.VISIBLE);
        }
        resetButtonStatus();
    }

    @OnClick({R.id.header_left, R.id.header_right, R.id.btn_empty, R.id.btn_commit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.header_left:
                finish();
                break;
            case R.id.header_right:
                Intent intent = new Intent(Intent.ACTION_DIAL);
                Uri data = Uri.parse("tel:" + getResources().getString(R.string.about_phone));
                intent.setData(data);
                startActivity(intent);
                break;
            case R.id.btn_empty:
                Intent pictureIntent = new Intent();
                pictureIntent.setClass(this, CameraActivity.class);
                pictureIntent.putExtra("state", 2);
                pictureIntent.putExtra("part", picture);
                startActivityForResult(pictureIntent, REQUEST_CODE);
                break;
            case R.id.btn_commit:
                if (detailObj.getState() == 3
                        && !TextUtils.isEmpty(detailObj.getProcessSheetPhoto())
                        && !picture.hasPhoto()) {
                    showToast("请修改红框里的图片");
                    return;
                }
                showProgressDialog();
                uploadImg2QiNiu("processSheetPhoto", picture.getPhotoPath());
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
                if (!TextUtils.isEmpty(carPart.getPhotoPath())) {
                    picture = carPart;
                    Glide.with(this).load(carPart.getPhotoPath()).into(iv_empty);
                    if (error_empty != null) error_empty.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            resetButtonStatus();
        }
    }

    private void setImageUrl(String path, ImageView imageView, ImageView imageView1) {
        Glide.with(this).load(BuildConfig.IMAGE_DOMAIN + path).into(imageView);
        if (imageView1 != null) imageView1.setVisibility(View.VISIBLE);
    }

    private void resetButtonStatus() {
        btnSubmit.setEnabled(!TextUtils.isEmpty(picture.getPhotoPath()));
    }

    private void commit(String key) {
        Map<String, Object> map = new HashMap<>();
        map.put("filename", key);
        UdriveRestClient.getClentInstance().updateIllegal(detailObj.getId(), map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseRetObj<ViolationObj>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseRetObj<ViolationObj> refuelObjBaseRetObj) {
                        if (refuelObjBaseRetObj != null && refuelObjBaseRetObj.getCode() == 1) {
                            showToast("提交成功");
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
                                commit(res.getString("key"));
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
}
