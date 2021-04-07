package in.thegforest.chatting.Main.Chat;


import in.thegforest.chatting.Main.Notification.MyResponse;
import in.thegforest.chatting.Main.Notification.Sender;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
             {
            "Content-Type:application/json",
            "Authorization:key=AAAACThidYY:APA91bFC9kHI0MgjRhb7OXBvMK_gw-4H9OjO9-jw8anV5UV6m5R8YA7MAS4Mcj2Zzj2xQkO0-3j1Xo6A-97U-4xzurOz6SBYyg1ajKZF1WQQlxQIAmFOwCdHJWg7G-9izUkagD1PWwAW"
             }
    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
