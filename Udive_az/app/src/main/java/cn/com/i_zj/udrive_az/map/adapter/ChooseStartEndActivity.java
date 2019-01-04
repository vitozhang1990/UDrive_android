package cn.com.i_zj.udrive_az.map.adapter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
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
        PoiSearch.OnPoiSearchListener, TextWatcher {

    @BindView(R.id.ed_search)
    EditText searchText;
    @BindView(R.id.history_layout)
    LinearLayout history_layout;
    @BindView(R.id.rv_history)
    RecyclerView rv_history;
    @BindView(R.id.rv_address)
    RecyclerView rv_address;


    private String keyWord = "";// 要输入的poi搜索关键字
    private PoiSearch.Query query;// Poi查询条件类
    private PoiSearch poiSearch;// POI搜索
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
                            helper.setText(R.id.tv_name, ai.getTitle());
                            helper.setText(R.id.tv_adress, ai.getName());
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
        query = new PoiSearch.Query(keyWord, "", "成都");
        query.setPageSize(15);
        query.setPageNum(0);

        poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();
    }

    @Override
    public void onPoiSearched(PoiResult poiResult, final int i) {
        if (i == 1000) {
            final ArrayList<PoiItem> poiItems = poiResult.getPois();
            history_layout.setVisibility(View.GONE);
            rv_address.setVisibility(View.VISIBLE);
            RecyclerViewUtils.initLiner(
                    ChooseStartEndActivity.this, rv_address,
                    R.layout.item_poisearch, poiItems, new OnGlobalListener() {
                        @Override
                        public <T> void logic(BaseViewHolder helper, T item) {
                            PoiItem poiItem = (PoiItem) item;
                            helper.setText(R.id.tv_name, poiItem.getTitle());
                            helper.setText(R.id.tv_adress, poiItem.getAdName());
                        }
                    }, new BaseQuickAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                            AddressInfo addressInfo = new AddressInfo();
                            addressInfo.setName(poiItems.get(position).getAdName());
                            addressInfo.setTitle(poiItems.get(position).getTitle());
                            addressInfo.setLat(poiItems.get(position).getLatLonPoint().getLatitude());
                            addressInfo.setLng(poiItems.get(position).getLatLonPoint().getLongitude());
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

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

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
}
