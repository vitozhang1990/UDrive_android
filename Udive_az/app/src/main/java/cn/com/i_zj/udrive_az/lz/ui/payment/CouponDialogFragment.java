package cn.com.i_zj.udrive_az.lz.ui.payment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.login.AccountInfoManager;
import cn.com.i_zj.udrive_az.login.SessionManager;
import cn.com.i_zj.udrive_az.lz.bean.CouponPayEvent;
import cn.com.i_zj.udrive_az.model.AccountInfoResult;
import cn.com.i_zj.udrive_az.model.UnUseCouponResult;
import cn.com.i_zj.udrive_az.network.UdriveRestClient;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by wo on 2018/9/2.
 * 优惠券DialogFragment
 */

public class CouponDialogFragment extends BottomSheetDialogFragment implements BaseQuickAdapter.OnItemClickListener {

    private List<UnUseCouponResult.DataBean> list;
    private CouponAdapter couponAdapter;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;

    private TextView mTvNoUseCoupon;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_conpou, container, false);

        progressBar = inflate.findViewById(R.id.progressBar);
        recyclerView = inflate.findViewById(R.id.recycler);
        mTvNoUseCoupon=inflate.findViewById(R.id.tv_no_use_coupon);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        list = new ArrayList<>();

        couponAdapter = new CouponAdapter(R.layout.item_coupon, list);
        recyclerView.setAdapter(couponAdapter);
        couponAdapter.setOnItemClickListener(this);
        couponAdapter.bindToRecyclerView(recyclerView);
        findUnUsePreferential();
        initEvent();
        return inflate;
    }
    private  void initEvent(){
        mTvNoUseCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new CouponPayEvent(null));
            }
        });
    }

    public void findUnUsePreferential() {
        AccountInfoResult accountInfo = AccountInfoManager.getInstance().getAccountInfo();
        if (accountInfo == null) {
            Toast.makeText(getContext(), "数据请求失败", Toast.LENGTH_SHORT).show();
            return;
        }
        UdriveRestClient.getClentInstance().findUnUsePreferential(SessionManager.getInstance().getAuthorization(), accountInfo.data.userId + "")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UnUseCouponResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(UnUseCouponResult value) {
                        if (value.getData().size() == 0) {
                            couponAdapter.setEmptyView(R.layout.layout_empty);
                        }
                        progressBar.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        couponAdapter.addData(value.getData());
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println(e.getMessage());
                        progressBar.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        couponAdapter.setEmptyView(R.layout.layout_error);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        EventBus.getDefault().post(new CouponPayEvent(list.get(position)));
    }
}
