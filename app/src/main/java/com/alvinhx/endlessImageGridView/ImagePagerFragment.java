package com.alvinhx.endlessImageGridView;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.fivehundredpx.api.PxApi;
import com.fivehundredpx.api.auth.AccessToken;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ImagePagerFragment extends Fragment {
    public static final int INDEX = 2;
    public static final String TAG = "ImagePagerFragment";

    private AccessToken accessToken;
    private Activity mContext;
    private Context context;

    private int selectedIndex;
    private int passedIndex;
    private String passedImgId = null;

    private ImageAdapter adapter;
    private ViewPager pager;
    //private ArrayList<HashMap<String, Object>> ImgArray = null;

    private LoadingPhotosTask loadingPhotosTask = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_image_pager, container, false);

        mContext = getActivity();
        context = getActivity().getApplicationContext();

        // current photo index
        passedIndex = getArguments().getInt(ActivityConstants.TAG_IMAGE_POSITION, 0);
        passedImgId = getArguments().getString(ActivityConstants.TAG_IMG_ID);

        selectedIndex = passedIndex;

        pager = (ViewPager) rootView.findViewById(R.id.pager);

        //getting access token
        accessToken = Config.getInstance().getKey("auth_token");
        Log.d(TAG, accessToken.getToken());


        //getting UI ready
        adapter = new ImageAdapter(getActivity());
        pager.setAdapter(adapter);
        pager.setCurrentItem(passedIndex);
        pager.setTransitionName(passedImgId);
        pager.addOnPageChangeListener(mListener);


        // testing animation function
       /* Transition transition = TransitionInflater.from(getActivity()).inflateTransition(R.transition.changebounds_with_arc);
        getActivity().getWindow().setSharedElementEnterTransition(transition);
        transition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) { }

            @Override
            public void onTransitionEnd(Transition transition) { animateRevealShow(rootView);}

            @Override
            public void onTransitionCancel(Transition transition) { }

            @Override
            public void onTransitionPause(Transition transition) { }

            @Override
            public void onTransitionResume(Transition transition) { }

        });*/

        return rootView;
    }

    // testing animation class
    private void animateRevealShow(View viewRoot) {
        int cx = (viewRoot.getLeft() + viewRoot.getRight()) / 2;
        int cy = (viewRoot.getTop() + viewRoot.getBottom()) / 2;
        int finalRadius = Math.max(viewRoot.getWidth(), viewRoot.getHeight());

        Animator anim = ViewAnimationUtils.createCircularReveal(viewRoot, cx, cy, 0, finalRadius);
        viewRoot.setVisibility(View.VISIBLE);
        anim.setDuration(1000);
        anim.setInterpolator(new AccelerateInterpolator());
        anim.start();
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.d(TAG, "onPause");
        ImageGridFragment.updateState(getActivity(),selectedIndex,String.valueOf(Data.photos_map.get(selectedIndex).get(ActivityConstants.TAG_IMG_ID)));
    }



    // display image slider in full screen
    private class ImageAdapter extends PagerAdapter {
        private LayoutInflater inflater;

        ImageAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return Data.photos_map.size();
        }

        @Override
        public Object instantiateItem(ViewGroup view, int position){
            final View imageLayout = inflater.inflate(R.layout.pager_image_layout,view,false);
            assert imageLayout != null;
            ImageView imageView = (ImageView) imageLayout.findViewById(R.id.img);
            Log.d(TAG, "current index is " + position);
            final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.loading);
            Picasso.with(context).load(String.valueOf(Data.photos_map.get(position).get(ActivityConstants.TAG_IMG_HIRES_URL))).into(imageView);
            view.addView(imageLayout, 0);

            return imageLayout;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }
    }

    // detecting pager swiping event -> load new contents when reach the end
    private ViewPager.OnPageChangeListener mListener = new ViewPager.OnPageChangeListener(){
        boolean isCalling;
        boolean mPageEnd;

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if(mPageEnd && position==selectedIndex && !isCalling ){
                //when reach the end of slide
                Log.d(TAG,"ok");
                loadingPhotosTask = new LoadingPhotosTask();
                loadingPhotosTask.execute(ActivityConstants.URL_GET_PHOTO_THUMBNAIL440 + "&page=" + Data.offset);
                mPageEnd = false;
                isCalling = true;
                // avoid repeating request.
            }else{
                mPageEnd = false;
                isCalling =false;
            }
        }

        @Override
        public void onPageSelected(int position) {
            selectedIndex = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if(selectedIndex== adapter.getCount()-2){
                mPageEnd = true;
            }
        }
    };

    // loading new photos , updating the global photo array
    private class LoadingPhotosTask extends AsyncTask<String,Void,Void>{
        JSONObject jobj_photo = null;

        @Override
        protected Void doInBackground(String... params) {
            final String consumerkey = mContext.getString(R.string.px_consumer_key);
            final String comsumerSecret = mContext.getString(R.string.px_consumer_secret);
            final String url = params[0];
            jobj_photo = new JSONObject();

            if (null != accessToken) {
                jobj_photo = new PxApi(accessToken, consumerkey, comsumerSecret).get(url);
            }

            new ProcessPhotosData().ProcessData(jobj_photo,Data.photos_map);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //Data.photos_map = ImgArray;
            if (loadingPhotosTask != null) {
                loadingPhotosTask = null;
            }
            adapter.notifyDataSetChanged();

            Data.offset++;
        }
    }

    // launch with animated transition
    public static void launch(Activity activity,View transView,String img_id,int position){
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,transView,img_id);
        Intent intent = new Intent(activity,SimpleImageActivity.class);
        intent.putExtra(ActivityConstants.FRAGMENT_INDEX,ImagePagerFragment.INDEX);
        intent.putExtra(ActivityConstants.TAG_IMAGE_POSITION,position);
        intent.putExtra(ActivityConstants.TAG_IMG_ID, img_id);
        ActivityCompat.startPostponedEnterTransition(activity);
        activity.startActivity(intent,options.toBundle());
    }
}
