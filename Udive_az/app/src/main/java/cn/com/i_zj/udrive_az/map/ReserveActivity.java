package cn.com.i_zj.udrive_az.map;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextPaint;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import cn.com.i_zj.udrive_az.DBSBaseActivity;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.login.SessionManager;
import cn.com.i_zj.udrive_az.lz.ui.payment.ActConfirmOrder;
import cn.com.i_zj.udrive_az.lz.ui.payment.PaymentActivity;
import cn.com.i_zj.udrive_az.map.adapter.ChoosStartEndActivity;
import cn.com.i_zj.udrive_az.model.CarInfoEntity;
import cn.com.i_zj.udrive_az.model.CarInfoResult;
import cn.com.i_zj.udrive_az.model.CreateOderBean;
import cn.com.i_zj.udrive_az.model.DoorBean;
import cn.com.i_zj.udrive_az.model.GeoCoordinate;
import cn.com.i_zj.udrive_az.model.GetReservation;
import cn.com.i_zj.udrive_az.model.OrderDetailResult;
import cn.com.i_zj.udrive_az.model.ParksResult;
import cn.com.i_zj.udrive_az.model.UnFinishOrderResult;
import cn.com.i_zj.udrive_az.model.ret.RetParkObj;
import cn.com.i_zj.udrive_az.network.UdriveRestClient;
import cn.com.i_zj.udrive_az.utils.CarTypeImageUtils;
import cn.com.i_zj.udrive_az.utils.GetCenterPointFromListOfCoordinates;
import cn.com.i_zj.udrive_az.utils.SizeUtils;
import cn.com.i_zj.udrive_az.utils.StringUtils;
import cn.com.i_zj.udrive_az.utils.ToastUtil;
import cn.com.i_zj.udrive_az.utils.ToolsUtils;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import pub.devrel.easypermissions.EasyPermissions;

import static cn.com.i_zj.udrive_az.utils.GetCenterPointFromListOfCoordinates.getCenterPoint400;

/**
 * 等待用车---行程中
 */
