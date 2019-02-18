package cn.com.i_zj.udrive_az.lz.ui.accountinfo.certification;

import android.Manifest;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;

import org.apache.commons.codec.binary.Base64;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.i_zj.udrive_az.DBSBaseActivity;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.login.AccountInfoManager;
import cn.com.i_zj.udrive_az.lz.bean.CameraEvent;
import cn.com.i_zj.udrive_az.lz.ui.accountinfo.ActIdentificationCameraTwo;
import cn.com.i_zj.udrive_az.model.AccountInfoResult;
import cn.com.i_zj.udrive_az.model.AliDrivingIDBackEntity;
import cn.com.i_zj.udrive_az.model.AliDrivingIDEntity;
import cn.com.i_zj.udrive_az.model.DriverResult;
import cn.com.i_zj.udrive_az.model.ImageUrlResult;
import cn.com.i_zj.udrive_az.model.req.AddDriverCardInfo;
import cn.com.i_zj.udrive_az.network.UdriveRestClient;
import cn.com.i_zj.udrive_az.utils.Constants;
import cn.com.i_zj.udrive_az.utils.ScreenManager;
import cn.com.i_zj.udrive_az.utils.SizeUtils;
import cn.com.i_zj.udrive_az.utils.StringUtils;
import cn.com.i_zj.udrive_az.utils.ToolsUtils;
import cn.com.i_zj.udrive_az.utils.image.GlideRoundTransform;
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

/**
 * @author JayQiu
 * @create 2018/10/31
 * @Describe 驾照审核
 */
public class ActIdentificationDrivingLicense extends DBSBaseActivity implements EasyPermissions.PermissionCallbacks {
    @BindView(R.id.iv_one)
    ImageView ivOne;
    @BindView(R.id.iv_two)
    ImageView ivTwo;
    @BindView(R.id.tv_type)
    TextView tvType;
    @BindView(R.id.sp_type)
    Spinner etType;
    @BindView(R.id.et_number)
    EditText etNumber;
    @BindView(R.id.btn_commit)
    Button btnCommit;
    private File idOneFile;
    private File idTwoFile;
    private String[] typeDate;

