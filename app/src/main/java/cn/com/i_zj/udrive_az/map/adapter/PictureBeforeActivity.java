package cn.com.i_zj.udrive_az.map.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.i_zj.udrive_az.BuildConfig;
import cn.com.i_zj.udrive_az.DBSBaseActivity;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.map.MapUtils;
import cn.com.i_zj.udrive_az.utils.ToolsUtils;
import cn.com.i_zj.udrive_az.utils.qiniu.Auth;

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

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_picture_before;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapUtils.statusBarColor(this);
        this.mContext = this;
    }

    @OnClick({R.id.iv_back, R.id.text_left, R.id.text_right, R.id.text_left_before, R.id.text_left_after,
            R.id.text_right_before, R.id.text_right_after, R.id.text_after})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.text_left:
                startActivityForResult(CameraActivity.class, 1);
                checkBoxLeft.setChecked(!checkBoxLeft.isChecked());
                break;
            case R.id.text_right:
                checkBoxRight.setChecked(!checkBoxRight.isChecked());
                break;
            case R.id.text_left_before:
                checkBoxLeftBefore.setChecked(!checkBoxLeftBefore.isChecked());
                break;
            case R.id.text_left_after:
                checkBoxLeftAfter.setChecked(!checkBoxLeftAfter.isChecked());
                break;
            case R.id.text_right_before:
                checkBoxRightBefore.setChecked(!checkBoxRightBefore.isChecked());
                break;
            case R.id.text_right_after:
                checkBoxRightAfter.setChecked(!checkBoxRightAfter.isChecked());
                break;
            case R.id.text_after:
                checkBoxAfter.setChecked(!checkBoxAfter.isChecked());
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        uploadImg2QiNiu(data.getStringExtra("picPath"));
    }

    private void uploadImg2QiNiu(String picPath) {
        showProgressDialog("上传中...", true);
        UploadManager uploadManager = new UploadManager();
        // 设置图片名字
        String key = "icon_" + ToolsUtils.getUniqueId(mContext) + ".png";
        uploadManager.put(picPath, key, Auth.create(BuildConfig.AccessKey, BuildConfig.SecretKey).uploadToken("izjimage"), new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject res) {
                dissmisProgressDialog();
                if (info.isOK()) {
                    String headpicPath = "http://ot6991tvl.bkt.clouddn.com/" + key;
                }
            }
        }, null);
    }
}
