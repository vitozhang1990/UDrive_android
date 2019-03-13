package cn.com.i_zj.udrive_az.refuel;

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
import cn.com.i_zj.udrive_az.lz.ui.payment.PaymentActivity;
import cn.com.i_zj.udrive_az.map.MapUtils;
import cn.com.i_zj.udrive_az.map.TravelingActivity;
import cn.com.i_zj.udrive_az.model.OrderResult;
import cn.com.i_zj.udrive_az.utils.Constants;

public class RefuelHistoryActivity extends DBSBaseActivity
        implements BaseQuickAdapter.OnItemClickListener, OnRefreshListener {

    @BindView(R.id.swipeRefresh)
    SmartRefreshLayout smartRefreshLayout;
    @BindView(R.id.recycler)
    RecyclerView recyclerView;

    private List<OrderResult.OrderItem> list = new ArrayList<>();
    private RefuelAdapter refuelAdapter;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_refuel_history;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapUtils.statusBarColor(this);

        smartRefreshLayout.setOnRefreshListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        refuelAdapter = new RefuelAdapter(list);
        refuelAdapter.setOnItemClickListener(this);
        refuelAdapter.bindToRecyclerView(recyclerView);
        refuelAdapter.setEnableLoadMore(false);
    }

    @OnClick(R.id.iv_back)
    void onClick() {
        finish();
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        OrderResult.OrderItem orderItem = list.get(position);
        if (orderItem.status == Constants.ORDER_MOVE) {//行程中
            startActivity(TravelingActivity.class);
        } else if (orderItem.status == Constants.ORDER_WAIT_PAY) {// 待付款
            Intent intent1 = new Intent(this, ActConfirmOrder.class);
            intent1.putExtra(PaymentActivity.ORDER_NUMBER, orderItem.number);
            startActivity(intent1);
        } else if (orderItem.status == Constants.ORDER_FINISH) {// 已完成
            Intent intent2 = new Intent(this, ActOrderPayment.class);
            intent2.putExtra(PaymentActivity.ORDER_NUMBER, orderItem.number);
            intent2.putExtra(ActOrderPayment.TITLE, getResources().getString(R.string.order_finish));
            startActivity(intent2);
        }
    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
//        getFindTripOrders();
    }
}
