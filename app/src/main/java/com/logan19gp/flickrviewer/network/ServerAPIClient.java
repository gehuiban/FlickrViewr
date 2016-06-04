package com.logan19gp.flickrviewer.network;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.logan19gp.flickrviewer.MainApplication;

import org.json.JSONObject;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by george on 6/2/2016.
 */
public class ServerAPIClient
{
    private static final String TAG                 = ServerAPIClient.class.getSimpleName();
    private static RequestQueue mRequestQueue       = null;
    private static RetryPolicy retryPolicy          = null;
    private static final int socketTimeout          = 10000;//10 seconds
    protected static Gson gson                      = new Gson();

    /**
     *
     */
    public static class RawJSONandStringBody
    {
        public final JSONObject jsonObject;
        public final String body;
        private Serializable extra;

        public RawJSONandStringBody(String body, JSONObject jsonObject)
        {
            this.jsonObject = jsonObject;
            this.body = body;
        }

        public void putExtra(Serializable extra)
        {
            this.extra = extra;
        }

        public Serializable getExtra()
        {
            return extra;
        }

    }

    /**
     *
     * @return
     */
    public static Map<String, String> serverHeaders()
    {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Application-Name", "test_tickets");
        headers.put("Accept", "*/*");
        headers.put("DNT", "1");
        return headers;
    }

    /**
     *
     */
    public ServerAPIClient()
    {

    }

    /**
     *
     * @param server
     * @param methodName
     * @param parameterValues
     * @return
     */
    private String constructGETrequestURL(String server, String methodName, HashMap<String, String> parameterValues)
    {
        Uri.Builder builder = Uri.parse(server).buildUpon();
        builder.path(methodName);
        if (parameterValues != null && parameterValues.size() > 0)
        {
            for (String key : parameterValues.keySet())
            {
                builder.appendQueryParameter(key, parameterValues.get(key));
            }
        }
        return builder.toString();
    }

    /**
     *
     * @param request
     */
    public static void addRequestToQueue(Request request)
    {
        request.setRetryPolicy(retryPolicy);
        if (mRequestQueue == null)
        {
            Log.i(TAG, "The request queue is re-initialized");
            initializeRequestQueue(MainApplication.getAppContext());
        }
        mRequestQueue.add(request);
    }

    /**
     *
     * @param context
     */
    public static synchronized void initializeRequestQueue(Context context)
    {
        if (mRequestQueue == null)
        {
            retryPolicy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            mRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
    }

    /**
     *
     * @param methodType
     * @param url
     * @param requestBody
     * @param responseClass
     * @param serverHeadersOverrides
     * @param contentTypeOverride
     * @param <T>
     * @return
     */
    public <T> ResponseOrError<T> addRequest_synchronous_custom(final int methodType, final String url, String requestBody,
                        final Class<T> responseClass, final Map<String, String> serverHeadersOverrides, final String contentTypeOverride)
    {
        long startTime = System.currentTimeMillis();
        try
        {
            RequestFuture<T> future = RequestFuture.newFuture();
            Log.d(TAG, "******** call *********" + methodType + "  " + requestBody);
            JsonRequest<T> request = new JsonRequest<T>(methodType, url, requestBody, future, future)
            {
                @Override
                protected VolleyError parseNetworkError(VolleyError volleyError)
                {
                    try
                    {
                        ServerError serverError = null;
                        String jsonString = new String(volleyError.networkResponse.data, HttpHeaderParser.parseCharset(volleyError.networkResponse.headers));
                        Log.d(TAG, "ERROR_FROM_SERVER:" + jsonString);
                        serverError = gson.fromJson(jsonString, ServerError.class);
                        if (serverError == null)
                        {
                            return volleyError;
                        }

                        return serverError;
                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                        return volleyError;
                    }
                }

                @Override
                protected Response<T> parseNetworkResponse(NetworkResponse response)
                {
                    try
                    {
                        String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                        Log.d(TAG, "response:  " + jsonString + " \n ********** end call *********");
                        if (responseClass == JSONObject.class)
                        {
                            JSONObject jObject = new JSONObject(jsonString);
                            return Response.success((T) jObject, HttpHeaderParser.parseCacheHeaders(response));
                        }
                        else if (responseClass == RawJSONandStringBody.class)
                        {
                            JSONObject jObject = new JSONObject(jsonString);
                            RawJSONandStringBody retObj = new RawJSONandStringBody(jsonString, jObject);
                            return Response.success((T) retObj, HttpHeaderParser.parseCacheHeaders(response));
                        }
                        T responseAsResponseClass = gson.fromJson(jsonString, responseClass);
                        return Response.success(responseAsResponseClass, HttpHeaderParser.parseCacheHeaders(response));
                    }
                    catch (UnsupportedEncodingException e)
                    {
                        return Response.error(new ParseError(e));
                    }
                    catch (Exception ex)
                    {
                        return Response.error(new VolleyError(ex));
                    }
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError
                {
                    if (serverHeadersOverrides != null)
                    {
                        return serverHeadersOverrides;
                    }
                    else
                    {
                        return serverHeaders();
                    }
                }

                @Override
                public String getBodyContentType()
                {
                    if (contentTypeOverride != null)
                    {
                        return contentTypeOverride;
                    }
                    else
                    {
                        return super.getBodyContentType();
                    }
                }
            };

            addRequestToQueue(request);

            try
            {
                T responseAsResponseClass = null;
                responseAsResponseClass = future.get(); // this will block
                return new ResponseOrError<T>(responseAsResponseClass, null);
            }
            catch (InterruptedException ex)
            {
                return new ResponseOrError<T>(null, new VolleyError(ex));
            }
            catch (ExecutionException ex)
            {
                ex.printStackTrace();
                if (ex.getCause() instanceof ServerError)
                {
                    return new ResponseOrError<T>(null, (ServerError) ex.getCause());
                }
                else
                {
                    return new ResponseOrError<T>(null, new VolleyError(ex.getMessage()));
                }
            }
        }
        finally
        {
            long endTime = System.currentTimeMillis();
            Log.d("REQUEST_TIME", "REQUEST_TIME(new):" + (endTime - startTime) + ":" + url);
        }
    }
}
