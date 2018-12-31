package cn.com.i_zj.udrive_az.login;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextPaint;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.com.i_zj.udrive_az.DBSBaseFragment;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.lz.bean.ParkRemark;
import cn.com.i_zj.udrive_az.lz.ui.accountinfo.certification.ActIdentificationDrivingLicense;
import cn.com.i_zj.udrive_az.lz.ui.accountinfo.certification.ActIdentificationIDCard;
import cn.com.i_zj.udrive_az.lz.ui.order.OrderActivity;
import cn.com.i_zj.udrive_az.map.MapUtils;
import cn.com.i_zj.udrive_az.map.ReserveActivity;
import cn.com.i_zj.udrive_az.map.fragment.BaseFragmentAdapter;
import cn.com.i_zj.udrive_az.map.fragment.CarsFragment;
import cn.com.i_zj.udrive_az.model.AccountInfoResult;
import cn.com.i_zj.udrive_az.model.CarInfoResult;
import cn.com.i_zj.udrive_az.model.ParksResult;
import cn.com.i_zj.udrive_az.model.ReserVationBean;
import cn.com.i_zj.udrive_az.network.UObserver;
import cn.com.i_zj.udrive_az.network.UdriveRestClient;
import cn.com.i_zj.udrive_az.utils.Constants;
import cn.com.i_zj.udrive_az.utils.SizeUtils;
import cn.com.i_zj.udrive_az.utils.ToastUtil;
import cn.com.i_zj.udrive_az.utils.dialog.NavigationDialog;
import cn.com.i_zj.udrive_az.utils.dialog.ParkDetailDialog;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import pub.devrel.easypermissions.EasyPermissions;

import static android.widget.Toast.makeText;

/**
 * 地图MapFragment
 */
public class MapFragment extends DBSBaseFragment implements AMapLocationListener, EasyPermissions.PermissionCallbacks {


    @BindView(R.id.map)
    MapView mMapView;

    @BindView(R.id.viewpager)
    ViewPager mViewPager;

    @BindView(R.id.tblayout)
    TabLayout tabLayout;

    @BindView(R.id.rl_where)
    RelativeLayout rl_where;

    @BindView(R.id.ll_info)
    LinearLayout ll_info;

    @BindView(R.id.rl_car_info)
    RelativeLayout rlCarinfo;

    @BindView(R.id.tv_pname)
    TextView tv_pname;

    @BindView(R.id.tv_paradress)
    TextView tv_adress;
    @BindView(R.id.tv_city)
    TextView tv_centerName;
    @BindView(R.id.tv_dis)
    TextView tv_dis;
    @BindView(R.id.iv_refresh)
    ImageView ivRefresh;

    @BindView(R.id.tv_park_detail)
    TextView tvParkDetail;

    Unbinder unbinder;
    private AMap mAmap;
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    private boolean isFirstLoc = true;

    private List<Fragment> fragments;
    //TODO 为啥要全局变量
    private ArrayList<ParksResult.DataBean> dataBeans = new ArrayList<>();

    private ArrayList<CarInfoResult.DataBean> carBeans = new ArrayList<>();
    private CarInfoResult.DataBean bunldBean;
    private ParksResult.DataBean buldParkBean;
    private LatLng myLocationlatLng;
    String parkid;
    String carid;
    private Animation animRefresh;
    private ParkDetailDialog parkDetailDialog;

