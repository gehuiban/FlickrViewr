package com.logan19gp.flickrviewer.network;

import java.io.Serializable;

/**
 * Created by george on 6/2/2016.
 */
public class CommonResponse implements Serializable
{
    private String error;
    private String status;
    private String summary;

    public CommonResponse()
    {

    }

    public boolean isValid()
    {
        return error == null;
    }

    public String getError()
    {
        return error;
    }

    public String getStatus()
    {
        return status;
    }

    public String getSummary()
    {
        return summary;
    }
}
