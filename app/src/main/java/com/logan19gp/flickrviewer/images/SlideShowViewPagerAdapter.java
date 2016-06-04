package com.logan19gp.flickrviewer.images;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.logan19gp.flickrviewer.R;

import java.util.ArrayList;

/**
 * Created by george on 6/3/2016.
 */
public class SlideShowViewPagerAdapter extends PagerAdapter
{
    private LayoutInflater inflater;
    private int[] drawableIds;
    private int[] textViewOverlayIds;
    private ArrayList<int[]> textViewOverlayoutTextStringIds = new ArrayList<int[]>();
    private ArrayList<int[]> textViewOverlayoutTextColors = new ArrayList<int[]>();
    private ArrayList<int[]> textViewOverlayoutOutlineTextColors = new ArrayList<int[]>();

    private Integer textSubstitutionLayout;
    private int[] textViewIds;
    private int[] textResourceIds;

    public SlideShowViewPagerAdapter(LayoutInflater inflater)
    {
        this.inflater = inflater;
    }

    public void setDrawablesToDisplay(int[] drawableIds)
    {
        this.drawableIds = drawableIds;
    }

    public void setDrawablesToDisplay(int[] drawableIds, int[] textViewOverlayIds,
            ArrayList<int[]> textViewOverlayoutTextStringIds,
            ArrayList<int[]> textViewOverlayoutTextColors,
            ArrayList<int[]> textViewOverlayoutOutlineTextColors)
    {
        this.drawableIds = drawableIds;
        this.textViewOverlayIds = textViewOverlayIds;
        this.textViewOverlayoutTextStringIds = textViewOverlayoutTextStringIds;
        this.textViewOverlayoutTextColors = textViewOverlayoutTextColors;
        this.textViewOverlayoutOutlineTextColors = textViewOverlayoutOutlineTextColors;
    }

    public void setTextSubstitutionLayout(int layoutId)
    {
        this.textSubstitutionLayout = layoutId;
    }

    public void setTextViewIds(int... ids)
    {
        this.textViewIds = ids;
    }

    public void setTextResourceIds(int... ids)
    {
        this.textResourceIds = ids;
    }

    @Override
    public int getCount()
    {
        if (drawableIds != null)
        {
            return drawableIds.length;
        }
        else
        {
            return textResourceIds.length / textViewIds.length;
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object)
    {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position)
    {

        if (drawableIds != null)
        {
            View itemView = inflater.inflate(R.layout.slide, container, false);

            ImageView slide_image_view = (ImageView) itemView.findViewById(R.id.slide_image_view);

            slide_image_view.setImageResource(drawableIds[position]);

            if (textViewOverlayIds != null)
            {
                for (int inx = 0; inx < textViewOverlayIds.length; ++inx)
                {
                    TextView textView = (TextView) itemView.findViewById(textViewOverlayIds[inx]);

                    int stringId = textViewOverlayoutTextStringIds.get(position)[inx];

                    if (textViewOverlayoutTextColors != null && textViewOverlayoutTextColors.size() > 0)
                    {
                        textView.setTextColor(textViewOverlayoutTextColors.get(position)[inx]);
                    }

//                    if (textViewOverlayoutOutlineTextColors != null && textViewOverlayoutOutlineTextColors.size() > 0)
//                    {
//                        ((OutlinedTextView) textView).setOutlineColor(textViewOverlayoutTextColors.get(position)[inx]);
//                    }

                    textView.setText(stringId);
                }
            }

            // Add viewpager_item.xml to ViewPager
            ((ViewPager) container).addView(itemView);

            return itemView;
        }
        else if (textSubstitutionLayout != null)
        {
            View itemView = inflater.inflate(textSubstitutionLayout, container, false);

            for (int inx = 0; inx < textViewIds.length; ++inx)
            {
                TextView textView = (TextView) itemView.findViewById(textViewIds[inx]);
                int resourceIndex = textViewIds.length * position + inx;
                textView.setText(inflater.getContext().getResources().getString(textResourceIds[resourceIndex]));
            }

            // Add viewpager_item.xml to ViewPager
            ((ViewPager) container).addView(itemView);
            return itemView;
        }
        else
        {
            return null;
        }

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object)
    {
        // Remove viewpager_item.xml from ViewPager
        ((ViewPager) container).removeView((ViewGroup) object);

    }
}
