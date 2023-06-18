package com.example.ass4.api;

import com.example.ass4.MyApplication;
import com.example.ass4.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginAPI {
    Retrofit retrofit;
    WebServiceAPI webServiceAPI;
    public LoginAPI(){
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl(MyApplication.getContext().getString(R.string.BaseUrl))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        webServiceAPI = retrofit.create(WebServiceAPI.class);
    }
    public Boolean getToken(String username, String password){
        RequestGetTokenAPI requestGetTokenAPI = new RequestGetTokenAPI(username, password);
//        Gson gson= new Gson();
//        String json = gson.toJson(requestGetTokenAPI);
        Call<String> call = webServiceAPI.getToken(requestGetTokenAPI);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    MyApplication.setToken("Bearer "+response.body());
                }
        }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
            }
        });
        return MyApplication.isTokenSet();
    }

}
