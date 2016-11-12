package com.appify.vidstream.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.appify.vidstream.app_10.R;
import com.appify.vidstream.constants.ApplicationConstants;
import com.appify.vidstream.model.VideoModel;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;
import com.google.android.youtube.player.YouTubeThumbnailLoader.ErrorReason;

public class VideoThumbnailListBaseAdapter extends BaseAdapter implements ApplicationConstants {

    private Activity activity;
    private List<VideoModel> videoModels;
    private YouTubeThumbnailLoader thumbnailLoader;

    public VideoThumbnailListBaseAdapter(Activity activity, List<VideoModel> videoModels) {
        this.activity = activity;
        this.videoModels = videoModels;
    }

    @Override
    public int getCount() {
        return videoModels.size();
    }

    @Override
    public Object getItem(int location) {
        return videoModels.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void clearVideoThumbnailList() {
        videoModels.clear();
        notifyDataSetChanged();
    }

    private static class viewHolder {
        TextView thumbnailname;
        TextView thumbnailid;
        YouTubeThumbnailView thumbnailView;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        try {
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            viewHolder holder = new viewHolder();
            final VideoModel model = videoModels.get(position);

            if (convertView == null)
                convertView = new View(activity);
            convertView = inflater.inflate(R.layout.video_thumbal_list, null);

            holder.thumbnailname = (TextView) convertView.findViewById(R.id.videoTextList);
            holder.thumbnailid = (TextView) convertView.findViewById(R.id.videoIdList);
            holder.thumbnailView = (YouTubeThumbnailView) convertView.findViewById(R.id.videoThumbalList);

            convertView.setTag(holder);
            holder = (viewHolder) convertView.getTag();

            holder.thumbnailname.setText(model.getVideoName());
            holder.thumbnailid.setText(model.getVideoId());
            holder.thumbnailView.initialize(API_KEY, new YouTubeThumbnailView.OnInitializedListener() {
                @Override
                public void onInitializationSuccess(
                        YouTubeThumbnailView youTubeThumbnailView,
                        YouTubeThumbnailLoader youTubeThumbnailLoader) {

                    thumbnailLoader = youTubeThumbnailLoader;

                            thumbnailLoader
                            .setOnThumbnailLoadedListener(new ThumbnailLoadedListener());

                    thumbnailLoader
                            .setVideo(model.getVideoId());
                }

                @Override
                public void onInitializationFailure(
                        YouTubeThumbnailView youTubeThumbnailView,
                        YouTubeInitializationResult youTubeInitializationResult) {
                    // write something for failure
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }


    private final class ThumbnailLoadedListener implements
            YouTubeThumbnailLoader.OnThumbnailLoadedListener {
        @Override
        public void onThumbnailError(YouTubeThumbnailView arg0, ErrorReason arg1) {
        }

        @Override
        public void onThumbnailLoaded(YouTubeThumbnailView arg0, String arg1) {
        }
    }

    public void releaseListThumbnailView(){
        try {
            if (thumbnailLoader != null) {
                thumbnailLoader.release();
                Log.e("releaseListThumbnailView>>>","YouTubeThumbnailLoader");
            }
        }catch (Exception e){e.printStackTrace();}
    }

}