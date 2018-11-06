package com.pickerview.adapter;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

/**
 * The simple Array wheel adapter
 * 
 * @param <T>
 *            the element type
 */
public class ArrayWheelAdapter<T> implements WheelAdapter {

	/** The default items length */
	public static final int DEFAULT_LENGTH = 4;
	// items
	public ArrayList<T> items;
	// length
	private int length;

	/**
	 * Constructor
	 * 
	 * @param items
	 *            the items
	 * @param length
	 *            the max items length
	 */
	public ArrayWheelAdapter(ArrayList<T> items, int length) {
		this.items = items;
		this.length = length;
	}

	/**
	 * Contructor
	 * 
	 * @param items
	 *            the items
	 */
	public ArrayWheelAdapter(ArrayList<T> items) {
		this(items, DEFAULT_LENGTH);
	}

	@Override
	public Object getItem(int index) {
		if (index >= 0 && index < items.size()) {
			return items.get(index);
		}
		return "";
	}

	@Override
	public int getItemsCount() {
		return items.size();
	}

	@Override
	public int indexOf(Object o) {
		Log.e("indexOf", "====" + items.indexOf(o));
		return items.indexOf(o);
	}

}
