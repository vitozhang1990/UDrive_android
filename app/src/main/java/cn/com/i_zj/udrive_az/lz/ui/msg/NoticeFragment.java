package cn.com.i_zj.udrive_az.lz.ui.msg;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import butterknife.BindView;
import cn.com.i_zj.udrive_az.DBSBaseFragment;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.lz.adapter.NoticeAdapter;
import cn.com.i_zj.udrive_az.view.EmptyView;

/**
 * @author JayQiu
 * @create 2018/11/13
 * @Describe
 */
public class NoticeFragment extends DBSBaseFragment {
    @BindView(R.id.recycler)
    RecyclerView recycler;
    @BindView(R.id.swipeRefresh)
    SmartRefreshLayout swipeRefresh;
    EmptyView emptyView;
    private NoticeAdapter noticeAdapter;

    public static NoticeFragment newInstance() {
        NoticeFragment fragment = new NoticeFragment();
        return fragment;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_list;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        emptyView=new EmptyView(recycler.getContext(),recycler);
        emptyView.setImage(R.mipmap.pic_notice_null);
        emptyView.setMsg("暂无通知哦~");
        noticeAdapter= new NoticeAdapter(R.layout.item_notice_list,null);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        noticeAdapter.bindToRecyclerView(recycler);
        noticeAdapter.setEmptyView(emptyView);
    }
}
