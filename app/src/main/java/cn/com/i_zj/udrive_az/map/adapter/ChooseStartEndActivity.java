package cn.com.i_zj.udrive_az.map.adapter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.i_zj.udrive_az.DBSBaseActivity;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.map.MapUtils;
import cn.com.i_zj.udrive_az.model.AddressInfo;
import cn.com.i_zj.udrive_az.utils.Constants;
import cn.com.i_zj.udrive_az.utils.LocalCacheUtils;

public class ChooseStartEndActivity extends DBSBaseActivity implements
        Inputtips.InputtipsListener, TextWatcher {

    @BindView(R.id.ed_search)
    EditText searchText;
    @BindView(R.id.history_layout)
    LinearLayout history_layout;
    @BindView(R.id.rv_history)
    RecyclerView rv_history;
    @BindView(R.id.rv_address)
    RecyclerView rv_address;

    private String keyWord = "";// 要输入的poi搜索关键字
    private ArrayList<AddressInfo> historyAddress;
    private GlobalAdapter historyAdapter;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_choos_start_end;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapUtils.statusBarColor(this);
        searchText.addTextChangedListener(this);
        String data = LocalCacheUtils.getPersistentSettingString(Constants.SP_GLOBAL_NAME, Constants.SP_ADDRESS, "");
        historyAddress = new Gson().fromJson(data, new TypeToken<List<AddressInfo>>() {
        }.getType());
        if (historyAddress == null || historyAddress.size() == 0) {
            history_layout.setVisibility(View.GONE);
        } else {
            history_layout.setVisibility(View.VISIBLE);
            historyAdapter = RecyclerViewUtils.initLiner(
                    ChooseStartEndActivity.this, rv_history,
                    R.layout.item_poisearch, historyAddress, new OnGlobalListener() {
                        @Override
                        public <T> void logic(BaseViewHolder helper, T item) {
                            AddressInfo ai = (AddressInfo) item;
                            helper.setText(R.id.tv_name, ai.getName());
                            helper.setText(R.id.tv_adress, ai.getAddress());
                        }
                    }, new BaseQuickAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                            Intent intent = getIntent();
                            intent.putExtra("poiItem", historyAddress.get(position));
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    });
        }
    }

    @OnClick({R.id.iv_back, R.id.trash})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.trash:
                if (historyAddress != null) {
                    historyAddress.clear();
                }
                if (historyAdapter != null) {
                    historyAdapter.notifyDataSetChanged();
                }
                LocalCacheUtils.removePersistentSetting(Constants.SP_GLOBAL_NAME, Constants.SP_ADDRESS);
                break;
        }
    }


    /**
     * 开始进行poi搜索
     */
    protected void doSearchQuery() {
        InputtipsQuery inputquery = new InputtipsQuery(keyWord, "");

        Inputtips inputTips = new Inputtips(this, inputquery);
        inputTips.setInputtipsListener(this);
        inputTips.requestInputtipsAsyn();
    }

    private String checkEditText(EditText editText) {
        if (editText != null && editText.getText() != null
                && !("".equals(editText.getText().toString().trim()))) {
            return editText.getText().toString().trim();
        } else {
            return "";
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        keyWord = checkEditText(searchText);
        if (!"".equals(keyWord)) {
            doSearchQuery();
        } else {
            rv_address.setVisibility(View.GONE);
            if (historyAddress != null && historyAddress.size() > 0) {
                history_layout.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onGetInputtips(List<Tip> list, int i) {
        if (i == 1000) {
            history_layout.setVisibility(View.GONE);
            rv_address.setVisibility(View.VISIBLE);
            RecyclerViewUtils.initLiner(
                    ChooseStartEndActivity.this, rv_address,
                    R.layout.item_poisearch, list, new OnGlobalListener() {
                        @Override
                        public <T> void logic(BaseViewHolder helper, T item) {
                            Tip poiItem = (Tip) item;
                            helper.setText(R.id.tv_name, poiItem.getName());
                            helper.setText(R.id.tv_adress, poiItem.getAddress());
                        }
                    }, new BaseQuickAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                            Tip tip = (Tip) adapter.getItem(position);
                            AddressInfo addressInfo = new AddressInfo();
                            addressInfo.setAddress(tip.getAddress());
                            addressInfo.setName(tip.getName());
                            addressInfo.setLat(tip.getPoint().getLatitude());
                            addressInfo.setLng(tip.getPoint().getLongitude());
                            boolean same = false;
                            if (historyAddress == null) {
                                historyAddress = new ArrayList<>();
                            }
                            for (AddressInfo info : historyAddress) {
                                if (info.same(addressInfo)) {
                                    same = true;
                                    break;
                                }
                            }
                            if (!same) {
                                historyAddress.add(addressInfo);
                                LocalCacheUtils.savePersistentSettingString(Constants.SP_GLOBAL_NAME, Constants.SP_ADDRESS, new Gson().toJson(historyAddress));
                            }
                            Intent intent = getIntent();
                            intent.putExtra("poiItem", addressInfo);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    });
        }
    }
}
