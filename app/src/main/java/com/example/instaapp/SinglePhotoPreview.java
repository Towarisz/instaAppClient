package com.example.instaapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.instaapp.databinding.ActivityMainPageBinding;
import com.example.instaapp.databinding.ActivitySinglePhotoPreviewBinding;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import api.ApiInterface;
import api.model.PhotoModel;
import api.model.ProfileModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SinglePhotoPreview extends AppCompatActivity {
    private ActivitySinglePhotoPreviewBinding activitySinglePhotoPreviewBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySinglePhotoPreviewBinding = ActivitySinglePhotoPreviewBinding.inflate(getLayoutInflater());
        View view = activitySinglePhotoPreviewBinding.getRoot();
        setContentView(view);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.2.122:3000")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiInterface Api = retrofit.create(ApiInterface.class);

        Call<PhotoModel> call = Api.getPhotoById((long) getIntent().getExtras().get("id"));
        call.enqueue(new Callback<PhotoModel>() {
            @Override
            public void onResponse(Call<PhotoModel> call, Response<PhotoModel> response) {
                if(response.code()!= 200){
                    try {
                        Log.d("XXX", response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    PhotoModel photo = response.body();
                    Call<ProfileModel> call1 = Api.getProfileByID(photo.author);
                    call1.enqueue(new Callback<ProfileModel>() {
                        @Override
                        public void onResponse(Call<ProfileModel> call, Response<ProfileModel> response) {
                            if(response.code()!= 200){
                                try {
                                    Log.d("XXX", response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }else{
                                activitySinglePhotoPreviewBinding.profileName.setText(response.body().name + " " + response.body().lastName);
                            }
                        }

                        @Override
                        public void onFailure(Call<ProfileModel> call, Throwable t) {
                            Log.e("XXX", t.getMessage());

                        }
                    });
                    if(photo.url.indexOf("mp4") == -1){
                        Picasso.get().load("http://192.168.2.122:3000/api/photos/image/"+photo.id).memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).into(activitySinglePhotoPreviewBinding.Photo);
                    }else{
                        activitySinglePhotoPreviewBinding.playerView.setVisibility(View.VISIBLE);
                        activitySinglePhotoPreviewBinding.Photo.setVisibility(View.GONE);
                        ExoPlayer player = new ExoPlayer.Builder(activitySinglePhotoPreviewBinding.getRoot().getContext()).build();
                        activitySinglePhotoPreviewBinding.playerView.setPlayer(player);
                        MediaItem mediaItem = MediaItem.fromUri(Uri.parse("http://192.168.2.122:3000/api/photos/image/"+photo.id));
                        player.setMediaItem(mediaItem);
                        player.prepare();
                        player.play();
                    }

                    Picasso.get().load("http://192.168.2.122:3000/api/profile/pic/"+photo.author).placeholder(R.drawable.baseline_person_24).error(R.drawable.baseline_person_24).memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).into(activitySinglePhotoPreviewBinding.profilePicture);
                }
            }

            @Override
            public void onFailure(Call<PhotoModel> call, Throwable t) {

            }
        });


    }
}