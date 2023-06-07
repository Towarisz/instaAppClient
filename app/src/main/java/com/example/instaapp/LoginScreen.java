package com.example.instaapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.transition.Explode;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.example.instaapp.databinding.ActivityMainBinding;
import com.google.gson.Gson;

import java.io.IOException;

import api.ApiInterface;
import api.model.PostMessage;
import api.model.UserLogin;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginScreen extends AppCompatActivity {
    private ActivityMainBinding activityMainBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        getWindow().setExitTransition(new Explode());

        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = activityMainBinding.getRoot();
        setContentView(view);

        SharedPreferences sharedPref = activityMainBinding.getRoot().getContext().getSharedPreferences(
                "token", activityMainBinding.getRoot().getContext().MODE_PRIVATE);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.2.122:3000")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiInterface Api = retrofit.create(ApiInterface.class);

        // zmiana activity po kliknieciu na registerscreen
        activityMainBinding.createAccountBtn.setOnClickListener((view1)->{
            Intent intent = new Intent(LoginScreen.this, RegisterScreen.class);
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        });

        activityMainBinding.loginBtn.setOnClickListener((view1)->{
            if(activityMainBinding.editTextTextEmailAddress.getText().toString().trim().equals("") ||
               activityMainBinding.editTextTextPassword.getText().toString().trim().equals("")){
                Toast.makeText(this,"All fields are required",Toast.LENGTH_LONG).show();
            }else{
                Call<PostMessage> call = Api.login( new UserLogin(
                        activityMainBinding.editTextTextEmailAddress.getText().toString().trim(),
                        activityMainBinding.editTextTextPassword.getText().toString().trim()));
                activityMainBinding.loginBtn.setBackgroundColor(0x88888888);
                activityMainBinding.loginBtn.setEnabled(false);
                call.enqueue(new Callback<PostMessage>() {
                    @Override
                    public void onResponse(Call<PostMessage> call, Response<PostMessage> response) {
                        if(response.code() == 401){

                            Gson gson = new Gson();
                            try {
                                PostMessage message = gson.fromJson(response.errorBody().string(),PostMessage.class);
                                Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(message.getMessage()));
                                new AlertDialog.Builder(activityMainBinding.getRoot().getContext())
                                        .setTitle("Verification")
                                        .setMessage("Press OK to verify your account")
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                startActivity(intent);
                                                finish();
                                            }
                                        })
                                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                finish();
                                            }
                                        })
                                        .show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }else if(response.code() != 200){
                            try {
                                Gson gson = new Gson();
                                PostMessage message = gson.fromJson(response.errorBody().string(),PostMessage.class);
                                Toast.makeText(activityMainBinding.getRoot().getContext(),message.getMessage() ,Toast.LENGTH_LONG).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            activityMainBinding.loginBtn.setBackgroundColor(0xFFFFFFFF);
                            activityMainBinding.loginBtn.setEnabled(true);
                            return;

                        }else{
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("token", response.body().getMessage());
                            editor.apply();
                            Intent intent = new Intent(LoginScreen.this, MainPage.class);
                            finish();
                            startActivity(intent);
                            activityMainBinding.loginBtn.setBackgroundColor(0xFFFFFFFF);
                            activityMainBinding.loginBtn.setEnabled(true);
                        }
                    }

                    @Override
                    public void onFailure(Call<PostMessage> call, Throwable t) {
                        activityMainBinding.loginBtn.setEnabled(true);
                        activityMainBinding.loginBtn.setBackgroundColor(255255255);
                        Log.d("XXX", t.getMessage());
                    }
                });
            }
    });
    }
}