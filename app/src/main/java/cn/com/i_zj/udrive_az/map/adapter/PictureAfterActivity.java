package cn.com.i_zj.udrive_az.map.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

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
import cn.com.i_zj.udrive_az.utils.ToolsUtils;
import cn.com.i_zj.udrive_az.utils.qiniu.Auth;

public class PictureAfterActivity extends DBSBaseActivity {

    @BindView(R.id.iv_neishi)
    ImageView iv_neishi;
    @BindView(R.id.iv_left)
    ImageView iv_left;
    @BindView(R.id.iv_right)
    ImageView iv_right;
    @BindView(R.id.iv_tail)
    ImageView iv_tail;

    @BindView(R.id.btnSubmit)
    Button btnSubmit;

    private Context mContext;
    private int REQUEST_CODE = 1002;
    private String backPath, rightFrontPath, leftFrontPath, innerPath;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            btnSubmit.setEnabled(true);
        }
    };

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_picture_after;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapUtils.statusBarColor(this);
        this.mContext = this;
    }

    @OnClick({R.id.iv_back, R.id.btn_neishi, R.id.btn_left, R.id.btn_right, R.id.btn_tail, R.id.btnSubmit})
    public void onClick(View view) {
        Intent intent = new Intent();
        intent.setClass(this, CameraActivity.class);
        intent.putExtra("state", 2);
        if (!TextUtils.isEmpty(backPath)) intent.putExtra("backPath", backPath);
        if (!TextUtils.isEmpty(rightFrontPath)) intent.putExtra("rightFrontPath", rightFrontPath);
        if (!TextUtils.isEmpty(leftFrontPath)) intent.putExtra("leftFrontPath", leftFrontPath);
        if (!TextUtils.isEmpty(innerPath)) intent.putExtra("innerPath", innerPath);
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_neishi:
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.btn_left:
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.btn_right:
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.btn_tail:
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.btnSubmit:
                break;
        }
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
        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        if (requestCode == REQUEST_CODE) {
            if (!TextUtils.isEmpty(data.getStringExtra("backPath"))) {
                backPath = data.getStringExtra("backPath");
                uploadImg2QiNiu("backPath", backPath);
            }
            if (!TextUtils.isEmpty(data.getStringExtra("rightFrontPath"))) {
                rightFrontPath = data.getStringExtra("rightFrontPath");
                uploadImg2QiNiu("rightFrontPath", rightFrontPath);
            }
            if (!TextUtils.isEmpty(data.getStringExtra("leftFrontPath"))) {
                leftFrontPath = data.getStringExtra("leftFrontPath");
                uploadImg2QiNiu("leftFrontPath", leftFrontPath);
            }
            if (!TextUtils.isEmpty(data.getStringExtra("innerPath"))) {
                innerPath = data.getStringExtra("innerPath");
                uploadImg2QiNiu("innerPath", innerPath);
            }
            updateUI();
        }
    }

    private Map<String, String> picMap = new HashMap<>();

    private void uploadImg2QiNiu(final String type, final String path) {
        new Thread() {
            public void run() {
                UploadManager uploadManager = new UploadManager();
                // 设置图片名字
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                String key = type + ToolsUtils.getUniqueId(mContext) + "_" + sdf.format(new Date()) + ".png";
                uploadManager.put(path, key, Auth.create(BuildConfig.AccessKey, BuildConfig.SecretKey).uploadToken("izjimage"), new UpCompletionHandler() {
                    @Override
                    public void complete(String key, ResponseInfo info, JSONObject res) {
                        Log.i("qiniu", key + ",\r\n " + info + ",\r\n " + res);
                        if (info.isOK()) {
                            try {
                                picMap.put(type, res.getString("key"));
                                if (picMap.size() == 4) {
                                    mHandler.sendEmptyMessage(0);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, null);
            }
        }.start();
    }

    private void setImage(String path, ImageView imageView) {
        Uri uri = Uri.fromFile(new File(path));
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setImageURI(uri);
    }
}
