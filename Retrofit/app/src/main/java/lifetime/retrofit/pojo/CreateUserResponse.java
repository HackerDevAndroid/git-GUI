package lifetime.retrofit.pojo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by MyPC on 16/10/2017.
 */

public class CreateUserResponse {

    @SerializedName("name")
    public String name;
    @SerializedName("job")
    public String job;
    @SerializedName("id")
    public String id;
    @SerializedName("createAt")
    public String createAt;
}
