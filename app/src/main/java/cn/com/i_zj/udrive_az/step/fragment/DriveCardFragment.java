package cn.com.i_zj.udrive_az.step.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.baidu.ocr.ui.camera.CameraActivity;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.apache.commons.codec.binary.Base64;
import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.i_zj.udrive_az.BuildConfig;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.event.StepEvent;
import cn.com.i_zj.udrive_az.login.AccountInfoManager;
import cn.com.i_zj.udrive_az.model.AccountInfoResult;
import cn.com.i_zj.udrive_az.model.AliDrivingIDBackEntity;
import cn.com.i_zj.udrive_az.model.DriverResult;
import cn.com.i_zj.udrive_az.model.req.AddDriverCardInfo;
import cn.com.i_zj.udrive_az.network.UdriveRestClient;
import cn.com.i_zj.udrive_az.utils.Constants;
import cn.com.i_zj.udrive_az.utils.StringUtils;
import cn.com.i_zj.udrive_az.utils.ToolsUtils;
import cn.com.i_zj.udrive_az.utils.qiniu.Auth;
import cn.com.i_zj.udrive_az.widget.CommonAlertDialog;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.yokeyword.fragmentation.SupportFragment;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DriveCardFragment extends SupportFragment {
    private static final int REQUEST_CODE_CAMERA_One = 102;
    private static final int REQUEST_CODE_CAMERA_Two = 103;

    @BindView(R.id.iv_one)
    ImageView ivDriveImageOne;
    @BindView(R.id.iv_two)
    ImageView ivDriveImageTwo;
    @BindView(R.id.sp_type)
    Spinner etType;
    @BindView(R.id.et_number)
    EditText etNumber;

    private Context mContext;
    private String[] typeDate;
    private AddDriverCardInfo addDriverCardInfo;
    protected Dialog progressDialog;

    Disposable disposable;
    CommonAlertDialog alertDialog;

    public static DriveCardFragment newInstance() {
        Bundle args = new Bundle();

        DriveCardFragment fragment = new DriveCardFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_driving_license, container, false);
        ButterKnife.bind(this, view);

        mContext = getContext();
        addDriverCardInfo = new AddDriverCardInfo();

        typeDate = getResources().getStringArray(R.array.vehicle_type);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                mContext, R.layout.item_simple_spinner,
                typeDate);
        etType.setAdapter(adapter);
        // 默认显示
        etType.setSelection(5);
        etType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                addDriverCardInfo.setDriverType(typeDate[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
                addDriverCardInfo.setArchiveNo(numtr);
            }
        });

        addDriverCardInfo.setDriverType("C1");
        return view;
    }

    @OnClick({R.id.iv_one, R.id.iv_two, R.id.btn_commit})
    void onClick(View view) {
        Intent intent = new Intent(getActivity(), CameraActivity.class);
        switch (view.getId()) {
            case R.id.iv_one:
                intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                        new File(getActivity().getFilesDir(), "drive_front_pic.jpg").getAbsolutePath());
                intent.putExtra(CameraActivity.KEY_CONTENT_TYPE,
                        CameraActivity.CONTENT_TYPE_GENERAL);
                startActivityForResult(intent, REQUEST_CODE_CAMERA_One);
                break;
            case R.id.iv_two:
                intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                        new File(getActivity().getFilesDir(), "drive_back_pic.jpg").getAbsolutePath());
                intent.putExtra(CameraActivity.KEY_CONTENT_TYPE,
                        CameraActivity.CONTENT_TYPE_GENERAL);
                startActivityForResult(intent, REQUEST_CODE_CAMERA_Two);
                break;
            case R.id.btn_commit:
                if (TextUtils.isEmpty(addDriverCardInfo.getDriverLicencePhotoMasterLocal())) {
                    showToast("需要拍摄驾驶证正页");
                    return;
                }
                if (TextUtils.isEmpty(addDriverCardInfo.getDriverLicencePhotoSlaveLocal())) {
                    showToast("需要拍摄驾驶证副页");
                    return;
                }
                if (TextUtils.isEmpty(addDriverCardInfo.getArchiveNo())) {
                    showToast("需要填写档案编号");
                    return;
                }
                uploadImg2QiNiu(0,  new File(getActivity().getFilesDir(), "drive_front_pic.jpg").getAbsolutePath());
                uploadImg2QiNiu(1,  new File(getActivity().getFilesDir(), "drive_back_pic.jpg").getAbsolutePath());
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (data == null) {
            return;
        }

        if (requestCode == REQUEST_CODE_CAMERA_One) {
            File front = new File(getActivity().getFilesDir(), "drive_front_pic.jpg");
            addDriverCardInfo.setDriverLicencePhotoMasterLocal(front.getAbsolutePath());
            Glide.with(mContext)
                    .load(Uri.fromFile(front))
                    .centerCrop()
                    .placeholder(R.mipmap.pic_driverlicensefrontbg)
                    .error(R.mipmap.pic_driverlicensefrontbg)
                    .into(ivDriveImageOne);

            RecognizeService.recDrivingLicense(getActivity(),
                    front.getAbsolutePath(),
                    result -> {
                        JsonObject circleObject = (JsonObject) new JsonParser().parse(result);
                        try {
                            JsonObject center = circleObject.get("words_result").getAsJsonObject();
                            JsonObject center1 = center.get("准驾车型").getAsJsonObject();
                            String vehicleType = center1.get("words").getAsString();
                            int position = getVehicleType(vehicleType);
                            if (position != -1) {
                                etType.setSelection(position);
                                addDriverCardInfo.setDriverType(vehicleType);
                            }

                            addDriverCardInfo.setValidatTime(center.get("有效期限").getAsJsonObject().get("words").getAsString());
                            addDriverCardInfo.setExpireTime(center.get("至").getAsJsonObject().get("words").getAsString());
                            addDriverCardInfo.setIssueTime(center.get("初次领证日期").getAsJsonObject().get("words").getAsString());
                            addDriverCardInfo.setAddress(center.get("住址").getAsJsonObject().get("words").getAsString());
                            addDriverCardInfo.setName(center.get("姓名").getAsJsonObject().get("words").getAsString());
                            addDriverCardInfo.setDriverLicenceNumber(center.get("证号").getAsJsonObject().get("words").getAsString());
                            addDriverCardInfo.setSex(center.get("性别").getAsJsonObject().get("words").getAsString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
        } else if (requestCode == REQUEST_CODE_CAMERA_Two) {
            File back = new File(getActivity().getFilesDir(), "drive_back_pic.jpg");
            addDriverCardInfo.setDriverLicencePhotoSlaveLocal(back.getAbsolutePath());
            Glide.with(mContext)
                    .load(Uri.fromFile(back))
                    .centerCrop()
                    .placeholder(R.mipmap.pic_driverlicensebg)
                    .error(R.mipmap.pic_driverlicensebg)
                    .into(ivDriveImageTwo);

            postDrivingImage(back);
        }
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

    private void uploadDriverInfo() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
        UdriveRestClient.getClentInstance().addDriver(addDriverCardInfo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DriverResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(DriverResult value) {
                        dissmisProgressDialog();
                        if (value != null && value.getCode() == 1) {
                            showToast("驾驶证信息提交成功");
                            AccountInfoResult accountInfo = AccountInfoManager.getInstance().getAccountInfo();
                            accountInfo.data.driverState = Constants.ID_UNDER_REVIEW;
                            AccountInfoManager.getInstance().cacheAccount(accountInfo);
                            EventBus.getDefault().post(new StepEvent(3));
                        } else {
                            if (value != null) {
                                if (value.getCode() == 1031) {
                                    if (alertDialog != null && alertDialog.isShowing()) {
                                        return;
                                    }
                                    alertDialog = CommonAlertDialog.builder(getContext())
                                            .setImageTitle(true)
                                            .setTitle("证件重复")
                                            .setMsg("驾驶证已被注册，请致电400-614-1888")
                                            .setNegativeButton("取消", v -> {
                                                if (getActivity() != null) {
                                                    getActivity().finish();
                                                }
                                            })
                                            .setPositiveButton("重新上传", v -> {

                                            })
                                            .build()
                                            .show();
                                } else {
                                    ToastUtils.showShort("信息提交失败Code:" + value.getCode());
                                }
                            } else {
                                ToastUtils.showShort("信息提交失败, 请重试");
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        dissmisProgressDialog();
                        showToast("驾驶证信息提交失败");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void uploadImg2QiNiu(int type, String path) {
        showProgressDialog();
        new Thread() {
            public void run() {
                UploadManager uploadManager = new UploadManager();
                // 设置图片名字
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                String key = "nxnk"+ type +"_" + ToolsUtils.getUniqueId(mContext) + "_" + sdf.format(new Date()) + ".png";
                uploadManager.put(path, key, Auth.create(BuildConfig.AccessKey, BuildConfig.SecretKey).uploadToken("izjimage"), new UpCompletionHandler() {
                    @Override
                    public void complete(String key, ResponseInfo info, org.json.JSONObject res) {
                        if (info.isOK()) {
                            switch (type) {
                                case 0:
                                    addDriverCardInfo.setDriverLicencePhotoMaster(key);
                                    break;
                                case 1:
                                    addDriverCardInfo.setDriverLicencePhotoSlave(key);
                                    break;
                            }
                            if (!TextUtils.isEmpty(addDriverCardInfo.getDriverLicencePhotoMaster())
                                    && !TextUtils.isEmpty(addDriverCardInfo.getDriverLicencePhotoSlave())) {
                                uploadDriverInfo();
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

    private void postDrivingImage(File file) {
        Boolean is_old_format = false;//如果文档的输入中含有inputs字段，设置为True， 否则设置为False
        //请根据线上文档修改configure字段
        JSONObject configObj = new JSONObject();
        configObj.put("side", "back");
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
        postToAli(bodys);
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

    public void postToAli(String json) {
        OkHttpClient build = new OkHttpClient.Builder()
                .sslSocketFactory(createSSLSocketFactory())
                .hostnameVerifier(new TrustAllHostnameVerifier())
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
                showToast("驾照解析失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response == null || response.body() == null) {
                    showToast("驾照解析失败");
                    return;
                }
                try {
                    String string = response.body().string();
                    AliDrivingIDBackEntity drivingIDBackEntity = JSONObject.parseObject(string, AliDrivingIDBackEntity.class);

                    updateEditText(drivingIDBackEntity.getArchive_no());
                    addDriverCardInfo.setArchiveNo(drivingIDBackEntity.getArchive_no());
                } catch (Exception e) {
                    e.printStackTrace();
                    showToast("驾照解析失败");
                }

            }
        });
    }

    private void updateEditText(String no) {
        etNumber.setText(no);
    }

    private void showToast(String message) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show());
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

    public void showProgressDialog() {
        if (getActivity() == null) {
            return;
        }
        if (null == progressDialog) {
            progressDialog = new Dialog(getActivity(), R.style.MyDialog);
            progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            progressDialog.setContentView(R.layout.dialog_loading);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
        }
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    public void dissmisProgressDialog() {
        if (null != progressDialog && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
