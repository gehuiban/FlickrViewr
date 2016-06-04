package com.logan19gp.flickrviewer.network;

/**
 * Created by george on 6/2/2016.
 */
public class ServerException extends Exception
{

    private static final long serialVersionUID = 1L;

    public ServerException(String message)
    {
        super(message);
    }

    public ServerException(String message, Throwable exception)
    {
        super(message, exception);
    }

}
