package com.logan19gp.flickrviewer.network;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by george on 6/2/2016.
 */
public abstract class ServerRequestTask<BODY_CLASS, RESPONSE extends ResponseOrError<?>> extends AsyncTask<BODY_CLASS, Void, ResponseOrError<?>>
{

    @Override
    protected void onPreExecute()
    {
    }

    protected void errorAction(ResponseOrError<?> error)
    {

    }

    @Override
    protected void onPostExecute(ResponseOrError<?> result)
    {
        try
        {
            if (result != null)
            {
                if (result.isValid())
                {
                    processSuccessResponse(result);
                }
                else
                {
                    if (result.isServerError())
                    {
                        result.getServerError().printStackTrace();
                        HashMap<String, ArrayList<ServerError.ValueRuleMessage>> errorMap = result.getServerError().getErrorMap();

                        if (errorMap != null)
                        {
                            StringBuilder errorMsg = new StringBuilder();
                            for (String key : errorMap.keySet())
                            {
                                ArrayList<ServerError.ValueRuleMessage> vrms = errorMap.get(key);
                                if (vrms != null)
                                {
                                    for (ServerError.ValueRuleMessage vrm : vrms)
                                    {
                                        errorMsg.append("" + vrm.message);
                                    }
                                }
                            }
                        }
                    }
                    else
                    {
                        result.getError().printStackTrace();
                    }
                    errorAction(result);
                }
            }
            else
            {
                errorAction(result);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            errorAction(result);
        }
    }

    @Override
    protected ResponseOrError<?> doInBackground(BODY_CLASS... body)
    {
        return doInBackground();
    }

    protected abstract ResponseOrError<?> doInBackground();

    protected abstract void processSuccessResponse(ResponseOrError<?> successResponse);
}