public class ReserveActivity extends DBSBaseActivity implements AMapLocationListener, RouteSearch.OnRouteSearchListener {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_canel)
    TextView tvCanel;
    @BindView(R.id.rl_dengdai)
    RelativeLayout rlDengdai;
    @BindView(R.id.rl_xingzhengzhong)
    RelativeLayout rlXingzhengzhong;
    @BindView(R.id.btn_yuding)
    Button btnYuding;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_qidian)
    TextView tv_qidian;
    @BindView(R.id.tv_zhongdian)
    TextView tv_zhongdian;
    @BindView(R.id.tv_carnum)
    TextView tvCarnum;
    @BindView(R.id.tv_color)
    TextView tvColor;
    @BindView(R.id.tv_color1)
    TextView tvColor1;
    @BindView(R.id.tv_xinghao)
    TextView tvXinghao;
    @BindView(R.id.tv_brand2)
    TextView tvXinghao2;
    @BindView(R.id.tv_time)
    TextView tv_time;
    @BindView(R.id.tv_gonglishu)
    TextView tvgonglishu;
    @BindView(R.id.rl_suoding)
    RelativeLayout rlSuoche;
    @BindView(R.id.rl_kaisuo)
    RelativeLayout rlKaiSuo;
    @BindView(R.id.tv_carnum2)
    TextView tv_carnum2;
    @BindView(R.id.tv_gonglishu1)
    TextView tv_gonglishu1;
    @BindView(R.id.rl_xunche)
    RelativeLayout rlxunche;

    @BindView(R.id.tv_replace_parking)
    TextView mTvReplaceParking;
    @BindView(R.id.iv_kefu)
    ImageView mIvKeFu;
    private MapView mMapView;
    @BindView(R.id.tv_pname)
    TextView mTvPname;
    @BindView(R.id.tv_genghuan)
    TextView tvGenHuan;
    @BindView(R.id.iv_car)
    ImageView mIvCar;
    @BindView(R.id.iv_car1)
    ImageView mIvCar1;

    private AMap mAmap;
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    private boolean isFirstLoc = true;
    private int state = 0;
    //从预约界面进来的值
    private CarInfoResult.DataBean bunldBean;
    private ParksResult.DataBean parkBean;
    //预约信息bean
    private GetReservation reservationBean;
    //行程中bean
    private UnFinishOrderResult orderResultBean;
    //TODO 为啥要全局变量
    private ArrayList<ParksResult.DataBean> dataBeans = new ArrayList<>();
    private double startLatitude;
    private double startLongitude;
    private CarInfoEntity car;
    private ParksResult.DataBean toPark;
    private ParksResult.DataBean fromPark;
    private int minute = 15;//这是分钟
    private int second = 0;//这是分钟后面的秒数。这里是以30分钟为例的，所以，minute是30，second是0
    private Timer timer;
    private TimerTask timerTask;
    private int btnState = 0;
    String endParkId;
    private long startTime = 0;

    String carId;
    String oderId;
    String orderNum;

    LatLonPoint latLonPoint1;
    LatLonPoint latLonPoint2;
    String yuyeID;
    //判断从那个界面进来
    String type;
    private RouteSearch mRouteSearch;
    private CountDownTimer countDownTimer;


    private void startTimerCount() {
        if (null != countDownTimer) {
            countDownTimer.cancel();
        }
        long time = System.currentTimeMillis() - startTime;
        if (time > 1000 * 60 * 15) {
            ToastUtils.showShort("预约结束");
            finish();
            return;
        }
        countDownTimer = new CountDownTimer(1000 * 60 * 4 - time, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (null != tv_time) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");//12小时制
                    Date date = new Date();
                    date.setTime(millisUntilFinished);
                    tv_time.setText(simpleDateFormat.format(date));
                }
            }

            @Override
            public void onFinish() {
                if (null != tv_time) {
                    finish();// 预约时间结束
                }
            }
        };
        countDownTimer.start();

    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_reserve;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapUtils.statusBarColor(this);
        setContentView(R.layout.activity_reserve);
        initViews();
        initViewstMap(savedInstanceState);
        initEvent();
        if (!EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            EasyPermissions.requestPermissions(this, "您必须授予我们定位权限才可以正常使用", 101, Manifest.permission.ACCESS_FINE_LOCATION);
        }
        tv_time.setText("00:" + minute + ":" + second);


        fetchParks();
    }

    private void initViews() {
        mMapView = findViewById(R.id.map);
        final Intent intent = getIntent();
        if (intent != null) {
            type = intent.getStringExtra("type");
            if (type.equals("1")) {// 预约
                bunldBean = (CarInfoResult.DataBean) intent.getSerializableExtra("bunld");

                parkBean = (ParksResult.DataBean) intent.getSerializableExtra("bunldPark");
                yuyeID = intent.getStringExtra("id");
                if (bunldBean != null && parkBean != null) {
                    tvCarnum.setText(bunldBean.getPlateNumber() + "");
                    tvColor.setText(bunldBean.getCarColor());
                    LogUtils.e(bunldBean.getCarColor());
                    tvColor1.setText(bunldBean.getCarColor());
                    tvXinghao.setText(bunldBean.getBrand());
                    tvXinghao2.setText(bunldBean.getBrand());
                    startLatitude = parkBean.getLatitude();
                    startLongitude = parkBean.getLongitude();
                    tv_qidian.setText(parkBean.getName());
                    tv_carnum2.setText(bunldBean.getPlateNumber() + "");
                    startTime = System.currentTimeMillis();
                    latLonPoint1 = new LatLonPoint(startLatitude, startLongitude);
                    fromPark = new ParksResult.DataBean();
                    fromPark.setLongitude(startLongitude);
                    fromPark.setLatitude(startLatitude);
                    fromPark.setName(parkBean.getName());
                    tvgonglishu.setText(bunldBean.getMaxDistance() + "km");
                    Glide.with(ReserveActivity.this).load(CarTypeImageUtils.getCarImageByBrand(bunldBean.getBrand(), bunldBean.getCarColor())).into(mIvCar);
                    startTimerCount();
                }

            } else if (type.equals("2")) {// 重新进入APP 进入预约界面
                reservationBean = (GetReservation) intent.getSerializableExtra("bunld");
                LogUtils.e("===========>" + reservationBean.getData().getCreateTime());
                yuyeID = intent.getStringExtra("id");
                if (reservationBean != null) {

                    tv_qidian.setText(reservationBean.getData().getName());
                    tvCarnum.setText(reservationBean.getData().getPlateNumber());
                    tvColor.setText(reservationBean.getData().getCarColor());
                    tvXinghao.setText(reservationBean.getData().getBrand());
                    startLatitude = reservationBean.getData().getLatitude();
                    startLongitude = reservationBean.getData().getLongitude();
                    tvColor1.setText(reservationBean.getData().getCarColor());
                    tvXinghao2.setText(reservationBean.getData().getBrand());
                    tv_carnum2.setText(reservationBean.getData().getPlateNumber());
                    startTime = reservationBean.getData().getCreateTime();
                    latLonPoint1 = new LatLonPoint(startLatitude, startLongitude);
                    Glide.with(ReserveActivity.this).load(CarTypeImageUtils.getCarImageByBrand(reservationBean.getData().getBrand(), reservationBean.getData().getCarColor())).into(mIvCar);
                    tvgonglishu.setText(reservationBean.getData().getRemainderRange() + "km");
                    startTimerCount();
                }
            } else if (type.equals("3")) {// 行程中
                orderResultBean = (UnFinishOrderResult) intent.getSerializableExtra("bunld");
                if (orderResultBean != null) {
                    state = 1;
                    ivBack.setVisibility(View.INVISIBLE);
                    rlDengdai.setVisibility(View.GONE);
                    rlXingzhengzhong.setVisibility(View.VISIBLE);
                    tvTitle.setText("行程中");
                    tvCanel.setVisibility(View.GONE);
                    btnYuding.setText("结束行程");
                    mTvReplaceParking.setVisibility(View.GONE);//  可以更换地址
                    btnState = 1;
                    carId = String.valueOf(orderResultBean.getData().getCarId());
                    oderId = String.valueOf(orderResultBean.getData().getId());
                    orderNum = String.valueOf(orderResultBean.getData().getNumber());
                    if (orderResultBean.getData().getFromPark() != null) {
                        fromPark = orderResultBean.getData().getFromPark();
                        latLonPoint1 = new LatLonPoint(fromPark.getLatitude(), fromPark.getLongitude());
                        startLatitude = fromPark.getLatitude();
                        startLongitude = fromPark.getLongitude();
                    }
                    if (orderResultBean.getData().getToPark() != null) {
                        toPark = orderResultBean.getData().getToPark();

                        mTvPname.setText(toPark.getName());
                        latLonPoint2 = new LatLonPoint(toPark.getLatitude(), toPark.getLongitude());
                    }
                    if (orderResultBean.getData().getCar() != null) {
                        car = orderResultBean.getData().getCar();
                        tvXinghao2.setText(car.getBrand());
                        tv_carnum2.setText(car.getPlateNumber());
                        tvColor1.setText(car.getCarColor());
                        tv_gonglishu1.setText(car.getMaxDistance() + "km");
                        Glide.with(ReserveActivity.this).load(CarTypeImageUtils.getCarImageByBrand(car.getBrand(), car.getCarColor())).into(mIvCar1);
                    }

                }

            }

        }


    }

    private void initEvent() {
        tv_zhongdian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(ReserveActivity.this, ChoosStartEndActivity.class);
                intent1.putExtra("startLatitude", startLatitude);
                intent1.putExtra("startLongitude", startLongitude);
                intent1.putExtra("parkName", tv_qidian.getText().toString().trim());

                startActivityForResult(intent1, 101);
            }
        });
        //更换地址提示
        tvGenHuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(ReserveActivity.this, ChoosStartEndActivity.class);
                intent1.putExtra("startLatitude", startLatitude);
                intent1.putExtra("startLongitude", startLongitude);
                if (fromPark != null) {
                    intent1.putExtra("parkName", fromPark.getName());
                }

                startActivityForResult(intent1, 101);
            }
        });
        mIvKeFu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                Uri data = Uri.parse("tel:" + getResources().getString(R.string.about_phone));
                intent.setData(data);
                startActivity(intent);
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
//    MapUtils.setMapCustomStyleFile(this, mAmap);
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(this);
        //启动定位
        mLocationClient.startLocation();

        //测试marker
        mAmap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {


                for (int i = 0; i < dataBeans.size(); i++) {
                    if (dataBeans.get(i).getId() == Integer.parseInt(marker.getObject().toString())) {// 选择的终点停车场位置
                        latLonPoint2 = new LatLonPoint(dataBeans.get(i).getLatitude(), dataBeans.get(i).getLongitude());
                        toPark = dataBeans.get(i);
                        endParkId = toPark.getId() + "";
                        if (btnState == 0) {
                            tv_zhongdian.setText(dataBeans.get(i).getName());
                            tv_zhongdian.setTextColor(getResources().getColor(R.color.color_map_btn));
                        } else {// 行程中

                            updateDestinationPark();
                        }

                    }
                }
                if (latLonPoint2 != null && latLonPoint1 != null) {
                    mRouteSearch = new RouteSearch(ReserveActivity.this);
                    mRouteSearch.setRouteSearchListener(ReserveActivity.this);
                    final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
                            latLonPoint2, latLonPoint1);
                    RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo, RouteSearch.DrivingDefault, null,
                            null, "");// 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
                    mRouteSearch.calculateDriveRouteAsyn(query);// 异步路径规划驾车模式查询
