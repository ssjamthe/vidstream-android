package com.appify.vidstream.adapter;

import java.util.List;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.appify.vidstream.app_10.R;
import com.appify.vidstream.control.AppController;
import com.appify.vidstream.model.CategoriesModel;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CategoryListBaseAdapter extends BaseAdapter {

    private Activity activity;
    private List<CategoriesModel> categoriesModels;

    public CategoryListBaseAdapter(Activity activity, List<CategoriesModel> categoriesModels) {
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

    public void clearCategoryList() {
        categoriesModels.clear();
        notifyDataSetChanged();
    }

    private static class CategoryListViewHolder {
        TextView name;
        TextView id;
        NetworkImageView image;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ImageLoader imageLoader = AppController.getInstance().getImageLoader();
        CategoryListViewHolder viewHolder = new CategoryListViewHolder();
        // getting model data for the row
        final CategoriesModel model = categoriesModels.get(position);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.category_list_item, null);
            viewHolder.name = (TextView) convertView.findViewById(R.id.categoriListText);
            viewHolder.id = (TextView) convertView.findViewById(R.id.categoriListID);
            viewHolder.image = (NetworkImageView) convertView.findViewById(R.id.categoriListImage);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (CategoryListViewHolder) convertView.getTag();
        }
        viewHolder.name.setText(model.getCatTitle());
        viewHolder.id.setText(model.getCateID());
        viewHolder.image.setImageUrl(model.getCatImage(), imageLoader);
        System.out.println("List ImageUrl: " + model.getCatImage());
        return convertView;
    }
}