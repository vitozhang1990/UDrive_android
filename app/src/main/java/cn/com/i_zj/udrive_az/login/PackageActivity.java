package cn.com.i_zj.udrive_az.login;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.blankj.utilcode.util.ToastUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.i_zj.udrive_az.DBSBaseActivity;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.map.MapUtils;
import cn.com.i_zj.udrive_az.map.WaitingActivity;
import cn.com.i_zj.udrive_az.map.fragment.BaseFragmentAdapter;
import cn.com.i_zj.udrive_az.map.fragment.PackageFragment;
import cn.com.i_zj.udrive_az.model.GetReservation;
import cn.com.i_zj.udrive_az.model.ParkDetailResult.DataBean.CarVosBean;
import cn.com.i_zj.udrive_az.model.ReserVationBean;
import cn.com.i_zj.udrive_az.network.UdriveRestClient;
import cn.com.i_zj.udrive_az.widget.ViewPagerIndicator;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class PackageActivity extends DBSBaseActivity implements ViewPager.OnPageChangeListener,
        CompoundButton.OnCheckedChangeListener, PackageFragment.PackageSelect {

    @BindView(R.id.viewpager)
    ViewPager mViewPager;
    @BindView(R.id.indicator_line)
    ViewPagerIndicator mIndicatorCircleLine;
    @BindView(R.id.checkbox)
    CheckBox checkbox;

    private ArrayList<CarVosBean> carBeans;
    private List<Fragment> fragments = new ArrayList<>();
    private int position;
    private int parkId;
    private CarVosBean selectCar;

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_package;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapUtils.setStatusBar(this);

        parkId = getIntent().getIntExtra("parkId", 0);
        position = getIntent().getIntExtra("position", 0);
        carBeans = (ArrayList<CarVosBean>) getIntent().getSerializableExtra("list");
        if (carBeans == null || carBeans.size() == 0) {
            showToast("数据错误");
            finish();
            return;
        }
        selectCar = carBeans.get(position);

        fragments.clear();
        for (CarVosBean carVosBean : carBeans) {
            PackageFragment carsFragment = PackageFragment.newInstance(carVosBean);
            carsFragment.setListener(this);
            fragments.add(carsFragment);
        }
        mViewPager.addOnPageChangeListener(this);
        BaseFragmentAdapter myPagerAdapter = new BaseFragmentAdapter(getSupportFragmentManager(), fragments);
        mViewPager.setAdapter(myPagerAdapter);
        mViewPager.setCurrentItem(position);
        mIndicatorCircleLine.setViewPager(mViewPager, fragments.size());

        checkbox.setOnCheckedChangeListener(this);
    }

    @OnClick({R.id.btn_yuding})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_yuding:
                reservation();
                break;
        }
    }

    private void reservation() {
        Map<String, String> map = new HashMap<>();
        map.put("carId", String.valueOf(selectCar.getId()));
        map.put("startParkId", String.valueOf(parkId));
        map.put("deductibleStatus", checkbox.isChecked() ? "1" : "0");
        for (CarVosBean.CarPackageVo carPackageVo : selectCar.getCarPackageVos()) {
            if (carPackageVo.isExpand()) {
                map.put("carPackageId", String.valueOf(carPackageVo.getId()));
                break;
            }
        }
        showProgressDialog();
        UdriveRestClient.getClentInstance().reservation(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(reserVationBean -> {
                    if (reserVationBean == null) {
                        ToastUtils.showShort("数据请求失败");
                        return false;
                    }
                    if (reserVationBean.getCode() != 1) {
                        ToastUtils.showShort(reserVationBean.getMessage());
                        return false;
                    }
                    if (reserVationBean.getData() == null) {
                        ToastUtils.showShort("数据返回错误");
                        return false;
                    }
                    if (reserVationBean.getData().getOrderType() != 0) {
//                        showUnfinshOrder();
                        return false;
                    }
                    return true;
                })
                .flatMap((Function<ReserVationBean, ObservableSource<GetReservation>>) reserVationBean ->
                        UdriveRestClient.getClentInstance().getReservation()
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread()))
                .filter(reserVationBean -> {
                    if (reserVationBean == null) {
                        ToastUtils.showShort("数据请求失败");
                        return false;
                    }
                    if (reserVationBean.getCode() != 1) {
                        ToastUtils.showShort(reserVationBean.getMessage());
                        return false;
                    }
                    if (reserVationBean.getData() == null) {
                        ToastUtils.showShort("数据返回错误");
                        return false;
                    }
                    return true;
                })
                .subscribe(new Observer<GetReservation>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(GetReservation result) {
                        dissmisProgressDialog();
                        Intent intent = new Intent(PackageActivity.this, WaitingActivity.class);
                        intent.putExtra("bunld", result);
                        startActivity(intent);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        dissmisProgressDialog();
                        ToastUtils.showShort("预约失败了");
                    }

                    @Override
                    public void onComplete() {
                        dissmisProgressDialog();
                    }
                });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.bottom_silent, R.anim.bottom_out);
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
        selectCar = carBeans.get(i);
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            return;
        }
        new AlertDialog.Builder(PackageActivity.this)
                .setTitle("不计免赔服务")
                .setMessage("购买不计免赔（5元/次）无需承担车辆保险所包含的部分经济责任")
                .setCancelable(false)
                .setNegativeButton("不购买", null)
                .setPositiveButton("仍然购买", (dialog, which) -> checkbox.setChecked(true))
                .create().show();
    }

    @Override
    public void onSelect(CarVosBean carPackageVo) {
        selectCar = carPackageVo;
        for (CarVosBean carVosBean : carBeans) {
            if (carVosBean.getId() == carPackageVo.getId()) {
                carVosBean.setCarPackageVos(carPackageVo.getCarPackageVos());
                break;
            }
        }
    }
}