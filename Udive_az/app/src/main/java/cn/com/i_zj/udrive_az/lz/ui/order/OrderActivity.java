package cn.com.i_zj.udrive_az.lz.ui.order;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.ArrayList;
import java.util.List;

import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.login.SessionManager;
import cn.com.i_zj.udrive_az.lz.ui.payment.PaymentActivity;
import cn.com.i_zj.udrive_az.lz.ui.payment.PaymentDialogFragment;
import cn.com.i_zj.udrive_az.model.OrderResult;
import cn.com.i_zj.udrive_az.network.UdriveRestClient;
import cn.com.i_zj.udrive_az.utils.Constants;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 我的订单
 */
public class OrderActivity extends AppCompatActivity implements BaseQuickAdapter.OnItemClickListener,
        BaseQuickAdapter.OnItemChildClickListener, SwipeRefreshLayout.OnRefreshListener {
    private List<OrderResult.OrderItem> list;
    private OrderAdapter orderAdapter;
    private PaymentDialogFragment paymentDialogFragment;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.lz_my_order);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mSwipeRefreshLayout = findViewById(R.id.swipeRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        RecyclerView recyclerView = findViewById(R.id.recycler);
        list = new ArrayList<>();
        orderAdapter = new OrderAdapter(R.layout.item_order, list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(orderAdapter);

        orderAdapter.setOnItemClickListener(this);
        orderAdapter.setOnItemChildClickListener(this);
        orderAdapter.bindToRecyclerView(recyclerView);
        orderAdapter.setEnableLoadMore(false);

    }

    @Override
    protected void onStart() {
        super.onStart();
        getFindTripOrders();
    }

    @Override
    public void onRefresh() {

        getFindTripOrders();
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Intent intent = new Intent(this, PaymentActivity.class);
        OrderResult.OrderItem orderItem = list.get(position);
        intent.putExtra(PaymentActivity.ORDER_NUMBER, orderItem.number);
        if (orderItem.status == Constants.ORDER_MOVE) {
            intent.putExtra(PaymentActivity.TITLE, getResources().getString(R.string.order_move));
        } else if (orderItem.status == Constants.ORDER_WAIT_PAY) {
            intent.putExtra(PaymentActivity.TITLE, getResources().getString(R.string.order_wait_pay));
            startActivity(intent);
        } else if (orderItem.status == Constants.ORDER_FINISH) {
            intent.putExtra(PaymentActivity.TITLE, getResources().getString(R.string.order_finish));
            startActivity(intent);
        }


    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        switch (view.getId()) {
            case R.id.btn_connect:
                Toast.makeText(this, "联系客服 " + position, Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_pay_or_finish:
                Toast.makeText(this, "立即付款 " + position, Toast.LENGTH_SHORT).show();
                handlePayOrFinish();
                break;
            default:
                break;
        }
    }

    private void handlePayOrFinish() {
        handlePay();
    }

    private void handlePay() {
        if (paymentDialogFragment != null && !paymentDialogFragment.isHidden()) {
            paymentDialogFragment.dismiss();
        }
        paymentDialogFragment = new PaymentDialogFragment();
        paymentDialogFragment.show(getSupportFragmentManager(), "pay");
    }

    private void getFindTripOrders() {
        list.clear();
        orderAdapter.setNewData(list);
        UdriveRestClient.getClentInstance().queryAllOrdersByUser(SessionManager.getInstance().getAuthorization())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<OrderResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mSwipeRefreshLayout.setRefreshing(true);
                    }

                    @Override
                    public void onNext(OrderResult value) {
                        if (value == null) {
                            orderAdapter.setEmptyView(R.layout.layout_error);
                            return;
                        }
                        List<OrderResult.OrderItem> data = value.orderList;
                        if (data.size() == 0) {
                            orderAdapter.setEmptyView(R.layout.layout_empty);
                        }
                        orderAdapter.addData(data);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mSwipeRefreshLayout.setRefreshing(false);
                        orderAdapter.setEmptyView(R.layout.layout_error);
                    }

                    @Override
                    public void onComplete() {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
    }

}
