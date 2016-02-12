package com.alvinhx.endlessImageGridView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import com.fivehundredpx.api.PxApi;
import com.fivehundredpx.api.auth.AccessToken;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Main page to show grid of thumbnails
 *
 * Created by solor on 2016-02-10.
 */
public class ImageGridFragment extends Fragment {
    public static final int INDEX = 1;
    public static final String TAG = "ImageGridFragment";

    private Toolbar toolbar;
    private GridView gridview;
    private SimpleAdapter adapter = null;

    private AccessToken accessToken;
    private JSONObject jobj_photo = null;

    //private int offset = 1;// count current item amount
    //private ArrayList<HashMap<String, Object>> ImgArray = null;
    private GETPhotosTask getPhotosTask = null;

    private Activity mContext;
    private Context context;

    private static int currentPosition = 0;
    private static String transName = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_grid, container, false);

        ActivityCompat.postponeEnterTransition(getActivity());

        mContext = getActivity();
        context = getActivity().getApplicationContext();

        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("All Photos");
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        }

        gridview = (GridView) rootView.findViewById(R.id.grid);

        //getting access token
        accessToken = Config.getInstance().getKey("auth_token");
        Log.d(TAG, accessToken.getToken());

        // getting photos list infor -> request for 440px img
        if (accessToken != null) {
            //ImgArray = new ArrayList<HashMap<String, Object>>();
            getPhotosTask = new GETPhotosTask();
            getPhotosTask.execute(ActivityConstants.URL_GET_PHOTO_THUMBNAIL440 + "&page=" + Data.offset);
        } else {
            Log.d("MainActivity", "accesstoken is none");
        }

        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG,"onResume");
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        //update current view position
        gridview.smoothScrollToPosition(currentPosition);

    }

    /*
    * AsyncTask to request photos array data from API
    */
    private class GETPhotosTask extends AsyncTask<String, Void, Void> {

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
            final String url = params[0];
            jobj_photo = new JSONObject();

            //making request to get photo data
            if (null != accessToken) {
                jobj_photo = new PxApi(accessToken, consumerkey, comsumerSecret).get(url);
            }

            //process received json data
            new ProcessPhotosData().ProcessData(jobj_photo, Data.photos_map);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // add images into grid view
            AddCardToView(Data.photos_map);
            if (getPhotosTask != null) {
                getPhotosTask = null;
            }

            Data.offset++;

            if (mDialog != null && mDialog.isShowing()) {
                mDialog.dismiss();
            }

        }
    }

    /*
    * function to add images into grid view
    */
    private void AddCardToView(final ArrayList<HashMap<String, Object>> imgArray) {
        currentPosition = gridview.getFirstVisiblePosition();

        //set up adapter
        adapter = new ExtendedSimpleAdapter(getActivity().getApplicationContext(), Data.photos_map, R.layout.cardview_layout, new String[]{ActivityConstants.TAG_IMG_URL, ActivityConstants.TAG_IMG_NAME}, new int[]{R.id.img, R.id.title});

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
                if (totalItemCount != 0 && (firstVisibleItem + visibleItemCount == totalItemCount) && getPhotosTask == null) {
                    //reach the end

                    Log.v("reach the end", "reach the end");
                    getPhotosTask = new GETPhotosTask();
                    getPhotosTask.execute(ActivityConstants.URL_GET_PHOTO_THUMBNAIL440 + "&page=" + Data.offset);
                }

            }
        });
        gridview.setAdapter(adapter);
        gridview.setSelection(currentPosition);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Data.photos_map = ImgArray;
                currentPosition = position;
                transName = String.valueOf(imgArray.get(position).get(ActivityConstants.TAG_IMG_ID));
                view.findViewById(R.id.img).setTransitionName(transName);
                //Picasso.with(context).load(String.valueOf(Data.photos_map.get(position).get(ActivityConstants.TAG_IMG_HIRES_URL))).into((ImageView) view.findViewById(R.id.img));
                Log.d(TAG, "Tansit name is " + transName + " position number is  " + String.valueOf(position));
                ImagePagerFragment.launch(getActivity(), view.findViewById(R.id.img), transName, position);
            }
        });

    }

    public static void updateState(Activity activity,int index,String _transName){
        if(currentPosition!=index){
            currentPosition = index;
            transName = _transName;
            Log.d(TAG,"updateState index and transName is " + currentPosition + " " + transName);
        }
    }
}