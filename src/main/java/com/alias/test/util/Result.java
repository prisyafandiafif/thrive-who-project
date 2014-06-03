package com.alias.test.util;

import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by aliakbars on 11/20/13.
 */
public class Result {

    private String status;
    private String message;

    public Result(HttpResponse response) throws IOException, JSONException {
        if (response.getStatusLine().getStatusCode() != 200) {
            this.setStatus("error");
            this.setMessage("Error " + response.getStatusLine().getStatusCode() + ": " + response.getStatusLine().getReasonPhrase());
        } else {
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            String jsonresponse = reader.readLine();
            JSONObject json = new JSONObject(jsonresponse);
            this.setStatus(json.getString("status"));
            this.setMessage(json.getString("message"));
        }
    }

    public Result(String status, String message) {
        this.setStatus(status);
        this.setMessage(message);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
