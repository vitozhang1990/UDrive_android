package cn.com.i_zj.udrive_az.lz.ui.mycar;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.lz.bean.MyCarBean;

public class MyCatAdapter extends BaseQuickAdapter<MyCarBean, BaseViewHolder> {
    public MyCatAdapter(int layoutResId, @Nullable List<MyCarBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(final BaseViewHolder helper, MyCarBean item) {

        ImageView ivStop = helper.getView(R.id.iv_stop);
        TextView tvMove = helper.getView(R.id.tv_move);
        if (item.getType() == 0) {
            ivStop.setVisibility(View.GONE);
            tvMove.setVisibility(View.VISIBLE);
        } else {
            ivStop.setVisibility(View.VISIBLE);
            tvMove.setVisibility(View.GONE);
        }

        View view = helper.getView(R.id.content);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myCarOnItemClickListener != null) {
                    myCarOnItemClickListener.onItemClick(view, helper.getLayoutPosition());
                }
            }
        });
        View deleteView = helper.getView(R.id.right);
        deleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myCarOnItemClickListener != null) {
                    myCarOnItemClickListener.onItemDeleteClick(view, helper.getLayoutPosition());
                }
            }
        });
    }

    private MyCarOnItemClickListener myCarOnItemClickListener;

    public void setMyCarOnItemClickListener(MyCarOnItemClickListener listener) {
        myCarOnItemClickListener = listener;
    }

    public interface MyCarOnItemClickListener {

        void onItemClick(View view, int position);

        void onItemDeleteClick(View view, int position);

    }

}
