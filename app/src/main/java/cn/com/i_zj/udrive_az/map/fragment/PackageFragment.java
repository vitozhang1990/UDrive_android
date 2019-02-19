package cn.com.i_zj.udrive_az.map.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.model.ParkDetailResult.DataBean.CarVosBean;
import cn.com.i_zj.udrive_az.utils.CarTypeImageUtils;
import cn.com.i_zj.udrive_az.widget.ScaleBar;

public class PackageFragment extends Fragment {

    @BindView(R.id.iv_car)
    ImageView ivCar;
    @BindView(R.id.tv_carname)
    TextView tvCarname;
    @BindView(R.id.tv_color)
    TextView tvColor;
    @BindView(R.id.tv_zuowei)
    TextView tvZuowei;
    @BindView(R.id.tv_ranliao)
    TextView tvRanliao;
    @BindView(R.id.tv_carnum)
    TextView tvCarnum;
    @BindView(R.id.tv_xuhang)
    TextView tvXuhang;
    @BindView(R.id.tv_traffic_control)
    TextView mTvTrafficControl;

    @BindView(R.id.package_recycler)
    RecyclerView recycler;
    @BindView(R.id.scale_bar)
    ScaleBar scale_bar;

    private View v;
    private Unbinder unbinder;
    private CarVosBean mCarVosBean;

    public static PackageFragment newInstance(CarVosBean carVosBean) {
        Bundle args = new Bundle();
        args.putSerializable("car", carVosBean);

        PackageFragment fragment = new PackageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null != v) {
            ViewGroup parent = (ViewGroup) v.getParent();
            if (null != parent) {
                parent.removeView(v);
            }
        } else {
            v = inflater.inflate(R.layout.dialog_package_item, container, false);
        }
        unbinder = ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();

        mCarVosBean = (CarVosBean) bundle.getSerializable("car");
        if (mCarVosBean != null) {
            tvCarnum.setText(mCarVosBean.getPlateNumber());
            tvCarname.setText(mCarVosBean.getBrand());
            tvColor.setText(mCarVosBean.getCarColor());
            tvZuowei.setText(String.valueOf(mCarVosBean.getSeatNumber()) + "座");
            tvXuhang.setText(String.valueOf(mCarVosBean.getMaxDistance()));
            if ("北汽LITE".equals(mCarVosBean.getBrand())) {
                tvRanliao.setText("电动车");
            }

            Glide.with(getActivity())
                    .load(CarTypeImageUtils.getCarImageByBrand(mCarVosBean.getBrand(), mCarVosBean.getCarColor()))
                    .into(ivCar);
            if (mCarVosBean.isTrafficControl()) {
                mTvTrafficControl.setVisibility(View.VISIBLE);
            } else {
                mTvTrafficControl.setVisibility(View.GONE);
            }
            if (mCarVosBean.getTotalMileage() > 0) {
                scale_bar.setMark(((float) mCarVosBean.getMaxDistance()) / mCarVosBean.getTotalMileage());
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
