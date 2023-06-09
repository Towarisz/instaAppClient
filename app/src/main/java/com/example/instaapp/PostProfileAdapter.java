package com.example.instaapp;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

import api.ApiInterface;
import api.model.PhotoModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PostProfileAdapter extends ArrayAdapter<Long> {
    public PostProfileAdapter(@NonNull Context context, @NonNull List<Long> _photos) {
        super(context,0, _photos);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.image_on_profile,parent,false
            );
        }
        ImageView imageViewPhoto = convertView.findViewById(R.id.image);
        PlayerView playerView = convertView.findViewById(R.id.player_view);
        Long photo = getItem(position);

        if(photo != null){
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://192.168.2.122:3000")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            ApiInterface Api = retrofit.create(ApiInterface.class);
            Call<PhotoModel> call = Api.getPhotoById(photo);
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
                        if(response.body().url.indexOf("mp4") == -1){
                            Picasso.get().load("http://192.168.2.122:3000/api/photos/image/"+response.body().id).memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).into(imageViewPhoto);
                        }else{
                            playerView.setVisibility(View.VISIBLE);
                            imageViewPhoto.setVisibility(View.GONE);
                            ExoPlayer player = new ExoPlayer.Builder(parent.getContext()).build();
                            playerView.setPlayer(player);
                            MediaItem mediaItem = MediaItem.fromUri(Uri.parse("http://192.168.2.122:3000/api/photos/image/"+response.body().id));
                            player.setMediaItem(mediaItem);
                            player.prepare();
                            player.play();
                        }



                    }
                }

                @Override
                public void onFailure(Call<PhotoModel> call, Throwable t) {
                    Log.e("XXX", t.getMessage());

                }
            });
        }
        return convertView;
    }
}
