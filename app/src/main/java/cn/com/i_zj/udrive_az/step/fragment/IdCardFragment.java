package cn.com.i_zj.udrive_az.step.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.IDCardParams;
import com.baidu.ocr.sdk.model.IDCardResult;
import com.baidu.ocr.ui.camera.CameraActivity;
import com.bumptech.glide.Glide;
import com.pickerview.TimePickerView;

import java.io.File;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.model.req.AddIdCardInfo;
import cn.com.i_zj.udrive_az.utils.StringUtils;
import cn.com.i_zj.udrive_az.utils.ToolsUtils;
import me.yokeyword.fragmentation.SupportFragment;
import pub.devrel.easypermissions.EasyPermissions;

public class IdCardFragment extends SupportFragment implements EasyPermissions.PermissionCallbacks, TextWatcher {
    private static final int REQUEST_CODE_CAMERA = 102;
    @BindView(R.id.iv_idcard_one)
    ImageView ivIdcardOne;
    @BindView(R.id.iv_idcard_two)
    ImageView ivIdcardTwo;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.et_number)
    EditText etNumber;
    @BindView(R.id.tv_start_time)
    TextView tvStartTime;
    @BindView(R.id.tv_end_time)
    TextView tvEndTime;
    @BindView(R.id.btn_commit)
    Button btnCommit;

    private String[] mPerms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
    private Context mContext;

    private int type; //0 ：身份证正面   1 ： 身份证反面   2 ： 进入活体检测
    private AddIdCardInfo addIdCardInfo;

    public static IdCardFragment newInstance() {
        Bundle args = new Bundle();

        IdCardFragment fragment = new IdCardFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_identification_idcard, container, false);
        ButterKnife.bind(this, view);

        mContext = getContext();
        addIdCardInfo = new AddIdCardInfo();
        etName.addTextChangedListener(this);
        etNumber.addTextChangedListener(this);
        return view;
    }

    @OnClick({R.id.iv_idcard_one, R.id.iv_idcard_two, R.id.tv_start_time, R.id.tv_end_time, R.id.btn_commit})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_idcard_one:
                type = 0;
                if (EasyPermissions.hasPermissions(mContext, mPerms)) {
                    startPage();
                } else {
                    type = 0;
                    EasyPermissions.requestPermissions(this, "需要申请拍照、文件读写权限", 1, mPerms);
                }
                break;
            case R.id.iv_idcard_two:
                type = 1;
                if (EasyPermissions.hasPermissions(mContext, mPerms)) {
                    startPage();
                } else {
                    EasyPermissions.requestPermissions(this, "需要申请拍照、文件读写权限", 1, mPerms);
                }
                break;
            case R.id.tv_start_time:
                showTimeDialog(true);
                break;
            case R.id.tv_end_time:
                showTimeDialog(false);
                break;
            case R.id.btn_commit:
                if (!commitVerify()) {
                    return;
                }
                type = 2;
                if (EasyPermissions.hasPermissions(mContext, mPerms)) {
                    startPage();
                } else {
                    EasyPermissions.requestPermissions(this, "需要申请拍照、文件读写权限", 1, mPerms);
                }
                break;
        }
    }

    private void startPage() {
        Intent intent = new Intent(mContext, CameraActivity.class);
        switch (type) {
            case 0:
                intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                        new File(getActivity().getFilesDir(), "front_pic.jpg").getAbsolutePath());
                intent.putExtra(CameraActivity.KEY_NATIVE_ENABLE, true);
                intent.putExtra(CameraActivity.KEY_NATIVE_MANUAL, true);
                intent.putExtra(CameraActivity.KEY_CONTENT_TYPE, CameraActivity.CONTENT_TYPE_ID_CARD_FRONT);
                startActivityForResult(intent, REQUEST_CODE_CAMERA);
                break;
            case 1:
                intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                        new File(getActivity().getFilesDir(), "back_pic.jpg").getAbsolutePath());
                intent.putExtra(CameraActivity.KEY_NATIVE_ENABLE, true);
                intent.putExtra(CameraActivity.KEY_NATIVE_MANUAL, true);
                intent.putExtra(CameraActivity.KEY_CONTENT_TYPE, CameraActivity.CONTENT_TYPE_ID_CARD_BACK);
                startActivityForResult(intent, REQUEST_CODE_CAMERA);
                break;
            case 2:
                start(DetectionFragment.newInstance(addIdCardInfo));
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != REQUEST_CODE_CAMERA || resultCode != Activity.RESULT_OK) {
            return;
        }
        if (data == null) {
            return;
        }

        String contentType = data.getStringExtra(CameraActivity.KEY_CONTENT_TYPE);
        if (TextUtils.isEmpty(contentType)) {
            return;
        }
        if (CameraActivity.CONTENT_TYPE_ID_CARD_FRONT.equals(contentType)) {
            File front = new File(getActivity().getFilesDir(), "front_pic.jpg");
            addIdCardInfo.setFrontPic(front.getAbsolutePath());

            Glide.with(mContext)
                    .load(Uri.fromFile(front))
                    .centerCrop()
                    .placeholder(R.mipmap.pic_idcardscanbg)
                    .error(R.mipmap.pic_idcardscanbg)
                    .into(ivIdcardOne);
            recIDCard(IDCardParams.ID_CARD_SIDE_FRONT, front.getAbsolutePath());
        } else if (CameraActivity.CONTENT_TYPE_ID_CARD_BACK.equals(contentType)) {
            File back = new File(getActivity().getFilesDir(), "back_pic.jpg");
            addIdCardInfo.setBackPic(back.getAbsolutePath());

            Glide.with(mContext)
                    .load(Uri.fromFile(back))
                    .centerCrop()
                    .placeholder(R.mipmap.pic_idcardfrontscanbg)
                    .error(R.mipmap.pic_idcardfrontscanbg)
                    .into(ivIdcardTwo);
            recIDCard(IDCardParams.ID_CARD_SIDE_BACK, back.getAbsolutePath());
        }
    }

    private void recIDCard(String idCardSide, String filePath) {
        IDCardParams param = new IDCardParams();
        param.setImageFile(new File(filePath));
        param.setIdCardSide(idCardSide);// 设置身份证正反面
        param.setDetectDirection(true);// 设置方向检测
        param.setImageQuality(40);// 设置图像参数压缩质量0-100, 越大图像质量越好但是请求时间越长。 不设置则默认值为20
        OCR.getInstance(getContext()).recognizeIDCard(param, new OnResultListener<IDCardResult>() {
            @Override
            public void onResult(IDCardResult result) {
                if (result != null) {
                    if (result.getName() != null) {
                        etName.setText(result.getName().toString());
                    }
                    if (result.getIdNumber() != null) {
                        etNumber.setText(result.getIdNumber().toString());
                    }
                    if (result.getSignDate() != null) {
                        Date date = ToolsUtils.getDate(result.getSignDate().getWords(), "yyyyMMdd");
                        tvStartTime.setText(ToolsUtils.getTime(date, "yyyy.MM.dd"));
                        addIdCardInfo.setValidaTime(result.getSignDate().getWords());
                    }
                    if (result.getExpiryDate() != null) {
                        Date date = ToolsUtils.getDate(result.getExpiryDate().getWords(), "yyyyMMdd");
                        tvEndTime.setText(ToolsUtils.getTime(date, "yyyy.MM.dd"));
                        addIdCardInfo.setExpireTime(result.getExpiryDate().getWords());
                    }
                    if (result.getAddress() != null) {
                        addIdCardInfo.setAddress(result.getAddress().getWords());
                    }
                    if (result.getGender() != null) {
                        addIdCardInfo.setSex(result.getGender().getWords());
                    }
                    if (result.getGender() != null) {
                        addIdCardInfo.setSex(result.getGender().getWords());
                    }
                    if (result.getBirthday() != null) {
                        addIdCardInfo.setIdentityTime(result.getBirthday().getWords());
                    }
                    if (result.getEthnic() != null) {
                        addIdCardInfo.setNationality(result.getEthnic().getWords());
                    }
                    if (result.getIssueAuthority() != null) {
                        addIdCardInfo.setIssue(result.getIssueAuthority().getWords());
                    }
                    if (commitDateVerify()) {
                        btnCommit.setEnabled(true);
                    } else {
                        btnCommit.setEnabled(false);
                    }
                }
            }

            @Override
            public void onError(OCRError error) {
                Toast.makeText(mContext, "识别出错，请自行填写", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean commitDateVerify() {
        if (addIdCardInfo == null) {
            return false;
        }
        if (StringUtils.isEmpty(addIdCardInfo.getFrontPic())) {
            return false;
        }
        if (StringUtils.isEmpty(addIdCardInfo.getBackPic())) {
            return false;
        }
        if (StringUtils.isEmpty(etName.getText().toString())) {
            return false;
        }
        if (StringUtils.isEmpty(etNumber.getText().toString())) {
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

    private boolean commitVerify() {
        if (addIdCardInfo == null) {
            return false;
        }
        if (StringUtils.isEmpty(addIdCardInfo.getFrontPic())) {
            Toast.makeText(mContext, "请拍摄身份证正面照", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (StringUtils.isEmpty(addIdCardInfo.getBackPic())) {
            Toast.makeText(mContext, "请拍摄身份证反面照", Toast.LENGTH_SHORT).show();
            return false;
        }
        String idName = etName.getText().toString().trim();
        if (StringUtils.isEmpty(idName)) {
            Toast.makeText(mContext, "请输入证件姓名", Toast.LENGTH_SHORT).show();
            return false;
        }
        addIdCardInfo.setRealName(idName);

        String idNum = etNumber.getText().toString().trim();
        if (StringUtils.isEmpty(idNum)) {
            Toast.makeText(mContext, "请输入证件号码", Toast.LENGTH_SHORT).show();
            return false;
        }
        addIdCardInfo.setIdentityCardNumber(idNum);

        String startTime = addIdCardInfo.getValidaTime();
        if (StringUtils.isEmpty(startTime)) {
            Toast.makeText(mContext, "请输入证件有效期开始时间", Toast.LENGTH_SHORT).show();
            return false;
        }
        String endTime = addIdCardInfo.getExpireTime();
        if (StringUtils.isEmpty(endTime)) {
            Toast.makeText(mContext, "请输入证件有效期结束时间", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void showTimeDialog(boolean isStratTime) {
        TimePickerView mTpBirthday = new TimePickerView(mContext, TimePickerView.Type.YEAR_MONTH_DAY);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.hasPermissions(mContext, mPerms)) {
            startPage();
        } else {
            Toast.makeText(mContext, "尚未赋予对应权限", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Toast.makeText(mContext, "权限被拒绝，无法进行下一步", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (commitDateVerify()) {
            btnCommit.setEnabled(true);
        } else {
            btnCommit.setEnabled(false);
        }
    }
}
