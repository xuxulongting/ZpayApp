package com.spreadtrum.iit.zpayapp.network.volley_okhttp;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by SPREADTRUM\ting.long on 16-10-17.
 */

/**
 * 当服务器响应为空时，new JSONObject(jsonString)（jsonString为空）会出现"Volley JSONException: End of input at character 0 of",
 * 为了避开这个错误，重写了parseNetworkResponse（），使得即使服务器响应值为空，也不会出现上述错误。但要注意，
 * Keep in mind that with this fix, and in the event of an empty response from the server,
 * the request callback will return a null reference in place of the JSONObject.
 * 这是否有必要？
 */
public class MyJsonObjectRequest extends JsonObjectRequest {
    public MyJsonObjectRequest(String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(url, jsonRequest, listener, errorListener);
    }
    public MyJsonObjectRequest(int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));

            JSONObject result = null;

            if (jsonString != null && jsonString.length() > 0)
                result = new JSONObject(jsonString);

            return Response.success(result,
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }
}