//                    mAmap.moveCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(
//                            new LatLng(latLonPoint1.getLatitude(), latLonPoint1.getLongitude()), new LatLng(latLonPoint2.getLatitude(), latLonPoint2.getLongitude())), 20));
                }
                return true;
            }
        });

        btnYuding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (btnState == 0) {// 开始用车
                    if (tv_zhongdian.getText().toString().equals("") || tv_zhongdian.getText().toString().equals("输入目的地选择停车场")) {
                        ToastUtil.show(ReserveActivity.this, "请先选择目的地车场");
                        return;
                    }
                    new AlertDialog.Builder(ReserveActivity.this)
                            .setTitle("不计免赔")
                            .setMessage("出现车辆故障或者碰撞免赔偿")
                            .setNegativeButton("不需要", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    createOder("0");
                                }
                            })
                            .setPositiveButton("购买", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    createOder("1");
                                }
                            })
                            .create().show();
                } else {// 结束用车

                    new AlertDialog.Builder(ReserveActivity.this)
                            .setMessage("确认要结束此次行车？")
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finishOder();
                                }
                            })
                            .create().show();

                }


            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state == 1) {
//                    state = 0;
//                    rlDengdai.setVisibility(View.VISIBLE);
//                    rlXingzhengzhong.setVisibility(View.GONE);
//                    tvTitle.setText("等待用车");
//                    tvCanel.setVisibility(View.VISIBLE);
//                    btnYuding.setText("开始用车");
                    ToastUtils.showShort("您还有未完成的订单");
                } else {
                    cancelReservationDialog();

                }
            }
        });
        tvCanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelReservationDialog();
            }
        });
        rlKaiSuo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opencloseDoor("0");
            }
        });
        rlSuoche.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opencloseDoor("1");

            }
        });
        rlxunche.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opencloseDoor("2");
            }
        });
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        if (state == 0) {
            cancelReservationDialog();
        } else {
            ToastUtils.showShort("您还有未完成的订单");
        }

    }

    private void cancelReservationDialog() {
        new AlertDialog.Builder(ReserveActivity.this)
                .setTitle("取消预定")
                .setMessage("确定要取消预定么？")
                .setNegativeButton("先不取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("取消预定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        cancelReservation();
                    }
                })
                .create().show();
    }

    private void cancelReservation() {
        Map<String, String> map = new HashMap<>();
        map.put("reservationId", yuyeID.trim());

        String token = SessionManager.getInstance().getAuthorization();
        LogUtils.e(yuyeID + token);
        showProgressDialog("正在取消");
        UdriveRestClient.getClentInstance().cancelReservation(token, map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DoorBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(DoorBean bean) {
                        dissmisProgressDialog();
                        if (bean != null) {
                            if (bean.getCode() == 1) {
                                ToastUtils.showShort("取消订单成功");
                                finish();
                            } else {
                                ToastUtils.showShort(bean.getMessage());
                            }
                        } else {
                            ToastUtils.showShort("取消订单失败");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        dissmisProgressDialog();
                        ToastUtils.showShort("取消订单失败");
                    }

                    @Override
                    public void onComplete() {
                        dissmisProgressDialog();
                    }
                });
    }

    /**
     * 开始用车
     *
     * @param status 0.不需要不计免赔,1.需要不计免赔
     */
    private void createOder(String status) {
        Map<String, String> map = new HashMap<>();
        map.put("destinationParkId", endParkId);
        map.put("deductibleStatus", status);
        String token = SessionManager.getInstance().getAuthorization();
        LogUtils.e(endParkId + "--" + status);
        showProgressDialog("开始用车");
        UdriveRestClient.getClentInstance().createOder(token, map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CreateOderBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(CreateOderBean bean) {
                        dissmisProgressDialog();
                        if (bean != null) {
                            if (bean.getCode() == 1) {
                                state = 1;
                                rlDengdai.setVisibility(View.GONE);
                                rlXingzhengzhong.setVisibility(View.VISIBLE);
                                tvTitle.setText("行程中");
                                tvCanel.setVisibility(View.GONE);
                                btnYuding.setText("结束行程");
                                btnState = 1;
                                carId = String.valueOf(bean.getData().getCarId());
                                oderId = String.valueOf(bean.getData().getId());
                                orderNum = String.valueOf(bean.getData().getNumber());
                                ivBack.setVisibility(View.INVISIBLE);
                                if ("1".equals(type)) {
                                    tv_gonglishu1.setText(bunldBean.getMaxDistance() + "km");
                                    Glide.with(ReserveActivity.this).load(CarTypeImageUtils.getCarImageByBrand(bunldBean.getBrand(), bunldBean.getCarColor())).into(mIvCar1);
                                } else if ("2".equals(type)) {
                                    tv_gonglishu1.setText(reservationBean.getData().getRemainderRange() + "km");
                                    Glide.with(ReserveActivity.this).load(CarTypeImageUtils.getCarImageByBrand(reservationBean.getData().getBrand(), reservationBean.getData().getCarColor())).into(mIvCar1);
                                }
                                if (toPark != null) {
                                    mTvPname.setText(toPark.getName());
                                }

                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        dissmisProgressDialog();
                    }

                    @Override
                    public void onComplete() {
                        dissmisProgressDialog();
                    }
                });
    }

    private void opencloseDoor(String status) {
        Map<String, String> map = new HashMap<>();
        map.put("orderId", oderId);
        map.put("carId", carId);
        String token = SessionManager.getInstance().getAuthorization();
        LogUtils.e(oderId + "--" + carId);

        if (status.equals("0")) {
            showProgressDialog("正在开锁");
            UdriveRestClient.getClentInstance().openCar(token, map)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<DoorBean>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onNext(DoorBean bean) {
                            dissmisProgressDialog();
                            if (bean != null) {
                                if (bean.getCode() == 1) {
                                    ToastUtils.showShort("开锁成功");
                                } else {
                                    ToastUtils.showShort(bean.getMessage());
                                }
                            } else {
                                ToastUtils.showShort("开锁失败");
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            dissmisProgressDialog();
                            ToastUtils.showShort("开锁失败");
                        }

                        @Override
                        public void onComplete() {
                            dissmisProgressDialog();
                        }
                    });
        } else if (status.equals("1")) {
            showProgressDialog("正在锁车");
            UdriveRestClient.getClentInstance().lockCar(token, map)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<DoorBean>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onNext(DoorBean bean) {
                            dissmisProgressDialog();
                            if (bean != null) {
                                if (bean.getCode() == 1) {
                                    ToastUtils.showShort("锁车成功");
                                } else {
                                    ToastUtils.showShort(bean.getMessage());
                                }
                            } else {
                                ToastUtils.showShort("锁车失败");
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            dissmisProgressDialog();
                            ToastUtils.showShort("锁车失败");
                        }

                        @Override
                        public void onComplete() {
                            dissmisProgressDialog();
                        }
                    });
        } else {
            showProgressDialog("正在寻车");
            UdriveRestClient.getClentInstance().searchCarBySound(token, map)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<DoorBean>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onNext(DoorBean bean) {
                            dissmisProgressDialog();
                            if (bean != null) {
                                if (bean.getCode() == 1) {
                                    ToastUtils.showShort("寻车成功");
                                } else {
                                    ToastUtils.showShort(bean.getMessage());
                                }
                            } else {
                                ToastUtils.showShort("寻车失败");
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            dissmisProgressDialog();
                            ToastUtils.showShort("寻车失败");
                        }

                        @Override
                        public void onComplete() {
                            dissmisProgressDialog();
                        }
                    });
        }

    }

    private void finishOder() {
        showProgressDialog("正在还车");
        String token = SessionManager.getInstance().getAuthorization();
        UdriveRestClient.getClentInstance().completeTripOrder(token, orderNum)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<OrderDetailResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(OrderDetailResult bean) {
                        dissmisProgressDialog();
                        if (bean != null) {
                            if (bean.code == 1) {
                                ToastUtils.showShort("还车成功");
                                Intent intent1 = new Intent(ReserveActivity.this, ActConfirmOrder.class);
                                intent1.putExtra(PaymentActivity.ORDER_NUMBER, orderNum);
                                startActivity(intent1);
                                finish();
                            } else if (bean.code == 1002) {
                                ToastUtils.showShort(bean.message);
                            } else {
                                ToastUtils.showShort(bean.message);
                            }
                        } else {
                            ToastUtils.showShort("还车失败");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        dissmisProgressDialog();
                        e.printStackTrace();
                        ToastUtils.showShort("还车失败了");
                    }

                    @Override
                    public void onComplete() {
                        dissmisProgressDialog();
                    }
                });
    }

    /**
     * 获取停车场信息
     */
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
                        for (int i = 0; i < dataBeans.size(); i++) {
                            MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(dataBeans.get(i).getLatitude(), dataBeans.get(i).getLongitude()));
                            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(getMyBitmap(R.mipmap.ic_cheweishu_llinshi, "P")));
                            Marker marker = mAmap.addMarker(markerOptions);
                            marker.setObject(dataBeans.get(i).getId());
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

    private void updateDestinationPark() {
        Map<String, Object> map = new HashMap<>();
        map.put("orderNum", orderNum);
        map.put("destinationParkId", toPark.getId() + "");
        showProgressDialog("正在更换还车点");
        String token = SessionManager.getInstance().getAuthorization();
        UdriveRestClient.getClentInstance().updateDestinationPark(token, map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RetParkObj>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(RetParkObj retParkObj) {
                        dissmisProgressDialog();
                        if (retParkObj.getCode() == 1) {
                            toPark = retParkObj.getDate();
                            if (toPark != null) {
                                mTvPname.setText(toPark.getName());
                            }
                        }
                        ToastUtils.showShort(retParkObj.getMessage());
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        dissmisProgressDialog();
                        ToastUtils.showShort("更新换车点失败了");
                    }

                    @Override
                    public void onComplete() {
                        dissmisProgressDialog();
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
                myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
                mAmap.setMyLocationStyle(myLocationStyle);
                mAmap.setMyLocationEnabled(true);
                //去掉放大缩小 增加回到当前位置
                UiSettings uiSettings = mAmap.getUiSettings();
                uiSettings.setZoomControlsEnabled(false);

//                startLongitude = aMapLocation.getLongitude();
//                startLatitude = aMapLocation.getLatitude();
                mLocationClient.stopLocation();
                mAmap.moveCamera(CameraUpdateFactory.zoomTo(11));
                //将地图移动到定位点
                mAmap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude())));
                //----------什么鬼意思
                if (type.equals("1")) {
                    float distance = AMapUtils.calculateLineDistance(
                            new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude()),
                            new LatLng(parkBean.getLatitude(), parkBean.getLongitude())
                    );
                    int dis = (int) (distance / 1000);
