package com.appify.vidstream.adapter;

import java.util.List;
import com.appify.vidstream.model.CatZationModel;
import com.appify.vidstream.app_10.R;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CategorizationBaseAdapter extends BaseAdapter{
	private Activity activity;
	private List<CatZationModel> catzationModels;
	
	public CategorizationBaseAdapter(Activity activity, List<CatZationModel> catzationModels) {
		this.activity = activity;
		this.catzationModels = catzationModels;
	}

	@Override
	public int getCount() {
		return catzationModels.size();
	}

	@Override
	public Object getItem(int location) {
		return catzationModels.get(location);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void clearCatzList(){
		catzationModels.clear();
		notifyDataSetChanged();
	}

	private static class CategorizationViewHolder
	{
		TextView categorizationName;
		TextView categorizationSelectedID;
	}

	@Override
	public View getView(int position, View convertView , ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		CategorizationViewHolder viewHolder = new CategorizationViewHolder();
		//getting model data from the row
		CatZationModel model = catzationModels.get(position);

		if(convertView == null) {
			convertView = inflater.inflate(R.layout.catzation_row, null);
			viewHolder.categorizationName = (TextView) convertView.findViewById(R.id.categorizationSpinName);
			viewHolder.categorizationSelectedID = (TextView) convertView.findViewById(R.id.categorizationSelectedID);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (CategorizationViewHolder) convertView.getTag();
		}
		viewHolder.categorizationName.setText(model.getCatZationName());
		viewHolder.categorizationSelectedID.setText(model.getCatZationId());

		return convertView;
	}
}
