package cn.com.i_zj.udrive_az.refuel;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.i_zj.udrive_az.DBSBaseActivity;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.map.MapUtils;
import cn.com.i_zj.udrive_az.model.OilHistoryEntity;
import cn.com.i_zj.udrive_az.model.ret.BaseRetObj;
import cn.com.i_zj.udrive_az.network.UdriveRestClient;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class RefuelHistoryActivity extends DBSBaseActivity implements OnRefreshListener {

    @BindView(R.id.swipeRefresh)
    SmartRefreshLayout smartRefreshLayout;
    @BindView(R.id.recycler)
    RecyclerView recyclerView;

    private List<OilHistoryEntity> list = new ArrayList<>();
    private RefuelAdapter refuelAdapter;
    private String orderNumber;

    public static void startActivity(Context context, String orderNumber) {
        Intent intent = new Intent(context, RefuelHistoryActivity.class);
        intent.putExtra("orderNumber", orderNumber);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_refuel_history;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapUtils.statusBarColor(this);

        orderNumber = getIntent().getStringExtra("orderNumber");
        if (TextUtils.isEmpty(orderNumber)) {
            showToast("没有传入订单号");
            finish();
        }
        smartRefreshLayout.setOnRefreshListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        refuelAdapter = new RefuelAdapter(list);
        refuelAdapter.bindToRecyclerView(recyclerView);
        refuelAdapter.setEnableLoadMore(false);

        smartRefreshLayout.autoRefresh();
    }

    @OnClick(R.id.iv_back)
    void onClick() {
        finish();
    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        getAllOil();
    }

    private void getAllOil() {
        UdriveRestClient.getClentInstance().refuelHistory(orderNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseRetObj<List<OilHistoryEntity>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseRetObj<List<OilHistoryEntity>> listBaseRetObj) {
                        refuelAdapter.setEmptyView(R.layout.item_refuel_empty);
                        smartRefreshLayout.finishRefresh(true);
                        if (listBaseRetObj == null || listBaseRetObj.getCode() != 1) {
                            showToast("尚未获取到数据");
                            return;
                        }
                        list.clear();
                        list.addAll(listBaseRetObj.getDate());
                        refuelAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {
                        smartRefreshLayout.finishRefresh(true);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
