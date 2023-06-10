package com.example.instaapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.example.instaapp.databinding.ActivityMainPageBinding;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import api.ApiInterface;
import api.model.PhotoModel;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
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
                    activityMainPageBinding.postList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            PhotoModel item = (PhotoModel) adapterView.getItemAtPosition(i);
                            Intent intent = new Intent(getApplicationContext(),SinglePhotoPreview.class);
                            intent.putExtra("id",item.id);
                            startActivity(intent);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<PhotoModel>> call, Throwable t) {

            }
        });
        activityMainPageBinding.createPost.setOnClickListener(view1 -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(MainPage.this);
            alert.setTitle("Choose from ");
            alert.setMessage("Select your method of sending photo");
            alert.setPositiveButton("Camera", new AlertDialog.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    Log.d("xxx", "kotek 1");
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        Log.d("xxx", "kotek 2");
                        startActivityForResult(intent, 200);
                    }
                }
            });
            alert.setNeutralButton("Storage", new AlertDialog.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("*/*");
                    startActivityForResult(intent, 100);
                }

            });

            alert.show();

        });

        activityMainPageBinding.profile.setOnClickListener(view1 -> {
            Intent intent = new Intent(getApplicationContext(), Profile.class);
            finish();
            startActivity(intent);
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200) {
            if (resultCode == RESULT_OK) {
                File pic = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                File dir = new File(pic, "instaAppStoch");
                SharedPreferences settings = getSharedPreferences("loginSettings", Context.MODE_PRIVATE);

                Bundle extras = data.getExtras();
                Log.d("xxx", "getExtras(): " + extras.get("data"));
                Bitmap b = (Bitmap) extras.get("data");
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                b.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                FileOutputStream fs = null;

                try {
                    fs = new FileOutputStream(pic.getPath() + File.separator + "instaAppStoch" + File.separator + "img.png");
                    fs.write(byteArray);
                    fs.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                File file = new File(pic.getPath() + File.separator + "instaAppStoch" + File.separator + "img.png");

                RequestBody fileRequest = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), fileRequest);

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("http://192.168.2.122:3000")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                ApiInterface Api = retrofit.create(ApiInterface.class);
                String token = "Bearer "+settings.getString("token",null);
                Call<PhotoModel> call = Api.sendPhoto(token,body);
                call.enqueue(new Callback<PhotoModel>() {
                    @Override
                    public void onResponse(Call<PhotoModel> call, Response<PhotoModel> response) {
                        if(response.code() == 200){
                            Intent intent = new Intent(getApplicationContext(),PhotoEditionActivity.class);
                            intent.putExtra("video",false);
                            intent.putExtra("photoId",response.body().id);
                            overridePendingTransition(0, 0);
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<PhotoModel> call, Throwable t) {

                        Log.d("xxx", t.getMessage());
                    }
                });


            }
        } else if (requestCode == 100) {
            SharedPreferences settings = getSharedPreferences("loginSettings", Context.MODE_PRIVATE);
            Uri imgData = data.getData();


            File file = new File(getRealPathFromURI(imgData, MainPage.this));

            RequestBody fileRequest = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), fileRequest);
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://192.168.2.122:3000")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            ApiInterface Api = retrofit.create(ApiInterface.class);
            String token = "Bearer "+settings.getString("token",null);
            Call<PhotoModel> call = Api.sendPhoto(token,body);
            call.enqueue(new Callback<PhotoModel>() {
                @Override
                public void onResponse(Call<PhotoModel> call, Response<PhotoModel> response) {
                    if(response.code() == 200){
                        Intent intent = new Intent(getApplicationContext(),PhotoEditionActivity.class);
                        intent.putExtra("photoId",response.body().id);
                        if(response.body().url.indexOf("mp4") == -1){
                            intent.putExtra("video",false);
                        }else{
                            intent.putExtra("video",true);
                        }
                        overridePendingTransition(0, 0);
                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(intent);
                    }
                }

                @Override
                public void onFailure(Call<PhotoModel> call, Throwable t) {
                    Log.d("xxx", t.getMessage());
                }
            });
        }
    }
    public String getRealPathFromURI(Uri contentURI, Activity context) {
        String[] projection = { MediaStore.Images.Media.DATA };
        @SuppressWarnings("deprecation")
        Cursor cursor = context.managedQuery(contentURI, projection, null,
                null, null);
        if (cursor == null)
            return null;
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        if (cursor.moveToFirst()) {
            String s = cursor.getString(column_index);
            return s;
        }
        return null;
    }
}