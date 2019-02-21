package cn.com.i_zj.udrive_az.map.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.math.BigDecimal;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.com.i_zj.udrive_az.BuildConfig;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.map.adapter.GlobalAdapter;
import cn.com.i_zj.udrive_az.map.adapter.OnGlobalListener;
import cn.com.i_zj.udrive_az.map.adapter.RecyclerViewUtils;
import cn.com.i_zj.udrive_az.model.ParkDetailResult.DataBean.CarVosBean;
import cn.com.i_zj.udrive_az.model.ParkDetailResult.DataBean.CarVosBean.CarPackageVo;
import cn.com.i_zj.udrive_az.utils.CarTypeImageUtils;
import cn.com.i_zj.udrive_az.utils.HtmlTagHandler;
import cn.com.i_zj.udrive_az.web.WebActivity;
import cn.com.i_zj.udrive_az.widget.ScaleBar;

public class PackageFragment extends Fragment implements OnGlobalListener, BaseQuickAdapter.OnItemClickListener {

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
    private GlobalAdapter mAdapter;

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
        if (mCarVosBean == null) {
            return;
        }
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
        //构造一个标准套餐
        CarPackageVo packageVo = new CarPackageVo();
        packageVo.setExpand(true);
        packageVo.setStandard(true);
        packageVo.setStandardTime(decimal(mCarVosBean.getTimeFee(), 100));
        packageVo.setStandardMile(decimal(mCarVosBean.getMileagePrice(), 100));
        if (mCarVosBean.getCarPackageVos() == null) {
            mCarVosBean.setCarPackageVos(new ArrayList<>());
        }
        boolean has = false;
        for (CarPackageVo cc : mCarVosBean.getCarPackageVos()) {
            if (cc.isStandard()) {
                has = true;
                break;
            }
        }
        if (!has) {
            mCarVosBean.getCarPackageVos().add(0, packageVo);
        }

        mAdapter = RecyclerViewUtils.initLiner(
                getActivity(), recycler,
                R.layout.item_package, mCarVosBean.getCarPackageVos(),
                this, this);
    }

    @OnClick(R.id.jifei_layout)
    void jifeiClick() {
        WebActivity.startWebActivity(getActivity(), BuildConfig.DOMAIN + "/cost/");
    }

    private double decimal(int top, int below) {
        return new BigDecimal((float) top / below).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    @Override
    public <T> void logic(BaseViewHolder helper, T item) {
        CarPackageVo packageVo = (CarPackageVo) item;
        helper.setBackgroundRes(R.id.package_root, packageVo.isExpand() ? R.drawable.shape_package_all_black : R.drawable.shape_package_black);
        helper.setTextColor(R.id.package_text_1, packageVo.isExpand() ? Color.parseColor("#ffffff") : Color.parseColor("#030303"));
        helper.setTextColor(R.id.package_text_2, packageVo.isExpand() ? Color.parseColor("#ffffff") : Color.parseColor("#030303"));
        helper.setGone(R.id.package_detail, !packageVo.isStandard() && packageVo.isExpand());

        helper.setText(R.id.package_text_1, packageVo.isStandard() ? "标准" : packageVo.getPackageName());
        if (!packageVo.isStandard()) {
            helper.setText(R.id.package_text_2, Html.fromHtml("<b><myfont size='18sp'>"
                            + packageVo.getAmount() / 100 + "</myfont></b> 元", null
                    , new HtmlTagHandler("myfont")));
            helper.setText(R.id.package_content, packageVo.getDurationTime() / 60 + " 小时时长费 + "
                    + packageVo.getMileage() + " 公里里程费");
            helper.setText(R.id.package_duration, packageVo.getStartTime() + " - " + packageVo.getEndTime());
            helper.setGone(R.id.package_duration, !TextUtils.isEmpty(packageVo.getStartTime())
                    || !TextUtils.isEmpty(packageVo.getEndTime()));
        } else {
            helper.setText(R.id.package_text_2, Html.fromHtml("<b><myfont size='18sp'>" + packageVo.getStandardTime()
                    + "</myfont></b> 元/分钟 + " + "<b><myfont size='18sp'>" + packageVo.getStandardMile()
                    + "</myfont></b> 元/公里", null, new HtmlTagHandler("myfont")));
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        for (int i = 0; i < mCarVosBean.getCarPackageVos().size(); i++) {
            mCarVosBean.getCarPackageVos().get(i).setExpand(i == position);
            if (listener != null && i == position) {
                listener.onSelect(mCarVosBean);
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private PackageSelect listener;

    public PackageSelect getListener() {
        return listener;
    }

    public void setListener(PackageSelect listener) {
        this.listener = listener;
    }

    public interface PackageSelect {
        void onSelect(CarVosBean carPackageVo);
    }
}
