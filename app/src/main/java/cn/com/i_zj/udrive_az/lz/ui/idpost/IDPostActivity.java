package cn.com.i_zj.udrive_az.lz.ui.idpost;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import cn.com.i_zj.udrive_az.BaseActivity;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.login.AccountInfoManager;
import cn.com.i_zj.udrive_az.lz.bean.CameraEvent;
import cn.com.i_zj.udrive_az.lz.ui.camera.CameraActivity;
import cn.com.i_zj.udrive_az.lz.ui.idregister.IDRegisterActivity;
import cn.com.i_zj.udrive_az.lz.ui.idregister.IdBean;
import cn.com.i_zj.udrive_az.model.AccountInfoResult;
import cn.com.i_zj.udrive_az.model.IDResult;
import cn.com.i_zj.udrive_az.model.ImageUrlResult;
import cn.com.i_zj.udrive_az.network.UdriveRestClient;
import cn.com.i_zj.udrive_az.utils.Constants;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pub.devrel.easypermissions.EasyPermissions;

/**
 *
 *  手持身份证信息
 *  身份证信息
 */
public class IDPostActivity extends BaseActivity implements View.OnClickListener, EasyPermissions.PermissionCallbacks {

    private static final String TAG = IDPostActivity.class.getSimpleName();
    private String mFrontUrl;
    private String mBehindUrl;
    private File mCameraFile;
    private static final int REQUEST_CAMERA = 0;
    private ImageView mImage;
    private Button mBtnCommit;
    private EditText mEtNumber;
    private String mIdUrl;
    private EditText mEtName;
    private ProgressBar mPb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idpost);

        EventBus.getDefault().register(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.id_post_title);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mEtName = findViewById(R.id.et_name);
        mEtNumber = findViewById(R.id.et_number);
        mImage = findViewById(R.id.image);
        mBtnCommit = findViewById(R.id.btn_commit);

        mImage.setOnClickListener(this);
        mBtnCommit.setOnClickListener(this);

        mFrontUrl = getIntent().getStringExtra(Constants.URL_IDENTITY_CARD_PHOTO_FRONT);
        mBehindUrl = getIntent().getStringExtra(Constants.URL_IDENTITY_CARD_PHOTO_BEHIND);

        Bundle bundleExtra = getIntent().getExtras();
        if (bundleExtra != null) {
            IdBean idBean = (IdBean) bundleExtra.getSerializable(Constants.URL_BEAN);
            if (idBean != null) {
                mEtName.setText(idBean.getName());
                mEtNumber.setText(idBean.getNum());
            }
        }

        mPb = findViewById(R.id.pb);

        System.out.println("mFrontUrl = " + mFrontUrl);
        System.out.println("mBehindUrl = " + mBehindUrl);
        checkPermission();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }

    /**
     * 检测照相机权限
     */
    private void checkPermission() {
        boolean camera = EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA);

        if (!camera) {
            EasyPermissions.requestPermissions(this, getString(R.string.lz_request_permission), 1,
                    Manifest.permission.CAMERA);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);

    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        System.out.println(perms);

        if (perms.size() > 0) {
            Toast.makeText(this, R.string.lz_camera_permission_success, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.lz_camera_permission_fail, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Toast.makeText(this, R.string.lz_camera_permission_fail, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image:
//                Intent positiveIntent = new Intent(this, CameraActivity.class);
//                positiveIntent.putExtra(IDRegisterActivity.CODE_CAMERA, IDRegisterActivity.CODE_ID);
//                startActivity(positiveIntent);
                handleImage();
                break;
            case R.id.btn_commit:
                uploadId();
                break;
            default:
                break;
        }
    }

    /**
     * 使用相机
     */
    private void handleImage() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File dirPath = new File(Environment.getExternalStorageDirectory() + CameraActivity.CAMERA_TEMP_DIR_PATH);
        boolean mkdirs;
        if (!dirPath.exists()) {
            mkdirs = dirPath.mkdirs();
        } else {
            mkdirs = true;
        }
        if (mkdirs) {
            mCameraFile = new File(dirPath, System.currentTimeMillis() + ".jpg");
            System.out.println("mCameraFile.exists() = " + mCameraFile.exists());
            if (!mCameraFile.exists()) {
                try {
                    mCameraFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            //改变Uri  com.xykj.customview.fileprovider注意和xml中的一致
            Uri uri = FileProvider.getUriForFile(this, "cn.com.i_zj.udrive_az", mCameraFile);
            //添加权限
            System.out.println("uri = " + uri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(intent, REQUEST_CAMERA);
        } else {
            Toast.makeText(this, R.string.lz_no_write_permission, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
            if (mCameraFile == null) {
                Toast.makeText(this, R.string.lz_get_image_fail, Toast.LENGTH_SHORT).show();
            } else {
                uploadImage(mCameraFile.getPath());
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void cameraEvent(CameraEvent cameraEvent) {
        System.out.println("===============================================");
        if (IDRegisterActivity.CODE_ID == cameraEvent.getCode()) {
            uploadImage(cameraEvent.getPath());
        }
    }

    //上传手持身份证信息
    private void uploadId() {
        String idName = mEtName.getText().toString();
        String idNumber = mEtNumber.getText().toString();
        if (TextUtils.isEmpty(idName)) {
            Toast.makeText(this, R.string.realname, Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(idNumber)) {
            Toast.makeText(this, R.string.id_number, Toast.LENGTH_SHORT).show();
            return;
        }
        if (idNumber.length() < 3) {
            Toast.makeText(this, "身份证信息不对,请重新输入", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(mIdUrl)) {
            Toast.makeText(this, R.string.people_image, Toast.LENGTH_SHORT).show();
            return;
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("realName", idName);
        map.put("identityCardNumber", idNumber);
        map.put("handCardPhoto", mIdUrl);
        map.put("identityCardPhotoFront", mFrontUrl);
        map.put("identityCardPhotoBehind", mBehindUrl);

        UdriveRestClient.getClentInstance().postAddIdCardInfo(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<IDResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(IDResult value) {
                        Toast.makeText(IDPostActivity.this, R.string.id_post_success, Toast.LENGTH_SHORT).show();
                        AccountInfoResult accountInfo = AccountInfoManager.getInstance().getAccountInfo();
                        accountInfo.data.idCardState = Constants.ID_UNDER_REVIEW;
                        AccountInfoManager.getInstance().cacheAccount(accountInfo);
                        setResult(RESULT_OK);
                        finish();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(IDPostActivity.this, R.string.id_post_fail, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void uploadImage(final String path) {

        mPb.setVisibility(View.VISIBLE);
        mImage.setVisibility(View.INVISIBLE);
        final File file = new File(path);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);

        MultipartBody.Part body =
                MultipartBody.Part.createFormData("filename", file.getName(), requestFile);

        UdriveRestClient.getClentInstance().postImage(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ImageUrlResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ImageUrlResult value) {
                        if (value != null) {
                            mIdUrl = value.data;
                        }
                        mImage.setVisibility(View.VISIBLE);
                        mPb.setVisibility(View.GONE);
                        Glide.with(IDPostActivity.this).load(Uri.fromFile(file)).crossFade().placeholder(R.mipmap.ic_people).error(R.mipmap.ic_people).into(mImage);

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(IDPostActivity.this, R.string.upload_image_fail, Toast.LENGTH_SHORT).show();
                        mImage.setVisibility(View.VISIBLE);
                        mPb.setVisibility(View.GONE);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
