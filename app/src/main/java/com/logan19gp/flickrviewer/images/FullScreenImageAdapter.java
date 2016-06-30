package com.logan19gp.flickrviewer.images;


import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.logan19gp.flickrviewer.R;
import com.logan19gp.flickrviewer.utils.ImageUtil;

public class FullScreenImageAdapter extends PagerAdapter
{
    private Activity activity;
    private ImageAdapter photos;
    private LayoutInflater inflater;

    public FullScreenImageAdapter(Activity activity, ImageAdapter photos)
    {
        this.activity = activity;
        this.photos = photos;
    }

    @Override
    public int getCount()
    {
        return this.photos.getCount();
    }

    @Override
    public boolean isViewFromObject(View view, Object object)
    {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position)
    {
        TouchImageView imgDisplay;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.layout_fullscreen_image, container, false);
        imgDisplay = (TouchImageView) viewLayout.findViewById(R.id.imgDisplay);
        ImageUtil.loadImageFromURLIntoImageView(ImageUtil.getURL(photos.getItem(position), null),
                imgDisplay, 0, R.mipmap.tiny_transparent_block, R.mipmap.tiny_transparent_block);
        container.addView(viewLayout);
        return viewLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object)
    {
        container.removeView((RelativeLayout) object);
    }
}