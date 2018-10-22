package cn.com.i_zj.udrive_az.lz.ui.idregister;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.com.i_zj.udrive_az.BaseActivity;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.login.AccountInfoManager;
import cn.com.i_zj.udrive_az.login.SessionManager;
import cn.com.i_zj.udrive_az.lz.bean.CameraEvent;
import cn.com.i_zj.udrive_az.lz.ui.camera.CameraActivity;
import cn.com.i_zj.udrive_az.lz.ui.idpost.IDPostActivity;
import cn.com.i_zj.udrive_az.lz.util.HttpUtils;
import cn.com.i_zj.udrive_az.model.AccountInfoResult;
import cn.com.i_zj.udrive_az.model.DriverResult;
import cn.com.i_zj.udrive_az.model.ImageUrlResult;
import cn.com.i_zj.udrive_az.network.UdriveRestClient;
import cn.com.i_zj.udrive_az.utils.Constants;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pub.devrel.easypermissions.EasyPermissions;

public class IDRegisterActivity extends BaseActivity implements View.OnClickListener, EasyPermissions.PermissionCallbacks {

    private static final String TAG = IDRegisterActivity.class.getSimpleName();

    private ImageView mIvPositive;
    private ImageView mIvNegative;
    private Button mBtnNext;
    private Unbinder unBind;

    private ProgressBar mPbFront;
    private ProgressBar mPbBehind;

    private static final int CODE_CAMERA_ERROR = -1;
    private static final int CODE_CAMERA_POSITIVE = 0;
    private static final int CODE_CAMERA_NEGATIVE = 1;
    public static final int CODE_ID = 3;

    public static final String CODE_CAMERA = "code_camera";

    private String mFrontUrl = "";
    private String mBehindUrl = "";
    private String title;
    private LinearLayout mLine_check;
    private CheckBox mCheckBox;

