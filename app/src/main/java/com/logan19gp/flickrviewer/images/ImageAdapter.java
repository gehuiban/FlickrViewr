package com.logan19gp.flickrviewer.images;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.logan19gp.flickrviewer.MainApplication;
import com.logan19gp.flickrviewer.R;
import com.logan19gp.flickrviewer.SearchActivity;
import com.logan19gp.flickrviewer.api.OnEndOfListListener;
import com.logan19gp.flickrviewer.api.Photo;
import com.logan19gp.flickrviewer.api.Photos;
import com.logan19gp.flickrviewer.utils.ImageUtil;
import com.logan19gp.flickrviewer.utils.LogUtil;

import java.util.List;

/**
 * Created by george on 6/3/2016.
 */
public class ImageAdapter extends ArrayAdapter<Photo>
{
    private List<Photo> photos;
    private Activity activity;
    private PanZoomImageViewPager viewPager;
    private LinearLayout pagerContainer;
    private OnEndOfListListener endOfListListener;

    public ImageAdapter(Activity activity, PanZoomImageViewPager viewPager, LinearLayout pagerContainer,
                        Photos photos, OnEndOfListListener endOfListListener)
    {
        super(activity, 0, photos.getPhotosList());
        this.photos = photos.getPhotosList();
        this.activity = activity;
        this.viewPager = viewPager;
        this.pagerContainer = pagerContainer;
        this.endOfListListener = endOfListListener;
    }

    public void addItem(Photo photo)
    {
        this.insert(photo, this.getCount());
    }

    public String getItemUrl(int position)
    {
        return ImageUtil.getURL(this.getItem(position));
    }

    public View getView(final int position, View convertView, ViewGroup parent)
    {
        ImageView imageView;
        if (convertView == null)
        {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            convertView = layoutInflater.inflate(R.layout.adapter_image_item, null);
        }
        imageView = (ImageView) convertView.findViewById(R.id.image_view_adapter);
            imageView.setLayoutParams(new GridView.LayoutParams(ImageUtil.getScreenWidth(activity), ViewGroup.LayoutParams.WRAP_CONTENT));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(0, 0, 0, 0);
        final ImageAdapter imageAdapter = this;
        imageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                LogUtil.log("click on image" + photos.get(position).getId());
                pagerContainer.setVisibility(View.VISIBLE);
                MainApplication.saveToPrefsInt(SearchActivity.IMAGE_OPENED, position);
                FullScreenImageAdapter adapter = new FullScreenImageAdapter(activity, imageAdapter);
                viewPager.setAdapter(adapter);
                viewPager.setCurrentItem(position);
            }
        });

        ImageUtil.loadImageFromURLIntoImageView(getItemUrl(position), imageView, ImageUtil.getScreenWidth(activity),
                R.mipmap.tiny_transparent_block, R.mipmap.tiny_transparent_block);
        if(photos != null && photos.size() > 0 && endOfListListener != null && photos.size() < position + 2)
        {
            endOfListListener.addMorePhotos();
        }
        return imageView;
    }

}
