package com.alvinhx.endlessImageGridView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.fivehundredpx.api.PxApi;
import com.fivehundredpx.api.auth.AccessToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * processing json result from api
 * Created by solor on 2016-02-09.
 */
public class ProcessPhotosData {

    ProcessPhotosData procressPhotoData;

    public static final String TAG = "ProcessPhotoData class";

    public void ProcessData(JSONObject jobj_data, ArrayList<HashMap<String, Object>> arr) {
        try {
            if (jobj_data.has("photos") && (jobj_data.get("photos") instanceof JSONArray)) {

                //get photo array
                JSONArray jar_photo = jobj_data.getJSONArray("photos");
                Log.d(TAG, "photo length is  " + jar_photo.length());
                //Log.d(TAG, "photo array is  " + jobj_photo.toString());
                for (int i = 0; i < jar_photo.length(); i++) {
                    JSONObject c = jar_photo.getJSONObject(i);
                    String id = c.getString("id");
                    String name = c.getString("name");

                    // get photo thumbnail url and uncropped img url
                    JSONArray img_url = c.getJSONArray("images");
                    String thumb_url = null;
                    String hires_url = null;
                    for (int j = 0; j < img_url.length(); j++) {
                        JSONObject jobj_imgurl = img_url.getJSONObject(j);
                        if (jobj_imgurl.getInt("size") == 440) {
                            thumb_url = jobj_imgurl.getString("https_url");
                        } else if (jobj_imgurl.getInt("size") == 4) {
                            hires_url = jobj_imgurl.getString("https_url");
                        }
                    }


                    // get user infor
                    JSONObject user_info = c.getJSONObject("user");
                    String user_id = user_info.getString("id");
                    String user_name = user_info.getString("username");
                    String user_picurl = user_info.getString("userpic_url");

                    HashMap<String, Object> map = new HashMap<String, Object>();
                    map.put(ActivityConstants.TAG_IMG_ID, id);
                    map.put(ActivityConstants.TAG_IMG_NAME, name);
                    map.put(ActivityConstants.TAG_IMG_URL, thumb_url);
                    map.put(ActivityConstants.TAG_IMG_HIRES_URL, hires_url);
                    map.put(ActivityConstants.TAG_USER_ID, user_id);
                    map.put(ActivityConstants.TAG_USER_NAME, user_name);
                    map.put(ActivityConstants.TAG_USER_PIC_URL, user_picurl);
                    arr.add(map);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}