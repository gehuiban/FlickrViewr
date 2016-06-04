package com.logan19gp.flickrviewer.network;

import android.util.Log;

import com.android.volley.VolleyError;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by george on 6/2/2016.
 */
public class ServerError extends VolleyError implements Serializable
{
    private String message;
    private String result;
    private String code;
    private String error;
    private Integer status;
    private String summary;

    private JsonObject invalidAttributes;
    private String model;

    private HashMap<String, ArrayList<ValueRuleMessage>> invalidAttributesMap = null;

    public static class ValueRuleMessage
    {
        public final String value;
        public final String rule;
        public final String message;

        public ValueRuleMessage(String value, String rule, String message)
        {
            this.value = value;
            this.rule = rule;
            this.message = message;
        }

        @Override
        public String toString()
        {
            return "value:" + value + ":rule:" + rule + ":message:" + message;
        }
    }

    public ServerError()
    {

    }

    /**
     *
     * @param value
     * @param valueName
     * @return
     * @throws ServerException
     */
    public static long convertToLong(String value, String valueName) throws ServerException
    {
        if (value == null)
        {
            throw new ServerException("Server failed to return '" + valueName + "'");
        }

        try
        {
            long valueAsLong = Long.parseLong(value);
            return valueAsLong;
        }
        catch (Exception ex)
        {
            throw new ServerException("Server returned '" + valueName + "' in an invalid format:" + value, ex);
        }
    }

    /**
     *
     * @return
     */
    public String getError()
    {
        return error;
    }

    /**
     *
     * @return
     */
    public Integer getStatus()
    {
        return status;
    }

    /**
     *
     * @return
     */
    public String getSummary()
    {
        return summary;
    }

    public String getMessage()
    {
        return super.getMessage();
        //return message;
    }

    /**
     * @deprecated
     * @return
     * @throws ServerException
     */
    public long getCode() throws ServerException
    {
        return convertToLong(code, "code");
    }

    /**
     * @deprecated
     * @return
     * @throws ServerException
     */
    public long getResult() throws ServerException
    {
        if (result == null)
        {
            return 0;
        }
        else
        {
            return convertToLong(result, "result");
        }
    }

    public JsonObject getInvalidAttributes()
    {
        return invalidAttributes;
    }

    public String getModel()
    {
        return model;
    }

    public HashMap<String, ArrayList<ValueRuleMessage>> getErrorMap()
    {
        if (getInvalidAttributes() != null && invalidAttributesMap == null)
        {
            invalidAttributesMap = new HashMap<String, ArrayList<ValueRuleMessage>>();
            for (Map.Entry<String, JsonElement> element : getInvalidAttributes().entrySet())
            {
                String key = element.getKey();
                JsonElement jsonElement = element.getValue();
                JsonArray jsonArray = jsonElement.getAsJsonArray();
                for (int inx = 0; inx < jsonArray.size(); ++inx)
                {
                    JsonElement elm = jsonArray.get(inx);
                    JsonObject obj = elm.getAsJsonObject();
                    JsonPrimitive value = obj.getAsJsonPrimitive("value");
                    JsonPrimitive rule = obj.getAsJsonPrimitive("rule");
                    JsonPrimitive message = obj.getAsJsonPrimitive("message");

                    ArrayList<ValueRuleMessage> vrms = invalidAttributesMap.get(key);
                    if (vrms == null)
                    {
                        vrms = new ArrayList<ValueRuleMessage>();
                        invalidAttributesMap.put(key, vrms);
                    }
                    vrms.add(new ValueRuleMessage(value != null ? value.getAsString() : "unknown value", rule != null ? rule.getAsString(): "unknown rule", message != null ? message.getAsString(): "unknown message"));
                }
            }
        }
        return invalidAttributesMap;
    }

    public void dumpInvalidArgsToLog()
    {
        HashMap<String, ArrayList<ValueRuleMessage>> errorMap = getErrorMap();
        if (errorMap != null)
        {
            for (String key : errorMap.keySet())
            {
                Log.d("invalid arg", "invalid arg:" + key + ":" + errorMap.get(key));
            }
        }
    }

}
