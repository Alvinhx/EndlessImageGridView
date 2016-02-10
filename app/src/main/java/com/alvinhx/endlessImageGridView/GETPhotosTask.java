package com.alvinhx.endlessImageGridView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.fivehundredpx.api.PxApi;
import com.fivehundredpx.api.auth.AccessToken;

import org.json.JSONObject;

/**
 * Created by solor on 2016-02-09.
 */
public class GETPhotosTask extends AsyncTask<String, Void, JSONObject> {
    private static final String TAG = "GETPhotosTask";
    ProgressDialog mDialog;
    JSONObject jobj_photo = null;
    private AccessToken accessToken = null;

    public interface Delegate {
        public void onSuccess(JSONObject jobj_photo);
        public void onFail(Exception e);
    }

    private Delegate _d;
    private Activity mContext;

    public GETPhotosTask(Delegate delegate,Activity mContext){
        this._d = delegate;
        this.mContext = mContext;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mDialog = new ProgressDialog(mContext, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        mDialog.setMessage("Loading images...");
        mDialog.show();
        Log.d(TAG, "get photo task now");
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        final String consumerkey = mContext.getString(R.string.px_consumer_key);
        final String comsumerSecret = mContext.getString(R.string.px_consumer_secret);
        accessToken = Config.getInstance().getKey("auth_token");
        final String url = params[0];
        jobj_photo = new JSONObject();

        if(null!=accessToken){
        jobj_photo = new PxApi(accessToken, consumerkey, comsumerSecret).get(url);}

        return jobj_photo;
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        if (null != result) {
            //Log.d(TAG, "GET PHOTO RESULT" + jobj_photo.toString());
            _d.onSuccess(result);
        }

        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }

    }

}
