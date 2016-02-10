package com.alvinhx.endlessImageGridView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.fivehundredpx.api.FiveHundredException;
import com.fivehundredpx.api.PxApi;
import com.fivehundredpx.api.auth.AccessToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements GETPhotosTask.Delegate {

    private String TAG = "MainActivity";
    private AccessToken accessToken = null;
    private ArrayList<HashMap<String, Object>> ImgArray = null;

    JSONObject jobj_photo = null;

    private int offset = 0;// count current item amount
    private GridView gridview;
    private Toolbar toolbar;
    private Bitmap scrBitmap;

    private GETPhotosTask getPhotosTask = null;
    private boolean isLoading = true;

    private Activity mContext;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        context = this.getApplicationContext();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setTitle("All Photos");
        }

        gridview = (GridView) findViewById(R.id.grid);

        //getting access token
        accessToken = Config.getInstance().getKey("auth_token");
        Log.d(TAG, accessToken.getToken());

        // getting photos list infor -> request for 440px img
        if (accessToken != null) {
            ImgArray = new ArrayList<HashMap<String, Object>>();
            getPhotosTask = new GETPhotosTask();
            getPhotosTask.execute(ActivityConstants.GET_PHOTO_THUMBNAIL440);
        } else {
            Log.d("MainActivity", "accesstoken is none");
        }


    }


    public class GETPhotosTask extends AsyncTask<String, Void, Void> {

        private ProgressDialog mDialog = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog = new ProgressDialog(mContext, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
            mDialog.setMessage("Loading images...");
            mDialog.show();
            Log.d(TAG, "get photo task now");
        }

        @Override
        protected Void doInBackground(String... params) {
            final String consumerkey = mContext.getString(R.string.px_consumer_key);
            final String comsumerSecret = mContext.getString(R.string.px_consumer_secret);
            accessToken = Config.getInstance().getKey("auth_token");
            final String url = params[0];
            jobj_photo = new JSONObject();

            if (null != accessToken) {
                jobj_photo = new PxApi(accessToken, consumerkey, comsumerSecret).get(url);
            }

            try {
                if (jobj_photo.has("photos") || (jobj_photo.get("photos") instanceof JSONArray)) {

                    //get photo array
                    JSONArray jar_photo = jobj_photo.getJSONArray("photos");
                    Log.d(TAG, "photo length is  " + jar_photo.length());
                    for (int i = 0; i < jar_photo.length(); i++) {
                        JSONObject c = jar_photo.getJSONObject(i);
                        String id = c.getString("id");
                        String name = c.getString("name");

                        // get photo thumbnail url
                        JSONArray img_url = c.getJSONArray("images");
                        JSONObject jobj_imgurl = img_url.getJSONObject(0);
                        String thumb_url = jobj_imgurl.getString("https_url");

                        // get user infor
                        JSONObject user_info = c.getJSONObject("user");
                        String user_id = user_info.getString("id");
                        String user_name = user_info.getString("username");
                        String user_picurl = user_info.getString("userpic_url");

                        HashMap<String, Object> map = new HashMap<String, Object>();
                        map.put(ActivityConstants.TAG_IMG_ID, id);
                        map.put(ActivityConstants.TAG_IMG_NAME, name);
                        map.put(ActivityConstants.TAG_IMG_URL, thumb_url);
                        map.put(ActivityConstants.TAG_USER_ID, user_id);
                        map.put(ActivityConstants.TAG_USER_NAME, user_name);
                        map.put(ActivityConstants.TAG_USER_PIC_URL, user_picurl);
                        ImgArray.add(map);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            AddCardToView(ImgArray);
            if (getPhotosTask != null) {
                getPhotosTask = null;
            }


            if (mDialog != null && mDialog.isShowing()) {
                mDialog.dismiss();
            }

        }
    }

    private void GetImageListTask(JSONObject jobj_photo) {
        final JSONObject jobj_local = jobj_photo;

        new AsyncTask<Integer, Void, String>() {

            @Override
            protected String doInBackground(Integer... params) {
                //processing photo data from bucket
                try {
                    if (jobj_local.has("photos") || (jobj_local.get("photos") instanceof JSONArray)) {

                        //get photo array
                        JSONArray jar_photo = jobj_local.getJSONArray("photos");
                        Log.d(TAG, "photo length is  " + jar_photo.length());
                        for (int i = 0; i < jar_photo.length(); i++) {
                            JSONObject c = jar_photo.getJSONObject(i);
                            String id = c.getString("id");
                            String name = c.getString("name");

                            // get photo thumbnail url
                            JSONArray img_url = c.getJSONArray("images");
                            JSONObject jobj_imgurl = img_url.getJSONObject(0);
                            String thumb_url = jobj_imgurl.getString("https_url");

                            // get user infor
                            JSONObject user_info = c.getJSONObject("user");
                            String user_id = user_info.getString("id");
                            String user_name = user_info.getString("username");
                            String user_picurl = user_info.getString("userpic_url");

                            HashMap<String, Object> map = new HashMap<String, Object>();
                            map.put(ActivityConstants.TAG_IMG_ID, id);
                            map.put(ActivityConstants.TAG_IMG_NAME, name);
                            map.put(ActivityConstants.TAG_IMG_URL, thumb_url);
                            map.put(ActivityConstants.TAG_USER_ID, user_id);
                            map.put(ActivityConstants.TAG_USER_NAME, user_name);
                            map.put(ActivityConstants.TAG_USER_PIC_URL, user_picurl);
                            ImgArray.add(map);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                AddCardToView(ImgArray);
                if (getPhotosTask != null) {
                    getPhotosTask = null;
                }
            }


        }.execute();

    }

    private void AddCardToView(ArrayList<HashMap<String, Object>> imgArray) {
        int currentPosition = gridview.getFirstVisiblePosition();
        int scrollState = 0;

        //set up adapter
        SimpleAdapter adapter = new ExtendedSimpleAdapter(getApplicationContext(), ImgArray, R.layout.cardview_layout, new String[]{ActivityConstants.TAG_IMG_URL, ActivityConstants.TAG_IMG_NAME}, new int[]{R.id.img, R.id.title});

        gridview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    scrollState = 1;
                } else {
                    scrollState = 0;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (totalItemCount < offset) {
                    offset = totalItemCount;
                    if (totalItemCount == 0) {
                        isLoading = true;
                    }
                }

                if (isLoading && (totalItemCount > offset)) {
                    isLoading = false;
                    offset = totalItemCount;
                }

                if (totalItemCount != 0 && (firstVisibleItem + visibleItemCount == totalItemCount) && !isLoading&&getPhotosTask==null) {
                    // reach the end
                    //offset = totalItemCount;
                    Log.v("reach the end", "reach the end");
                    getPhotosTask = new GETPhotosTask();
                    getPhotosTask.execute(ActivityConstants.GET_PHOTO_THUMBNAIL440);
                }

            }
        });
        gridview.setAdapter(adapter);
        gridview.setSelection(currentPosition);
    }

    @Override
    public void onSuccess(JSONObject jobj_photo) {
        if (null != jobj_photo) {
            //Log.d(TAG, "receive photo array" + jobj_photo);
            GetImageListTask(jobj_photo);
        }
    }


    @Override
    public void onFail(Exception e) {

    }
}