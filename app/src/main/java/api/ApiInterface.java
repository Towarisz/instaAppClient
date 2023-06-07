package api;

import api.model.PostMessage;
import api.model.UserLogin;
import api.model.UserRegister;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiInterface {
    // USERS
    @POST("/api/user/register")
    Call<PostMessage> register(@Body UserRegister user);

    @POST("/api/user/login")
    Call<PostMessage> login(@Body UserLogin user);

    @GET("/api/user/logout")
    Call<PostMessage> logout();

    // TAGI
    @POST("/api/tags")
    Call<PostMessage> createTag(String name);

    @GET("/api/tags")
    Call<PostMessage> getAllTags();

    @GET("/api/tags/raw")
    Call<PostMessage> getAllTagsRaw();

    @GET("/api/tags/{id}")
    Call<PostMessage> getTag(@Path("id") int postId);

    //PROFILE

    @PATCH("/api/profile")
    Call<PostMessage> changeName(String name,String lastname);

    @GET("/api/profile")
    Call<PostMessage> getProfile();

    @POST("/api/profile")
    Call<PostMessage> Profile();
}
