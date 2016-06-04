package com.logan19gp.flickrviewer.api;

/**
 * Created by george on 6/2/2016.
 */
public class Photo
{
    private Long id;
    private String owner;
    private String secret;
    private String server;
    private String title;
    private String farm;
    private Byte ispublic;
    private Byte isfriend;
    private Byte isfamily;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getFarm()
    {
        return farm;
    }

    public void setFarm(String farm)
    {
        this.farm = farm;
    }

    public String getOwner()
    {
        return owner;
    }

    public void setOwner(String owner)
    {
        this.owner = owner;
    }

    public String getSecret()
    {
        return secret;
    }

    public void setSecret(String secret)
    {
        this.secret = secret;
    }

    public String getServer()
    {
        return server;
    }

    public void setServer(String server)
    {
        this.server = server;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public Byte getIspublic()
    {
        return ispublic;
    }

    public void setIspublic(Byte ispublic)
    {
        this.ispublic = ispublic;
    }

    public Byte getIsfriend()
    {
        return isfriend;
    }

    public void setIsfriend(Byte isfriend)
    {
        this.isfriend = isfriend;
    }

    public Byte getIsfamily()
    {
        return isfamily;
    }

    public void setIsfamily(Byte isfamily)
    {
        this.isfamily = isfamily;
    }

    @Override
    public String toString()
    {
        return "Photo{" +
                "id=" + id +
                ", owner='" + owner + '\'' +
                ", secret='" + secret + '\'' +
                ", server='" + server + '\'' +
                ", title='" + title + '\'' +
                ", ispublic=" + ispublic +
                ", isfriend=" + isfriend +
                ", isfamily=" + isfamily +
                '}';
    }
}
