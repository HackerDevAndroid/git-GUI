package lifetime.retrofit.pojo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by MyPC on 16/10/2017.
 */

public class User {

    @SerializedName("name")
    public String name;
    @SerializedName("id")
    public String id;
    @SerializedName("job")
    public String job;

    public User(String name, String job) {
        this.name = name;
        this.job = job;
    }

    @SerializedName("createAt")
    public String createdAt;


}
