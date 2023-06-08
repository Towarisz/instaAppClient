package com.example.instaapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.instaapp.databinding.ActivityMainPageBinding;
import com.example.instaapp.databinding.ActivityPhotoEditionBinding;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import api.ApiInterface;
import api.model.FilterModel;
import api.model.PhotoModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PhotoEditionActivity extends AppCompatActivity {
    private ActivityPhotoEditionBinding activityPhotoEditionBinding;
    private long photoId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityPhotoEditionBinding = ActivityPhotoEditionBinding.inflate(getLayoutInflater());
        View view = activityPhotoEditionBinding.getRoot();
        setContentView(view);
        photoId = (long) getIntent().getExtras().get("photoId");
        Picasso.get().load("http://192.168.2.122:3000/api/photos/image/"+photoId).memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).into(activityPhotoEditionBinding.imagePreview);

        activityPhotoEditionBinding.grayscale.setOnClickListener(view1 -> {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://192.168.2.122:3000")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            ApiInterface Api = retrofit.create(ApiInterface.class);
            SharedPreferences settings = getSharedPreferences("loginSettings", Context.MODE_PRIVATE);
            String token = "Bearer "+settings.getString("token",null);

            Call<PhotoModel> call = Api.applayFilter(token,new FilterModel(photoId,"GRAYSCALE"));
            call.enqueue(new Callback<PhotoModel>() {
                @Override
                public void onResponse(Call<PhotoModel> call, Response<PhotoModel> response) {
                    if(response.code()!= 200){

                    }else{
                        Picasso.get().load("http://192.168.2.122:3000/api/photos/image/"+photoId).memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).into(activityPhotoEditionBinding.imagePreview);
                    }
                }

                @Override
                public void onFailure(Call<PhotoModel> call, Throwable t) {

                }
        });
    });
        activityPhotoEditionBinding.negate.setOnClickListener(view1 -> {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://192.168.2.122:3000")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            ApiInterface Api = retrofit.create(ApiInterface.class);
            SharedPreferences settings = getSharedPreferences("loginSettings", Context.MODE_PRIVATE);
            String token = "Bearer "+settings.getString("token",null);
            Call<PhotoModel> call = Api.applayFilter(token,new FilterModel(photoId,"NEGATE"));
            call.enqueue(new Callback<PhotoModel>() {
                @Override
                public void onResponse(Call<PhotoModel> call, Response<PhotoModel> response) {
                    if(response.code()!= 200){

                    }else{
                        Picasso.get().load("http://192.168.2.122:3000/api/photos/image/"+photoId).memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).into(activityPhotoEditionBinding.imagePreview);
                    }
                }

                @Override
                public void onFailure(Call<PhotoModel> call, Throwable t) {

                }
        });
    });


    }

}