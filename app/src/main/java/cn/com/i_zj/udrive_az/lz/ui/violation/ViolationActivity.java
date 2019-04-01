package cn.com.i_zj.udrive_az.lz.ui.violation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.com.i_zj.udrive_az.DBSBaseActivity;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.map.MapUtils;
import cn.com.i_zj.udrive_az.model.ret.BaseRetObj;
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

    private List<String> list = new ArrayList<>();
    private ViolationAdapter mAdapter;

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
        UdriveRestClient.getClentInstance().illegalList(size, page)
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
        startActivity(ViolationDetailActivity.class);
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
