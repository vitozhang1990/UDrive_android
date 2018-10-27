package cn.com.i_zj.udrive_az.lz.ui.coupon;

import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.login.AccountInfoManager;
import cn.com.i_zj.udrive_az.DBSBaseActivity;
import cn.com.i_zj.udrive_az.login.SessionManager;
import cn.com.i_zj.udrive_az.model.AccountInfoResult;
import cn.com.i_zj.udrive_az.model.UnUseCouponResult;
import cn.com.i_zj.udrive_az.network.UdriveRestClient;
import cn.com.i_zj.udrive_az.view.EmptyView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 优惠券列表
 */
public class CouponListActivity extends DBSBaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;
    private ArrayList<UnUseCouponResult.DataBean> mList;
    private CouponAdapter mAdapter;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_coupon_list;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("优惠券列表");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mList = new ArrayList<>();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new CouponAdapter(R.layout.item_coupon, mList);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.bindToRecyclerView(mRecyclerView);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        findUnUsePreferential();

    }

    public void findUnUsePreferential() {
        mSwipeRefreshLayout.setRefreshing(true);
        AccountInfoResult accountInfo = AccountInfoManager.getInstance().getAccountInfo();
        if (accountInfo == null) {
            Toast.makeText(this, "数据请求失败", Toast.LENGTH_SHORT).show();
            mSwipeRefreshLayout.setRefreshing(false);
            return;
        }
        UdriveRestClient.getClentInstance().findAllPreferential(SessionManager.getInstance().getAuthorization(), accountInfo.data.userId + "")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UnUseCouponResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(UnUseCouponResult value) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        if (value.getData().size() == 0) {
                            EmptyView emptyView= new EmptyView(mRecyclerView.getContext(),mRecyclerView);
                            emptyView.setImage(R.mipmap.pic_coupon_null);
                            emptyView.setMsg("暂无可用优惠券");
                            mAdapter.setEmptyView(emptyView);
                        }
                        mAdapter.addData(value.getData());
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println(e.getMessage());
                        mAdapter.setEmptyView(R.layout.layout_error);
                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onComplete() {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
    }

    @Override
    public void onRefresh() {
        mList.clear();
        mAdapter.setNewData(mList);
        findUnUsePreferential();
    }
}