//                    tvgonglishu.setText(dis + "km");
//                    tv_gonglishu1.setText(dis + "/km");
                } else if (type.equals("1")) {
                    float distance = AMapUtils.calculateLineDistance(
                            new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude()),
                            new LatLng(reservationBean.getData().getLatitude(), reservationBean.getData().getLongitude())
                    );
                    int dis = (int) (distance / 1000);
//                    tvgonglishu.setText(dis + "km");
//                    tv_gonglishu1.setText(dis + "/km");
                }

                isFirstLoc = false;
            }
        }
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
        textPaint.setTextSize(SizeUtils.sp2px(this, 16));
        textPaint.setColor(getResources().getColor(R.color.white));
        canvas.translate(canvas.getWidth() / 2, canvas.getHeight() / 2);
        canvas.drawLine(canvas.getWidth() / 2, 0, canvas.getWidth() / 2, 0, textPaint);
        float baseLineY = Math.abs(textPaint.ascent() + textPaint.descent()) / 2;
        float textWidth = textPaint.measureText(pm_val);
        canvas.drawText(pm_val, -textWidth / 2, baseLineY - 5, textPaint);// 设置bitmap上面的文字位置
        return bitmap;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mLocationClient.stopLocation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
        mLocationClient.onDestroy();

        if (null != countDownTimer) {
            countDownTimer.cancel();
        }
