package cn.com.i_zj.udrive_az.step.fragment;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.i_zj.udrive_az.R;
import me.yokeyword.fragmentation.SupportFragment;
import pub.devrel.easypermissions.EasyPermissions;

public class IdCardFragment extends SupportFragment implements EasyPermissions.PermissionCallbacks {

    private String[] mPerms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
    private Context mContext;

    public static IdCardFragment newInstance() {
        Bundle args = new Bundle();

        IdCardFragment fragment = new IdCardFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_identification_idcard, container, false);
        ButterKnife.bind(this, view);

        mContext = getContext();
        return view;
    }

    @OnClick({R.id.btn_commit})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_commit:
                if (EasyPermissions.hasPermissions(mContext, mPerms)) {
                    startWithPop(DetectionFragment.newInstance());
                } else {
                    EasyPermissions.requestPermissions(this, "需要申请拍照、文件读写权限", 1, mPerms);
                }
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.hasPermissions(mContext, mPerms)) {
            startWithPop(DetectionFragment.newInstance());
        } else {
            Toast.makeText(mContext, "尚未赋予对应权限", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Toast.makeText(mContext, "权限被拒绝，无法进行下一步", Toast.LENGTH_SHORT).show();
    }
}
