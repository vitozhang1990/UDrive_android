package cn.com.i_zj.udrive_az.map.adapter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.blankj.utilcode.util.LogUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.map.MapUtils;
import cn.com.i_zj.udrive_az.map.ReserveActivity;
import cn.com.i_zj.udrive_az.model.ParksResult;
import cn.com.i_zj.udrive_az.network.UdriveRestClient;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ChoosStartEndActivity extends AppCompatActivity implements PoiSearch.OnPoiSearchListener {
    private String keyWord = "";// 要输入的poi搜索关键字
    private ProgressDialog progDialog = null;// 搜索时进度条
    private PoiResult poiResult; // poi返回的结果
    private int currentPage = 0;// 当前页面，从0开始计数
    private PoiSearch.Query query;// Poi查询条件类
    private PoiSearch poiSearch;// POI搜索
    private AppCompatEditText searchText;// 输入搜索关键字
    private RecyclerView rv_poi;
    private double startLatitude;
    private double startLongitude;
    private ArrayList<ParksResult.DataBean> dataBeans = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapUtils.statusBarColor(this);
        setContentView(R.layout.activity_choos_start_end);
        fetchParks();
        searchText=findViewById(R.id.tv_zhongdian);
        rv_poi=findViewById(R.id.rv_poi);
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        startLatitude=getIntent().getDoubleExtra("startLatitude",0);
        startLongitude=getIntent().getDoubleExtra("startLongitude",0);
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                keyWord = checkEditText(searchText);
                if(!keyWord.equals("")){
                    doSearchQuery();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }
    /**
     * 开始进行poi搜索
     */
    protected void doSearchQuery() {
        LogUtils.e(keyWord);
        currentPage = 0;
        query = new PoiSearch.Query(keyWord,"","成都");// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(10);// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);// 设置查第一页

        poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();
    }
    @Override
    public void onPoiSearched(PoiResult poiResult, final int i) {
        LogUtils.e(i);
        if(i==1000){
            LogUtils.e("----");
            LogUtils.e(poiResult.getPageCount());
            final ArrayList<PoiItem> poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
            LogUtils.e(poiItems.size());
            RecyclerViewUtils.initLiner(
                    ChoosStartEndActivity.this, rv_poi,
                    R.layout.item_poisearch, poiItems, new OnGlobalListener() {
                        @Override
                        public <T> void logic(BaseViewHolder helper, T item) {
                            PoiItem poiItem= (PoiItem) item;
                            LatLonPoint latLonPoint= poiItem.getLatLonPoint();
                            float distance = AMapUtils.calculateLineDistance(
                                    new LatLng(startLatitude,startLongitude),
                                    new LatLng(latLonPoint.getLatitude(),latLonPoint.getLongitude()));
                            helper.setText(R.id.tv_name,poiItem.getTitle());
                            helper.setText(R.id.tv_adress,poiItem.getAdName());
                            int dis= (int) (distance/1000);
                            helper.setText(R.id.tv_distance,dis+"km");
                        }
                    }, new BaseQuickAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
//
//                            Intent intent=new Intent(ChoosStartEndActivity.this, ReserveActivity.class);
//                            intent.putExtra("name",poiItems.get(position).getTitle());
//                            intent.putExtra("endLong",poiItems.get(position).getLatLonPoint().getLongitude());
//                            intent.putExtra("endLatin",poiItems.get(position).getLatLonPoint().getLatitude());
//                            setResult(101,intent);
//                            finish();
                            initMyParks();
                        }
                    });
        }

    }
    private void initMyParks(){
        RecyclerViewUtils.initLiner(
                ChoosStartEndActivity.this, rv_poi,
                R.layout.item_poisearch, dataBeans, new OnGlobalListener() {
                    @Override
                    public <T> void logic(BaseViewHolder helper, T item) {
                        ParksResult.DataBean poiItem= (ParksResult.DataBean) item;
                        LogUtils.e(poiItem.getName());
                        LatLonPoint latLonPoint= new LatLonPoint(poiItem.getLatitude(),poiItem.getLongitude());
                        float distance = AMapUtils.calculateLineDistance(
                                new LatLng(startLatitude,startLongitude),
                                new LatLng(latLonPoint.getLatitude(),latLonPoint.getLongitude()));
                        LogUtils.e("====="+distance);
                        helper.setText(R.id.tv_name,poiItem.getName());
                        helper.setText(R.id.tv_adress,poiItem.getAddress());
                        int dis= (int) (distance/1000);
                        helper.setText(R.id.tv_distance,dis+"km");
                    }
                }, new BaseQuickAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                        Intent intent=new Intent(ChoosStartEndActivity.this, ReserveActivity.class);
                        intent.putExtra("name",dataBeans.get(position).getName());
                        intent.putExtra("id",String.valueOf(dataBeans.get(position).getId()));
                        intent.putExtra("endLong",dataBeans.get(position).getLongitude());
                        intent.putExtra("endLatin",dataBeans.get(position).getLatitude());
                        setResult(101,intent);
                        finish();
                    }
                });
    }
    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }
    /**
     * 判断edittext是否null
     */
    public static String checkEditText(EditText editText) {
        if (editText != null && editText.getText() != null
                && !(editText.getText().toString().trim().equals(""))) {
            return editText.getText().toString().trim();
        } else {
            return "";
        }
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
