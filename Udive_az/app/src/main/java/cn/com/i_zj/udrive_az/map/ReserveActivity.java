package cn.com.i_zj.udrive_az.map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.com.i_zj.udrive_az.BaseActivity;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.login.SessionManager;
import cn.com.i_zj.udrive_az.map.adapter.ChoosStartEndActivity;
import cn.com.i_zj.udrive_az.model.CarInfoResult;
import cn.com.i_zj.udrive_az.model.CreateOderBean;
import cn.com.i_zj.udrive_az.model.DoorBean;
import cn.com.i_zj.udrive_az.model.GetReservation;
import cn.com.i_zj.udrive_az.model.ParksResult;
import cn.com.i_zj.udrive_az.model.UnFinishOrderResult;
import cn.com.i_zj.udrive_az.network.UdriveRestClient;
import cn.com.i_zj.udrive_az.overlay.DrivingRouteOverlay;
import cn.com.i_zj.udrive_az.utils.ToastUtil;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import pub.devrel.easypermissions.EasyPermissions;

public class ReserveActivity extends BaseActivity implements AMapLocationListener,RouteSearch.OnRouteSearchListener {
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
  private MapView mMapView;
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

  private double startLatitude;
  private double startLongitude;

  private int minute = 15;//这是分钟
  private int second = 0;//这是分钟后面的秒数。这里是以30分钟为例的，所以，minute是30，second是0
  private Timer timer;
  private TimerTask timerTask;
  private int btnState=0;
  String endParkId;

  String carId;
  String oderId;
  String orderNum;
  private RouteSearch routeSearch;

  LatLonPoint latLonPoint1;
  LatLonPoint latLonPoint2;
  String yuyeID;
  //判断从那个界面进来
  String type;
  //这是接收回来处理的消息
  @SuppressLint("HandlerLeak")
  private Handler handler = new Handler() {
    public void handleMessage(Message msg) {
      if (minute == 0) {
        if (second == 0) {
          tv_time.setText("Time out !");
          if (timer != null) {
            timer.cancel();
            timer = null;
          }
          if (timerTask != null) {
            timerTask = null;
          }
        } else {
          second--;
          if (second >= 10) {
            tv_time.setText("00:"+minute + ":" + second);
          } else {
            tv_time.setText("00:"+minute + ":0" + second);
          }
        }
      } else {
        if (second == 0) {
          second = 59;
          minute--;
          if (minute >= 10) {
            tv_time.setText("00:"+minute + ":" + second);
          } else {
            tv_time.setText("00:"+minute + ":" + second);
          }
        } else {
          second--;
          if (second >= 10) {
            if (minute >= 10) {
              tv_time.setText("00:"+minute + ":" + second);
            } else {
              tv_time.setText("00:"+minute + ":" + second);
            }
          } else {
            if (minute >= 10) {
              tv_time.setText("00:"+minute + ":0" + second);
            } else {
              tv_time.setText("00:"+minute + ":0" + second);
            }
          }
        }
      }
    }

  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    MapUtils.statusBarColor(this);
    setContentView(R.layout.activity_reserve);
    ButterKnife.bind(this);
    initViews();
    initViewstMap(savedInstanceState);
    if (!EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
      EasyPermissions.requestPermissions(this, "您必须授予我们定位权限才可以正常使用", 101, Manifest.permission.ACCESS_FINE_LOCATION);
    }
    tv_time.setText("00:"+minute + ":" + second);

    timerTask = new TimerTask() {

      @Override
      public void run() {
        Message msg = new Message();
        msg.what = 0;
        handler.sendMessage(msg);
      }
    };

    timer = new Timer();
    timer.schedule(timerTask, 0, 1000);

  }

