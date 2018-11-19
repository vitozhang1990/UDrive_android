package cn.com.i_zj.udrive_az.map.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.math.BigDecimal;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.model.CarInfoResult;
import cn.com.i_zj.udrive_az.utils.CarTypeImageUtils;
import cn.com.i_zj.udrive_az.utils.Constants;

/**
 * 首页选择弹出汽车信息
 */
public class CarsFragment extends Fragment {

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
    @BindView(R.id.rl1)
    RelativeLayout rl1;
    @BindView(R.id.v1)
    View v1;
    @BindView(R.id.tv_carnum)
    TextView tvCarnum;
    @BindView(R.id.tv1)
    TextView tv1;
    @BindView(R.id.tv_xuhang)
    TextView tvXuhang;
    @BindView(R.id.tv2)
    TextView tv2;
    @BindView(R.id.rl2)
    RelativeLayout rl2;
    @BindView(R.id.tv_fenzhong)
    TextView tvFenzhong;
    @BindView(R.id.tv3)
    TextView tv3;
    @BindView(R.id.tv_gongli)
    TextView tvGongli;
    @BindView(R.id.tv4)
    TextView tv4;
    @BindView(R.id.rl3)
    RelativeLayout rl3;
    @BindView(R.id.tv_traffic_control)
    TextView mTvTrafficControl;
    Unbinder unbinder;
    private TextView tvcarnum;
    private View v;
    CarInfoResult.DataBean carInfoResult;

    public CarsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (null != v) {
            ViewGroup parent = (ViewGroup) v.getParent();
            if (null != parent) {
                parent.removeView(v);
            }
        } else {
            v = inflater.inflate(R.layout.fragment_cars, container, false);
            /**
             * 控件的初始化
             */
        }
        unbinder = ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        CarInfoResult.DataBean result = new Gson().fromJson(bundle.getString(Constants.INTENT_KEY_CAR_DATA), CarInfoResult.DataBean.class);
        if(result!=null){
            tvCarnum.setText(result.getPlateNumber());
            tvCarname.setText(result.getBrand());
            tvColor.setText(result.getCarColor());
            tvZuowei.setText(String.valueOf(result.getSeatNumber()) + "座");
            tvXuhang.setText(String.valueOf(result.getMaxDistance()));
            tvFenzhong.setText(deciMal(result.getTimeFee(), 100) + "");
            tvGongli.setText(deciMal(result.getMileagePrice(), 100) + "");
            if("北汽LITE".equals(result.getBrand())){
                tvRanliao.setText("电动车");
            }

            Glide.with(getActivity()).load( CarTypeImageUtils.getCarImageByBrand(result.getBrand(),result.getCarColor())).into(ivCar);
            if(result.isTrafficControl()){
                mTvTrafficControl.setVisibility(View.VISIBLE);
            }else {
                mTvTrafficControl.setVisibility(View.GONE);
            }
        }

    }

    private double deciMal(int top, int below) {
        double result = new BigDecimal((float) top / below).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return result;
    }

//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EventBus.getDefault().register(this);
//    }
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void getResult(CarInfoResult.DataBean result) {
//        this.carInfoResult=result;
//        LogUtils.e(carInfoResult.getPlateNumber());
//    }

    /**
     * @param carItem s
     * @return fragment
     */
    public static CarsFragment newInstance(int carItem, CarInfoResult.DataBean result) {
        //0, 单位名称, 单位Id, parent_current
        CarsFragment myFragment = new CarsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("carItem", carItem);
        bundle.putString(Constants.INTENT_KEY_CAR_DATA, new Gson().toJson(result));
        LogUtils.e("newInstance==+" + result.getPlateNumber());
        myFragment.setArguments(bundle);
        return myFragment;
    }

    public void refresh(CarInfoResult.DataBean result) {
        if (result.getBrand() != null && !result.getBrand().equals("")) {

        }


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        EventBus.getDefault().unregister(this);
        unbinder.unbind();
    }

}
