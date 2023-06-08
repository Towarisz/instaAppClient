package com.example.instaapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;

import com.example.instaapp.databinding.ActivityPhotoEditionBinding;
import com.google.android.material.chip.Chip;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import api.ApiInterface;
import api.model.FilterModel;
import api.model.PhotoModel;
import api.model.TagIDModel;
import api.model.TagModel;
import api.model.TagResponseModel;
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
        if((boolean) getIntent().getExtras().get("video")){
            activityPhotoEditionBinding.negate.setEnabled(false);
            activityPhotoEditionBinding.grayscale.setEnabled(false);
        }

            LayoutInflater inflater = this.getLayoutInflater();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://192.168.2.122:3000")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            ApiInterface Api = retrofit.create(ApiInterface.class);
            Call<List<TagModel>> call = Api.getAllTags();
            call.enqueue(new Callback<List<TagModel>>() {
                @Override
                public void onResponse(Call<List<TagModel>> call, Response<List<TagModel>> response) {
                    if (response.code() != 200) {

                    } else {
                        for (TagModel tag:response.body()
                             ) {
                            Chip chip = (Chip) inflater.inflate(R.layout.tag, null, false);
                            chip.setText(tag.name);
                            chip.setId(tag.id);
                            activityPhotoEditionBinding.choiceGroup.addView(chip);
                        }

                    }
                }

                @Override
                public void onFailure(Call<List<TagModel>> call, Throwable t) {

                }
            });




        photoId = (long) getIntent().getExtras().get("photoId");
        Picasso.get().load("http://192.168.2.122:3000/api/photos/image/"+photoId).memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).into(activityPhotoEditionBinding.imagePreview);

        activityPhotoEditionBinding.grayscale.setOnClickListener(view1 -> {

            SharedPreferences settings = getSharedPreferences("loginSettings", Context.MODE_PRIVATE);
            String token = "Bearer "+settings.getString("token",null);

            Call<PhotoModel> call2 = Api.applayFilter(token,new FilterModel(photoId,"GRAYSCALE"));
            call2.enqueue(new Callback<PhotoModel>() {
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

            SharedPreferences settings = getSharedPreferences("loginSettings", Context.MODE_PRIVATE);
            String token = "Bearer "+settings.getString("token",null);
            Call<PhotoModel> call3 = Api.applayFilter(token,new FilterModel(photoId,"NEGATE"));
            call3.enqueue(new Callback<PhotoModel>() {
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

        activityPhotoEditionBinding.send.setOnClickListener(view1 -> {
            List<Integer> ids = activityPhotoEditionBinding.choiceGroup.getCheckedChipIds();
            ArrayList<TagIDModel> listOfTags = new ArrayList<TagIDModel>();
            for (int value:ids
                 ) {
                listOfTags.add(new TagIDModel(value));
            }
            long photoIdCopy = photoId;
            TagResponseModel response = new TagResponseModel(photoIdCopy,listOfTags);
            SharedPreferences settings = getSharedPreferences("loginSettings", Context.MODE_PRIVATE);
            String token = "Bearer "+settings.getString("token",null);
            Call<PhotoModel> call3 = Api.addTags(token,response);
            call3.enqueue(new Callback<PhotoModel>() {
                @Override
                public void onResponse(Call<PhotoModel> call, Response<PhotoModel> response) {
                    if(response.code()!= 200){

                    }else{
                        Intent intent = new Intent(getApplicationContext(),MainPage.class);
                        finish();
                        startActivity(intent);

                    }
                }

                @Override
                public void onFailure(Call<PhotoModel> call, Throwable t) {

                }
            });
        });


    }

}