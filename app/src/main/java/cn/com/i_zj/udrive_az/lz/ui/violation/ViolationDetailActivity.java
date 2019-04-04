package cn.com.i_zj.udrive_az.lz.ui.violation;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.i_zj.udrive_az.DBSBaseActivity;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.map.MapUtils;
import cn.com.i_zj.udrive_az.model.ret.BaseRetObj;
import cn.com.i_zj.udrive_az.model.ret.ViolationDetailObj;
import cn.com.i_zj.udrive_az.network.UdriveRestClient;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ViolationDetailActivity extends DBSBaseActivity {

    @BindView(R.id.header_title)
    TextView header_title;
    @BindView(R.id.header_image)
    ImageView header_image;
    @BindView(R.id.viola_order_time)
    TextView orderTime;
    @BindView(R.id.viola_order_pn)
    TextView orderPn;
    @BindView(R.id.viola_order_fromto)
    TextView orderFromTo;
    @BindView(R.id.viola_time)
    TextView time;
    @BindView(R.id.viola_address)
    TextView address;
    @BindView(R.id.viola_action)
    TextView action;

    private int id;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_violation_detail;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapUtils.statusBarColor(this);

        id = getIntent().getIntExtra("id", -1);
        if (id == -1) {
            showToast("获取到错误Id");
            return;
        }

        header_title.setText("违章信息");
        header_image.setImageResource(R.mipmap.ic_service);

        getDetail();
    }

    @OnClick({R.id.header_left, R.id.header_right, R.id.btn_commit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.header_left:
                finish();
                break;
            case R.id.header_right:
                Intent intent = new Intent(Intent.ACTION_DIAL);
                Uri data = Uri.parse("tel:" + getResources().getString(R.string.about_phone));
                intent.setData(data);
                startActivity(intent);
                break;
            case R.id.btn_commit:
                Intent deal = new Intent();
                deal.setClass(this, ViolationDealActivity.class);
                ViolationDetailObj obj = new ViolationDetailObj();
                obj.setState(4);
                obj.setProcessSheetPhoto("receiptPhotob7ce6e988f0865b083b9dcc010baaeb2_20190325161508.png");
                deal.putExtra("data", obj);
                startActivity(deal);
                break;
        }
    }

    private void getDetail() {
        showProgressDialog();
        UdriveRestClient.getClentInstance().getIllegal(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseRetObj<ViolationDetailObj>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(BaseRetObj<ViolationDetailObj> violationObjBaseRetObj) {
                        dissmisProgressDialog();
                        if (violationObjBaseRetObj == null || violationObjBaseRetObj.getCode() == 1) {
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        dissmisProgressDialog();
                    }

                    @Override
                    public void onComplete() {
                        dissmisProgressDialog();
                    }
                });
    }
}
