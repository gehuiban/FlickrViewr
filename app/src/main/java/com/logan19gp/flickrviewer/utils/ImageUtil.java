package com.logan19gp.flickrviewer.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;

import com.logan19gp.flickrviewer.api.Photo;
import com.logan19gp.flickrviewer.network.ServerAPIClient;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * Created by george on 6/3/2016.
 */
public class ImageUtil
{
    public enum  ImageSize
    {
        s	,//small square 75x75
        q	,//large square 150x150
        t	,//thumbnail, 100 on longest side
        m	,//small, 240 on longest side
        n	,//small, 320 on longest side
        z	,//medium 640, 640 on longest side
        c	,//medium 800, 800 on longest side†
        b	,//large, 1024 on longest side*
        h	,//large 1600, 1600 on longest side†
        k	,//large 2048, 2048 on longest side†
        o	//original image, either a jpg, gif or png, depending on source format
    }

    public static class ImageLoaderLoadingInfo
    {
        final long createdAt;
        final String url;

        public ImageLoaderLoadingInfo(String url)
        {
            createdAt = System.currentTimeMillis();
            this.url = url;
        }

        @Override
        public String toString()
        {
            return "ImageLoaderLoadingInfo:" + url + ":" + createdAt;
        }
    }

    /**
     *
     * @param photo
     * @return the URL for the Image
     */
    public static String getURL(Photo photo)
    {
        return getURL(photo, null);
    }

    /**
     *
     * @param photo
     * @param imageSize
     * @return the URL for the Image
     */
    public static String getURL(Photo photo, ImageSize imageSize)
    {//http://farm{farm-id}.staticflickr.com/{server-id}/{id}_{secret}.jpg
        String imgSize = "";
        if (imageSize != null)
        {
            imgSize = "_" + imgSize.toLowerCase();
        }
        String url = String.format("https://farm%s.staticflickr.com/%s/%d_%s.jpg", photo.getFarm(), photo.getServer(), photo.getId(), photo.getSecret() + imgSize);
        return url;
    }

    /**
     *
     * @param activity
     * @return screen size in pixels
     */
    public static int getScreenWidth(Activity activity)
    {
        return getScreenSize(activity).x;
    }

