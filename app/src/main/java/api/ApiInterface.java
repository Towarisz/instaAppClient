package api;

import android.content.SharedPreferences;

import com.example.instaapp.R;

import java.util.List;

import api.model.FilterModel;
import api.model.Kot;
import api.model.PhotoModel;
import api.model.PostMessage;
import api.model.ProfileModel;
import api.model.TagModel;
import api.model.TagResponseModel;
import api.model.UserLogin;
import api.model.UserProfile;
import api.model.UserRegister;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiInterface {
    // USERS
    @POST("/api/user/register")
    Call<PostMessage> register(@Body UserRegister user);

    @POST("/api/user/login")
    Call<PostMessage> login(@Body UserLogin user);

    @GET("/api/user/logout")
    Call<PostMessage> logout(@Header("Authorization") String token);

    // TAGI
    @POST("/api/tags")
    Call<PostMessage> createTag(String name);

    @GET("/api/tags")
    Call<List<TagModel>> getAllTags();

    @GET("/api/tags/raw")
    Call<PostMessage> getAllTagsRaw();

    @GET("/api/tags/{id}")
    Call<PostMessage> getTag(@Path("id") long postId);

    // PROFILE

    @PATCH("/api/profile")
    Call<UserProfile> changeName(@Header("Authorization") String token,@Body Kot body);

    @GET("/api/profile")
    Call<UserProfile> getProfile(@Header("Authorization") String token);

    @Multipart
    @POST("/api/profile")
    Call<ProfileModel> setProfilePhoto(@Header("Authorization") String token,@Part MultipartBody.Part body);

    // PHOTOS

    @PATCH("/api/photos/tags")
    Call<PhotoModel> addTags(@Header("Authorization") String token,@Body TagResponseModel body);

    @DELETE("/api/photos/{id}")
    Call<PostMessage> deletePhoto(@Path("id") int photoId);

    @Multipart
    @POST("/api/photos")
    Call<PhotoModel> sendPhoto(@Header("Authorization") String token,@Part MultipartBody.Part body);

    @GET("/api/photos")
    Call<List<PhotoModel>> getAllPhotos();

    @GET("/api/photos/{id}")
    Call<PhotoModel> getPhotoById(@Path("id")long id);

    @GET("/api/profile/{id}")
    Call<ProfileModel> getProfileByID(@Path("id") long author);

    // Filter

    @PATCH("/api/filters")
    Call<PhotoModel> applayFilter(@Header("Authorization") String token,@Body FilterModel filter);


}
