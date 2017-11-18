package lifetime.retrofit;

import lifetime.retrofit.pojo.MultipleResource;
import lifetime.retrofit.pojo.User;
import lifetime.retrofit.pojo.UserList;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by MyPC on 16/10/2017.
 */

 interface APIInterface {
    @GET("/api/unknown")
    Call<MultipleResource> doGetLisResource();

    @POST("/api/users")
    Call<User> createUser(@Body User user);

    @GET("/api/users?")
    Call<UserList> doGeUserList(@Query("page") String page);

    @FormUrlEncoded
    @POST("/api/users?")
    Call<UserList> doCreateUserWithField(@Field("name") String name, @Field("job") String job);


}
