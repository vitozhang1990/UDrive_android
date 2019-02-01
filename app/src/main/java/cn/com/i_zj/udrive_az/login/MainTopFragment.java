package cn.com.i_zj.udrive_az.login;

import android.content.Context;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.i_zj.udrive_az.DBSBaseFragment;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.event.CityUpdateEvent;
import cn.com.i_zj.udrive_az.event.NetWorkEvent;
import cn.com.i_zj.udrive_az.map.adapter.GlobalAdapter;
import cn.com.i_zj.udrive_az.map.adapter.OnGlobalListener;
import cn.com.i_zj.udrive_az.map.adapter.RecyclerViewUtils;
import cn.com.i_zj.udrive_az.model.CityListResult;
import cn.com.i_zj.udrive_az.model.ret.BaseRetObj;
import cn.com.i_zj.udrive_az.network.UdriveRestClient;
import cn.com.i_zj.udrive_az.utils.Constants;
import cn.com.i_zj.udrive_az.utils.LocalCacheUtils;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainTopFragment extends DBSBaseFragment {

    @BindView(R.id.tv_city)
    TextView city;
    @BindView(R.id.toolbar)
    LinearLayout toolBar;
    @BindView(R.id.city_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.mengceng)
    View mengceng;
    @BindView(R.id.city_checkbox)
    CheckBox checkBox;

    private boolean pickModel;
    private CityListResult cityInfo;
    private GlobalAdapter mAdapter;
    private ArrayList<CityListResult> cityList = new ArrayList<>();
    private boolean hasRequest = false;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_top;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdapter = RecyclerViewUtils.initRecycler(
                getActivity()
                , mRecyclerView
                , new GridLayoutManager(getActivity(), 3)
                , R.layout.item_city, cityList
                , new OnGlobalListener() {
                    @Override
                    public <T> void logic(BaseViewHolder helper, T item) {
                        CityListResult ai = (CityListResult) item;
                        helper.setText(R.id.city_name, ai.getAreaName());
                        if (cityInfo != null &&
                                (cityInfo.getAreaCode().equals(ai.getAreaCode()))
                                || cityInfo.getAreaName().equals(ai.getAreaName())) {
                            ((TextView) helper.getView(R.id.city_name)).setTypeface(Typeface.DEFAULT_BOLD);
                        } else {
                            ((TextView) helper.getView(R.id.city_name)).setTypeface(Typeface.DEFAULT);
                        }
                        Glide.with(getActivity()).load(ai.getImg()).crossFade().into((ImageView) helper.getView(R.id.city_pic));
                    }
                }
                , new BaseQuickAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                        cityInfo = cityList.get(position);
                        LocalCacheUtils.saveDeviceData(Constants.SP_GLOBAL_NAME, Constants.SP_CITY, cityInfo);
                        EventBus.getDefault().post(cityInfo);
                        pickModel = false;
                        updateUi();
                    }
                }, R.layout.item_city_empty);
        updateUi();
        requestCityList();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            connectivityManager.requestNetwork(new NetworkRequest.Builder().build(), new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(Network network) {
                    super.onAvailable(network);
                    requestCityList();
                }
            });
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(NetWorkEvent netWorkEvent) {
        requestCityList();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(CityUpdateEvent event) {
        cityInfo = LocalCacheUtils.getDeviceData(Constants.SP_GLOBAL_NAME, Constants.SP_CITY);
        updateUi();
    }

    private void requestCityList() {
        UdriveRestClient.getClentInstance().getCityList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseRetObj<List<CityListResult>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseRetObj<List<CityListResult>> listBaseRetObj) {
                        if (listBaseRetObj == null) {
                            return;
                        }
                        if (listBaseRetObj.getCode() != 1) {
                            return;
                        }
                        if (listBaseRetObj.getDate().size() > 0) {
                            LocalCacheUtils.saveDeviceData(Constants.SP_GLOBAL_NAME, Constants.SP_CITY_LIST, listBaseRetObj.getDate());
                        }
                        hasRequest = true;
                        cityList.clear();
                        cityList.addAll(listBaseRetObj.getDate());
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @OnClick({R.id.city_layout, R.id.mengceng})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.city_layout:
                if (!hasRequest) {
                    ToastUtils.showShort("尚未请求到城市信息，请稍后再试");
                    requestCityList();
                    return;
                }
                pickModel = !pickModel;
                cityInfo = LocalCacheUtils.getDeviceData(Constants.SP_GLOBAL_NAME, Constants.SP_CITY);
                mAdapter.notifyDataSetChanged();
                break;
            case R.id.mengceng:
                pickModel = false;
                break;
        }
        updateUi();
    }

    private void updateUi() {
        checkBox.setChecked(pickModel);
        mengceng.setVisibility(pickModel ? View.VISIBLE : View.GONE);
        mRecyclerView.setVisibility(pickModel ? View.VISIBLE : View.GONE);
        toolBar.setBackgroundResource(pickModel ? R.drawable.bg_map_top1 : R.drawable.bg_map_top);
        if (cityInfo != null) {
            city.setText(cityInfo.getAreaName());
        }
    }
}

