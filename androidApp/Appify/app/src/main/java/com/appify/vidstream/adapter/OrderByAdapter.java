package com.appify.vidstream.adapter;

import java.util.List;

import com.appify.vidstream.model.OrderByModel;

import com.appify.vidstream.app_10.R;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class OrderByAdapter extends BaseAdapter{

	private Activity activity;
	private List<OrderByModel> orderByModels;
	
	public OrderByAdapter(Activity activity, List<OrderByModel> orderByModels) {
		this.activity = activity;
		this.orderByModels = orderByModels;
	}
	
	@Override
	public int getCount() {
		return orderByModels.size();
	}

	@Override
	public Object getItem(int location) {
		return orderByModels.get(location);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void clearOrderByList(){
		orderByModels.clear();
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View contextView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if(contextView == null)
			contextView = inflater.inflate(R.layout.order_by, null);
		TextView textView = (TextView) contextView.findViewById(R.id.orderByList);
		OrderByModel model = orderByModels.get(position);
		textView.setText(model.getOrderTitle());
		return contextView;
	}

}
