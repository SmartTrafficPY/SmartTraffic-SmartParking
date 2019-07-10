package smarttraffic.smartparking.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.Serializable;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import smarttraffic.smartparking.R;
import smarttraffic.smartparking.SmartParkingAPI;
import smarttraffic.smartparking.apiFeed.LoginFeed;
import smarttraffic.smartparking.dataModels.Credentials;

public class LoginService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public LoginService() {
        super("LoginService");
    }

    static final String BASE_URL = "http://10.50.225.77:8000/smartparking/profiles/";

    public static final String LOGIN_ACTION = "Respuesta del Login";
    public static final String BAD_LOGIN_ACTION = "Error en el Login";

    @Override
    protected void onHandleIntent(Intent intent) {
        String pass = intent.getStringExtra("password");
        String alias = intent.getStringExtra("alias");
        Credentials credentials = new Credentials();
        credentials.setAlias(alias);
        credentials.setPassword(pass);

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        SmartParkingAPI smartParkingAPI = retrofit.create(SmartParkingAPI.class);
        Call<LoginFeed> call = smartParkingAPI.loginUser(credentials);

        try{
            Response<LoginFeed> result = call.execute();
            if (result.code() == 200){
                Intent responseIntent = new Intent();
                LoginFeed data = result.body();
                responseIntent.putExtra("identifier", data.getId());
                responseIntent.putExtra("age", data.getAge());
                responseIntent.putExtra("sex", data.getSex());
                responseIntent.setAction(LOGIN_ACTION);
                sendBroadcast(responseIntent);
            }else {
                Intent responseIntent = new Intent();
                responseIntent.putExtra("not_exists", "Wrong credentials");
                responseIntent.setAction(BAD_LOGIN_ACTION);
                sendBroadcast(responseIntent);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
