package cn.com.i_zj.udrive_az.lz.ui.order;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.i_zj.udrive_az.DBSBaseActivity;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.lz.ui.payment.ActConfirmOrder;
import cn.com.i_zj.udrive_az.lz.ui.payment.ActOrderPayment;
import cn.com.i_zj.udrive_az.map.MapUtils;
import cn.com.i_zj.udrive_az.map.TravelingActivity;
import cn.com.i_zj.udrive_az.model.OrderResult;
import cn.com.i_zj.udrive_az.network.UdriveRestClient;
import cn.com.i_zj.udrive_az.utils.Constants;
import cn.com.i_zj.udrive_az.utils.ScreenManager;
import cn.com.i_zj.udrive_az.view.EmptyView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 我的订单
 */
public class OrderActivity extends DBSBaseActivity
        implements BaseQuickAdapter.OnItemClickListener, OnRefreshListener {

    @BindView(R.id.swipeRefresh)
    SmartRefreshLayout smartRefreshLayout;
    @BindView(R.id.recycler)
    RecyclerView recyclerView;

    private List<OrderResult.OrderItem> list = new ArrayList<>();
    private OrderAdapter orderAdapter;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_order;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScreenManager.getScreenManager().pushActivity(this);
        MapUtils.statusBarColor(this);

        smartRefreshLayout.setOnRefreshListener(this);

        orderAdapter = new OrderAdapter(R.layout.item_order, list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(orderAdapter);

        orderAdapter.setOnItemClickListener(this);
        orderAdapter.bindToRecyclerView(recyclerView);
        orderAdapter.setEnableLoadMore(false);
    }

    @OnClick(R.id.iv_back)
    void onClick() {
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getFindTripOrders();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ScreenManager.getScreenManager().popActivity(this);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        OrderResult.OrderItem orderItem = list.get(position);
        if (orderItem.status == Constants.ORDER_MOVE) {//行程中
            startActivity(TravelingActivity.class);
        } else if (orderItem.status == Constants.ORDER_WAIT_PAY) {// 待付款
            Intent intent1 = new Intent(this, ActConfirmOrder.class);
            intent1.putExtra(ActConfirmOrder.ORDER_NUMBER, orderItem.number);
            intent1.putExtra(ActConfirmOrder.ORDER_ID, orderItem.id);
            startActivity(intent1);
        } else if (orderItem.status == Constants.ORDER_FINISH) {// 已完成
            Intent intent2 = new Intent(this, ActOrderPayment.class);
            intent2.putExtra(ActOrderPayment.ORDER_NUMBER, orderItem.number);
            intent2.putExtra(ActOrderPayment.ORDER_ID, orderItem.id);
            startActivity(intent2);
        }
    }

    private void getFindTripOrders() {
        showProgressDialog();
        UdriveRestClient.getClentInstance().queryAllOrdersByUser()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<OrderResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(OrderResult value) {
                        dissmisProgressDialog();
                        smartRefreshLayout.finishRefresh(true);
                        list.clear();
                        orderAdapter.setNewData(list);
                        if (value == null) {
                            orderAdapter.setEmptyView(R.layout.layout_error);
                            return;
                        }
                        List<OrderResult.OrderItem> data = value.orderList;
                        if (data == null || data.size() == 0) {
                            EmptyView emptyView= new EmptyView(recyclerView.getContext(),recyclerView);
                            emptyView.setImage(R.mipmap.pic_order_null);
                            emptyView.setMsg("还没有订单哦");
                            orderAdapter.setEmptyView(emptyView);
                        } else {
                            orderAdapter.addData(data);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        dissmisProgressDialog();
                        smartRefreshLayout.finishRefresh(true);
                        orderAdapter.setEmptyView(R.layout.layout_error);
                    }

                    @Override
                    public void onComplete() {
                        dissmisProgressDialog();
                    }
                });
    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        getFindTripOrders();
    }
}
