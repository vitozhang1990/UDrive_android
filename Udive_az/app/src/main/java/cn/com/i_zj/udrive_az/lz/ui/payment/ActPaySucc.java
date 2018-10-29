package cn.com.i_zj.udrive_az.lz.ui.payment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;

import butterknife.OnClick;
import cn.com.i_zj.udrive_az.DBSBaseActivity;
import cn.com.i_zj.udrive_az.MainActivity;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.lz.ui.order.OrderActivity;
import cn.com.i_zj.udrive_az.utils.ScreenManager;

/**
 * @author JayQiu
 * @create 2018/10/27
 * @Describe 支付成功
 */
public class ActPaySucc extends DBSBaseActivity {
    public static void startActPaySucc(Context context) {
        Intent intent = new Intent(context, ActPaySucc.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_pay_succ;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.pay_succ);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @OnClick(R.id.tv_to_home)
    public void onClick() {
        ScreenManager.getScreenManager().popAllActivityExceptOne(OrderActivity.class);
        startActivity(new Intent(ActPaySucc.this,MainActivity.class));
        finish();
    }
}
