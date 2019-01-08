package cn.com.i_zj.udrive_az.map.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.com.i_zj.udrive_az.model.ParkDetailResult.DataBean.CarVosBean;

public class CarCardPagerAdapter extends BasePagerAdapter<CarVosBean> {

    private List<CarVosBean> mDatas = new ArrayList<>();

    public CarCardPagerAdapter(FragmentManager fm, List<CarVosBean> datas) {
        super(fm);
        mDatas.clear();
        if (datas != null) mDatas.addAll(datas);
    }

    @Override
    public Fragment getItem(int position) {
        return CarsFragment.newInstance(position, mDatas.get(position));
    }

    @Override
    public CarVosBean getItemData(int position) {
        return mDatas.size() == 0 ? null : mDatas.get(position);
    }

    @Override
    protected boolean dataEquals(CarVosBean oldData, CarVosBean newData) {
        return oldData.equals(newData);
    }

    @Override
    public int getDataPosition(CarVosBean data) {
        return mDatas.indexOf(data);
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    public CarsFragment getCurrentFragmentItem() {
        return (CarsFragment) getCurrentPrimaryItem();
    }

    public void setNewData(List<CarVosBean> datas) {
        mDatas.clear();
        if (datas != null) mDatas.addAll(datas);
        notifyDataSetChanged();
    }

    public void addData(CarVosBean CarVosBean) {
        mDatas.add(CarVosBean);
        notifyDataSetChanged();
    }

    public void addData(int position, CarVosBean CarVosBean) {
        mDatas.add(position, CarVosBean);
        notifyDataSetChanged();
    }

    public void remove(int position) {
        mDatas.remove(position);
        notifyDataSetChanged();
    }

    public void moveData(int from, int to) {
        if (from == to) return;
        Collections.swap(mDatas, from, to);
        notifyDataSetChanged();
    }

    public void moveDataToFirst(int from) {
        CarVosBean tempData = mDatas.remove(from);
        mDatas.add(0, tempData);
        notifyDataSetChanged();
    }

    public void updateByPosition(int position, CarVosBean CarVosBean) {
        if (position >= 0 && mDatas.size() > position) {
            mDatas.set(position, CarVosBean);
            CarsFragment targetF = getCachedFragmentByPosition(position);
            if (targetF != null) {
                targetF.refresh(CarVosBean);
            }
        }
    }

    public CarsFragment getCachedFragmentByPosition(int position) {
        return (CarsFragment) getFragmentByPosition(position);
    }
}
