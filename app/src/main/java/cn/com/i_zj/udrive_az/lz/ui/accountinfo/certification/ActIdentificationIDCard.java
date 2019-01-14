package cn.com.i_zj.udrive_az.lz.ui.accountinfo.certification;

import android.Manifest;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.pickerview.TimePickerView;

import org.apache.commons.codec.binary.Base64;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
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
import cn.com.i_zj.udrive_az.event.CloseActivityEvent;
import cn.com.i_zj.udrive_az.login.SessionManager;
import cn.com.i_zj.udrive_az.lz.bean.CameraEvent;
import cn.com.i_zj.udrive_az.lz.ui.accountinfo.ActIdentificationCameraTwo;
import cn.com.i_zj.udrive_az.lz.ui.idregister.ALiIDCarBean;
import cn.com.i_zj.udrive_az.lz.ui.idregister.IdBean;
import cn.com.i_zj.udrive_az.model.ImageUrlResult;
import cn.com.i_zj.udrive_az.model.req.AddIdCardInfo;
import cn.com.i_zj.udrive_az.network.UdriveRestClient;
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
 * @Describe 实名认证--身份证认证
 */
public class ActIdentificationIDCard extends DBSBaseActivity implements EasyPermissions.PermissionCallbacks {
    @BindView(R.id.iv_idcard_one)
    ImageView ivIdcardOne;
    @BindView(R.id.iv_idcard_two)
    ImageView ivIdcardTwo;
    @BindView(R.id.tv_start_time)
    TextView tvStartTime;
    @BindView(R.id.tv_end_time)
    TextView tvEndTime;
    @BindView(R.id.btn_commit)
    Button btnCommit;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.et_number)
    EditText etNumber;
    private File idCardOneFile;
    private File idCardTwoFile;


    private AddIdCardInfo addIdCardInfo = null;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_identification_idcard;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.lz_account_true_name);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                finish();
            }
        });
