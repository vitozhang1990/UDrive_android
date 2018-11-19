package cn.com.i_zj.udrive_az.lz.ui.msg;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.trello.rxlifecycle2.android.FragmentEvent;

import butterknife.BindView;
import cn.com.i_zj.udrive_az.DBSBaseFragment;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.lz.adapter.EventListAdapter;
import cn.com.i_zj.udrive_az.model.ret.BaseRetObj;
import cn.com.i_zj.udrive_az.model.ret.RetEventObj;
import cn.com.i_zj.udrive_az.network.UObserver;
import cn.com.i_zj.udrive_az.network.UdriveRestClient;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/**
 * @author JayQiu
 * @create 2018/11/13
 * @Describe 活动
 */
public class EventFragment extends DBSBaseFragment {
    @BindView(R.id.recycler)
    RecyclerView recycler;
    @BindView(R.id.swipeRefresh)
    SmartRefreshLayout swipeRefresh;
    private EventListAdapter listAdapter;
    private  int page=1;
    public static EventFragment newInstance() {
        EventFragment fragment = new EventFragment();
        return fragment;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_list;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        listAdapter = new EventListAdapter(R.layout.item_event_list, null,this);
        recycler.setAdapter(listAdapter);
        listAdapter.bindToRecyclerView(recycler);
        swipeRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                page=1;
                initData();
            }
        });
        swipeRefresh.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                page++;
                initData();
            }
        });
        swipeRefresh.setEnableLoadmore(true);
        initData();

    }


    private  void initData(){
        UdriveRestClient.getClentInstance().activityPage(page,2)
                .subscribeOn(Schedulers.io())
                .compose(this.<BaseRetObj<RetEventObj>>bindUntilEvent(FragmentEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new UObserver<RetEventObj>() {
                    @Override
                    public void onSuccess(RetEventObj response) {

                        if(response!=null){

                            if(page==1){
                                listAdapter.replaceData(response.getList());
                            }else {
                                listAdapter.addData(response.getList());
                            }
                            swipeRefresh.setEnableLoadmore(!response.isLastPage());

                        }
                    }

                    @Override
                    public void onException(int code, String message) {
                        showToast(message);
                    }

                    @Override
                    public void onFinish() {
                        if(page==1){
                            swipeRefresh.finishRefresh(true);
                        }else {
                            swipeRefresh.finishLoadmore();
                        }

                    }
                });
    }
}