//        if (timer != null) {
//            timer.cancel();
//            timer = null;
//        }
//        if (timerTask != null) {
//            timerTask = null;
//        }
//        minute = -1;
//        second = -1;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 101) {
            String name = data.getStringExtra("name");
            Double endlong = data.getDoubleExtra("endLong", 0);
            Double endlatin = data.getDoubleExtra("endLatin", 0);
            endParkId = data.getStringExtra("id");

            if (toPark == null) {
                toPark = new ParksResult.DataBean();

            }
            toPark.setId(Integer.valueOf(endParkId));
            toPark.setName(name);
            toPark.setLatitude(endlatin);
            toPark.setLongitude(endlong);
            if (btnState == 0) {
                tv_zhongdian.setText(name);
                tv_zhongdian.setTextColor(getResources().getColor(R.color.color_map_btn));
            } else {
                updateDestinationPark();
            }


            latLonPoint1 = new LatLonPoint(startLatitude, startLongitude);
            latLonPoint2 = new LatLonPoint(endlatin, endlong);
            mRouteSearch = new RouteSearch(this);
            mRouteSearch.setRouteSearchListener(this);

            RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(
                    new RouteSearch.FromAndTo(latLonPoint2, latLonPoint1), RouteSearch.DrivingDefault, null, null, "");
            mRouteSearch.calculateDriveRouteAsyn(query);
