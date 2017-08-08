/*
package com.example.sarika.alzheimerassistant.network;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.example.sarika.alzheimerassistant.bean.PostResponse;

import org.apache.http.HttpEntity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;

*/
/**
 * Created by sarika on 4/11/17.
 *//*


public class MultipartRequest<T> extends Request<T> {

    private MultipartEntityBuilder mBuilder = MultipartEntityBuilder.create();
    private final Response.Listener<T> mListener;
    private final File mImageFile;
    protected Map<String, String> mheaders;
    private String mBoundary;
    private Map<String, String> mParams;
    private String mimageName;
    private String mBodyContentType;

    public void setBoundary(String boundary) {
        this.mBoundary = boundary;
    }

    public MultipartRequest(Map<String,String> headers, String url,  Map<String,String> params, File imageFile,Response.Listener<T> listener, Response.ErrorListener errorListener ) {
        super(Method.POST, url, errorListener);
        mListener = listener;
        mheaders = headers;
        mImageFile = imageFile;
        mParams = params;
        buildMultipartEntity();
    }


    @Override
    public byte[] getPostBody() throws AuthFailureError {
        return super.getPostBody();
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return (mheaders != null) ? mheaders : super.getHeaders();
    }

    private void buildMultipartEntity() {
        for (Map.Entry<String, String> entry : mParams.entrySet()) {
            mBuilder.addTextBody(entry.getKey(), entry.getValue());
        }
        mBuilder.addBinaryBody("uploadedFile", mImageFile, ContentType.MULTIPART_FORM_DATA,mImageFile.getName());
        mBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        String parsed;
        try {
            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            parsed = new String(response.data);
        }
        return (Response<T>)Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(T response) {
        mListener.onResponse(response);
    }


    @Override
    public String getBodyContentType() {
        return mBodyContentType;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            HttpEntity entity = mBuilder.build();
            mBodyContentType = entity.getContentType().getValue();
            entity.writeTo(bos);
        } catch (IOException e) {
            VolleyLog.e("IOException writing to ByteArrayOutputStream bos, building the multipart request.");
        }

        return bos.toByteArray();
    }
}
*/
