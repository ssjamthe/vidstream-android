package com.appify.vidstream.adapter;

import com.appify.vidstream.app_12.R;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.appify.vidstream.control.AppController;
import com.appify.vidstream.model.CategoriesModel;

public class CategoryGridBaseAdapter extends BaseAdapter {
    private Activity activity;
    private List<CategoriesModel> categoriesModels;

    public CategoryGridBaseAdapter(Activity activity, List<CategoriesModel> categoriesModels) {
        this.activity = activity;
        this.categoriesModels = categoriesModels;
    }

    @Override
    public int getCount() {
        return categoriesModels.size();
    }

    @Override
    public Object getItem(int location) {
        return categoriesModels.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void clearCategoryGrid() {
        categoriesModels.clear();
        notifyDataSetChanged();
    }

    private static class CategoryGridViewHolder {
        TextView name;
        TextView id;
        NetworkImageView image;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ImageLoader imageLoader = AppController.getInstance().getImageLoader();
        CategoryGridViewHolder viewHolder = new CategoryGridViewHolder();
        // getting model data for the row
        final CategoriesModel model = categoriesModels.get(position);
        if (convertView == null){
            convertView = inflater.inflate(R.layout.category_grid_item, null);
            viewHolder.name = (TextView) convertView.findViewById(R.id.categoriGridText);
            viewHolder.id = (TextView) convertView.findViewById(R.id.categoriGridID);
            viewHolder.image = (NetworkImageView) convertView.findViewById(R.id.categoriGridImage);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (CategoryGridViewHolder) convertView.getTag();
        }
        viewHolder.name.setText(model.getCatTitle());
        viewHolder.id.setText(model.getCateID());
        viewHolder.image.setImageUrl(model.getCatImage(), imageLoader);
        System.out.println("Grid ImageURL: " + model.getCatImage());
        return convertView;
    }

}