    private AddDriverCardInfo addDriverCardInfo;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_driving_license;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScreenManager.getScreenManager().pushActivity(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.account_driver_license);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                finish();
            }
        });
        addDriverCardInfo = new AddDriverCardInfo();

        initView();
        initEvent();
        checkPermission();
    }

    private void initView() {
        int width = (ToolsUtils.getWindowWidth(ActIdentificationDrivingLicense.this) - SizeUtils.dp2px(ActIdentificationDrivingLicense.this, 24)) / 2;
        int height = (int) (width * 0.6);
        LinearLayout.LayoutParams layoutParamsOne = new LinearLayout.LayoutParams(width, height);
        layoutParamsOne.setMargins(0, 0, SizeUtils.dp2px(ActIdentificationDrivingLicense.this, 4), 0);
        ivOne.setLayoutParams(layoutParamsOne);
        LinearLayout.LayoutParams layoutParamsTwo = new LinearLayout.LayoutParams(width, height);
        layoutParamsTwo.setMargins(SizeUtils.dp2px(ActIdentificationDrivingLicense.this, 4), 0, 0, 0);
        ivTwo.setLayoutParams(layoutParamsTwo);
        typeDate = getResources().getStringArray(R.array.vehicle_type);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                ActIdentificationDrivingLicense.this, R.layout.item_simple_spinner,
                typeDate);
        // 把定义好的Adapter设定到spinner中
        etType.setAdapter(adapter);
        // 默认显示
        etType.setSelection(5);
        addDriverCardInfo.setDriverType("C1");
    }

    private void initEvent() {
        etNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String numtr = etNumber.getText().toString().trim();
                addDriverCardInfo.setArchiveNo(numtr);
                if (commitVerify()) {
                    btnCommit.setEnabled(true);
                } else {
                    btnCommit.setEnabled(false);
                }
            }
        });
        etType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                addDriverCardInfo.setDriverType(typeDate[position]);
                if (commitVerify()) {
                    btnCommit.setEnabled(true);
                } else {
                    btnCommit.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * 检测照相机权限
     */
    private void checkPermission() {
        boolean external = EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA);

        if (!external) {
            EasyPermissions.requestPermissions(this, getString(R.string.lz_request_permission), 1, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA);
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
            ToastUtils.showShort(R.string.permission_success);
        } else {
            ToastUtils.showShort(R.string.permission_file);
        }

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        ToastUtils.showShort(R.string.permission_request_fail);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void cameraEvent(CameraEvent cameraEvent) {
        if (ActIdentificationCameraTwo.DRIVING_LICENSE_POSITIVE == cameraEvent.getCode()) {
            idOneFile = new File(cameraEvent.getPath());
            Glide.with(ActIdentificationDrivingLicense.this)
                    .load(Uri.fromFile(idOneFile))
                    .centerCrop()
                    .transform(new GlideRoundTransform(this, SizeUtils.dp2px(ActIdentificationDrivingLicense.this, 4)))
                    .placeholder(R.mipmap.pic_driverlicensefrontbg)
                    .error(R.mipmap.pic_driverlicensefrontbg)
                    .into(ivOne);
            uploadImage(cameraEvent.getPath(), true);
        } else if (ActIdentificationCameraTwo.DRIVING_LICENSE_REVERSE == cameraEvent.getCode()) {
            idTwoFile = new File(cameraEvent.getPath());
            Glide.with(ActIdentificationDrivingLicense.this)
                    .load(Uri.fromFile(idTwoFile))
                    .centerCrop()
                    .transform(new GlideRoundTransform(this, SizeUtils.dp2px(ActIdentificationDrivingLicense.this, 4)))
                    .placeholder(R.mipmap.pic_driverlicensebg)
                    .error(R.mipmap.pic_driverlicensebg)
                    .into(ivTwo);
            uploadImage(cameraEvent.getPath(), false);
        }

    }

    @OnClick({R.id.iv_one, R.id.iv_two, R.id.btn_commit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_one:
                if (EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)) {
                    ActIdentificationCameraTwo.startActIdentificationCamera(ActIdentificationDrivingLicense.this, ActIdentificationCameraTwo.IDCARD_POSITIVE);
                } else {
                    ActIdentificationCameraTwo.startActIdentificationCamera(ActIdentificationDrivingLicense.this, ActIdentificationCameraTwo.DRIVING_LICENSE_POSITIVE);
                }
                break;
            case R.id.iv_two:
                if (EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)) {
                    ActIdentificationCameraTwo.startActIdentificationCamera(ActIdentificationDrivingLicense.this, ActIdentificationCameraTwo.IDCARD_POSITIVE);
                } else {
                    ActIdentificationCameraTwo.startActIdentificationCamera(ActIdentificationDrivingLicense.this, ActIdentificationCameraTwo.DRIVING_LICENSE_REVERSE);
                }
                break;
            case R.id.btn_commit:
                uploadDriverInfo();
                break;
        }
    }

    private void uploadImage(final String path, final boolean front) {

        final File file = new File(path);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);

        MultipartBody.Part body =
                MultipartBody.Part.createFormData("filename", file.getName(), requestFile);
        showProgressDialog();
        UdriveRestClient.getClentInstance().postImage(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ImageUrlResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ImageUrlResult value) {
                        dissmisThisProgressDialog();
                        if (value != null && value.code == 1) {
                            if (front) {
                                addDriverCardInfo.setDriverLicencePhotoMaster(value.data);
                                postDrivingImage(idOneFile, front);

                            } else {
                                addDriverCardInfo.setDriverLicencePhotoSlave(value.data);
                                postDrivingImage(idTwoFile, front);

                            }

                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        dissmisThisProgressDialog();
                        if (front) {
                            showToast("驾照正面上传失败");
                        } else {
                            showToast("驾照证反面上传失败");
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void postDrivingImage(File file, boolean front) {
        Boolean is_old_format = false;//如果文档的输入中含有inputs字段，设置为True， 否则设置为False
        //请根据线上文档修改configure字段
        JSONObject configObj = new JSONObject();
        try {
            if (front) {
                configObj.put("side", "face");
            } else {
                configObj.put("side", "back");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        String config_str = configObj.toString();
        // 对图像进行base64编码
        String imgBase64 = "";
        try {
            byte[] content = new byte[(int) file.length()];
            FileInputStream finputstream = new FileInputStream(file);
            finputstream.read(content);
            finputstream.close();
            imgBase64 = new String(Base64.encodeBase64(content));
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
        postToAli(bodys, front);
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

    public void postToAli(String json, final boolean front) {
        showProgressDialog();
        OkHttpClient build = new OkHttpClient.Builder()
                .sslSocketFactory(createSSLSocketFactory())
                .hostnameVerifier(new ActIdentificationDrivingLicense.TrustAllHostnameVerifier())
                .build();
        String url = "http:////dm-52.data.aliyun.com/rest/160601/ocr/ocr_driver_license.json";//带https的网址

        RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8"), json);

        final Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "APPCODE bdd04c8d724442b3b0df7533365521b4")
                .post(requestBody)
                .build();
        Call call = build.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                dissmisProgressDialog();
                showToastMsg("驾照解析失败");
            }

            @Override
            public void onResponse(Call call, Response response)
                    throws IOException {
                dissmisThisProgressDialog();
                if (response == null || response.body() == null) {
                    showToastMsg("驾照解析失败");
                    return;
                }
                try {
                    String string = response.body().string();
                    if (front) {
                        AliDrivingIDEntity drivingIDEntity = JSONObject.parseObject(string, AliDrivingIDEntity.class);
                        showData(drivingIDEntity, null, front);
                    } else {
                        AliDrivingIDBackEntity drivingIDBackEntity = JSONObject.parseObject(string, AliDrivingIDBackEntity.class);
                        showData(null, drivingIDBackEntity, front);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    showToastMsg("驾照解析失败");
                }

            }
        });
    }

    //上传驾驶证信息
    private void uploadDriverInfo() {

        if (TextUtils.isEmpty(addDriverCardInfo.getDriverLicencePhotoMaster())) {
            Toast.makeText(this, "需要驾驶证正页", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(addDriverCardInfo.getDriverLicencePhotoSlave())) {
            Toast.makeText(this, "需要驾驶证副页", Toast.LENGTH_SHORT).show();
            return;
        }


        UdriveRestClient.getClentInstance().addDriver(addDriverCardInfo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DriverResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(DriverResult value) {
                        if (value != null && value.getCode() == 1) {
                            showToast("驾驶证信息提交成功");
                            AccountInfoResult accountInfo = AccountInfoManager.getInstance().getAccountInfo();
                            accountInfo.data.driverState = Constants.ID_UNDER_REVIEW;
                            AccountInfoManager.getInstance().cacheAccount(accountInfo);
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            showToast("驾驶证信息提交失败");
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        showToast("驾驶证信息提交失败");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void showToastMsg(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtils.showShort(msg);
            }
        });
    }

    private void dissmisThisProgressDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dissmisProgressDialog();
            }
        });
    }

    private void showData(final AliDrivingIDEntity drivingIDEntity, final AliDrivingIDBackEntity drivingIDBackEntity, final boolean front) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (front) {

                    addDriverCardInfo.setValidatTime(drivingIDEntity.getStart_date());
                    addDriverCardInfo.setExpireTime(drivingIDEntity.getEnd_date());
                    addDriverCardInfo.setIssueTime(drivingIDEntity.getIssue_date());
                    addDriverCardInfo.setAddress(drivingIDEntity.getAddr());
                    addDriverCardInfo.setName(drivingIDEntity.getName());
                    addDriverCardInfo.setDriverLicenceNumber(drivingIDEntity.getNum());
                    addDriverCardInfo.setSex(drivingIDEntity.getSex());
                    int postion = getVehicleType(drivingIDEntity.getVehicle_type());
                    if (postion != -1) {
                        etType.setSelection(postion);
                        addDriverCardInfo.setDriverType(drivingIDEntity.getVehicle_type());
                    }

                } else {
                    addDriverCardInfo.setArchiveNo(drivingIDBackEntity.getArchive_no());
                    etNumber.setText(drivingIDBackEntity.getArchive_no());
                }
                if (commitVerify()) {
                    btnCommit.setEnabled(true);
                } else {
                    btnCommit.setEnabled(false);
                }
            }
        });
    }

    private int getVehicleType(String type) {
        if (StringUtils.isEmpty(type)) {
            return -1;
        }
        if (typeDate != null && typeDate.length > 0) {
            return Arrays.binarySearch(typeDate, type);
        } else {
            return -1;
        }

    }

    private boolean commitVerify() {
        if (addDriverCardInfo != null) {
            if (StringUtils.isEmpty(addDriverCardInfo.getDriverLicencePhotoMaster())) {
                return false;
            }
            if (StringUtils.isEmpty(addDriverCardInfo.getDriverLicencePhotoSlave())) {
                return false;
            }
            if (StringUtils.isEmpty(addDriverCardInfo.getDriverType())) {
                return false;
            }
            if (StringUtils.isEmpty(addDriverCardInfo.getArchiveNo())) {
                return false;
            }
            return true;
        }
        return false;
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
            sc.init(null, new TrustManager[]{new ActIdentificationDrivingLicense.TrustAllCerts()}, new SecureRandom());

            ssfFactory = sc.getSocketFactory();
        } catch (Exception e) {
        }
        return ssfFactory;
    }
}
