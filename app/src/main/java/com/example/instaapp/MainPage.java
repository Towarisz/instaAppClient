package com.example.instaapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.instaapp.databinding.ActivityMainPageBinding;
import com.example.instaapp.databinding.ActivityRegisterScreenBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import api.ApiInterface;
import api.model.PhotoModel;
import api.model.PostMessage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainPage extends AppCompatActivity {
    private ActivityMainPageBinding activityMainPageBinding;
    private List<PhotoModel> photos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainPageBinding = ActivityMainPageBinding.inflate(getLayoutInflater());
        View view = activityMainPageBinding.getRoot();
        setContentView(view);

        activityMainPageBinding.homePage.setOnClickListener((view1)->{
            finish();
            overridePendingTransition(0, 0);
            startActivity(getIntent());
            overridePendingTransition(0, 0);
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.2.122:3000")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiInterface Api = retrofit.create(ApiInterface.class);
        Call<List<PhotoModel>> call = Api.getAllPhotos();
        call.enqueue(new Callback<List<PhotoModel>>() {
            @Override
            public void onResponse(Call<List<PhotoModel>> call, Response<List<PhotoModel>> response) {
                if(response.code()!= 200){
                    try {
                        Log.d("XXX", response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    photos = new ArrayList<>(response.body());
                    PostArrayAdapter adapter = new PostArrayAdapter(activityMainPageBinding.getRoot().getContext(), photos);
                    activityMainPageBinding.postList.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<PhotoModel>> call, Throwable t) {

            }
        });


    }
}