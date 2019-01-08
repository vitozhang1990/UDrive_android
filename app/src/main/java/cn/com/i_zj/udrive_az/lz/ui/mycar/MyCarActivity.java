package cn.com.i_zj.udrive_az.lz.ui.mycar;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.ArrayList;
import java.util.List;

import cn.com.i_zj.udrive_az.BaseActivity;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.lz.bean.MyCarBean;

public class MyCarActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, MyCatAdapter.MyCarOnItemClickListener, Toolbar.OnMenuItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_car);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.lz_my_car_title);
        setSupportActionBar(toolbar);

        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        RecyclerView recyclerView = findViewById(R.id.recycler);

        swipeRefreshLayout.setOnRefreshListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<MyCarBean> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            MyCarBean myCarBean = new MyCarBean();
            myCarBean.setType(i%2);
            list.add(myCarBean);
        }
        MyCatAdapter adapter = new MyCatAdapter(R.layout.item_my_car, list);
        recyclerView.setAdapter(adapter);


        toolbar.setOnMenuItemClickListener(this);
        adapter.setMyCarOnItemClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.lz_my_car, menu);
        return true;
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, "position=" + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemDeleteClick(View view, int position) {
        Toast.makeText(this, "position=" + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {

        Toast.makeText(this, "添加", Toast.LENGTH_SHORT).show();
        return false;
    }
}