    @Override
    protected int getLayoutResource() {
//    MapUtils.setStatusBar(getActivity());

        return R.layout.activity_map;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        animRefresh = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_home_refresh);
        LinearInterpolator lin = new LinearInterpolator();//设置动画匀速运动
        animRefresh.setInterpolator(lin);
        init(savedInstanceState);
    }

    private void init(Bundle savedInstanceState) {

        initViewstMap(savedInstanceState);
        bunldBean = new CarInfoResult.DataBean();
        buldParkBean = new ParksResult.DataBean();
        if (!EasyPermissions.hasPermissions(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
            EasyPermissions.requestPermissions(this, "您必须授予我们定位权限才可以正常使用", 101, Manifest.permission.ACCESS_FINE_LOCATION);
        }
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                bunldBean = carBeans.get(position);
                carid = String.valueOf(bunldBean.getId());
                CarsFragment carsFragment = (CarsFragment) fragments.get(position);
                carsFragment.refresh(carBeans.get(position));
//        tv_pname.setText(carBeans.get(position).getParkName());
//        tv_adress.setText(carBeans.get(position).getParkAddress());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }

    private void fetchParks() {
        UdriveRestClient.getClentInstance().getParks()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ParksResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(ParksResult result) {
                        dataBeans.clear();
                        dataBeans.addAll(result.getData());
                        mAmap.clear();
                        for (int i = 0; i < dataBeans.size(); i++) {
                            MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(dataBeans.get(i).getLatitude(), dataBeans.get(i).getLongitude()));
                            if (dataBeans.get(i).getCooperate() > 0) {
                                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(getMyBitmap(R.mipmap.ic_cheweishu_monthly, String.valueOf(dataBeans.get(i).getValidCarCount()))));
                            } else {
                                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(getMyBitmap(R.mipmap.ic_cheweishu_llinshi, String.valueOf(dataBeans.get(i).getValidCarCount()))));
                            }

                            Marker marker = mAmap.addMarker(markerOptions);
                            marker.setObject(dataBeans.get(i).getId());
                        }
                        if (animRefresh != null) {
                            ivRefresh.clearAnimation();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    @BindView(R.id.btn_yuding)
    Button btn_yuding;

    @OnClick(R.id.btn_yuding)
    public void onYudingClick(View view) {
        if (bunldBean != null && buldParkBean != null) {
            if (bunldBean.isTrafficControl()) {
                showTrafficControlDialog();
            } else {
                yuyueVerify();
            }
        } else {
            ToastUtils.showShort("请先选择车辆");
        }
    }

    private void yuyueVerify() {
        if (SessionManager.getInstance().getAuthorization() != null) {
            AccountInfoResult accountInfo = AccountInfoManager.getInstance().getAccountInfo();

            if (accountInfo.data.idCardState == Constants.ID_AUTHORIZED_SUCCESS && accountInfo.data.driverState == Constants.ID_AUTHORIZED_SUCCESS) {
                if (accountInfo.data.depositState == 2) {
//                    yuyue();
                    showParkOutAmountDialog(100000f);
                } else {
                    ToastUtil.show(getActivity(), "请先缴纳押金");
                }
            } else {
                // 先认证身份证 成功后再认证驾照
                if (accountInfo.data.idCardState == Constants.ID_UNDER_REVIEW) {
                    makeText(getActivity(), "实名认证正在审核中", Toast.LENGTH_SHORT).show();
                    return;
                } else if (accountInfo.data.idCardState == Constants.ID_AUTHORIZED_SUCCESS) {// 认证成功
                    //开始认证认证驾照
                    if (accountInfo.data.driverState == Constants.ID_UNDER_REVIEW) {
                        makeText(getActivity(), "驾照认证正在审核中", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        showDriverStateDialog();
                    }
                } else {
                    showIdCardStateDialog();

                }

            }

        } else {
            LoginDialogFragment loginDialogFragment = new LoginDialogFragment();
            loginDialogFragment.show(getChildFragmentManager(), "login");
        }
    }

    private void showDriverStateDialog() {
        new AlertDialog.Builder(getActivity())
                .setMessage("您还没有绑定驾驶证")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("立即绑定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getActivity(), ActIdentificationDrivingLicense.class);
                        intent.putExtra(Constants.INTENT_TITLE, Constants.INTENT_DRIVER_INFO);
                        startActivity(intent);
                    }
                })
                .create().show();
    }

    /**
     * 显示实名认证Dialog
     */
    private void showIdCardStateDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setMessage("您没还没有实名认证")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("立即认证", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getActivity(), ActIdentificationIDCard.class);
                        intent.putExtra(Constants.INTENT_TITLE, Constants.INTENT_REGISTER_ID);
                        startActivity(intent);
                    }
                })
                .create();
        if (!alertDialog.isShowing()) {
            alertDialog.show();
        }
    }

    private void showParkOutAmountDialog(float cost) {
        new AlertDialog.Builder(getActivity())
                .setTitle("支付提示")
                .setMessage("该车辆出停车场时可能需要付费" + cost + "元，待订单结束后返还至账户余额")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        yuyue();
                    }
                })
                .create().show();
    }

    /**
     * 预约
     */
    private void yuyue() {
        LogUtils.e("222");
        Map<String, String> map = new HashMap<>();
        map.put("carId", carid);
        map.put("startParkId", parkid);
        String token = SessionManager.getInstance().getAuthorization();
        LogUtils.e(token);
        LogUtils.e(carid + "--" + parkid);
        showProgressDialog("正在预约");
        UdriveRestClient.getClentInstance().reservation(token, map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ReserVationBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(ReserVationBean result) {
                        dissmisProgressDialog();
                        if (result != null) {
                            if (result.getCode() == 1) {
                                if (!"".equals(result.getData()) && result.getData() != null) {
                                    if (result.getData().getOrderType() != 0) {
                                        showUnfinshOrder();
                                    } else {
                                        Intent intent = new Intent(getActivity(), ReserveActivity.class);
                                        intent.putExtra("type", "1");
                                        intent.putExtra("bunld", bunldBean);
                                        intent.putExtra("bunldPark", buldParkBean);
                                        intent.putExtra("id", result.getData().getReservationId() + "");
                                        startActivity(intent);
                                        ll_info.setVisibility(View.GONE);
                                        rl_where.setVisibility(View.GONE);
                                        btn_yuding.setVisibility(View.GONE);
                                    }

                                }

                            }
                        }
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

    //初始化地图
    private void initViewstMap(Bundle savedInstanceState) {
        mMapView.onCreate(savedInstanceState);// 此方法必须重写
        mAmap = mMapView.getMap();
        UiSettings uiSettings = mAmap.getUiSettings();
        uiSettings.setRotateGesturesEnabled(false);
        uiSettings.setTiltGesturesEnabled(false);
        MapUtils.setMapCustomStyleFile(getContext(), mAmap);
        //初始化定位
        mLocationClient = new AMapLocationClient(getActivity().getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(this);
        //启动定位
        mLocationClient.startLocation();
        //测试marker

        mAmap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                rl_where.setVisibility(View.GONE);
                for (int i = 0; i < dataBeans.size(); i++) {
                    if (dataBeans.get(i).getId() == Integer.parseInt(marker.getObject().toString())) {
                        parkid = String.valueOf(dataBeans.get(i).getId());
                        buldParkBean = dataBeans.get(i);
                        tv_pname.setText(dataBeans.get(i).getName());
                        tv_adress.setText(dataBeans.get(i).getName());
                        float distance = AMapUtils.calculateLineDistance(
                                myLocationlatLng,
                                new LatLng(dataBeans.get(i).getLatitude(), dataBeans.get(i).getLongitude())
                        );
                        int dis = (int) (distance / 1000);
                        tv_dis.setText(dis + "km");
                        break;
                    }
                }
                LogUtils.e(SessionManager.getInstance().getAuthorization());
                // 获取停车场车辆列表信息
//                showProgressDialog("正在获取信息");
                UdriveRestClient.getClentInstance().getCarInfo("/mobile/car/getReservationList/" + marker.getObject())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<CarInfoResult>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                            }

                            @Override
                            public void onNext(CarInfoResult result) {
//                                dissmisProgressDialog();
                                if (result.getData().size() != 0) {
                                    carBeans.clear();
                                    carBeans.addAll(result.getData());
                                    if (fragments != null) {
                                        for (int i = 0; i < fragments.size(); i++) {
                                            fragments.get(i).onDestroy();
                                        }
                                        if (fragments.size() > 0) {
                                            fragments.clear();
                                        }
                                    }

                                    //初始化list
                                    fragments = new ArrayList<>();
                                    ArrayList<Integer> imgs = new ArrayList<>();
                                    //for循环给list增加fragment
                                    for (int i = 0; i < result.getData().size(); i++) {
                                        LogUtils.e(result.getData().get(i).getPlateNumber());
                                        CarsFragment carsFragment = CarsFragment.newInstance(i, result.getData().get(i));
                                        fragments.add(carsFragment);
                                        imgs.add(R.drawable.view_selector);

                                    }

                                    bunldBean = result.getData().get(0);
                                    carid = String.valueOf(bunldBean.getId());
                                    BaseFragmentAdapter myPagerAdapter = new BaseFragmentAdapter(getFragmentManager(), fragments, imgs, getContext());
                                    mViewPager.setAdapter(myPagerAdapter);

                                    tabLayout.setupWithViewPager(mViewPager);
                                    //增加指示器个数
                                    for (int i = 0; i < tabLayout.getTabCount(); i++) {
                                        TabLayout.Tab tab = tabLayout.getTabAt(i);
                                        if (tab != null) {
                                            tab.setCustomView(myPagerAdapter.getTabView(i));
                                        }
                                    }


                                    CarsFragment carsFragment = (CarsFragment) fragments.get(0);
                                    carsFragment.refresh(result.getData().get(0));
                                    ll_info.setVisibility(View.VISIBLE);
                                    rlCarinfo.setVisibility(View.VISIBLE);
                                    btn_yuding.setVisibility(View.VISIBLE);
                                    btn_yuding.setEnabled(true);
                                    btn_yuding.setText(Utils.getApp().getResources().getString(R.string.mashangyuding));
                                } else {
                                    ll_info.setVisibility(View.VISIBLE);
                                    rlCarinfo.setVisibility(View.GONE);
                                    rl_where.setVisibility(View.GONE);
                                    btn_yuding.setVisibility(View.VISIBLE);
                                    btn_yuding.setText("暂无车辆");
                                    btn_yuding.setEnabled(false);
//                                    ToastUtils.showShort("该停车场暂无车辆信息");
                                }

                            }

                            @Override
                            public void onError(Throwable e) {
//                                dissmisProgressDialog();
                                e.printStackTrace();
                            }

                            @Override
                            public void onComplete() {
//                                dissmisProgressDialog();

                            }
                        });
                return false;
            }
        });
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
            if (isFirstLoc) {
//                这段代码是修改样式去掉阴影圆圈地图的
                MyLocationStyle myLocationStyle = new MyLocationStyle();
                myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));// 自定义精度范围的圆形边框颜色
                myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));//圆圈的颜色,设为透明的时候就可以去掉园区区域了
                myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(
                        R.mipmap.ic_location_arrow));
                myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
                mAmap.setMyLocationStyle(myLocationStyle);
                mAmap.setMyLocationEnabled(true);
                //去掉放大缩小 增加回到当前位置
                UiSettings uiSettings = mAmap.getUiSettings();
                uiSettings.setZoomControlsEnabled(false);

                mLocationClient.stopLocation();
                mAmap.moveCamera(CameraUpdateFactory.zoomTo(17));
                //将地图移动到定位点
                tv_centerName.setText(aMapLocation.getCity());
                myLocationlatLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                mAmap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude())));

                isFirstLoc = false;
            } else {
                //定位成功回调信息，设置相关消息

                //取出经纬度
                LatLng latLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                mLocationClient.stopLocation();
                //然后可以移动到定位点,使用animateCamera就有动画效果
                mAmap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
                ll_info.setVisibility(View.GONE);
                rl_where.setVisibility(View.GONE);
                btn_yuding.setVisibility(View.GONE);

            }
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        ToastUtils.showShort("获取成功");
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        ToastUtils.showShort("您拒绝了我们必要的一些权限,请在设置中授予定位权限！");
    }

    @Override
    public void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
        fetchParks();
    }

    @Override
    public void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mLocationClient.stopLocation();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
        mLocationClient.onDestroy();
    }

    //在marker上绘制文字
    protected Bitmap getMyBitmap(int mipMapId, String pm_val) {

        Bitmap bitmap = BitmapDescriptorFactory.fromResource(
                mipMapId).getBitmap();
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight());
        Canvas canvas = new Canvas(bitmap);
        TextPaint textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(SizeUtils.sp2px(getActivity(), 16));
        textPaint.setColor(getResources().getColor(R.color.white));
        canvas.translate(canvas.getWidth() / 2, canvas.getHeight() / 2);
        canvas.drawLine(canvas.getWidth() / 2, 0, canvas.getWidth() / 2, 0, textPaint);
        float baseLineY = Math.abs(textPaint.ascent() + textPaint.descent()) / 2;
        float textWidth = textPaint.measureText(pm_val);
        canvas.drawText(pm_val, -textWidth / 2, baseLineY - 5, textPaint);// 设置bitmap上面的文字位置
        return bitmap;
    }

    private void showTrafficControlDialog() {
        new AlertDialog.Builder(getActivity())
                .setTitle("限行提示")
                .setMessage("该车辆今日限行！因限行引起的违章费用将由您自行负责，请确认是否继续使用该车辆？")
                .setNegativeButton("继续使用", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        yuyueVerify();
                    }
                })
                .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create().show();
    }

    private void showUnfinshOrder() {
        new AlertDialog.Builder(getActivity())
                .setTitle("提示")
                .setMessage("您还有未完成的订单，请完成订单")
                .setNegativeButton("去完成", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(OrderActivity.class);
                    }
                })
                .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create().show();
    }

    @OnClick({R.id.iv_refresh, R.id.iv_mylocation, R.id.tv_park_detail})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_refresh:
                if (animRefresh != null) {
                    ivRefresh.startAnimation(animRefresh);
                }

                fetchParks();
                if (mLocationClient != null) {
                    mLocationClient.startLocation();
                }
                break;
            case R.id.iv_mylocation:
                if (mLocationClient != null) {
                    mLocationClient.startLocation();
                }
                break;
            case R.id.tv_park_detail:

                getParkRemark();
                break;
        }
    }


    @OnClick(R.id.rl1)
    public void onClick() {
        if (buldParkBean != null) {
            NavigationDialog navigationDialog = new NavigationDialog();
            Bundle bundle = new Bundle();
            bundle.putString("lng", String.valueOf(buldParkBean.getLongitude()));
            bundle.putString("lat", String.valueOf(buldParkBean.getLatitude()));
            navigationDialog.setArguments(bundle);
            navigationDialog.show(getChildFragmentManager(), "navigation");
        }
    }


    private void getParkRemark() {
        UdriveRestClient.getClentInstance().getParkRemark(parkid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new UObserver<ParkRemark>() {
                    @Override
                    public void onSuccess(ParkRemark response) {
                        parkDetailDialog = new ParkDetailDialog(getActivity());
                        response.setName(buldParkBean.getName());// 只能这样去取
                        parkDetailDialog.showData(response);
                        parkDetailDialog.show();
                    }


                    @Override
                    public void onFinish() {

                    }

                    @Override
                    public void onException(int code, String message) {
                        showToast(message);
                    }
                });
    }

}