    /**
     *
     * @param activity
     * @return
     */
    private static Point getScreenSize(Activity activity)
    {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    /**
     *
     * @param url
     * @param imgView
     * @param resize_size_in_pixels
     * @param placeholder_image_res_id
     * @param error_image_res_id
     */
    public static void loadImageFromURLIntoImageView(final String url, final ImageView imgView, final int resize_size_in_pixels,
                                                     final int placeholder_image_res_id, final int error_image_res_id)
    {
        synchronized (ImageLoader.getInstance())
        {
            try
            {
                ImageLoaderLoadingInfo loadingInfo = new ImageLoaderLoadingInfo(url);
                final long requestCreatedAt = loadingInfo.createdAt;
                imgView.setTag(loadingInfo);

                DisplayImageOptions options = new DisplayImageOptions.Builder().cacheOnDisk(true).cacheInMemory(true).
                        considerExifParams(true).showImageOnLoading(placeholder_image_res_id).
                        showImageOnFail(error_image_res_id).extraForDownloader(ServerAPIClient.serverHeaders()).build();
                ImageLoader.getInstance().displayImage(url, imgView, options, new ImageLoadingListener()
                {
                    @Override
                    public void onLoadingStarted(String imageUri, View view)
                    {

                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason)
                    {
                        imgView.setImageResource(error_image_res_id);
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage)
                    {

                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view)
                    {

                    }
                });
                if (false)  // turning this off for now
                    ImageLoader.getInstance().displayImage(url, imgView, options, new ImageLoadingListener()
                    {
                        @Override
                        public void onLoadingStarted(String imageUri, View view)
                        {

                            synchronized (ImageLoader.getInstance())
                            {
                                LogUtil.log("ImageLoader:1(load):" + imageUri + ":" + imgView.hashCode());
                                try
                                {
                                    Object tag = imgView.getTag();
                                    if (tag instanceof ImageLoaderLoadingInfo)
                                    {
                                        LogUtil.log("ImageLoader:2(load):" + imageUri + ":" + imgView.hashCode());
                                        ImageLoaderLoadingInfo info = (ImageLoaderLoadingInfo) tag;
                                        if (info.url != null && info.url.equals(url) && requestCreatedAt == info.createdAt)
                                        {
                                            LogUtil.log("ImageLoader:3(load):" + imageUri + ":" + imgView.hashCode());
                                            imgView.setImageResource(placeholder_image_res_id);
                                        } else
                                        {
                                            LogUtil.log("ImageLoader:4(load):" + imageUri + ":" + imgView.hashCode());
                                            imgView.setImageResource(placeholder_image_res_id);
                                        }
                                    } else
                                    {
                                        LogUtil.log("ImageLoader:5(load):" + imageUri + ":" + imgView.hashCode());
                                    }
                                }
                                catch (Exception ex)
                                {
                                    ex.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason)
                        {
                            synchronized (ImageLoader.getInstance())
                            {
                                LogUtil.log("ImageLoader:1(fail):" + imageUri + ":" + imgView.hashCode() + ":" + failReason);
                                try
                                {
                                    Object tag = imgView.getTag();
                                    if (tag instanceof ImageLoaderLoadingInfo)
                                    {
                                        LogUtil.log("ImageLoader:2(fail):" + imageUri + ":" + imgView.hashCode() + ":" + failReason);
                                        ImageLoaderLoadingInfo info = (ImageLoaderLoadingInfo) tag;
                                        if (info.url != null && info.url.equals(url) && requestCreatedAt == info.createdAt)
                                        {
                                            LogUtil.log("ImageLoader:3(fail):" + imageUri + ":" + imgView.hashCode() + ":" + failReason);
                                            failReason.getCause().printStackTrace();
                                            imgView.setImageResource(error_image_res_id);
                                        } else
                                        {
                                            LogUtil.log("ImageLoader:4(fail):" + imageUri + ":" + imgView.hashCode() + ":" + failReason);
                                        }
                                    } else
                                    {
                                        LogUtil.log("ImageLoader:5(fail):" + imageUri + ":" + imgView.hashCode() + ":" + failReason);
                                    }
                                }
                                catch (Exception ex)
                                {
                                    ex.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage)
                        {
                            synchronized (ImageLoader.getInstance())
                            {
                                LogUtil.log("ImageLoader:1(complete):" + imageUri + ":" + imgView.hashCode());
                                try
                                {
                                    Object tag = imgView.getTag();
                                    if (tag instanceof ImageLoaderLoadingInfo)
                                    {
                                        LogUtil.log("ImageLoader:2(complete):" + imageUri + ":" + imgView.hashCode());
                                        ImageLoaderLoadingInfo info = (ImageLoaderLoadingInfo) tag;
                                        if (info.url != null && info.url.equals(url) && requestCreatedAt == info.createdAt)
                                        {
                                            LogUtil.log("ImageLoader:3(complete):" + imageUri + ":" + imgView.hashCode());
                                            imgView.setImageBitmap(loadedImage);
                                        } else
                                        {
                                            LogUtil.log("ImageLoader:4(complete):" + imageUri + ":" + imgView.hashCode());
                                        }
                                    } else
                                    {
                                        LogUtil.log("ImageLoader:5(complete):" + imageUri + ":" + imgView.hashCode());
                                    }
                                }
                                catch (Exception ex)
                                {
                                    ex.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onLoadingCancelled(String imageUri, View view)
                        {
                            synchronized (ImageLoader.getInstance())
                            {
                                LogUtil.log("ImageLoader:1(cancelled):" + imageUri + ":" + imgView.hashCode());
                                try
                                {
                                    Object tag = imgView.getTag();
                                    if (tag instanceof ImageLoaderLoadingInfo)
                                    {
                                        LogUtil.log("ImageLoader:2(cancelled):" + imageUri + ":" + imgView.hashCode());
                                        ImageLoaderLoadingInfo info = (ImageLoaderLoadingInfo) tag;
                                        if (info.url != null && info.url.equals(url) && requestCreatedAt == info.createdAt)
                                        {
                                            LogUtil.log("ImageLoader:3(cancelled):" + imageUri + ":" + imgView.hashCode());
                                        } else
                                        {
                                            LogUtil.log("ImageLoader:4(cancelled):" + imageUri + ":" + imgView.hashCode());
                                        }
                                    } else
                                    {
                                        LogUtil.log("ImageLoader:5(cancelled):" + imageUri + ":" + imgView.hashCode());
                                    }
                                }
                                catch (Exception ex)
                                {
                                    ex.printStackTrace();
                                }
                            }
                        }
                    });
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }

}
