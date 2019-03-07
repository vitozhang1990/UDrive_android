package cn.com.i_zj.udrive_az.step.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.baidu.ocr.ui.camera.CameraActivity;
import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.event.StepEvent;
import cn.com.i_zj.udrive_az.utils.FileUtil;
import me.yokeyword.fragmentation.SupportFragment;

public class DriveCardFragment extends SupportFragment {
    private static final int REQUEST_CODE_CAMERA_One = 102;
    private static final int REQUEST_CODE_CAMERA_Two = 103;

    @BindView(R.id.iv_one)
    ImageView ivDriveImageOne;
    @BindView(R.id.iv_two)
    ImageView ivDriveImageTwo;

    private Context mContext;

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
                        FileUtil.getSaveFile(getActivity()).getAbsolutePath());
                intent.putExtra(CameraActivity.KEY_CONTENT_TYPE,
                        CameraActivity.CONTENT_TYPE_GENERAL);
                startActivityForResult(intent, REQUEST_CODE_CAMERA_Two);
                break;
            case R.id.btn_commit:
                EventBus.getDefault().post(new StepEvent(3));
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
            Glide.with(mContext)
                    .load(Uri.fromFile(front))
                    .centerCrop()
                    .placeholder(R.mipmap.pic_driverlicensefrontbg)
                    .error(R.mipmap.pic_driverlicensefrontbg)
                    .into(ivDriveImageOne);

            RecognizeService.recDrivingLicense(getActivity(),
                    front.getAbsolutePath(),
                    result -> {
                        Log.e("zhangwei", result);
                        Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
                    });
        } else if (requestCode == REQUEST_CODE_CAMERA_Two) {
            File back = new File(getActivity().getFilesDir(), "drive_back_pic.jpg");
            Glide.with(mContext)
                    .load(Uri.fromFile(back))
                    .centerCrop()
                    .placeholder(R.mipmap.pic_driverlicensebg)
                    .error(R.mipmap.pic_driverlicensebg)
                    .into(ivDriveImageTwo);

            RecognizeService.recCustom(getActivity(),
                    FileUtil.getSaveFile(getActivity()).getAbsolutePath(),
                    result -> {
                        Log.e("zhangwei", result);
                        Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
                    });
        }
    }
}
