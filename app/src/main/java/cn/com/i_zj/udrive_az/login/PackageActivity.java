package cn.com.i_zj.udrive_az.login;

import android.app.AlertDialog;
import android.app.Dialog;
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
import cn.com.i_zj.udrive_az.lz.ui.accountinfo.AccountInfoActivity;
import cn.com.i_zj.udrive_az.lz.ui.deposit.DepositActivity;
import cn.com.i_zj.udrive_az.lz.ui.order.OrderActivity;
import cn.com.i_zj.udrive_az.map.MapUtils;
import cn.com.i_zj.udrive_az.map.TravelingActivity;
import cn.com.i_zj.udrive_az.map.WaitingActivity;
import cn.com.i_zj.udrive_az.map.fragment.BaseFragmentAdapter;
import cn.com.i_zj.udrive_az.map.fragment.PackageFragment;
import cn.com.i_zj.udrive_az.model.GetReservation;
import cn.com.i_zj.udrive_az.model.ParkDetailResult.DataBean.CarVosBean;
import cn.com.i_zj.udrive_az.model.ParkOutAmount;
import cn.com.i_zj.udrive_az.model.ParksResult;
import cn.com.i_zj.udrive_az.model.ReserVationBean;
import cn.com.i_zj.udrive_az.network.UdriveRestClient;
import cn.com.i_zj.udrive_az.step.StepActivity;
import cn.com.i_zj.udrive_az.utils.ToastUtil;
import cn.com.i_zj.udrive_az.web.WebActivity;
import cn.com.i_zj.udrive_az.widget.CommonAlertDialog;
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
    private Disposable disposable;

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
            disposable = null;
        }
    }

    @OnClick({R.id.btn_yuding, R.id.bujimianpei, R.id.down_image})
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
                } else {
                    reservation(true);
                }
                break;
            case R.id.bujimianpei:
                WebActivity.startWebActivity(PackageActivity.this, BuildConfig.DOMAIN + "/deductible/");
                break;
            case R.id.down_image:
                finish();
                break;
        }
    }

    //限行Dialog
    private void showTrafficControlDialog() {
        CommonAlertDialog.builder(this)
                .setTitle("限行提示")
                .setMsg("该车辆今日限行！因限行引起的违章费用将由您自行负责，请确认是否继续使用该车辆？")
                .setMsgCenter(true)
                .setPositiveButton("取消", null)
                .setNegativeButton("继续使用", v -> reservation(true))
                .build()
                .show();
    }

    //停车费Dialog
    private void showParkOutAmountDialog(int cost) {
        CommonAlertDialog.builder(this)
                .setTitle("支付提示")
                .setMsg("该车辆出停车场时可能需要付费" + cost + "元，待订单结束后返还至账户余额")
                .setMsgCenter(true)
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", v -> reservation(false))
                .build()
                .show();
    }

    //实名Dialog
    private void showIdCardStateDialog() {
        CommonAlertDialog.builder(this)
                .setMsg("请先完成实名认证")
                .setMsgCenter(true)
                .setNegativeButton("取消", null)
                .setPositiveButton("去认证", v -> {
                    startActivity(AccountInfoActivity.class);
                    finish();
                })
                .build()
                .show();
    }

    //驾照Dialog
    private void showDriverStateDialog() {
        CommonAlertDialog.builder(this)
                .setMsg("请先完成驾驶认证")
                .setMsgCenter(true)
                .setNegativeButton("取消", null)
                .setPositiveButton("去认证", v -> {
                    startActivity(AccountInfoActivity.class);
                    finish();
                })
                .build()
                .show();
    }

    //押金Dialog
    private void showDepositDialog() {
        CommonAlertDialog.builder(this)
                .setMsg("请先缴纳押金")
                .setMsgCenter(true)
                .setNegativeButton("取消", null)
                .setPositiveButton("去缴纳", v -> {
                    startActivity(DepositActivity.class);
                    finish();
                })
                .build()
                .show();
    }

    private void showUnfinishedOrderDialog() {
        CommonAlertDialog.builder(this)
                .setMsg("您有未支付的订单，请先支付")
                .setMsgCenter(true)
                .setNegativeButton("取消", null)
                .setPositiveButton("去支付", v -> {
                    startActivity(OrderActivity.class);
                    finish();
                })
                .build()
                .show();
    }

    //未完成订单Dialog
    private void showUnfinshOrder() {
        CommonAlertDialog.builder(this)
                .setMsg("您有一个订单正在进行中，是否进入")
                .setMsgCenter(true)
                .setNegativeButton("取消", null)
                .setPositiveButton("进入", v -> {
                    startActivity(TravelingActivity.class);
                    finish();
                })
                .build()
                .show();
    }

    private void showIdCardFailure() {
        CommonAlertDialog.builder(this)
                .setMsg("您的实名认证审核失败")
                .setMsgCenter(true)
                .setNegativeButton("取消", null)
                .setPositiveButton("重新认证", v -> {
                    startActivity(AccountInfoActivity.class);
                    finish();
                })
                .build()
                .show();
    }

    private void showDriverFailure() {
        CommonAlertDialog.builder(this)
                .setMsg("您的驾驶证审核失败")
                .setMsgCenter(true)
                .setNegativeButton("取消", null)
                .setPositiveButton("重新认证", v -> {
                    startActivity(AccountInfoActivity.class);
                    finish();
                })
                .build()
                .show();
    }

    private void reservation(boolean showParkOutAmountDialog) {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
            disposable = null;
        }
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
        UdriveRestClient.getClentInstance().getParkOutAmount(String.valueOf(parkBean.getId()), String.valueOf(selectCar.getId()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(parkOutAmount -> {
                    if (parkOutAmount != null && parkOutAmount.getData() != null
                            && parkOutAmount.getData() > 0 && showParkOutAmountDialog) {
                        showParkOutAmountDialog(parkOutAmount.getData() / 100);
                        return false;
                    }
                    return true;
                })
                .flatMap((Function<ParkOutAmount, ObservableSource<ReserVationBean>>) parkOutAmount ->
                        UdriveRestClient.getClentInstance().reservation(map)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread()))
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
                            case 1019://未缴纳押金
                                showDepositDialog();
                                break;
                            case 1024://驾驶认证审核中
                                ToastUtil.show(this, "驾驶证正在审核中");
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
                            case 1028://身份证认证失败
                                showIdCardFailure();
                                break;
                            case 1029://驾驶证认证失败
                                showDriverFailure();
                                break;
                            case 1032:
                                if (reserVationBean.getData().getDeposit().getState() != 2) {
                                    showToast("请先完成认证");
                                    Intent intent = new Intent(this, StepActivity.class);
                                    intent.putExtra("data", reserVationBean.getData().getAuthResult());
                                    startActivity(intent);
                                } else if (reserVationBean.getData().getIdcard().getState() == 1
                                        && reserVationBean.getData().getDriver().getState() == 1) {
                                    ToastUtil.show(this, "认证正在审核中");
                                } else if (reserVationBean.getData().getIdcard().getState() == 1
                                        && reserVationBean.getData().getDriver().getState() == 2) {
                                    ToastUtil.show(this, "实名认证正在审核中");
                                } else if (reserVationBean.getData().getIdcard().getState() == 2
                                        && reserVationBean.getData().getDriver().getState() == 1) {
                                    ToastUtil.show(this, "驾驶证正在审核中");
                                } else {
                                    showToast("请先完成认证");
                                    Intent intent = new Intent(this, StepActivity.class);
                                    intent.putExtra("data", reserVationBean.getData().getAuthResult());
                                    startActivity(intent);
                                }
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
                        disposable = d;
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
        CommonAlertDialog.builder(this)
                .setTitle("不计免赔服务")
                .setMsg("购买不计免赔（5元/次）无需承担车辆保险所包含的部分经济责任")
                .setMsgCenter(true)
                .setNegativeButton("不购买", null)
                .setPositiveButton("仍然购买", v -> checkbox.setChecked(true))
                .build()
                .show();
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