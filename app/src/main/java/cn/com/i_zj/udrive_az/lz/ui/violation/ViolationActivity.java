package cn.com.i_zj.udrive_az.lz.ui.violation;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.i_zj.udrive_az.DBSBaseActivity;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.map.MapUtils;
import cn.com.i_zj.udrive_az.model.ret.BaseRetObj;
import cn.com.i_zj.udrive_az.model.ret.Violation;
import cn.com.i_zj.udrive_az.model.ret.ViolationObj;
import cn.com.i_zj.udrive_az.network.UdriveRestClient;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 违章
 */
public class ViolationActivity extends DBSBaseActivity
        implements BaseQuickAdapter.OnItemClickListener, OnRefreshLoadmoreListener {

    @BindView(R.id.header_title)
    TextView header_title;
    @BindView(R.id.swipeRefresh)
    SmartRefreshLayout smartRefreshLayout;
    @BindView(R.id.recycler)
    RecyclerView recyclerView;

    private List<Violation> list = new ArrayList<>();
    private ViolationAdapter mAdapter;
    private String orderNumber;

    private int size = 10;
    private int page = 1;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_violation;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapUtils.statusBarColor(this);
        orderNumber = getIntent().getStringExtra("number");

        smartRefreshLayout.setOnRefreshLoadmoreListener(this);
        mAdapter = new ViolationAdapter(list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(this);
        mAdapter.bindToRecyclerView(recyclerView);
        mAdapter.setEnableLoadMore(false);
        mAdapter.setEmptyView(R.layout.item_violation_empty);

        header_title.setText("交通违章");
    }

    @OnClick({R.id.header_left})
    void onClick(View view) {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getList(true);
    }

    private void getList(boolean refresh) {
        if (refresh) {
            page = 1;
        } else {
            page += 1;
        }
        showProgressDialog();
        Map<String, Object> map = new HashMap<>();
        map.put("pageSize", size);
        map.put("pageNumber", page);
        if (!TextUtils.isEmpty(orderNumber)) {
            map.put("number", orderNumber);
        }
        UdriveRestClient.getClentInstance().illegalList(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseRetObj<ViolationObj>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(BaseRetObj<ViolationObj> violationObjBaseRetObj) {
                        dissmisProgressDialog();
                        smartRefreshLayout.finishRefresh(true);
                        smartRefreshLayout.finishLoadmore();
                        if (violationObjBaseRetObj == null || violationObjBaseRetObj.getCode() == 1) {
                            if (refresh) {
                                list.clear();
                            }
                            list.addAll(violationObjBaseRetObj.getDate().getList());
                            mAdapter.notifyDataSetChanged();

                            smartRefreshLayout.setEnableLoadmore(violationObjBaseRetObj.getDate().getList().size() == size);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        dissmisProgressDialog();
                        smartRefreshLayout.finishRefresh(true);
                        smartRefreshLayout.finishLoadmore();
                    }

                    @Override
                    public void onComplete() {
                        dissmisProgressDialog();
                    }
                });
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (adapter.getItem(position) != null && adapter.getItem(position) instanceof Violation) {
            Violation violation = (Violation) adapter.getItem(position);
            Intent intent = new Intent();
            intent.setClass(this, ViolationDetailActivity.class);
            intent.putExtra("id", violation.getId());
            startActivity(intent);
        }
    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        getList(true);
    }

    @Override
    public void onLoadmore(RefreshLayout refreshlayout) {
        getList(false);
    }
}
