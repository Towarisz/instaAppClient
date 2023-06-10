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
import api.model.ProfileModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PostArrayAdapter extends ArrayAdapter<PhotoModel> {

    public PostArrayAdapter(@NonNull Context context, @NonNull List<PhotoModel> _photos) {
        super(context,0, _photos);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.post_list_item,parent,false
            );
        }
        TextView textViewName = convertView.findViewById(R.id.profileName);
        ImageView imageViewPhoto = convertView.findViewById(R.id.Photo);
        ImageView imageViewProfilePic = convertView.findViewById(R.id.profilePicture);
        PlayerView playerView = convertView.findViewById(R.id.player_view);
        PhotoModel photo = getItem(position);

        if(photo != null){
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://192.168.2.122:3000")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            ApiInterface Api = retrofit.create(ApiInterface.class);
            Call<ProfileModel> call = Api.getProfileByID(photo.author);
            call.enqueue(new Callback<ProfileModel>() {
                @Override
                public void onResponse(Call<ProfileModel> call, Response<ProfileModel> response) {
                    if(response.code()!= 200){
                        try {
                            Log.d("XXX", response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else{
                        textViewName.setText(response.body().name + " " + response.body().lastName);
                    }
                }

                @Override
                public void onFailure(Call<ProfileModel> call, Throwable t) {
                    Log.e("XXX", t.getMessage());

                }
            });
            if(photo.url.indexOf("mp4") == -1){
            Picasso.get().load("http://192.168.2.122:3000/api/photos/image/"+photo.id).memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).into(imageViewPhoto);
            }else{
                playerView.setVisibility(View.VISIBLE);
                imageViewPhoto.setVisibility(View.GONE);
                ExoPlayer player = new ExoPlayer.Builder(parent.getContext()).build();
                playerView.setPlayer(player);
                MediaItem mediaItem = MediaItem.fromUri(Uri.parse("http://192.168.2.122:3000/api/photos/image/"+photo.id));
                player.setMediaItem(mediaItem);
                player.prepare();
                player.play();
            }

            Picasso.get().load("http://192.168.2.122:3000/api/profile/pic/"+photo.author).placeholder(R.drawable.baseline_person_24).error(R.drawable.baseline_person_24).memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).into(imageViewProfilePic);
        }

        return convertView;
    }

}
