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
import cn.com.i_zj.udrive_az.BuildConfig;
import cn.com.i_zj.udrive_az.DBSBaseActivity;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.lz.ui.accountinfo.certification.ActIdentificationDrivingLicense;
import cn.com.i_zj.udrive_az.lz.ui.accountinfo.certification.ActIdentificationIDCard;
import cn.com.i_zj.udrive_az.lz.ui.order.OrderActivity;
import cn.com.i_zj.udrive_az.map.MapUtils;
import cn.com.i_zj.udrive_az.map.TravelingActivity;
import cn.com.i_zj.udrive_az.map.WaitingActivity;
import cn.com.i_zj.udrive_az.map.fragment.BaseFragmentAdapter;
import cn.com.i_zj.udrive_az.map.fragment.PackageFragment;
import cn.com.i_zj.udrive_az.model.GetReservation;
import cn.com.i_zj.udrive_az.model.ParkDetailResult.DataBean.CarVosBean;
import cn.com.i_zj.udrive_az.model.ParksResult;
import cn.com.i_zj.udrive_az.model.ReserVationBean;
import cn.com.i_zj.udrive_az.network.UdriveRestClient;
import cn.com.i_zj.udrive_az.utils.Constants;
import cn.com.i_zj.udrive_az.utils.ToastUtil;
import cn.com.i_zj.udrive_az.web.WebActivity;
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
    private ParksResult.DataBean parkBean;
    private CarVosBean selectCar;

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_package;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapUtils.setStatusBar(this);

        position = getIntent().getIntExtra("position", 0);
        parkBean = (ParksResult.DataBean) getIntent().getSerializableExtra("park");
        carBeans = (ArrayList<CarVosBean>) getIntent().getSerializableExtra("list");
        if (carBeans == null || carBeans.size() == 0 || parkBean == null) {
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

    @OnClick({R.id.btn_yuding, R.id.bujimianpei})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_yuding:
                if (SessionManager.getInstance().getAuthorization() == null) {
                    LoginDialogFragment loginDialogFragment = new LoginDialogFragment();
                    loginDialogFragment.show(getSupportFragmentManager(), "login");
                    return;
                }
                if (selectCar.isTrafficControl()) {
                    showTrafficControlDialog();
                } else if (parkBean != null && parkBean.getCooperate() == 0
                        && parkBean.getStopInAmount() > 0) { //只有非合作停车场才会有出场费
                    showParkOutAmountDialog(parkBean.getStopInAmount() / 100);
                } else {
                    reservation();
                }
                break;
            case R.id.bujimianpei:
                WebActivity.startWebActivity(PackageActivity.this, BuildConfig.DOMAIN + "/deductible/");
                break;
        }
    }

    //限行Dialog
    private void showTrafficControlDialog() {
        new AlertDialog.Builder(this)
                .setTitle("限行提示")
                .setMessage("该车辆今日限行！因限行引起的违章费用将由您自行负责，请确认是否继续使用该车辆？")
                .setCancelable(false)
                .setNegativeButton("取消", null)
                .setPositiveButton("继续使用", (dialog, which) -> {
                    if (parkBean != null && parkBean.getCooperate() == 0
                            && parkBean.getStopInAmount() > 0) { //只有非合作停车场才会有出场费
                        showParkOutAmountDialog(parkBean.getStopInAmount() / 100);
                    } else {
                        reservation();
                    }
                })
                .create().show();
    }

    //停车费Dialog
    private void showParkOutAmountDialog(int cost) {
        new AlertDialog.Builder(this)
                .setTitle("支付提示")
                .setMessage("该车辆出停车场时可能需要付费" + cost + "元，待订单结束后返还至账户余额")
                .setCancelable(false)
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", (dialog, which) -> reservation())
                .create()
                .show();
    }

    //实名Dialog
    private void showIdCardStateDialog() {
        new AlertDialog.Builder(this)
                .setMessage("您没还没有实名认证")
                .setCancelable(false)
                .setNegativeButton("取消", null)
                .setPositiveButton("立即认证", (dialog, which) -> {
                    Intent intent = new Intent(PackageActivity.this, ActIdentificationIDCard.class);
                    intent.putExtra(Constants.INTENT_TITLE, Constants.INTENT_REGISTER_ID);
                    startActivity(intent);
                    finish();
                })
                .create()
                .show();
    }

    //驾照Dialog
    private void showDriverStateDialog() {
        new AlertDialog.Builder(this)
                .setMessage("您还没有绑定驾驶证")
                .setCancelable(false)
                .setNegativeButton("取消", null)
                .setPositiveButton("立即绑定", (dialog, which) -> {
                    Intent intent = new Intent(PackageActivity.this, ActIdentificationDrivingLicense.class);
                    intent.putExtra(Constants.INTENT_TITLE, Constants.INTENT_DRIVER_INFO);
                    startActivity(intent);
                    finish();
                })
                .create()
                .show();
    }

    private void showUnfinishedOrderDialog() {
        new AlertDialog.Builder(this)
                .setTitle("通知")
                .setMessage("您有未付款的订单")
                .setCancelable(false)
                .setNegativeButton("取消", null)
                .setPositiveButton("去付款", (dialog, which) -> {
                    startActivity(OrderActivity.class);
                    finish();
                })
                .create()
                .show();
    }

    //未完成订单Dialog
    private void showUnfinshOrder() {
        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("您还有未完成的订单，请完成订单")
                .setCancelable(false)
                .setNegativeButton("取消", null)
                .setPositiveButton("去完成", (dialog, which) -> {
                    startActivity(TravelingActivity.class);
                    finish();
                })
                .create()
                .show();
    }

    private void reservation() {
        Map<String, String> map = new HashMap<>();
        map.put("carId", String.valueOf(selectCar.getId()));
        map.put("startParkId", String.valueOf(parkBean.getId()));
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
                        switch (reserVationBean.getCode()) {
                            case 1017://未进行身份认证
                                showIdCardStateDialog();
                                break;
                            case 1018://未进行驾驶认证
                                showDriverStateDialog();
                                break;
                            case 1019://未交纳押金
                                ToastUtil.show(this, "请先缴纳押金");
                                break;
                            case 1024://驾驶认证审核中
                                ToastUtil.show(this, "驾照认证正在审核中");
                                break;
                            case 1025://订单行程中
                                showUnfinshOrder();
                                break;
                            case 1026://订单未支付
                                showUnfinishedOrderDialog();
                                break;
                            case 1027://身份认证审核中
                                ToastUtil.show(this, "实名认证正在审核中");
                                break;
                            default:
                                ToastUtils.showShort(reserVationBean.getMessage());
                                break;
                        }
                        return false;
                    }
                    if (reserVationBean.getData() == null) {
                        ToastUtils.showShort("数据返回错误");
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
                        finish();
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