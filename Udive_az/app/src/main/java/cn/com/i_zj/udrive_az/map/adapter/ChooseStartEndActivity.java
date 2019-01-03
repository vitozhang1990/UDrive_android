package cn.com.i_zj.udrive_az.map.adapter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.i_zj.udrive_az.DBSBaseActivity;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.map.MapUtils;

public class ChooseStartEndActivity extends DBSBaseActivity implements
        PoiSearch.OnPoiSearchListener, TextWatcher {

    @BindView(R.id.ed_search)
    EditText searchText;
    @BindView(R.id.rv_history)
    RecyclerView rv_history;
    @BindView(R.id.rv_address)
    RecyclerView rv_address;


    private String keyWord = "";// 要输入的poi搜索关键字
    private PoiSearch.Query query;// Poi查询条件类
    private PoiSearch poiSearch;// POI搜索

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_choos_start_end;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapUtils.statusBarColor(this);
        searchText.addTextChangedListener(this);
    }

    @OnClick(R.id.iv_back)
    void back(View view) {
        finish();
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
                            Intent intent = getIntent();
                            intent.putExtra("poiItem", poiItems.get(position));
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
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
