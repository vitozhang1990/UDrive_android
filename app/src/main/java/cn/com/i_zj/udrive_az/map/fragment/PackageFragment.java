package cn.com.i_zj.udrive_az.map.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.math.BigDecimal;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.model.ParkDetailResult.DataBean.CarVosBean;
import cn.com.i_zj.udrive_az.utils.CarTypeImageUtils;

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
    @BindView(R.id.tv_fenzhong)
    TextView tvFenzhong;
    @BindView(R.id.tv_gongli)
    TextView tvGongli;
    @BindView(R.id.tv4)
    TextView tv4;
    @BindView(R.id.tv_traffic_control)
    TextView mTvTrafficControl;

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
            tvFenzhong.setText(deciMal(mCarVosBean.getTimeFee(), 100) + "");
            tvGongli.setText(deciMal(mCarVosBean.getMileagePrice(), 100) + "");
            if ("北汽LITE".equals(mCarVosBean.getBrand())) {
                tvRanliao.setText("电动车");
                tvGongli.setVisibility(View.GONE);
                tv4.setVisibility(View.GONE);
            }

            Glide.with(getActivity())
                    .load(CarTypeImageUtils.getCarImageByBrand(mCarVosBean.getBrand(), mCarVosBean.getCarColor()))
                    .into(ivCar);
            if (mCarVosBean.isTrafficControl()) {
                mTvTrafficControl.setVisibility(View.VISIBLE);
            } else {
                mTvTrafficControl.setVisibility(View.GONE);
            }
        }
    }

    private double deciMal(int top, int below) {
        double result = new BigDecimal((float) top / below).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return result;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