//        ScreenManager.getScreenManager().pushActivity(this);
        initView();
        initEvent();
        checkPermission();
    }


    private void initView() {
        int width = (ToolsUtils.getWindowWidth(ActIdentificationIDCard.this) - SizeUtils.dp2px(ActIdentificationIDCard.this, 24)) / 2;
        int height = (int) (width * 0.6);
        LinearLayout.LayoutParams layoutParamsOne = new LinearLayout.LayoutParams(width, height);
        layoutParamsOne.setMargins(0, 0, SizeUtils.dp2px(ActIdentificationIDCard.this, 4), 0);
        ivIdcardOne.setLayoutParams(layoutParamsOne);
        LinearLayout.LayoutParams layoutParamsTwo = new LinearLayout.LayoutParams(width, height);
        layoutParamsTwo.setMargins(SizeUtils.dp2px(ActIdentificationIDCard.this, 4), 0, 0, 0);
        ivIdcardTwo.setLayoutParams(layoutParamsTwo);
        addIdCardInfo = new AddIdCardInfo();
    }

    private void initEvent() {

        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String name = etName.getText().toString().trim();
                addIdCardInfo.setRealName(name);
                if (commitDateVerify()) {
                    btnCommit.setEnabled(true);
                } else {
                    btnCommit.setEnabled(false);
                }
            }
        });
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
                addIdCardInfo.setIdentityCardNumber(numtr);
                if (commitDateVerify()) {
                    btnCommit.setEnabled(true);
                } else {
                    btnCommit.setEnabled(false);
                }
            }
        });
    }

    @OnClick({R.id.iv_idcard_one, R.id.iv_idcard_two, R.id.tv_start_time, R.id.tv_end_time, R.id.btn_commit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_idcard_one:
                ActIdentificationCameraTwo.startActIdentificationCamera(ActIdentificationIDCard.this, ActIdentificationCameraTwo.IDCARD_POSITIVE);
                break;
            case R.id.iv_idcard_two:
                ActIdentificationCameraTwo.startActIdentificationCamera(ActIdentificationIDCard.this, ActIdentificationCameraTwo.IDCARD_REVERSE);
                break;
            case R.id.tv_start_time:
                showTimeDailog(true);
                break;
            case R.id.tv_end_time:
                showTimeDailog(false);
                break;
            case R.id.btn_commit:
                if (commitVerify()) {
                    ActBioassay.startActBioassay(ActIdentificationIDCard.this, addIdCardInfo);
                }

//                ActBioassay.startActBioassay(ActIdentificationIDCard.this, addIdCardInfo);
                break;
        }
    }

    private boolean commitVerify() {
        if (addIdCardInfo == null) {
            showToast("请拍摄身份证正面照");// 优先提示
            addIdCardInfo = new AddIdCardInfo();
            return false;
        }
        if (StringUtils.isEmpty(addIdCardInfo.getIdentityCardPhotoFront())) {
            showToast("请拍摄身份证正面照");
            return false;
        }
        if (StringUtils.isEmpty(addIdCardInfo.getIdentityCardPhotoBehind())) {
            showToast("请拍摄身份证反面照");
            return false;
        }
        //
        String idName = etName.getText().toString().trim();
        if (StringUtils.isEmpty(idName)) {
            showToast("请输入证件姓名");
            return false;
        }
        addIdCardInfo.setRealName(idName);
        //
        String idNum = etNumber.getText().toString().trim();
        if (StringUtils.isEmpty(idNum)) {
            showToast("请输入证件号码");
            return false;
        }
        addIdCardInfo.setIdentityCardNumber(idNum);
        //
        String startTime = addIdCardInfo.getValidaTime();
        if (StringUtils.isEmpty(startTime)) {
            showToast("请输入证件有效期开始时间");
            return false;
        }
        //
        String endTiem = addIdCardInfo.getExpireTime();
        if (StringUtils.isEmpty(endTiem)) {
            showToast("请输入证件有效期结束时间");
            return false;
        }
        return true;
    }

    private void showTimeDailog(final boolean isStratTime) {
        TimePickerView mTpBirthday = new TimePickerView(ActIdentificationIDCard.this, TimePickerView.Type.YEAR_MONTH_DAY);
        mTpBirthday.setCyclic(false);
        mTpBirthday.setTime(new Date());
        mTpBirthday.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date) {
                String mShowTime = ToolsUtils.getTime(date, "yyyy.MM.dd");
                String mTime = ToolsUtils.getTime(date, "yyyyMMdd");
                if (isStratTime) {
                    addIdCardInfo.setValidaTime(mTime);
                    tvStartTime.setText(mShowTime);
                } else {
                    addIdCardInfo.setExpireTime(mTime);
                    tvEndTime.setText(mShowTime);
                }
                if (commitDateVerify()) {
                    btnCommit.setEnabled(true);
                } else {
                    btnCommit.setEnabled(false);
                }
            }

            @Override
            public void onCancel() {

            }
        });
        mTpBirthday.show();
    }

    /**
     * 检测照相机权限
     */
    private void checkPermission() {
        boolean external = EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE, Manifest.permission.CAMERA);

        if (!external) {
            EasyPermissions.requestPermissions(this, getString(R.string.lz_request_permission), 1, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE, Manifest.permission.CAMERA);
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
        if (ActIdentificationCameraTwo.IDCARD_POSITIVE == cameraEvent.getCode()) {
            idCardOneFile = new File(cameraEvent.getPath());
            Glide.with(ActIdentificationIDCard.this)
                    .load(Uri.fromFile(idCardOneFile))
                    .centerCrop()
                    .transform(new GlideRoundTransform(this, SizeUtils.dp2px(ActIdentificationIDCard.this, 4)))
                    .placeholder(R.mipmap.pic_idcardscanbg)
                    .error(R.mipmap.pic_idcardscanbg)
                    .into(ivIdcardOne);

            uploadImage(cameraEvent.getPath(), true);
        } else if (ActIdentificationCameraTwo.IDCARD_REVERSE == cameraEvent.getCode()) {
            idCardTwoFile = new File(cameraEvent.getPath());
            Glide.with(ActIdentificationIDCard.this)
                    .load(Uri.fromFile(idCardTwoFile))
                    .centerCrop()
                    .transform(new GlideRoundTransform(this, SizeUtils.dp2px(ActIdentificationIDCard.this, 4)))
                    .crossFade()
                    .placeholder(R.mipmap.pic_idcardfrontscanbg)
                    .error(R.mipmap.pic_idcardfrontscanbg)
                    .into(ivIdcardTwo);
            uploadImage(cameraEvent.getPath(), false);

        }

    }

    private void uploadImage(final String path, final boolean front) {

        final File file = new File(path);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);

        MultipartBody.Part body =
                MultipartBody.Part.createFormData("filename", file.getName(), requestFile);
        showProgressDialog();
        UdriveRestClient.getClentInstance().postImage(SessionManager.getInstance().getAuthorization(), body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ImageUrlResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ImageUrlResult value) {
                        dissmisThisProgressDialog();
                        if (value != null) {
                            if (addIdCardInfo == null) {
                                addIdCardInfo = new AddIdCardInfo();
                            }
                            if (front) {
                                addIdCardInfo.setIdentityCardPhotoFront(value.data);
                                postIDCarImage(idCardOneFile, front);
                            } else {
                                addIdCardInfo.setIdentityCardPhotoBehind(value.data);
                                postIDCarImage(idCardTwoFile, front);
                            }

                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        dissmisThisProgressDialog();
                        if (front) {
                            showToast("身份证正面上传失败");
                        } else {
                            showToast("身份证反面上传失败");
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void postIDCarImage(File file, boolean front) {
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
                dissmisProgressDialog();
                showToastMsg("身份证解析失败");
            }

            @Override
            public void onResponse(Call call, Response response)
                    throws IOException {
                dissmisThisProgressDialog();
                if (response == null || response.body() == null || response.code() != 200) {
                    showToastMsg("身份证解析失败");
                    return;
                }
                try {
                    String string = response.body().string();
                    if (front) {
                        IdBean idBean = JSONObject.parseObject(string, IdBean.class);
                        if (idBean == null) {
                            showToastMsg("身份证解析失败");
                            return;
                        }
                        showData(idBean, null, front);
                    } else {
                        ALiIDCarBean idCarBean = JSONObject.parseObject(string, ALiIDCarBean.class);
                        showData(null, idCarBean, front);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    showToastMsg("身份证解析失败");
                }

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

    private void showData(final IdBean idBean, final ALiIDCarBean idCarBean, final boolean front) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (front) {
                    addIdCardInfo.setRealName(idBean.getName());
                    addIdCardInfo.setAddress(idBean.getAddress());
                    addIdCardInfo.setSex(idBean.getSex());
                    addIdCardInfo.setIdentityTime(idBean.getBirth());
                    addIdCardInfo.setNationality(idBean.getNationality());
                    etName.setText(idBean.getName());
                    etNumber.setText(idBean.getNum());
                } else {
                    try {
                        addIdCardInfo.setValidaTime(idCarBean.getStart_date());
                        addIdCardInfo.setExpireTime(idCarBean.getEnd_date());
                        addIdCardInfo.setIssue(idCarBean.getIssue());
                        tvStartTime.setText(StringUtils.strToDateFormat(idCarBean.getStart_date()));
                        tvEndTime.setText(StringUtils.strToDateFormat(idCarBean.getEnd_date()));
                    } catch (Exception e) {

                    }

                }
                if (commitDateVerify()) {
                    btnCommit.setEnabled(true);
                } else {
                    btnCommit.setEnabled(false);
                }

            }
        });
    }

    private boolean commitDateVerify() {
        if (addIdCardInfo != null) {
            if (StringUtils.isEmpty(addIdCardInfo.getIdentityCardPhotoFront())) {
                return false;
            }
            if (StringUtils.isEmpty(addIdCardInfo.getIdentityCardPhotoBehind())) {
                return false;
            }
            if (StringUtils.isEmpty(addIdCardInfo.getRealName())) {
                return false;
            }
            if (StringUtils.isEmpty(addIdCardInfo.getIdentityCardNumber())) {
                return false;
            }
            if (StringUtils.isEmpty(addIdCardInfo.getValidaTime())) {
                return false;
            }
            if (StringUtils.isEmpty(addIdCardInfo.getExpireTime())) {
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
            sc.init(null, new TrustManager[]{new TrustAllCerts()}, new SecureRandom());

            ssfFactory = sc.getSocketFactory();
        } catch (Exception e) {
        }
        return ssfFactory;
    }

    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onDataSynEvent(CloseActivityEvent event) {
        finish();
    }

}
