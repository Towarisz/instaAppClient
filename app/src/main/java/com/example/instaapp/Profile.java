package com.example.instaapp;

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
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import com.example.instaapp.databinding.ActivityProfileBinding;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import api.ApiInterface;
import api.model.Kot;
import api.model.PhotoModel;
import api.model.PostMessage;
import api.model.ProfileModel;
import api.model.UserProfile;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Profile extends AppCompatActivity {
    private ActivityProfileBinding activityProfileBinding;
    private List<Long> photos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityProfileBinding = ActivityProfileBinding.inflate(getLayoutInflater());
        View view = activityProfileBinding.getRoot();

        setContentView(view);


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.2.122:3000")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiInterface Api = retrofit.create(ApiInterface.class);
        SharedPreferences settings = getSharedPreferences("loginSettings", Context.MODE_PRIVATE);
        String token = "Bearer "+settings.getString("token",null);
        Call<UserProfile> call = Api.getProfile(token);
        call.enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                if(response.code()!= 200){
                    try {
                        Log.d("XXX", response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    activityProfileBinding.profileName.setText(response.body().name + "\n" + response.body().lastName);
                    Picasso.get().load("http://192.168.2.122:3000/api/profile/pic/"+response.body().id).memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).placeholder(R.drawable.baseline_person_24).error(R.drawable.baseline_person_24).into(activityProfileBinding.profilePicture);
                    photos = new ArrayList<Long>(response.body().photos);
                    PostProfileAdapter adapter = new PostProfileAdapter(activityProfileBinding.getRoot().getContext(), photos);
                    activityProfileBinding.Images.setAdapter(adapter);
                    activityProfileBinding.Images.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            Long item = (long) adapterView.getItemAtPosition(i);
                            Intent intent = new Intent(getApplicationContext(),SinglePhotoPreview.class);
                            intent.putExtra("id",item);
                            startActivity(intent);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {

            }
        });

        activityProfileBinding.homePage.setOnClickListener((view1)->{
            finish();
            overridePendingTransition(0, 0);
            startActivity(new Intent(getApplicationContext(),MainPage.class));
            overridePendingTransition(0, 0);
        });
        activityProfileBinding.profile.setOnClickListener((view1)->{
            finish();
            overridePendingTransition(0, 0);
            startActivity(getIntent());
            overridePendingTransition(0, 0);
        });

        activityProfileBinding.createPost.setOnClickListener(view1 -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(Profile.this);
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
        activityProfileBinding.profileSettings.setOnClickListener(view1 -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(Profile.this);
            alert.setTitle("Settings");
            alert.setPositiveButton("Change Photo", new AlertDialog.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("*/*");
                    startActivityForResult(intent, 300);
                }
            });

            alert.setNegativeButton("Change Name", new AlertDialog.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activityProfileBinding.getRoot().getContext());
                    builder.setTitle("Name Change");
                    

                    final EditText input = new EditText(activityProfileBinding.getRoot().getContext());
                    final EditText input2 = new EditText(activityProfileBinding.getRoot().getContext());
                    final LinearLayout layout = new LinearLayout(activityProfileBinding.getRoot().getContext());
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    layout.setOrientation(LinearLayout.VERTICAL);
                    input.setWidth(0);
                    input2.setWidth(0);
                    layout.addView(input);
                    layout.addView(input2);
                    builder.setView(layout);


                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final String name = input.getText().toString();
                            final String lastname = input2.getText().toString();

                            Retrofit retrofit = new Retrofit.Builder()
                                    .baseUrl("http://192.168.2.122:3000")
                                    .addConverterFactory(GsonConverterFactory.create())
                                    .build();

                            ApiInterface Api = retrofit.create(ApiInterface.class);
                            String token = "Bearer "+settings.getString("token",null);
                            Call<UserProfile> call = Api.changeName(token,new Kot(name,lastname));
                            call.enqueue(new Callback<UserProfile>() {
                                @Override
                                public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                                    finish();
                                    startActivity(getIntent());
                                }

                                @Override
                                public void onFailure(Call<UserProfile> call, Throwable t) {

                                    Log.d("xxx", t.getMessage());
                                }
                            });


                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();
                }
            });

            alert.setNeutralButton("Logout", new AlertDialog.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("http://192.168.2.122:3000")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    ApiInterface Api = retrofit.create(ApiInterface.class);
                    String token = "Bearer "+settings.getString("token",null);
                    Call<PostMessage> call = Api.logout(token);
                    call.enqueue(new Callback<PostMessage>() {
                        @Override
                        public void onResponse(Call<PostMessage> call, Response<PostMessage> response) {
                            Intent intent = new Intent(getApplicationContext(),LoginScreen.class);
                            finish();
                            startActivity(intent);
                        }

                        @Override
                        public void onFailure(Call<PostMessage> call, Throwable t) {

                            Log.d("xxx", t.getMessage());
                        }
                    });
                }

            });

            alert.show();
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
                            intent.putExtra("photoId",response.body().id);
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
        } else if (requestCode == 100) {
            SharedPreferences settings = getSharedPreferences("loginSettings", Context.MODE_PRIVATE);
            Uri imgData = data.getData();


            File file = new File(getRealPathFromURI(imgData, Profile.this));

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
        }else if(requestCode == 300){
            SharedPreferences settings = getSharedPreferences("loginSettings", Context.MODE_PRIVATE);
            Uri imgData = data.getData();


            File file = new File(getRealPathFromURI(imgData, Profile.this));

            RequestBody fileRequest = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), fileRequest);
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://192.168.2.122:3000")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            ApiInterface Api = retrofit.create(ApiInterface.class);
            String token = "Bearer "+settings.getString("token",null);
            Call<ProfileModel> call = Api.setProfilePhoto(token,body);
            call.enqueue(new Callback<ProfileModel>() {
                @Override
                public void onResponse(Call<ProfileModel> call, Response<ProfileModel> response) {
                    if(response.code() == 200){
                        overridePendingTransition(0, 0);
                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(getIntent());
                    }
                }

                @Override
                public void onFailure(Call<ProfileModel> call, Throwable t) {
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