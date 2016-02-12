package com.alvinhx.endlessImageGridView;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.fivehundredpx.api.FiveHundredException;
import com.fivehundredpx.api.auth.AccessToken;
import com.fivehundredpx.api.tasks.XAuth500pxTask;


/**
 * Created by solor on 2016-02-09.
 */
public class LoginActivity extends AppCompatActivity implements XAuth500pxTask.Delegate{
    String TAG = "LoginAcitivy";
    public static String TAG_ACCESS_TOKEN = "access_auth_token";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        XAuth500pxTask loginTask = new XAuth500pxTask(this);
        loginTask.execute(getString(R.string.px_consumer_key), getString(R.string.px_consumer_secret),getString(R.string.user_name),getString(R.string.password));
        // login using testing account. Normally password is never stored in the app.Here just for testing purpose, temporally store password into string.
    }

    @Override
    public void onSuccess(AccessToken result) {
        Log.d(TAG, "success " + result);

        Config bean = Config.getInstance();
        bean.setKey("auth_token",result);

        Intent intent = new Intent(this, SimpleImageActivity.class);
        startActivity(intent);
        finish();


    }

    @Override
    public void onFail(FiveHundredException e) {
        Log.d(TAG, "unsuccess ");
        e.printStackTrace();
    }
}
