package cn.com.i_zj.udrive_az.lz.ui.order;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.login.SessionManager;
import cn.com.i_zj.udrive_az.lz.ui.payment.ActConfirmOrder;
import cn.com.i_zj.udrive_az.lz.ui.payment.ActOrderPayment;
import cn.com.i_zj.udrive_az.lz.ui.payment.PaymentActivity;
import cn.com.i_zj.udrive_az.lz.ui.payment.PaymentDialogFragment;
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
public class OrderActivity extends AppCompatActivity implements BaseQuickAdapter.OnItemClickListener,
        BaseQuickAdapter.OnItemChildClickListener {
    private List<OrderResult.OrderItem> list;
    private OrderAdapter orderAdapter;
    private PaymentDialogFragment paymentDialogFragment;
    private SmartRefreshLayout smartRefreshLayout;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        ScreenManager.getScreenManager().pushActivity(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.lz_my_order);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                finish();
            }
        });

        smartRefreshLayout = findViewById(R.id.swipeRefresh);
        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getFindTripOrders();
            }
        });

        recyclerView = findViewById(R.id.recycler);
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
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

        OrderResult.OrderItem orderItem = list.get(position);

        if (orderItem.status == Constants.ORDER_MOVE) {//行程中
            Intent intent = new Intent(this, PaymentActivity.class);
            intent.putExtra(PaymentActivity.ORDER_NUMBER, orderItem.number);
            intent.putExtra(PaymentActivity.TITLE, getResources().getString(R.string.order_move));
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

        UdriveRestClient.getClentInstance().queryAllOrdersByUser(SessionManager.getInstance().getAuthorization())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<OrderResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(OrderResult value) {
                        smartRefreshLayout.finishRefresh(true);
                        list.clear();
                        orderAdapter.setNewData(list);
                        if (value == null) {
                            orderAdapter.setEmptyView(R.layout.layout_error);
                            return;
                        }
                        List<OrderResult.OrderItem> data = value.orderList;
                        if (data.size() == 0) {
                            EmptyView emptyView= new EmptyView(recyclerView.getContext(),recyclerView);
                            emptyView.setImage(R.mipmap.pic_order_null);
                            emptyView.setMsg("还没有订单哦");
                            orderAdapter.setEmptyView(emptyView);
                        }
                        orderAdapter.addData(data);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        smartRefreshLayout.finishRefresh(true);
                        orderAdapter.setEmptyView(R.layout.layout_error);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

}
