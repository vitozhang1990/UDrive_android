package cn.com.i_zj.udrive_az.lz.ui.msg;

import cn.com.i_zj.udrive_az.DBSBaseFragment;
import cn.com.i_zj.udrive_az.R;

/**
 * @author JayQiu
 * @create 2018/11/13
 * @Describe
 */
public class NoticeFragment extends DBSBaseFragment {
    public static NoticeFragment newInstance() {
        NoticeFragment fragment = new NoticeFragment();
        return fragment;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_list;
    }
}
