package rohan.app.com.kickstarter.Network;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import rohan.app.com.kickstarter.Model.Project;


/**
 * Created by rohan on 06-12-2016.
 */

public interface APIService {

    @GET("kickstarter")
    Call<List<Project>> getAllProjects();

}
