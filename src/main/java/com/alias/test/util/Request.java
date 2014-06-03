package com.alias.test.util;

import android.util.Base64;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;

import java.io.IOException;
import java.util.List;

/**
 * Created by aliakbars on 11/20/13.
 */
public class Request {

    public static Result post(String url, List<NameValuePair> params) throws IOException, JSONException {
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);
        post.setHeader("Content-type", "application/x-www-form-urlencoded");
        post.setEntity(new UrlEncodedFormEntity(params));
        HttpResponse response = client.execute(post);
        return new Result(response);
    }

    public static Result postWithBasicAuth(String url, String username, String password, List<NameValuePair> params) throws IOException, JSONException {
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);
        String authorization = "Basic " + Base64.encodeToString((username + ":" + password).getBytes(), Base64.NO_WRAP);
        post.setHeader("Authorization", authorization);
        post.setHeader("Content-type", "application/x-www-form-urlencoded");
        post.setEntity(new UrlEncodedFormEntity(params));
        HttpResponse response = client.execute(post);
        return new Result(response);
    }

    public static Result get(String url) throws IOException, JSONException {
        HttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(url);
        HttpResponse response = client.execute(get);
        return new Result(response);
    }
}