    private IdBean mIdBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_id_register);

        unBind = ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        Toolbar toolbar = findViewById(R.id.toolbar);

        TextView tv1 = findViewById(R.id.tv_1);
        TextView tv2 = findViewById(R.id.tv_2);
        title = getIntent().getStringExtra(Constants.INTENT_TITLE);
        toolbar.setTitle(title);


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        mIvPositive = findViewById(R.id.iv_positive);
        mIvNegative = findViewById(R.id.iv_negative);
        mBtnNext = findViewById(R.id.btn_next);

        mPbFront = findViewById(R.id.pb_front);
        mPbBehind = findViewById(R.id.pb_behind);


        mLine_check = findViewById(R.id.line_check);
        mCheckBox = findViewById(R.id.checkbox);

        mIvPositive.setOnClickListener(this);
        mIvNegative.setOnClickListener(this);
        mBtnNext.setOnClickListener(this);
        mLine_check.setOnClickListener(this);
        if (TextUtils.equals(title, Constants.INTENT_DRIVER_INFO)) {
            mLine_check.setVisibility(View.VISIBLE);
            tv1.setText("点击图片上传驾驶证正页");
            tv2.setText("点击图片上传驾驶证副页");
            mBtnNext.setText("提交");
            mIvNegative.setImageResource(R.mipmap.ic_drvier_p);
        }

        deleteTempFile();
        checkPermission();
    }

    /**
     * 检测照相机权限
     */
    private void checkPermission() {
        boolean external = EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (!external) {
            EasyPermissions.requestPermissions(this, getString(R.string.lz_request_permission), 1, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);

    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if (perms.size() > 0) {
            Toast.makeText(this, R.string.permission_success, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.permission_file, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Toast.makeText(this, R.string.permission_request_fail, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_positive:
                Intent positiveIntent = new Intent(this, CameraActivity.class);
                positiveIntent.putExtra(CODE_CAMERA, CODE_CAMERA_POSITIVE);
                startActivity(positiveIntent);
                break;
            case R.id.iv_negative:
                Intent negativeIntent = new Intent(this, CameraActivity.class);
                negativeIntent.putExtra(CODE_CAMERA, CODE_CAMERA_NEGATIVE);
                startActivity(negativeIntent);
                break;
            case R.id.line_check:
                mCheckBox.setChecked(!mCheckBox.isChecked());
                break;
            case R.id.btn_next:
                if (TextUtils.equals(title, Constants.INTENT_DRIVER_INFO)) {
                    uploadDriverInfo();
                } else {
                    gotoIdPost();
                }
                break;
            default:
                break;
        }
    }

    private void gotoIdPost() {
        if (TextUtils.isEmpty(mFrontUrl)) {
            Toast.makeText(this, R.string.nedd_front, Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(mBehindUrl)) {
            Toast.makeText(this, R.string.need_behind, Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, IDPostActivity.class);
        intent.putExtra(Constants.URL_IDENTITY_CARD_PHOTO_FRONT, mFrontUrl);
        intent.putExtra(Constants.URL_IDENTITY_CARD_PHOTO_BEHIND, mBehindUrl);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.URL_BEAN, mIdBean);
        intent.putExtras(bundle);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            finish();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void cameraEvent(CameraEvent cameraEvent) {
        if (CODE_CAMERA_ERROR == cameraEvent.getCode()) {
        } else if (CODE_CAMERA_POSITIVE == cameraEvent.getCode()) {
            mIvPositive.setVisibility(View.INVISIBLE);
            mPbFront.setVisibility(View.VISIBLE);
            uploadImage(cameraEvent.getPath(), true);

        } else if (CODE_CAMERA_NEGATIVE == cameraEvent.getCode()) {
            mIvNegative.setVisibility(View.INVISIBLE);
            mPbBehind.setVisibility(View.VISIBLE);
            uploadImage(cameraEvent.getPath(), false);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unBind.unbind();
        EventBus.getDefault().unregister(this);
    }

    private void deleteTempFile() {
        File dir = new File(getFilesDir() + CameraActivity.CAMERA_TEMP_DIR_PATH);
        if (dir.exists()) {
            File[] files = dir.listFiles();
            for (File file : files) {
                file.delete();
            }
        }
    }

    private void uploadImage(final String path, final boolean front) {

        final File file = new File(path);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);

        MultipartBody.Part body =
                MultipartBody.Part.createFormData("filename", file.getName(), requestFile);

        UdriveRestClient.getClentInstance().postImage(SessionManager.getInstance().getAuthorization(), body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ImageUrlResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ImageUrlResult value) {
                        if (value != null) {
                            if (front) {
                                mFrontUrl = value.data;
                            } else {
                                mBehindUrl = value.data;
                            }
                        }

                        if (!TextUtils.equals(title, Constants.INTENT_DRIVER_INFO) && front) {
                            postImage(path, front);
                        } else {
                            visible(file.getPath(), front);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(IDRegisterActivity.this, R.string.upload_image_fail, Toast.LENGTH_SHORT).show();
                        if (front) {
                            mIvPositive.setVisibility(View.VISIBLE);
                            mPbFront.setVisibility(View.GONE);
                        } else {
                            mIvNegative.setVisibility(View.VISIBLE);
                            mPbBehind.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //上传驾驶证信息
    private void uploadDriverInfo() {
        if (!mCheckBox.isChecked()) {
            if (TextUtils.isEmpty(mFrontUrl)) {
                Toast.makeText(this, "需要驾驶证正页", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(mBehindUrl)) {
                Toast.makeText(this, "需要驾驶证副页", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            if (TextUtils.isEmpty(mFrontUrl)) {
                mFrontUrl = "http://pdaxdtr0a.bkt.clouddn.com/6bbf4627b0144246a8871200dbe1f7e3.png";
            }
            if (TextUtils.isEmpty(mBehindUrl)) {
                mBehindUrl = "http://pdaxdtr0a.bkt.clouddn.com/6bbf4627b0144246a8871200dbe1f7e3.png";
            }
        }

        HashMap<String, Object> map = new HashMap<>();
        map.put("driverLicencePhotoMaster", mFrontUrl);
        map.put("driverLicencePhotoSlave", mBehindUrl);

        UdriveRestClient.getClentInstance().addDriver(SessionManager.getInstance().getAuthorization(), map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DriverResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(DriverResult value) {
                        Toast.makeText(IDRegisterActivity.this, "驾驶证信息上传成功", Toast.LENGTH_SHORT).show();
                        AccountInfoResult accountInfo = AccountInfoManager.getInstance().getAccountInfo();
                        accountInfo.data.driverState = Constants.ID_UNDER_REVIEW;
                        AccountInfoManager.getInstance().cacheAccount(accountInfo);
                        setResult(RESULT_OK);
                        finish();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(IDRegisterActivity.this, R.string.id_post_fail, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void postImage(String url, boolean front) {
        String imgFile = url;
        Boolean is_old_format = false;//如果文档的输入中含有inputs字段，设置为True， 否则设置为False
        //请根据线上文档修改configure字段
        JSONObject configObj = new JSONObject();
        try {
            configObj.put("side", "face");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String config_str = configObj.toString();
        // 对图像进行base64编码
        String imgBase64 = "";
        try {
            File file = new File(imgFile);
            byte[] content = new byte[(int) file.length()];
            FileInputStream finputstream = new FileInputStream(file);
            finputstream.read(content);
            finputstream.close();
            imgBase64 = new String(org.apache.commons.codec.binary.Base64.encodeBase64(content));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        // 拼装请求body的json字符串
        JSONObject requestObj = new JSONObject();
        try {
            if (is_old_format) {
                JSONObject obj = new JSONObject();
                obj.put("image", getParam(50, imgBase64));
                if (config_str.length() > 0) {
                    obj.put("configure", getParam(50, config_str));
                }
                JSONArray inputArray = new JSONArray();
                inputArray.add(obj);
                requestObj.put("inputs", inputArray);
            } else {
                requestObj.put("image", imgBase64);
                if (config_str.length() > 0) {
                    requestObj.put("configure", config_str);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String bodys = requestObj.toString();
        postToAli(bodys, url, front);
    }

    /*
 * 获取参数的json对象
 */
    public static JSONObject getParam(int type, String dataValue) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("dataType", type);
            obj.put("dataValue", dataValue);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }


    public void postToAli(String json, final String path, final boolean front) {
        OkHttpClient build = new OkHttpClient.Builder()
                .sslSocketFactory(createSSLSocketFactory())
                .hostnameVerifier(new TrustAllHostnameVerifier())
                .build();
        String url = "http://dm-51.data.aliyun.com/rest/160601/ocr/ocr_idcard.json";//带https的网址

        RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8"), json);

        final Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "APPCODE 36b05d1ad5b8405c913ae0fe7ea7d15d")
                .post(requestBody)
                .build();
        Call call = build.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(IDRegisterActivity.this, "身份证解析失败", Toast.LENGTH_SHORT).show();
                        visible(path, front);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response)
                    throws IOException {
                if (response == null || response.body() == null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(IDRegisterActivity.this, "身份证解析失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }
                try {
                    String string = response.body().string();
                    IdBean idBean = new Gson().fromJson(string, IdBean.class);
                    mIdBean = idBean;
                } catch (Exception e) {
                    e.printStackTrace();
                   runOnUiThread(new Runnable() {
                       @Override
                       public void run() {
                           Toast.makeText(IDRegisterActivity.this, "身份证解析失败", Toast.LENGTH_SHORT).show();
                       }
                   });
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        visible(path, front);
                    }
                });
            }
        });
    }

    private void visible(String path, boolean front) {
        if (front) {
            mIvPositive.setVisibility(View.VISIBLE);
            mPbFront.setVisibility(View.GONE);
            Glide.with(IDRegisterActivity.this).load(Uri.fromFile(new File(path))).crossFade().placeholder(R.mipmap.ic_id_front).error(R.mipmap.ic_id_front).into(mIvPositive);
        } else {
            mIvNegative.setVisibility(View.VISIBLE);
            mPbBehind.setVisibility(View.GONE);

            if (TextUtils.equals(title, Constants.INTENT_DRIVER_INFO)) {
                Glide.with(IDRegisterActivity.this).load(Uri.fromFile(new File(path))).crossFade().placeholder(R.mipmap.ic_drvier_p).error(R.mipmap.ic_drvier_p).into(mIvNegative);

            } else {
                Glide.with(IDRegisterActivity.this).load(Uri.fromFile(new File(path))).crossFade().placeholder(R.mipmap.ic_id_behind).error(R.mipmap.ic_id_behind).into(mIvNegative);
            }
        }
    }

    private static class TrustAllCerts implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    private static class TrustAllHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    private static SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new TrustAllCerts()}, new SecureRandom());

            ssfFactory = sc.getSocketFactory();
        } catch (Exception e) {
        }
        return ssfFactory;
    }

}
