package cn.com.i_zj.udrive_az.step.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.i_zj.udrive_az.R;
import me.yokeyword.fragmentation.SupportFragment;

public class DriveCardFragment extends SupportFragment {

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
        return view;
    }

    @OnClick({R.id.btn_commit})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_commit:
                startWithPop(DepositFragment.newInstance());
                break;
        }
    }
}