//            mAmap.moveCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(
//                    new LatLng(startLatitude, startLongitude), new LatLng(endlatin, endlong)), 20));

        }
    }

    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int errorCode) {
        if (errorCode == 1000) {
            mAmap.clear();
            DrivePath drivePath = driveRouteResult.getPaths().get(0);
            NewDrivingRouteOverlay drivingRouteOverlay = new NewDrivingRouteOverlay(this, mAmap, drivePath, driveRouteResult.getStartPos(),
                    driveRouteResult.getTargetPos(), null);
            drivingRouteOverlay.setNodeIconVisibility(false);//设置节点marker是否显示
            drivingRouteOverlay.addToMap();
            drivingRouteOverlay.zoomToSpan();

//            mAmap.moveCamera((CameraUpdateFactory.zoomTo(11)) );
//            mAmap.animateCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(
//                    new LatLng(latLonPoint1.getLatitude(), latLonPoint1.getLongitude()),
//                    new LatLng(latLonPoint2.getLatitude(), latLonPoint2.getLongitude())), 11));

            for (int i = 0; i < dataBeans.size(); i++) {
                MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(dataBeans.get(i).getLatitude(), dataBeans.get(i).getLongitude()));

                if ((latLonPoint1.getLatitude() == dataBeans.get(i).getLatitude()) && (latLonPoint1.getLongitude() == dataBeans.get(i).getLongitude())) {// 开始点
                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(getMyBitmap(R.mipmap.ic_cheweishu_llinshi, "始")));
                } else if ((latLonPoint2.getLatitude() == dataBeans.get(i).getLatitude()) && (latLonPoint2.getLongitude() == dataBeans.get(i).getLongitude())) {// 结束点
                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(getMyBitmap(R.mipmap.ic_cheweishu_llinshi, "终")));
                } else {
                    String name = dataBeans.get(i).getName();
                    if (!StringUtils.isEmpty(name) && name.contains("临时")) {
                        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(getMyBitmap(R.mipmap.ic_cheweishu_monthly, "P")));
                    } else {
                        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(getMyBitmap(R.mipmap.ic_cheweishu_llinshi, "P")));
                    }

                }
                Marker marker = mAmap.addMarker(markerOptions);
                marker.setObject(dataBeans.get(i).getId());

            }

            LatLngBounds.Builder bounds = new LatLngBounds.Builder();
            bounds.include(new LatLng(latLonPoint1.getLatitude(), latLonPoint1.getLongitude()));
            bounds.include(new LatLng(latLonPoint2.getLatitude(), latLonPoint2.getLongitude()));
            int bottom = ToolsUtils.getWindowHeight(ReserveActivity.this) - ToolsUtils.getWindowHeight(ReserveActivity.this) / 3 - SizeUtils.dp2px(ReserveActivity.this, 48);
            mAmap.moveCamera(CameraUpdateFactory.newLatLngBoundsRect(bounds.build(), 0, 0, SizeUtils.dp2px(ReserveActivity.this, 48), bottom));
        } else {
//            ToastUtils.showShort("线路规划失败");
        }

    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

    }

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

    }
}