  private void initViews() {
    mMapView = findViewById(R.id.map);
    final Intent intent = getIntent();
    if (intent != null) {
        type =intent.getStringExtra("type");
        if(type.equals("1")){
            bunldBean = (CarInfoResult.DataBean) intent.getSerializableExtra("bunld");

            parkBean= (ParksResult.DataBean) intent.getSerializableExtra("bunldPark");
            yuyeID=intent.getStringExtra("id");
            if(bunldBean!=null&&parkBean!=null){
                tv_qidian.setText(bunldBean.getParkName());
                tvCarnum.setText(bunldBean.getPlateNumber());
                tvColor.setText(bunldBean.getCarColor());
                LogUtils.e(bunldBean.getCarColor());
                tvColor1.setText(bunldBean.getCarColor());
                tvXinghao.setText(bunldBean.getBrand());
                tvXinghao2.setText(bunldBean.getBrand());
                startLatitude=parkBean.getLatitude();
                startLongitude=parkBean.getLongitude();
                tv_qidian.setText(parkBean.getName());
                tv_carnum2.setText(bunldBean.getPlateNumber());
            }

        }else if(type.equals("2")){
            reservationBean= (GetReservation) intent.getSerializableExtra("bunld");
            if(reservationBean!=null){
                tv_qidian.setText(reservationBean.getData().getName());
                tvCarnum.setText(reservationBean.getData().getPlateNumber());
                tvColor.setText(reservationBean.getData().getCarColor());
                tvXinghao.setText(reservationBean.getData().getBrand());
                startLatitude=reservationBean.getData().getLatitude();
                startLongitude=reservationBean.getData().getLongitude();
                tvColor1.setText(reservationBean.getData().getCarColor());
                tvXinghao2.setText(reservationBean.getData().getBrand());
                tv_carnum2.setText(reservationBean.getData().getPlateNumber());
            }
        }else if(type.equals("3")){
            orderResultBean= (UnFinishOrderResult) intent.getSerializableExtra("bunld");
            if(orderResultBean!=null){
                state = 1;
                rlDengdai.setVisibility(View.GONE);
                rlXingzhengzhong.setVisibility(View.VISIBLE);
                tvTitle.setText("行程中");
                tvCanel.setVisibility(View.GONE);
                btnYuding.setText("结束行程");
                btnState=1;
                carId=String.valueOf(orderResultBean.getData().getCarId());
                oderId=String.valueOf(orderResultBean.getData().getId());
                orderNum=String.valueOf(orderResultBean.getData().getNumber());

            }

        }

    }
    tv_zhongdian.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent1=new Intent(ReserveActivity.this,ChoosStartEndActivity.class);
        intent1.putExtra("startLatitude",startLatitude);
        intent1.putExtra("startLongitude",startLongitude);
        startActivityForResult(intent1,101);
      }
    });
  }

  //初始化地图
  private void initViewstMap(Bundle savedInstanceState) {
    mMapView.onCreate(savedInstanceState);// 此方法必须重写
    mAmap = mMapView.getMap();
//    MapUtils.setMapCustomStyleFile(this, mAmap);
    //初始化定位
    mLocationClient = new AMapLocationClient(getApplicationContext());
    //设置定位回调监听
    mLocationClient.setLocationListener(this);
    //启动定位
    mLocationClient.startLocation();
    //测试marker

    btnYuding.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
          if(tv_zhongdian.getText().toString().equals("")||tv_zhongdian.getText().toString().equals("输入目的地选择停车场")){
              ToastUtil.show(ReserveActivity.this,"请先选择目的地车场");
              return;
          }
        if(btnState==0){
          new AlertDialog.Builder(ReserveActivity.this)
                  .setTitle("不计免赔")
                  .setMessage("出现车辆故障或者碰撞免赔偿")
                  .setNegativeButton("不需要", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                      fetchParks("0");
                    }
                  })
                  .setPositiveButton("购买", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                      fetchParks("1");
                    }
                  })
                  .create().show();
        }else {
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
          state = 0;
          rlDengdai.setVisibility(View.VISIBLE);
          rlXingzhengzhong.setVisibility(View.GONE);
          tvTitle.setText("等待用车");
          tvCanel.setVisibility(View.VISIBLE);
          btnYuding.setText("开始用车");
        } else {
          new AlertDialog.Builder(ReserveActivity.this)
                  .setTitle("取消预定")
                  .setMessage("确定要取消预定么？")
                  .setNegativeButton("先不", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                  })
                  .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cancelReservation();
                    }
                  })
                  .create().show();

        }
      }
    });
    tvCanel.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        new AlertDialog.Builder(ReserveActivity.this)
                .setTitle("取消预定")
                .setMessage("确定要取消预定么？")
                .setNegativeButton("先不", new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {

                  }
                })
                .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {

                    cancelReservation();
                  }
                })
                .create().show();
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
        if(state==0){
            new AlertDialog.Builder(ReserveActivity.this)
                    .setTitle("取消预定")
                    .setMessage("确定要取消预定么？")
                    .setNegativeButton("先不", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            cancelReservation();
                        }
                    })
                    .create().show();
        }else {
            ToastUtils.showShort("您还有未完成的订单");
        }

    }

    private void cancelReservation (){
        Map<String,String> map=new HashMap<>();
        map.put("reservationId",yuyeID.trim());

        String token= SessionManager.getInstance().getAuthorization();
        LogUtils.e(yuyeID+token);
    UdriveRestClient.getClentInstance().cancelReservation(token,map)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Observer<DoorBean>() {
              @Override
              public void onSubscribe(Disposable d) {
              }

              @Override
              public void onNext(DoorBean bean) {
                if(bean!=null){
                  if(bean.getCode()==1){
                    ToastUtils.showShort("取消订单成功");
                    finish();
                  }else {
                      ToastUtils.showShort(bean.getMessage());
                  }
                }else {

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
  private void fetchParks(String status) {
    Map<String,String> map=new HashMap<>();
    map.put("destinationParkId",endParkId);
    map.put("deductibleStatus",status);
    String token= SessionManager.getInstance().getAuthorization();
    LogUtils.e(endParkId+"--"+status);
    UdriveRestClient.getClentInstance().createOder(token,map)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Observer<CreateOderBean>() {
              @Override
              public void onSubscribe(Disposable d) {
              }

              @Override
              public void onNext(CreateOderBean bean) {
                    if(bean!=null){
                      if(bean.getCode()==1){
                        state = 1;
                        rlDengdai.setVisibility(View.GONE);
                        rlXingzhengzhong.setVisibility(View.VISIBLE);
                        tvTitle.setText("行程中");
                        tvCanel.setVisibility(View.GONE);
                        btnYuding.setText("结束行程");
                        btnState=1;
                        carId=String.valueOf(bean.getData().getCarId());
                        oderId=String.valueOf(bean.getData().getId());
                        orderNum=String.valueOf(bean.getData().getNumber());
                      }
                    }else {

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
  private void opencloseDoor(String status) {
    Map<String,String> map=new HashMap<>();
    map.put("orderId",oderId);
    map.put("carId",carId);
    String token= SessionManager.getInstance().getAuthorization();
    LogUtils.e(oderId+"--"+carId);
    if(status.equals("0")){
      UdriveRestClient.getClentInstance().openCar(token,map)
              .subscribeOn(Schedulers.io())
              .observeOn(AndroidSchedulers.mainThread())
              .subscribe(new Observer<DoorBean>() {
                @Override
                public void onSubscribe(Disposable d) {
                }

                @Override
                public void onNext(DoorBean bean) {
                  if(bean!=null){
                    if(bean.getCode()==1){
                      ToastUtils.showShort("开锁成功");
                    }
                  }else {

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
    }else if(status.equals("1")) {
      UdriveRestClient.getClentInstance().lockCar(token,map)
              .subscribeOn(Schedulers.io())
              .observeOn(AndroidSchedulers.mainThread())
              .subscribe(new Observer<DoorBean>() {
                @Override
                public void onSubscribe(Disposable d) {
                }

                @Override
                public void onNext(DoorBean bean) {
                  if(bean!=null){
                    if(bean.getCode()==1){
                      ToastUtils.showShort("锁车成功");
                    }
                  }else {

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
    }else {
        UdriveRestClient.getClentInstance().searchCarBySound(token,map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DoorBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(DoorBean bean) {
                        if(bean!=null){
                            if(bean.getCode()==1){
                                ToastUtils.showShort("寻车成功");
                            }
                        }else {

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

  }
  private void finishOder() {
    String token= SessionManager.getInstance().getAuthorization();
    UdriveRestClient.getClentInstance().completeTripOrder(token,orderNum)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Observer<DoorBean>() {
              @Override
              public void onSubscribe(Disposable d) {
              }

              @Override
              public void onNext(DoorBean bean) {
                if(bean!=null){
                  if(bean.getCode()==1){
                    ToastUtils.showShort("还车成功");
                    finish();
                  }else if(bean.getCode()==1002){
                      ToastUtils.showShort(bean.getMessage());
                  }else {
                      ToastUtils.showShort(bean.getMessage());
                  }
                }else {

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

        startLongitude=aMapLocation.getLongitude();
        startLatitude=aMapLocation.getLatitude();
        mLocationClient.stopLocation();
        mAmap.moveCamera(CameraUpdateFactory.zoomTo(17));
        //将地图移动到定位点
        mAmap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude())));

        if(type.equals("1")){
            float distance = AMapUtils.calculateLineDistance(
                    new LatLng(aMapLocation.getLatitude(),aMapLocation.getLongitude()),
                    new LatLng(parkBean.getLatitude(),parkBean.getLongitude())
            );
            int dis= (int) (distance/1000);
            tvgonglishu.setText(dis+"/km");
            tv_gonglishu1.setText(dis+"/km");
        }else if(type.equals("1")){
            float distance = AMapUtils.calculateLineDistance(
                    new LatLng(aMapLocation.getLatitude(),aMapLocation.getLongitude()),
                    new LatLng(reservationBean.getData().getLatitude(),reservationBean.getData().getLongitude())
            );
            int dis= (int) (distance/1000);
            tvgonglishu.setText(dis+"/km");
            tv_gonglishu1.setText(dis+"/km");
        }

        isFirstLoc = false;
      }
    }
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
    if (timer != null) {
      timer.cancel();
      timer = null;
    }
    if (timerTask != null) {
      timerTask = null;
    }
    minute = -1;
    second = -1;
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode==101){
      String name=data.getStringExtra("name");
      Double endlong=data.getDoubleExtra("endLong",0);
      Double endlatin=data.getDoubleExtra("endLatin",0);
      endParkId=data.getStringExtra("id");
      tv_zhongdian.setText(name);
      tv_zhongdian.setTextColor(getResources().getColor(R.color.color_map_btn));


//
//      mAmap.addMarker(new MarkerOptions().position(new LatLng(endlatin,endlong)).title("北京").snippet("DefaultMarker"));
//      //绘制线
//      List<LatLng> latLngs = new ArrayList<LatLng>();
//      latLngs.add(new LatLng(startLatitude,startLongitude));
//      latLngs.add(new LatLng(endlatin,endlong));
//      Polyline polyline =mAmap.addPolyline(new PolylineOptions().
//              addAll(latLngs).width(10).color(getResources().getColor(R.color.blue)));

       latLonPoint1 = new LatLonPoint(startLatitude, startLongitude);
       latLonPoint2 = new LatLonPoint(endlatin, endlong);
      routeSearch = new RouteSearch(this);
      routeSearch.setRouteSearchListener(this);

      RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(
              new RouteSearch.FromAndTo(latLonPoint1,
                      latLonPoint2), RouteSearch.DrivingDefault, null, null, "");
      routeSearch.calculateDriveRouteAsyn(query);
      mAmap.moveCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(
              new LatLng(startLatitude,startLongitude),new LatLng(endlatin, endlong)),20));

    }
  }

  @Override
  public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

  }

  @Override
  public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {
    DrivePath drivePath = driveRouteResult.getPaths().get(0);
    NewDrivingRouteOverlay drivingRouteOverlay = new NewDrivingRouteOverlay(this, mAmap, drivePath, driveRouteResult.getStartPos(),
            driveRouteResult.getTargetPos(),null);
    drivingRouteOverlay.setNodeIconVisibility(false);//隐藏转弯的节点
    drivingRouteOverlay.addToMap();
    drivingRouteOverlay.zoomToSpan();
    mAmap.animateCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(
            new LatLng(latLonPoint1.getLatitude(),latLonPoint1.getLongitude()),
            new LatLng(latLonPoint2.getLatitude(),latLonPoint2.getLongitude())),50));
  }

  @Override
  public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

  }

  @Override
  public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

  }
}
