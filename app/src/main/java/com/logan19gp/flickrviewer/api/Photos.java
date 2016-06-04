package com.logan19gp.flickrviewer.api;

import java.util.ArrayList;

/**
 * Created by george on 6/2/2016.
 */
public class Photos
{
    private Integer page;
    private Integer pages;
    private Integer perpage;
    private Integer total;
    private Photo[] photo;
    private ArrayList<Photo> photosArray;
    private String stat;

    public Integer getPage()
    {
        return page;
    }

    public void setPage(Integer page)
    {
        this.page = page;
    }

    public Integer getPages()
    {
        return pages;
    }

    public void setPages(Integer pages)
    {
        this.pages = pages;
    }

    public Integer getPerpage()
    {
        return perpage;
    }

    public void setPerpage(Integer perpage)
    {
        this.perpage = perpage;
    }

    public Integer getTotal()
    {
        return total;
    }

    public void setTotal(Integer total)
    {
        this.total = total;
    }

    public Photo[] getPhotos()
    {
        return photo;
    }

    public void setPhotos(Photo[] photo)
    {
        this.photo = photo;
    }
    public ArrayList<Photo> getPhotosList()
    {
        if (photosArray == null)
        {
            photosArray = new ArrayList<>();
        }
        if (photosArray != null && photo != null && photosArray.size() != photo.length)
        {
            photosArray.clear();
            for (Photo item : photo)
            {
                photosArray.add(item);
            }
        }
        return photosArray;
    }
    public void addPhoto(Photo photo)
    {
        if (photosArray == null)
        {
            photosArray = new ArrayList<Photo>();
        }
        this.photosArray.add(photo);
    }

    public String getStat()
    {
        return stat;
    }

    public void setStat(String stat)
    {
        this.stat = stat;
    }

    @Override
    public String toString()
    {
        return "Photos{" +
                "page=" + page +
                ", pages=" + pages +
                ", perpage=" + perpage +
                ", total=" + total +
                ", photos=" + photo +
                ", stat='" + stat + '\'' +
                '}';
    }
}
