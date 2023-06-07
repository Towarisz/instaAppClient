package com.example.instaapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.instaapp.databinding.ActivityRegisterScreenBinding;

import api.ApiInterface;
import api.model.PostMessage;
import api.model.UserRegister;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterScreen extends AppCompatActivity {
    private ActivityRegisterScreenBinding activityRegisterScreenBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityRegisterScreenBinding = ActivityRegisterScreenBinding.inflate(getLayoutInflater());
        View view = activityRegisterScreenBinding.getRoot();
        setContentView(view);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.2.122:3000")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiInterface Api = retrofit.create(ApiInterface.class);

        activityRegisterScreenBinding.registerBtn.setOnClickListener((view1)->{
             if(activityRegisterScreenBinding.editTextName.getText().toString().trim().equals("") ||
                activityRegisterScreenBinding.editTextLastName.getText().toString().trim().equals("") ||
                activityRegisterScreenBinding.editTextTextEmailAddress.getText().toString().trim().equals("") ||
                activityRegisterScreenBinding.editTextTextPassword.getText().toString().trim().equals("")){
            Toast.makeText(this,"All fields are required",Toast.LENGTH_LONG).show();
            }else{
            Call<PostMessage> call = Api.register( new UserRegister(
                    activityRegisterScreenBinding.editTextName.getText().toString().trim(),
                    activityRegisterScreenBinding.editTextLastName.getText().toString().trim(),
                    activityRegisterScreenBinding.editTextTextEmailAddress.getText().toString().trim(),
                    activityRegisterScreenBinding.editTextTextPassword.getText().toString().trim()));
            activityRegisterScreenBinding.registerBtn.setEnabled(false);
                 activityRegisterScreenBinding.registerBtn.setBackgroundColor(0x88888888);
            call.enqueue(new Callback<PostMessage>() {
                @Override
                public void onResponse(Call<PostMessage> call, Response<PostMessage> response) {
                    if(response.code() != 200){
                        Log.d("xxx",response.body().getMessage());
                        activityRegisterScreenBinding.registerBtn.setEnabled(true);
                        activityRegisterScreenBinding.registerBtn.setBackgroundColor(0xFFFFFFFF);
                        return;
                    }else{
                        Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(response.body().getMessage()));

                        new AlertDialog.Builder(activityRegisterScreenBinding.getRoot().getContext())
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
                        activityRegisterScreenBinding.registerBtn.setEnabled(true);
                        activityRegisterScreenBinding.registerBtn.setBackgroundColor(0xFFFFFFFF);

                    }
                }

                @Override
                public void onFailure(Call<PostMessage> call, Throwable t) {
                    activityRegisterScreenBinding.registerBtn.setEnabled(true);
                    activityRegisterScreenBinding.registerBtn.setBackgroundColor(0xFFFFFFFF);
                    t.getMessage();
                }
            });
            }
        });
    }
}