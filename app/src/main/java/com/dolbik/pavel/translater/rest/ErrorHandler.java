package com.dolbik.pavel.translater.rest;


import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.HttpException;

public class ErrorHandler {

    private static volatile ErrorHandler instance;


    private ErrorHandler() {}


    public static ErrorHandler getInstance() {
        if (instance == null) {
            synchronized (ErrorHandler.class) {
                if (instance == null) {
                    instance = new ErrorHandler();
                }
            }
        }
        return instance;
    }


    public String getErrorMessage(Throwable throwable) {
        if (throwable instanceof HttpException) {
            HttpException exception = (HttpException) throwable;
            ResponseBody responseBody = exception.response().errorBody();
            if (responseBody != null) {
                try {
                    String errorMsg = responseBody.string();
                    responseBody.close();
                    if (!TextUtils.isEmpty(errorMsg)) {
                        Object object = new JSONTokener(errorMsg).nextValue();
                        if (object instanceof JSONObject) {
                            JSONObject jsonObject = (JSONObject) object;
                            String errorCode     = "";
                            String errorMessage  = "";
                            if (jsonObject.has("code")) {
                                errorCode = jsonObject.getString("code");
                            }
                            if (jsonObject.has("message")) {
                                errorMessage = jsonObject.getString("message");
                            }
                            return new StringBuilder(errorCode).append(" ").append(errorMessage).toString();
                        }
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        return throwable.getLocalizedMessage();
    }

}
