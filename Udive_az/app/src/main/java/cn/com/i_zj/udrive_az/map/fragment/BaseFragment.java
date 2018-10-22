package cn.com.i_zj.udrive_az.map.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;


import butterknife.ButterKnife;


/**
 * 创建人：${liuwei}
 * 创建时间：Created by liuwei on 17-11-1.
 * 修改备注：
 */
public abstract class BaseFragment extends Fragment {
    protected Context mContext;
    private boolean isPrepared;
    private boolean isFirstResume = true;
    private boolean isFirstVisible = true;
    private boolean isFirstInvisible = true;
    public int top = 0;
    private ProgressDialog loadDialog;
    protected View mMainView;
    protected FragmentManager childFm;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity.getApplicationContext();
        childFm = getChildFragmentManager();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected abstract int setLayout(LayoutInflater inflater);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMainView = inflater.inflate(setLayout(inflater), container, false);
        ButterKnife.bind(this, mMainView);
        init(savedInstanceState);
        return mMainView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initprepare();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getUserVisibleHint()) {
            onUserInvisible();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isFirstResume) {
            isFirstResume = false;
            return;
        }
        if (getUserVisibleHint()) {
            onUserVisble();
        }
    }

    public synchronized void initprepare() {
        if (isPrepared) {
            onFirstUserVisible();
        } else
            isPrepared = true;
    }

    protected abstract void onFirstUserVisible();


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (isFirstVisible) {
                isFirstVisible = false;
                initprepare();
            } else
                onUserVisble();
        } else {
            if (isFirstInvisible) {
                isFirstInvisible = false;
                onFirstUserInvisble();
            } else
                onUserInvisible();
        }
    }

    protected abstract void onUserInvisible();

    protected abstract void onFirstUserInvisble();

    protected abstract void onUserVisble();


    protected abstract void init(Bundle savedInstanceState);

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //System.out.println("onDestoryView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dismissDialog();
        loadDialog = null;
        //System.out.println("onDestory");
    }

    public void showLoadDialog(String message) {
        if (loadDialog == null) {
            loadDialog = ProgressDialog.show(getActivity(), "", message);
            loadDialog.setCancelable(true);
        } else if (!loadDialog.isShowing()) {
            loadDialog.show();
            loadDialog.setCancelable(true);
        }
    }

    public void showLoadDialog() {
        showLoadDialog("加载中");
    }

    public void dismissDialog() {
        if (loadDialog != null && loadDialog.isShowing()) {
            loadDialog.dismiss();
        }
    }

    /**
     * 强制隐藏键盘
     */
    public void forceHideIM() {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager == null) {
            return;
        }
        View decorView = getActivity().getWindow().getDecorView();
        inputMethodManager.hideSoftInputFromWindow(decorView.getWindowToken(), 0);
    }
}
